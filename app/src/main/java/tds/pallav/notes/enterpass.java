package tds.pallav.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import tds.pallav.notes.activity.MainActivity;

public class enterpass extends AppCompatActivity {

    Button button;
    String password;
    EditText EditText15;
    TextView textView;
    private Object EditText;
    private Object TextView;
    private FingerprintManager fingerprintManager;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterpass);


        SharedPreferences settings = getSharedPreferences("PREFS",0);
        password = settings.getString("password", "");

        button = (Button) findViewById(R.id.confirm_button);
        EditText15 = (EditText) findViewById(R.id.get_pass);
        Button bio_button = findViewById(R.id.bio_button);

        final Executor executor = Executors.newSingleThreadExecutor();

        final BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(this)
                .setTitle("Biometric login for Notes")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButton("Exit Notes", executor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }
                }

                ).build();


        final enterpass activity = this;
 
        bio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        biometricPrompt.authenticate(new CancellationSignal(), executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(enterpass.this, "done", Toast.LENGTH_LONG);
                    }
                });
            }

        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text15 = EditText15.getText().toString();


                if(text15.equals(password))
                {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                }
                else {
                    Toast.makeText(enterpass.this, "Password is Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}