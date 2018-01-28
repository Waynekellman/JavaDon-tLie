package com.nyc.javadontlie;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.nyc.javadontlie.database.AppDatabase;
import com.nyc.javadontlie.moneyModel.User;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText userName, password;
    private Button login,register;
    private AppDatabase db;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = findViewById(R.id.username_login);
        password = findViewById(R.id.password_login);
        login = findViewById(R.id.login_button);
        register = findViewById(R.id.gotoRegister);
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "Users").build();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                userList = db.userDao().getAll();
            }
        });
        thread.start();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = userName.getText().toString();
                String passwordForLogin = password.getText().toString();
                for (User user : userList) {
                    if (user.getUserName().equals(username) && user.getPassword().equals(passwordForLogin)){
                        Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                        String userJson = new Gson().toJson(user);
                        intent.putExtra("userName", username);
                        intent.putExtra("password", passwordForLogin);
                        startActivity(intent);
                        finish();
                        break;
                    }
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        Log.d(TAG, "onCreate: " + "loginActivity ran");
    }


}
