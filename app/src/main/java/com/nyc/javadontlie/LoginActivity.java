package com.nyc.javadontlie;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.nyc.javadontlie.database.AppDatabase;
import com.nyc.javadontlie.moneyModel.User;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText userNmae, password;
    private Button login;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNmae = findViewById(R.id.username_login);
        password = findViewById(R.id.password_login);
        login = findViewById(R.id.login_button);


        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "Users").build();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<User> users = db.userDao().getAll();
                String username = userNmae.getText().toString();
                String passwordForLogin = password.getText().toString();
                for (User user : users) {
                    if (user.getUserName().equals(username) && user.getPassword().equals(passwordForLogin)){
                        Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                        String userJson = new Gson().toJson(user);
                        startActivity(intent);
                    }
                }
            }
        });
    }
}
