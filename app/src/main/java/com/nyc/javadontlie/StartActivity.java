package com.nyc.javadontlie;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.nyc.javadontlie.controller.Adapter;
import com.nyc.javadontlie.database.AppDatabase;
import com.nyc.javadontlie.moneyModel.Games;
import com.nyc.javadontlie.moneyModel.User;
import com.nyc.javadontlie.roomDao.UserSingleton;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "StartActivity";
    private RecyclerView recyclerView;
    private Button newGame;
    private EditText newGameName, moneyAmount;
    private List<Games> gamesList;
    private Adapter adapter;
    private User user;
    private AppDatabase db;
    private UserSingleton userSingleton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        recyclerView = findViewById(R.id.recyclerView);

        newGame = findViewById(R.id.new_game);
        newGameName = findViewById(R.id.new_game_name);
        moneyAmount = findViewById(R.id.money_amount_start);
        userSingleton = UserSingleton.getInstance();


        setOnclick();

    }

    private void setOnclick() {
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = UserSingleton.getInstance().getUser();
                if (!newGameName.getText().toString().equals("") && !moneyAmount.getText().toString().equals("")) {
                    Boolean notCopy = true;
                    gamesList = user.getGameList() != null ? user.getGameList() : new ArrayList<>();
                    for (Games g : gamesList) {
                        if (g.getGameName().equals(newGameName.getText().toString())) {
                            notCopy = false;
                            break;
                        }
                    }

                    if (notCopy) {
                        Games games = new Games(newGameName.getText().toString(), Integer.parseInt(moneyAmount.getText().toString()));
                        user.addGameToList(games);
                        updateUser();
                        adapter.setGamesList(user.getGameList());
                        Intent intent = new Intent(StartActivity.this, MoneyActivity.class);
                        UserSingleton.getInstance().setUser(user);
                        UserSingleton.getInstance().setIndexInList(user.getGameList().size() - 1);
                        UserSingleton.getInstance().setGame(games);
                        newGameName.setText("");
                        moneyAmount.setText("");
                        startActivity(intent);
                    } else {
                        Toast.makeText(StartActivity.this, "No Copy Names Allowed", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }


    private void setUserFromSingleton() {
        user = userSingleton.getUser();
        if (user.getGameList() != null) {
            gamesList = user.getGameList();
        } else {
            gamesList = new ArrayList<>();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ran");
        setUserFromSingleton();

        recView();
    }

    public void updateUser() {
        new UpdateAsync().execute();
    }

    private void recView() {
        adapter = new Adapter(gamesList, this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
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

    private class UpdateAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            db.close();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "Users").build();
            db.userDao().updateUsers(user);
            return null;
        }
    }

}
