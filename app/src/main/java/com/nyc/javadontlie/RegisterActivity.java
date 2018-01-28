package com.nyc.javadontlie;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nyc.javadontlie.database.AppDatabase;
import com.nyc.javadontlie.moneyModel.User;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private EditText userName, password;
    private Button submit, clear;
    private AppDatabase db;
    private User newUser;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgister);
        userName = findViewById(R.id.username);
        password = findViewById(R.id.password);
        submit = findViewById(R.id.submit);
        clear = findViewById(R.id.clear);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "Users").build();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNameString = userName.getText().toString();
                String passwordString = password.getText().toString();
                newUser = new User(userNameString,passwordString);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {


                        db.userDao().insertAll(newUser);
                        userList = db.userDao().getAll();
                        for (User users : userList) {
                            Log.d(TAG, "onCreate: " + users.id + " " + users.getUserName());
                        }
                        if (db.userDao().getAll().size() == 0){
                            Log.d(TAG, "run: " + "List was deleted");
                        } else {
                            Log.d(TAG, "run: " + "List is still there");
                        }


                    }
                });
                thread.start();
                try {
                    thread.join(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        userList = db.userDao().getAll();
                        for (User users : userList) {
                            Log.d(TAG, "onCreate: " + users.id + " " + users.getUserName());
                            db.userDao().delete(users);
                        }
                        if (db.userDao().getAll().size() == 0){
                            Log.d(TAG, "run: " + "List was deleted");
                        }

                    }
                });
                thread.start();
                try {
                    thread.join(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        });





    }
}
