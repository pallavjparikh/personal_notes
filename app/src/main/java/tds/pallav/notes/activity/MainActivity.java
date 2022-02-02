package tds.pallav.notes.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.review.ReviewManager;
import com.suddenh4x.ratingdialog.AppRating;
import com.suddenh4x.ratingdialog.preferences.RatingThreshold;

import org.json.JSONArray;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import tds.pallav.notes.App;
import tds.pallav.notes.R;
import tds.pallav.notes.SettingsActivity;
import tds.pallav.notes.activity_makepass;
import tds.pallav.notes.adapter.DrawerAdapter;
import tds.pallav.notes.db.Controller;
import tds.pallav.notes.dialog.ImportDialog;
import tds.pallav.notes.dialog.SaveDialog;
import tds.pallav.notes.enterpass;
import tds.pallav.notes.fragment.MainFragment;
import tds.pallav.notes.fragment.template.RecyclerFragment;
import tds.pallav.notes.inner.Animator;
import tds.pallav.notes.inner.Formatter;
import tds.pallav.notes.model.Drawer;

public class MainActivity extends AppCompatActivity implements RecyclerFragment.Callbacks {
	public static final int PERMISSION_REQUEST = 3;
	private ReviewManager reviewManager;
	private DrawerLayout drawerLayout;
	public View drawerHolder;
	private boolean exitStatus = false;

	private MainFragment fragment;
	private Toolbar toolbar;
	private View selectionEdit;
	private boolean permissionNotGranted = false;
	private boolean checkForPermission = true;
	public Handler handler = new Handler();
	public Runnable runnable = new Runnable() {
		@Override
		public void run() {
			exitStatus = false;
		}
	};
	private AppRating.Builder ratingBuilder;
	private Object SettingsActivity;


	@SuppressLint({"WrongViewCast","SetTextI18n"})
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final View btnToggleDark = findViewById(R.id.btnToggleDark);
		SettingsActivity = new WeakReference<>(MainActivity.this);

		try {
			//noinspection ConstantConditions
			getSupportActionBar().setDisplayShowTitleEnabled(false);
		} catch (Exception ignored) {
		}

		new AppRating.Builder(this)
				.setMinimumLaunchTimes(2)
				.setMinimumDays(3)
				.setMinimumLaunchTimesToShowAgain(5)
				.setMinimumDaysToShowAgain(5)
				.setRatingThreshold(RatingThreshold.THREE)
				.showIfMeetsConditions();

		setupDrawer();

