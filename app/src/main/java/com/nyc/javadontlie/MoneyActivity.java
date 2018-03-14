package com.nyc.javadontlie;

import android.arch.persistence.room.Room;
import android.media.MediaPlayer;
import android.os.AsyncTask;
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

import com.nyc.javadontlie.database.AppDatabase;
import com.nyc.javadontlie.moneyModel.Games;
import com.nyc.javadontlie.moneyModel.LogArrayModel;
import com.nyc.javadontlie.moneyModel.User;
import com.nyc.javadontlie.roomDao.UserSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MoneyActivity extends AppCompatActivity {
    private final String TAG = "MoneyActivity";

    private EditText input, output;
    private TextView amount;
    private Button inputEnter, outputEnter;
    private String userName, password;
    private int amountMoney;
    private ArrayList<String> logArrayList;
    private FrameLayout frameLayout;
    LoggingFragment fragment;
    private Games game;
    private User user;
    private AppDatabase db;
    private int gameIndexInList;
    private MediaPlayer fxPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);

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
                    setLogFragAdapter();
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
                    setLogFragAdapter();
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
        new UpdateUserListAsync().execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: called");

        if (!game.getGameName().equals("")) {
            updateUser();
        }
    }

    private void setLogFragAdapter() {
        if (frameLayout.getVisibility() == View.VISIBLE) {
            if (fragment != null) {
                fragment.setAdapter(logArrayList);
            }
        }
        game.setLog(logArrayList);
    }

    private void setFields() {
        user = UserSingleton.getInstance().getUser();
        gameIndexInList = UserSingleton.getInstance().getIndexInList();
        userName = user.getUserName();
        password = user.getPassword();
        game = UserSingleton.getInstance().getGame();
        logArrayList = game.getLog();
        input = findViewById(R.id.input_amount);
        output = findViewById(R.id.output_amount);
        amount = findViewById(R.id.money_amount);
        inputEnter = findViewById(R.id.input_enter);
        outputEnter = findViewById(R.id.output_enter);
        frameLayout = findViewById(R.id.fragment_container);

    }

    private void setFragment() {
        fragment = new LoggingFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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

    private class UpdateUserListAsync extends AsyncTask<Void,Void,Void> {

        private List<Games> gamesList;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            gamesList = user.getGameList();
            gamesList.remove(gameIndexInList);
            gamesList.add(gameIndexInList, game);
            user.setGameList(gamesList);
            db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "Users").build();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            db.userDao().updateUsers(user);
            user = db.userDao().findByLogin(userName, password);
            UserSingleton.getInstance().setUser(user);
            Log.d(TAG, "doInBackground: called");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            db.close();

        }
    }
}
