package com.nyc.javadontlie;

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
import com.nyc.javadontlie.moneyModel.LogArrayModel;

import java.util.ArrayList;

public class MoneyActivity extends AppCompatActivity{
    private EditText input,output;
    private TextView amount;
    private Button inputEnter, outputEnter;
    private SharedPreferences gameInfo;
    private String gameName;
    private int amountMoney;
    private ArrayList<String> logArrayList;
    private final String TAG = "MoneyActivity";
    private Bundle bundle;
    private FrameLayout frameLayout;
    LoggingFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
        Intent intent = getIntent();

        input = findViewById(R.id.input_amount);
        output = findViewById(R.id.output_amount);
        amount = findViewById(R.id.money_amount);
        inputEnter = findViewById(R.id.input_enter);
        outputEnter = findViewById(R.id.output_enter);
        frameLayout = findViewById(R.id.fragment_container);
        bundle = new Bundle();
        bundle.putString(Constants.LOGGING_FRAG_KEY, gameName);
        gameInfo = getApplicationContext().getSharedPreferences("Game Money Activity",MODE_PRIVATE);
        logArrayList = new ArrayList<>();
        fragment = (LoggingFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null){
            fragment = new LoggingFragment();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment,fragment);
        fragment.setArguments(bundle);

        transaction.commit();
        if (gameInfo.getString("logArrayList" + gameName,null) != null){
            String logArrayString = gameInfo.getString("logArrayList" + gameName, gameName);
            LogArrayModel logArrayModel = new Gson().fromJson(logArrayString,LogArrayModel.class);
            logArrayList = logArrayModel.getArrayList();
        }

        gameName = intent.getStringExtra("name");
        amountMoney = Integer.parseInt(gameInfo.getString(gameName,"0"));
        amount.setText(String.valueOf(amountMoney));
        inputEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!input.getText().toString().equals("")){
                    amountMoney += Integer.parseInt(input.getText().toString());
                    amount.setText(String.valueOf(amountMoney));
                    logArrayList.add("Player added: " + input.getText().toString());
                    Log.d(TAG,logArrayList.get(logArrayList.size() - 1));

                    LogArrayModel logArrayModel = new LogArrayModel();
                    logArrayModel.setArrayList(logArrayList);

                    String gameLog = new Gson().toJson(logArrayModel);
                    input.setText("");
                    input.setEnabled(false);
                    input.setEnabled(true);
                    SharedPreferences.Editor editor = gameInfo.edit();
                    editor.clear();
                    editor.putString(gameName,String.valueOf(amountMoney));
                    editor.putString("logArrayList" + gameName,gameLog);
                    editor.commit();
                }
            }
        });
        outputEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!output.getText().toString().equals("")){
                    amountMoney -= Integer.parseInt(output.getText().toString());
                    amount.setText(String.valueOf(amountMoney));
                    logArrayList.add("Player subtracted: " + output.getText().toString());
                    Log.d(TAG,logArrayList.get(logArrayList.size() - 1));

                    LogArrayModel logArrayModel = new LogArrayModel();
                    logArrayModel.setArrayList(logArrayList);

                    String gameLog = new Gson().toJson(logArrayModel);
                    output.setText("");
                    output.setEnabled(false);
                    output.setEnabled(true);
                    SharedPreferences.Editor editor = gameInfo.edit();
                    editor.clear();
                    editor.putString(gameName,String.valueOf(amountMoney));
                    editor.putString("logArrayList" + gameName,gameLog);
                    editor.commit();

                }
            }
        });



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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
