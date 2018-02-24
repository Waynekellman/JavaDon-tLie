package com.nyc.javadontlie;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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

public class MoneyActivity extends AppCompatActivity {
    private final String TAG = "MoneyActivity";

    private EditText input, output;
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
    private String gameJson;
    private MediaPlayer fxPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);

        intent = getIntent();
        if (savedInstanceState != null) {
            gameIndexInList = savedInstanceState.getInt("gameIndex", intent.getIntExtra("gameIndex", -1));
            gameJson = savedInstanceState.getString("newGame");
        } else {
            gameIndexInList = intent.getIntExtra("gameIndex", -1);
            gameJson = intent.getStringExtra("newGame");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setFields();
        setFragment();
        setAmount();
        implementOnClicks();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if ( intent.getIntExtra("gameIndex", -1) != -1){

            outState.putInt("gameIndex",intent.getIntExtra("gameIndex", -1));
            outState.putString("newGame",intent.getStringExtra("newGame"));
        }
    }

    private void implementOnClicks() {
        inputEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!input.getText().toString().equals("")) {
                    playSound(R.raw.cha_ching);
                    amountMoney += Integer.parseInt(input.getText().toString());
                    game.setAmount(amountMoney);
                    amount.setText(String.valueOf(amountMoney));
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                    logArrayList.add(0, timeStamp + " Player added: " + input.getText().toString());
                    game.setLog(logArrayList);
                    setAdapter();
//                    if (!game.getGameName().equals("")) {
//                        updateUser();
//                    }
                    Log.d(TAG, logArrayList.get(logArrayList.size() - 1));

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
                if (!output.getText().toString().equals("")) {
                    playSound(R.raw.cha_ching);
                    amountMoney -= Integer.parseInt(output.getText().toString());
                    game.setAmount(amountMoney);
                    amount.setText(String.valueOf(amountMoney));
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                    logArrayList.add(0, timeStamp + " Player subtracted: " + output.getText().toString());
                    game.setLog(logArrayList);
                    setAdapter();
//                    if (!game.getGameName().equals("")) {
//                        updateUser();
//                    }
                    Log.d(TAG, logArrayList.get(logArrayList.size() - 1));

                    LogArrayModel logArrayModel = new LogArrayModel();
                    logArrayModel.setArrayList(logArrayList);

                    output.setText("");
                    output.setEnabled(false);
                    output.setEnabled(true);

                }
            }
        });
    }

    public void playSound(int _id)
    {
        if(fxPlayer != null)
        {
            fxPlayer.stop();
            fxPlayer.release();
        }
        fxPlayer = MediaPlayer.create(this, _id);
        if(fxPlayer != null)
            fxPlayer.start();
    }

    public void updateUser() {
        List<Games> gamesList = user.getGameList();
        gamesList.remove(gameIndexInList);
        gamesList.add(gameIndexInList, game);

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

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: called");

        if (!game.getGameName().equals("")) {
            updateUser();
        }
    }

    private void setAdapter() {
        if (frameLayout.getVisibility() == View.VISIBLE) {
            if (fragment != null) {
                fragment.setAdapter(logArrayList);
            }
        }
        game.setLog(logArrayList);
    }

    private void setFields() {

        sharedPreferences = getApplicationContext().getSharedPreferences("UserData", MODE_PRIVATE);
        if (sharedPreferences.getString("userName", null) != null) {
            userName = sharedPreferences.getString("userName", null);
            password = sharedPreferences.getString("password", null);
        }
        if (sharedPreferences.getString("GamesName", null) != null) {
            gameName = sharedPreferences.getString("GamesName", null);
        } else {
            gameName = intent.getStringExtra("GameName");
        }

        logArrayList = new ArrayList<>();
        game = new Gson().fromJson(gameJson, Games.class);
        if (game == null) {
            game = new Games("",0);
        }
        logArrayList = game.getLog();
        input = findViewById(R.id.input_amount);
        output = findViewById(R.id.output_amount);
        amount = findViewById(R.id.money_amount);
        inputEnter = findViewById(R.id.input_enter);
        outputEnter = findViewById(R.id.output_enter);
        frameLayout = findViewById(R.id.fragment_container);
        bundle = new Bundle();
        bundle.putString(Constants.LOGGING_FRAG_KEY, gameName);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "Users").build();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
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

    private void setFragment() {
        fragment = new LoggingFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        transaction.replace(R.id.fragment, fragment);
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
                if (frameLayout.getVisibility() == View.GONE) {
                    frameLayout.setVisibility(View.VISIBLE);
                    if (fragment != null) {
                        fragment.setAdapter(logArrayList);
                    }
                } else {
                    frameLayout.setVisibility(View.GONE);
                }
                return true;
            case R.id.about_log:
                // get a reference to the already created main layout
                LinearLayout mainLayout = (LinearLayout)
                        findViewById(R.id.money_layout);

                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_window_money_activity, null);

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
