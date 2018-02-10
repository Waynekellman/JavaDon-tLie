package com.nyc.javadontlie.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nyc.javadontlie.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Wayne Kellman on 1/13/18.
 */

public class LoggingAdapter extends RecyclerView.Adapter<LoggingAdapter.ViewHolder> {
    private ArrayList<String> logArray;

    public LoggingAdapter(ArrayList<String> logArrayList) {
        logArray = logArrayList;
        notifyDataSetChanged();
    }

    public void setLogArray(ArrayList<String> logArray) {
        this.logArray = logArray;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_logging, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.logText.setText(logArray.get(position));
    }

    @Override
    public int getItemCount() {
        return logArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView logText;

        public ViewHolder(View itemView) {
            super(itemView);
            logText = itemView.findViewById(R.id.log_text);
        }
    }
}
