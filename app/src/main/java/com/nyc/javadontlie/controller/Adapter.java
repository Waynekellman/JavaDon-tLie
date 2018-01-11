package com.nyc.javadontlie.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nyc.javadontlie.MoneyActivity;
import com.nyc.javadontlie.R;
import com.nyc.javadontlie.StartActivity;
import com.nyc.javadontlie.moneyModel.MoneyModel;

import java.util.List;

/**
 * Created by Wayne Kellman on 1/11/18.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

    List<MoneyModel> moneyModels;
    private SharedPreferences gameList;
    Activity thisActivity;

    public Adapter(List<MoneyModel> moneyModels, Activity thisActivity) {
        this.moneyModels = moneyModels;
        this.thisActivity = thisActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview,parent,false);
        gameList = view.getContext().getSharedPreferences("Game Model", Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.textView.setText(moneyModels.get(position).getName());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.view.getContext(), MoneyActivity.class);
                intent.putExtra("name",moneyModels.get(position).getName());
                intent.putExtra("amount",moneyModels.get(position).getAmount());
                holder.view.getContext().startActivity(intent);
            }
        });
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameList.edit().remove(moneyModels.get(position).getName()).apply();
                Intent intent = new Intent(holder.view.getContext(), StartActivity.class);
                holder.view.getContext().startActivity(intent);
                thisActivity.finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return moneyModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private View view;
        private Button button;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.game_name);
            view = itemView;
            button = itemView.findViewById(R.id.delete_game);
        }
    }
}
