package com.nyc.javadontlie;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nyc.javadontlie.database.AppDatabase;
import com.nyc.javadontlie.moneyModel.User;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText userName, password;
    private Button login, register;
    private AppDatabase db;
    private List<User> userList;
    private SharedPreferences sharedPreferences;
    private CheckBox checkBox;
    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


    }

    @Override
    protected void onResume() {
        super.onResume();
        userName = findViewById(R.id.username_login);
        password = findViewById(R.id.password_login);
        checkBox = findViewById(R.id.save_password);
        linearLayout = findViewById(R.id.login_layout);
        login = findViewById(R.id.login_button);
        register = findViewById(R.id.gotoRegister);
        sharedPreferences = getApplicationContext().getSharedPreferences("LoginScreen", MODE_PRIVATE);

        userName.setText("");
        password.setText("");
        Intent intent = getIntent();
        boolean fromRegister = intent.getBooleanExtra("fromRegister", false);
        if (!fromRegister) {

            if (sharedPreferences.getString("LoginPass", null) != null) {
                checkBox.setChecked(true);
                String userNameShared = sharedPreferences.getString("userName", null);
                String passwordShared = sharedPreferences.getString("password", null);
                userName.setText(userNameShared);
                password.setText(passwordShared);
            }
        }

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "Users").build();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                userList = db.userDao().getAll();
                db.close();
            }
        });
        thread.start();

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName.setEnabled(false);
                userName.setEnabled(true);
                password.setEnabled(false);
                password.setEnabled(true);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = userName.getText().toString();
                String passwordForLogin = password.getText().toString();
                boolean notRegistered = false;
                if (!userName.getText().toString().equals("") && !password.getText().toString().equals("")) {

                    for (User user : userList) {
                        if (user.getUserName().equals(username) && user.getPassword().equals(passwordForLogin)) {
                            Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                            intent.putExtra("userName", username);
                            intent.putExtra("password", passwordForLogin);
                            if (checkBox.isChecked()) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userName", username);
                                editor.putString("password", passwordForLogin);
                                editor.commit();
                            }
                            startActivity(intent);
                            notRegistered = false;
                            break;
                        } else {
                            notRegistered = true;
                        }
                    }

                    if (notRegistered) {
                        Toast.makeText(LoginActivity.this, "Login or Password is invalid", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        Log.d(TAG, "onCreate: " + "loginActivity ran");
    }
}