		selectionEdit = findViewById(R.id.selection_edit);
		selectionEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fragment.onEditSelected();
			}
		});

		if (savedInstanceState == null) {
			fragment = new MainFragment();

			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, fragment)
				.commit();
		}

		if (checkForPermission) {
			checkForPermission = false;
			if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				new MaterialDialog.Builder(this)
					.title(R.string.permission)
					.content(R.string.storage_permission)
					.positiveText(R.string.request)
					.negativeText(R.string.cancel)
					.negativeColor(ContextCompat.getColor(this, R.color.secondary_text))
					.onPositive(new MaterialDialog.SingleButtonCallback() {
						@Override
						public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
							dialog.dismiss();
							requestPermission();
						}
					})
					.onNegative(new MaterialDialog.SingleButtonCallback() {
						@Override
						public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
							dialog.dismiss();
							displayPermissionError();
						}
					})
					.show();
			}
		}


	}


	@Override
	public void onBackPressed() {
		if (drawerLayout.isDrawerOpen(drawerHolder)) {
			drawerLayout.closeDrawers();
			return;
		}

		if (fragment.selectionState) {
			fragment.toggleSelection(false);
			return;
		}

		if (exitStatus) {
			finish();
		} else {
			exitStatus = true;

			Snackbar.make(fragment.fab != null ? fragment.fab : toolbar, R.string.exit_message, Snackbar.LENGTH_LONG).show();

			handler.postDelayed(runnable, 3500);
		}
	}



	private void setupDrawer() {
		// Set date in drawer
		((TextView) findViewById(R.id.drawer_date)).setText(Formatter.formatDate());

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerHolder = findViewById(R.id.drawer_holder);
		ListView drawerList = (ListView) findViewById(R.id.drawer_list);

		// Navigation menu button
		findViewById(R.id.nav_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				drawerLayout.openDrawer(GravityCompat.START);
			}
		});

		// lock menu button
		findViewById(R.id.lock_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), enterpass.class);
				startActivity(intent);
				finish();
			}
		});
		// settings button
		findViewById(R.id.setting_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
				startActivity(intent);
				finish();
			}
		});

		// dark mode button
		findViewById(R.id.btnToggleDark).setOnClickListener(new View.OnClickListener() {
			@Override
					public void onClick(View view) {
						AppCompatDelegate
								.setDefaultNightMode(
										AppCompatDelegate
												.MODE_NIGHT_YES);
					}


		});
		// white mode button
		findViewById(R.id.btnTogglelight).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AppCompatDelegate
						.setDefaultNightMode(
								AppCompatDelegate
										.MODE_NIGHT_NO);
			}


		});

		// Settings button
		findViewById(R.id.settings_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onClickDrawer(Drawer.TYPE_SETTINGS);
			}
		});

		// Change Pin button
		findViewById(R.id.changepass_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), activity_makepass.class);
				startActivity(intent);
				finish();
			}
		});


		// Set adapter of drawer
		drawerList.setAdapter(new DrawerAdapter(
			getApplicationContext(),
			new DrawerAdapter.ClickListener() {
				@Override
				public void onClick(int type) {
					onClickDrawer(type);
				}
			}
		));
	}

	private void onClickDrawer(final int type) {
		drawerLayout.closeDrawers();

		try {
			handler.removeCallbacks(runnable);
		} catch (Exception ignored) {}

		new Thread() {
			@Override
			public void run() {
				try {
					// wait for completion of drawer animation
					sleep(500);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							switch (type) {
								case Drawer.TYPE_ABOUT:
									new MaterialDialog.Builder(MainActivity.this)
										.title(R.string.app_name)
										.content(R.string.about_desc)
										.positiveText(R.string.ok)
										.onPositive(new MaterialDialog.SingleButtonCallback() {
											@Override
											public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
												dialog.dismiss();
											}
										})
										.show();
									break;
								case Drawer.TYPE_BACKUP:
									backupData();
									break;
								case Drawer.TYPE_RESTORE:
									restoreData();
									break;
								case Drawer.TYPE_SETTINGS:

									new MaterialDialog.Builder(MainActivity.this)
										.title("About")
										.content("Made by Pallav Parikh")
											.negativeText("Rate on Play Store")
										.positiveText(R.string.ok)
										.onPositive(new MaterialDialog.SingleButtonCallback() {
											@Override
											public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
												dialog.dismiss();
											}
										})
											.onNegative(new MaterialDialog.SingleButtonCallback() {
												@Override
												public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
													try {
														startActivity(new Intent(Intent.ACTION_VIEW,
																Uri.parse("market://details?id=com.pallav.notes")));
													} catch (android.content.ActivityNotFoundException e) {
														startActivity(new Intent(Intent.ACTION_VIEW,
																Uri.parse("http://play.google.com/store/apps/details?id=com.pallav.notes")));
													}
												}
											})
										.show();
									break;
							}
						}
					});

					interrupt();
				} catch (Exception ignored) {
				}
			}
		}.start();
	}

	@Override
	public void onChangeSelection(boolean state) {
		if (state) {
			Animator.create(getApplicationContext())
				.on(toolbar)
				.setEndVisibility(View.INVISIBLE)
				.animate(R.anim.fade_out);
		} else {
			Animator.create(getApplicationContext())
				.on(toolbar)
				.setStartVisibility(View.VISIBLE)
				.animate(R.anim.fade_in);
		}
	}

	@Override
	public void toggleOneSelection(boolean state) {
		selectionEdit.setVisibility(state ? View.VISIBLE : View.GONE);
	}

	private void restoreData() {
		ImportDialog.newInstance(
			R.string.restore,
			new String[]{App.BACKUP_EXTENSION},
			new ImportDialog.ImportListener() {
				@Override
				public void onSelect(final String path) {
					new Thread() {
						@Override
						public void run() {
							try {
								readBackupFile(path);

								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										fragment.loadItems();

										Snackbar.make(fragment.fab != null ? fragment.fab : toolbar, R.string.data_restored, Snackbar.LENGTH_LONG).show();
									}
								});
							} catch (final Exception e){
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										new MaterialDialog.Builder(MainActivity.this)
											.title(R.string.restore_error)
											.positiveText(R.string.ok)
											.content(e.getMessage())
											.onPositive(new MaterialDialog.SingleButtonCallback() {
												@Override
												public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
													dialog.dismiss();
												}
											})
											.show();
									}
								});
							} finally {
								interrupt();
							}
						}
					}.start();
				}

				@Override
				public void onError(String msg) {
					new MaterialDialog.Builder(MainActivity.this)
						.title(R.string.restore_error)
						.positiveText(R.string.ok)
						.content(msg)
						.onPositive(new MaterialDialog.SingleButtonCallback() {
							@Override
							public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
								dialog.dismiss();
							}
						})
						.show();
				}
			}
		).show(getSupportFragmentManager(), "");
	}

	public void backupData() {
		SaveDialog.newInstance(
			R.string.backup,
			"notes",
			App.BACKUP_EXTENSION,
			new SaveDialog.SaveListener() {
				@Override
				public void onSelect(final String path) {
					new Thread() {
						@Override
						public void run() {
							try {
								saveBackupFile(path);

								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										new MaterialDialog.Builder(MainActivity.this)
											.title(R.string.backup)
											.positiveText(R.string.ok)
											.content(getString(R.string.backup_saved, path))
											.onPositive(new MaterialDialog.SingleButtonCallback() {
												@Override
												public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
													dialog.dismiss();
												}
											})
											.show();
									}
								});
							} catch (final Exception e) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										new MaterialDialog.Builder(MainActivity.this)
											.title(R.string.backup_error)
											.positiveText(R.string.ok)
											.content(e.getMessage())
											.onPositive(new MaterialDialog.SingleButtonCallback() {
												@Override
												public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
													dialog.dismiss();
												}
											})
											.show();
									}
								});
							} finally {
								interrupt();
							}
						}
					}.start();
				}

				@Override
				public void onError() {

				}

				@Override
				public void onCancel() {

				}
			}
		).show(getSupportFragmentManager(), "");
	}

	private void readBackupFile(String path) throws Exception {
		DataInputStream dis = new DataInputStream(new FileInputStream(path));
		byte[] backup_data = new byte[dis.available()];
		dis.readFully(backup_data);
		JSONArray json = new JSONArray(new String(backup_data));
		dis.close();

		Controller.instance.readBackup(json);
	}

	private void saveBackupFile(String path) throws Exception {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path);
			fos.write("[".getBytes("UTF-8"));
			Controller.instance.writeBackup(fos);
			fos.write("]".getBytes("UTF-8"));
			fos.flush();
		} finally {
			if (fos != null) fos.close();
		}
	}

	private void requestPermission() {
		ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
	}

	private void displayPermissionError() {
		new MaterialDialog.Builder(this)
			.title(R.string.permission_error)
			.content(R.string.permission_error_desc)
			.negativeText(R.string.request)
			.positiveText(R.string.continue_anyway)
			.negativeColor(ContextCompat.getColor(this, R.color.secondary_text))
			.onPositive(new MaterialDialog.SingleButtonCallback() {
				@Override
				public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
					dialog.dismiss();
					permissionNotGranted = false;
				}
			})
			.onNegative(new MaterialDialog.SingleButtonCallback() {
				@Override
				public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
					dialog.dismiss();
					requestPermission();
				}
			})
			.show();
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		if (permissionNotGranted) {
			permissionNotGranted = false;
			displayPermissionError();
		}
	}

	@SuppressLint("MissingSuperCall")
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[0])) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(getApplicationContext(), R.string.permission_granted, Toast.LENGTH_SHORT).show();
			} else {
				permissionNotGranted = true;
			}
		}
	}


}
