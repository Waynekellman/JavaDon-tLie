package com.nyc.javadontlie;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nyc.javadontlie.controller.Adapter;
import com.nyc.javadontlie.database.AppDatabase;
import com.nyc.javadontlie.moneyModel.Games;
import com.nyc.javadontlie.moneyModel.User;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "StartActivity";
    private RecyclerView recyclerView;
    private Button newGame;
    private EditText newGameName,moneyAmount;
    private List<Games> gamesList;
    private Adapter adapter;
    private Intent intent;
    private User user;
    private AppDatabase db;
    private List<User> userList;
    private String userName, password, userJson;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        recyclerView = findViewById(R.id.recyclerView);
        intent = getIntent();

        newGame = findViewById(R.id.new_game);
        newGameName = findViewById(R.id.new_game_name);
        moneyAmount = findViewById(R.id.money_amount_start);
        setUserAndPass();


        setOnclick();


    }

    private void setOnclick() {
        newGame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initiateUser();
                        if (!newGameName.getText().toString().equals("")&& !moneyAmount.getText().toString().equals("")) {
                            Boolean notCopy = true;
                            for (Games g : gamesList) {
                                if (g.getGameName().equals(newGameName.getText().toString())){
                                    notCopy = false;
                                    break;
                                }
                            }

                            if (notCopy) {
                                Games games = new Games(newGameName.getText().toString(), Integer.parseInt(moneyAmount.getText().toString()));
                                user.addGameToList(games);
                                try {
                                    updateUser();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("GamesName", newGameName.getText().toString());
                                editor.commit();
                                adapter.setGamesList(user.getGameList());
                                Intent intent = new Intent(StartActivity.this, MoneyActivity.class);
                                intent.putExtra("GameName", newGameName.getText().toString());
                                String gameJson = new Gson().toJson(games);
                                intent.putExtra("newGame", gameJson);
                                intent.putExtra("gameIndex", user.getGameList().size()-1);
                                newGameName.setText("");
                                moneyAmount.setText("");
                                startActivity(intent);
                            } else {
                                Toast.makeText(StartActivity.this,"No Copy Names Allowed",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }



    private void setUserAndPass() {
        userName = intent.getStringExtra("userName");
        password = intent.getStringExtra("password");
        sharedPreferences  = getApplicationContext().getSharedPreferences("UserData",MODE_PRIVATE);
        if (sharedPreferences.getString(userName,null) != null){
            userName = sharedPreferences.getString("userName", null);
            password = sharedPreferences.getString("password", null);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("userName", userName);
        editor.putString("password", password);
        editor.commit();
    }

    private void initiateUser() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "Users").build();
                user = db.userDao().findByLogin(userName,password);
                if (user.getGameList() != null ){
                    gamesList = user.getGameList();
                } else
                {
                    gamesList = new ArrayList<>();
                }
                db.close();
            }

        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setUserAndPass();
        initiateUser();

        recView();
    }

    public void updateUser() throws InterruptedException {

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "Users").build();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                db.userDao().updateUsers(user);
                db.close();

            }
        });
        thread.start();
        thread.join(5);
    }

    private void recView() {
        adapter = new Adapter(gamesList,this);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection


        switch (item.getItemId()) {
            case R.id.about_start:
                // get a reference to the already created main layout
                LinearLayout mainLayout = (LinearLayout)
                        findViewById(R.id.start_main_layout);

                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_window, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
