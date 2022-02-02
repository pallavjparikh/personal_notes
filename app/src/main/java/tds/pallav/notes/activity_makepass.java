package tds.pallav.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import tds.pallav.notes.activity.MainActivity;

public class activity_makepass extends AppCompatActivity {

    EditText editText12,editText3;
    String password;
    Button button3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makepass);


        editText3 = (EditText) findViewById(R.id.password_enter);
        editText12 = (EditText) findViewById(R.id.password_reenter);
        button3 = (Button) findViewById(R.id.button3);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text12 = editText12.getText().toString();
                String text3 = editText3.getText().toString();



                {
                    if (text3.equals(text12)){


                        SharedPreferences settings = getSharedPreferences("PREFS", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("password", String.valueOf(text12));
                        editor.apply();
                        editor.commit();


                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(activity_makepass.this, "didnt match", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
    }
}