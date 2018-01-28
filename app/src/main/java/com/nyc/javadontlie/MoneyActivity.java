package com.nyc.javadontlie;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nyc.javadontlie.database.AppDatabase;
import com.nyc.javadontlie.moneyModel.Games;
import com.nyc.javadontlie.moneyModel.LogArrayModel;
import com.nyc.javadontlie.moneyModel.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.fotoapparat.view.CameraView;

public class MoneyActivity extends AppCompatActivity{
    private final String TAG = "MoneyActivity";

    private EditText input,output;
    private TextView amount;
    private Button inputEnter, outputEnter;
    private String gameName, userName, password;
    private int amountMoney;
    private ArrayList<String> logArrayList;
    private Bundle bundle;
    private FrameLayout frameLayout;
    LoggingFragment fragment;
    private Intent intent;
    private CameraView cameraView;
    private Games game;
    private User user;
    private AppDatabase db;
    private SharedPreferences sharedPreferences;
    private int gameIndexInList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
        setFields();
        setFragment();
        setAmount();
        implementOnClicks();
        


    }

    private void implementOnClicks() {
        inputEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!input.getText().toString().equals("")){
                    amountMoney += Integer.parseInt(input.getText().toString());
                    game.setAmount(amountMoney);
                    amount.setText(String.valueOf(amountMoney));
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                    logArrayList.add(0,timeStamp + " Player added: " + input.getText().toString());
                    game.setLog(logArrayList);
                    setAdapter();
                    updateUser();
                    Log.d(TAG,logArrayList.get(logArrayList.size() - 1));

                    LogArrayModel logArrayModel = new LogArrayModel();
                    logArrayModel.setArrayList(logArrayList);

                    input.setText("");
                    input.setEnabled(false);
                    input.setEnabled(true);
                }
            }
        });
        outputEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!output.getText().toString().equals("")){
                    amountMoney -= Integer.parseInt(output.getText().toString());
                    game.setAmount(amountMoney);
                    amount.setText(String.valueOf(amountMoney));
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                    logArrayList.add(0,timeStamp + " Player subtracted: " + output.getText().toString());
                    game.setLog(logArrayList);
                    setAdapter();
                    updateUser();
                    Log.d(TAG,logArrayList.get(logArrayList.size() - 1));

                    LogArrayModel logArrayModel = new LogArrayModel();
                    logArrayModel.setArrayList(logArrayList);

                    output.setText("");
                    output.setEnabled(false);
                    output.setEnabled(true);

                }
            }
        });
    }

    public void updateUser(){
        List<Games> gamesList = user.getGameList();
        gamesList.remove(gameIndexInList);
        gamesList.add(gameIndexInList,game);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "Users").build();
        user.setGameList(gamesList);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                db.userDao().updateUsers(user);
                user = db.userDao().findByLogin(userName, password);
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

    private void setAdapter() {
        if (frameLayout.getVisibility() == View.VISIBLE){
            if (fragment != null) {
                fragment.setAdapter(logArrayList);
            }
        }
        game.setLog(logArrayList);
    }

    private void setFields() {
        intent = getIntent();
        sharedPreferences  = getApplicationContext().getSharedPreferences("UserData",MODE_PRIVATE);
        if (sharedPreferences.getString("userName",null) != null){
            userName = sharedPreferences.getString("userName", null);
            password = sharedPreferences.getString("password", null);
        }
        if (sharedPreferences.getString("GamesName", null) != null){
            gameName = sharedPreferences.getString("GamesName", null);
        } else {
            gameName = intent.getStringExtra("GameName");
        }

        gameIndexInList = intent.getIntExtra("gameIndex", -1);
        logArrayList = new ArrayList<>();
        String gameJson = intent.getStringExtra("newGame");
        game = new Gson().fromJson(gameJson,Games.class);
        logArrayList = game.getLog();
        input = findViewById(R.id.input_amount);
        output = findViewById(R.id.output_amount);
        amount = findViewById(R.id.money_amount);
        inputEnter = findViewById(R.id.input_enter);
        outputEnter = findViewById(R.id.output_enter);
        frameLayout = findViewById(R.id.fragment_container);
        cameraView = findViewById(R.id.camera_view);
        bundle = new Bundle();
        bundle.putString(Constants.LOGGING_FRAG_KEY, gameName);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "Users").build();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                user = db.userDao().findByLogin(userName,password);
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

    private void setFragment() {
        fragment = (LoggingFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null){
            fragment = new LoggingFragment();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        transaction.add(R.id.fragment,fragment);

        transaction.commit();
    }

    private void setAmount() {
        amountMoney = game.getAmount();
        amount.setText(String.valueOf(amountMoney));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.log_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        input.setEnabled(false);
        output.setEnabled(false);
        input.setEnabled(true);
        output.setEnabled(true);



        switch (item.getItemId()) {
            case R.id.log_text_menu:
                if (frameLayout.getVisibility() ==View.GONE){
                    frameLayout.setVisibility(View.VISIBLE);
                    if (fragment != null) {
                        fragment.setAdapter(logArrayList);
                    }
                } else {
                    frameLayout.setVisibility(View.GONE);
                }
                return true;
            case R.id.camera_icon:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("GamesName", gameName);
                editor.commit();
                Intent intent = new Intent(MoneyActivity.this, CameraActivity.class);
                intent.putExtra("gameName", gameName);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
