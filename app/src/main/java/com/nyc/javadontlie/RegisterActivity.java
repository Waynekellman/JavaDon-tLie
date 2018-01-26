package com.nyc.javadontlie;

import android.arch.persistence.room.Room;
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
    private Button submit;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgister);
        userName = findViewById(R.id.username);
        password = findViewById(R.id.password);
        submit = findViewById(R.id.submit);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "Users").build();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNameString = userName.getText().toString();
                String passwordString = password.getText().toString();
                User newUser = new User(userNameString,passwordString);
                db.userDao().insertAll(newUser);
                List<User> userList = db.userDao().getAll();
                for (User users : userList) {
                    Log.d(TAG, "onCreate: " + users.getUserName());
                }

            }
        });


    }
}
