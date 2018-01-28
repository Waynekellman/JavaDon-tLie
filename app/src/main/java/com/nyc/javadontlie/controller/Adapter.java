package com.nyc.javadontlie.controller;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nyc.javadontlie.MoneyActivity;
import com.nyc.javadontlie.R;
import com.nyc.javadontlie.StartActivity;
import com.nyc.javadontlie.database.AppDatabase;
import com.nyc.javadontlie.moneyModel.Games;
import com.nyc.javadontlie.moneyModel.User;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Wayne Kellman on 1/11/18.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

    private static final String TAG = "StarActivityAdapter";
    List<Games> gamesList;
    Activity thisActivity;
    private SharedPreferences sharedPreferences;
    private AppDatabase db;
    private String userName,password;
    private User user;



    public Adapter(List<Games> gamesList, Activity thisActivity) {
        this.gamesList = gamesList;
        this.thisActivity = thisActivity;
        this.sharedPreferences = thisActivity.getSharedPreferences("UserData",MODE_PRIVATE);
    }

    public void setGamesList(List<Games> gamesList) {
        this.gamesList = gamesList;
        notifyDataSetChanged();
    }
    private void setUserAndPass() {
        sharedPreferences  = thisActivity.getApplicationContext().getSharedPreferences("UserData",MODE_PRIVATE);
        if (sharedPreferences.getString("userName",null) != null){
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

                db = Room.databaseBuilder(thisActivity.getApplicationContext(),
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview,parent,false);
        setUserAndPass();
        initiateUser();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.textView.setText(gamesList.get(position).getGameName());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.view.getContext(), MoneyActivity.class);
                intent.putExtra("GameName",gamesList.get(position).getGameName());
                sharedPreferences.edit().putString("GamesName", gamesList.get(position).getGameName()).commit();
                String gameJson = new Gson().toJson(gamesList.get(position));
                intent.putExtra("gameIndex", position);
                intent.putExtra("newGame", gameJson);
                holder.view.getContext().startActivity(intent);
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: deleted " + gamesList.get(position).getGameName());
                deleteGames(gamesList.get(position));
                notifyDataSetChanged();
            }
        });

    }
    private void deleteGames(final Games games) {

        db = Room.databaseBuilder(thisActivity.getApplicationContext(),
                AppDatabase.class, "Users").build();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: " + gamesList.size());
                for (Games g : gamesList) {
                    if (g.getGameName().equals(games.getGameName())) {
                        gamesList.remove(g);
                    }
                    Log.d(TAG, "run: " + gamesList.size());
                    Log.d(TAG, "run: " + games.getGameName());
                    user.setGameList(gamesList);
                    db.userDao().updateUsers(user);
                    db.close();
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



    @Override
    public int getItemCount() {
        return gamesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private View view;
        private Button deleteButton;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.game_name);
            view = itemView;
            deleteButton = itemView.findViewById(R.id.delete_game);
        }
    }
}
