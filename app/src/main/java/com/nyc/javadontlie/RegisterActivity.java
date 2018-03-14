package com.nyc.javadontlie;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
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
    private User newUser;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userName = findViewById(R.id.username);
        password = findViewById(R.id.password);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userName.getText().toString().equals("") && !password.getText().toString().equals("")) {
                    String userNameString = userName.getText().toString();
                    String passwordString = password.getText().toString();
                    newUser = new User(userNameString, passwordString);
                    new RegisterAsync().execute();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("fromRegister", true);
                    startActivity(intent);
                    finish();
                }

            }
        });


    }

    public class RegisterAsync extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            db.close();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "Users").build();

            db.userDao().insertAll(newUser);
            userList = db.userDao().getAll();
            for (User users : userList) {
                Log.d(TAG, "onCreate: " + users.id + " " + users.getUserName());
            }
            if (db.userDao().getAll().size() == 0) {
                Log.d(TAG, "run: " + "List was deleted");
            } else {
                Log.d(TAG, "run: " + "List is still there");
            }
            return null;
        }
    }
}
