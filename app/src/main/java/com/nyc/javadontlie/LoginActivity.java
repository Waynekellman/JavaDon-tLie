package com.nyc.javadontlie;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nyc.javadontlie.database.AppDatabase;
import com.nyc.javadontlie.moneyModel.User;
import com.nyc.javadontlie.roomDao.UserSingleton;

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
    private UserSingleton userSingleton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = findViewById(R.id.username_login);
        password = findViewById(R.id.password_login);
        if (savedInstanceState != null) {
            userName.setText(savedInstanceState.getString("userName", null));
            password.setText(savedInstanceState.getString("password", null));
        }
        userSingleton = UserSingleton.getInstance();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!userName.getText().toString().equals("")) {
            outState.putString("userName", userName.getText().toString());
        }
        if (!password.getText().toString().equals("")) {
            outState.putString("password", password.getText().toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBox = findViewById(R.id.save_password);
        linearLayout = findViewById(R.id.login_layout);
        login = findViewById(R.id.login_button);
        register = findViewById(R.id.gotoRegister);
        sharedPreferences = getApplicationContext().getSharedPreferences("LoginPass", MODE_PRIVATE);

        userName.setText("");
        password.setText("");

        if (sharedPreferences.getBoolean("saveUserAndPass", false)) {
            checkBox.setChecked(true);
            String userNameShared = sharedPreferences.getString("userName", null);
            String passwordShared = sharedPreferences.getString("password", null);
            userName.setText(userNameShared);
            password.setText(passwordShared);
        }

        new UserListAsyncTask().execute();
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
                            userSingleton.setUser(user);
                            Intent intent = new Intent(LoginActivity.this, StartActivity.class);
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
        Log.d(TAG, "onResume: " + "loginActivity ran");
    }


    private class UserListAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "Users").build();

            userList = db.userDao().getAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            db.close();
        }
    }
}
