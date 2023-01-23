package tds.pallav.notes;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;

import tds.pallav.notes.activity.MainActivity;
import tds.pallav.notes.db.Controller;
import tds.pallav.notes.dialog.ImportDialog;
import tds.pallav.notes.dialog.SaveDialog;

import static tds.pallav.notes.activity.MainActivity.PERMISSION_REQUEST;

public class SettingsActivity extends AppCompatActivity {
    private boolean permissionNotGranted = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        MainActivity mActivity = new MainActivity();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);



        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .commit();
        }
        // backup button
        findViewById(R.id.backup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backupData();
            }
        });
        // back button

        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // restore button
        findViewById(R.id.restore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreData();
            }
        });

        // info button
        findViewById(R.id.myinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(SettingsActivity.this)
                        .title("About")
                        .content("Made by Pallav Parikh")
                        .negativeText("Contact Me")
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
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=pallavjparikh"));
                                startActivity(intent);
                            }
                        })
                        .show();

            }
        });

        // rate button
        findViewById(R.id.rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(SettingsActivity.this)
                        .title("Rate")
                        .content("If you like this app don't forget to give it 5 stars")
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

            }
        });


        // Change Pin button
        findViewById(R.id.changepin_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), activity_makepass.class);
                startActivity(intent);
                finish();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);



        }
    }
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        assert intent != null;
                        Uri uri = intent.getData();
                        Log.d("myTag", "This is my resultlauncher");
                        //byte[] bytes = getBytesFromUri(getApplicationContext(), uri);
                        try {
                            String filePath= PathUtil.getPath(getApplicationContext(), uri);
                            readBackupFile(filePath);
                            Log.d("myTag", filePath);
                        } catch (URISyntaxException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new MaterialDialog.Builder(SettingsActivity.this)
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
                        } catch (Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new MaterialDialog.Builder(SettingsActivity.this)
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
                        }






                    }
                }
            }
    );


    private void restoreData() {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Downloads.EXTERNAL_CONTENT_URI);
        }
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent = Intent.createChooser(intent, "select backup");
        //startActivityForResult(intent, 1);
        resultLauncher.launch(intent);


        //readBackupFile(r);


//        ImportDialog.newInstance(
//                R.string.restore,
//                new String[]{App.BACKUP_EXTENSION},
//                new ImportDialog.ImportListener() {
//                    @Override
//                    public void onSelect(final String path) {
//                        new Thread() {
//                            @Override
//                            public void run() {
//                                try {
//                                    readBackupFile(path);
//
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//
//
//                                        }
//                                    });
//                                } catch (final Exception e){
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            new MaterialDialog.Builder(SettingsActivity.this)
//                                                    .title(R.string.restore_error)
//                                                    .positiveText(R.string.ok)
//                                                    .content(e.getMessage())
//                                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                                        @Override
//                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                                            dialog.dismiss();
//                                                        }
//                                                    })
//                                                    .show();
//                                        }
//                                    });
//                                } finally {
//                                    interrupt();
//                                }
//                            }
//                        }.start();
//                    }
//
//                    @Override
//                    public void onError(String msg) {
//                        new MaterialDialog.Builder(SettingsActivity.this)
//                                .title(R.string.restore_error)
//                                .positiveText(R.string.ok)
//                                .content(msg)
//                                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                    @Override
//                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                        dialog.dismiss();
//                                    }
//                                })
//                                .show();
//                    }
//                }
//        ).show(getSupportFragmentManager(), "");
    }

    byte[] getBytesFromUri (Context context, Uri uri){
        InputStream iStream = null;
        try {

            iStream = context.getContentResolver().openInputStream(uri);
            JSONObject obj = new JSONObject(String.valueOf(iStream));
            DataInputStream dis = new DataInputStream(new DataInputStream(iStream));
            byte[] backup_data = new byte[dis.available()];

            Log.d(TAG, String.valueOf(backup_data));
            JSONArray json = new JSONArray(new String(backup_data));
            dis.close();
            Controller.instance.readBackup(json);



        }

        catch (Exception ex){

            runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new MaterialDialog.Builder(SettingsActivity.this)
                                                    .title(R.string.restore_error)
                                                    .positiveText(R.string.ok)
                                                    .content(ex.getMessage())
                                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .show();
                                        }
                                   });

        }
        return null;
    }



    private void readBackupFile(String path) throws Exception {
        DataInputStream dis = new DataInputStream(new FileInputStream(path));
        byte[] backup_data = new byte[dis.available()];

        dis.readFully(backup_data);
        JSONArray json = new JSONArray(new String(backup_data));
        dis.close();

        Controller.instance.readBackup(json);
    }
    public void backupData() {

        SaveDialog.newInstance(

                R.string.backup,
                "Perrsonalnotes",
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
                                            new MaterialDialog.Builder(SettingsActivity.this)
                                                    .title(R.string.backup)
                                                    .backgroundColor(R.attr.fabColor)

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
                                            new MaterialDialog.Builder(SettingsActivity.this)
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

//    private void requestPermission() {
//        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
//    }

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
                      //  requestPermission();
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