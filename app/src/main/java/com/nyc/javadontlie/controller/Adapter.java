package com.nyc.javadontlie.controller;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nyc.javadontlie.MoneyActivity;
import com.nyc.javadontlie.R;
import com.nyc.javadontlie.database.AppDatabase;
import com.nyc.javadontlie.moneyModel.Games;
import com.nyc.javadontlie.moneyModel.User;
import com.nyc.javadontlie.roomDao.UserSingleton;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Wayne Kellman on 1/11/18.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private static final String TAG = "StarActivityAdapter";
    List<Games> gamesList;
    Activity thisActivity;
    private AppDatabase db;
    private User user;


    public Adapter(List<Games> gamesList, Activity thisActivity) {
        this.gamesList = gamesList;
        this.thisActivity = thisActivity;
    }

    public void setGamesList(List<Games> gamesList) {
        this.gamesList = gamesList;
        notifyDataSetChanged();
    }
    private void initiateUser() {
        user = UserSingleton.getInstance().getUser();

        if (user.getGameList() != null) {
            gamesList = user.getGameList();
        } else {
            gamesList = new ArrayList<>();
            user.setGameList(gamesList);
            UserSingleton.getInstance().setUser(user);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview, parent, false);
        initiateUser();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.textView.setText(gamesList.get(position).getGameName());
        holder.deleteButton.setEnabled(true);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.view.getContext(), MoneyActivity.class);
                UserSingleton.getInstance().setGame(gamesList.get(position));
                UserSingleton.getInstance().setIndexInList(position);
                holder.view.getContext().startActivity(intent);
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: deleted " + gamesList.get(position).getGameName());
                holder.deleteButton.setEnabled(false);
                deleteGames(gamesList.get(position));
            }
        });

    }

    private void deleteGames(final Games games) {
        new DeleteAsync().execute(games);
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

    public class DeleteAsync extends AsyncTask<Games,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Games... games) {
            db = Room.databaseBuilder(thisActivity.getApplicationContext(),
                    AppDatabase.class, "Users").build();

            Log.d(TAG, "run: " + gamesList.size());
            for (Games g : gamesList) {
                if (g.getGameName().equals(games[0].getGameName())) {
                    gamesList.remove(g);
                    Log.d(TAG, "run: " + gamesList.size());
                    Log.d(TAG, "run: " + games[0].getGameName());
                    user.setGameList(gamesList);
                    db.userDao().updateUsers(user);
                    UserSingleton.getInstance().setUser(user);
                    break;
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            db.close();
            notifyDataSetChanged();

        }
    }
}
