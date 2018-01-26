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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.view.CameraView;

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
    private Intent intent;
    private CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
        setFields();
        setFragment();
        setArrayListFromSharedPreference();
        setAmount();
        implementOnClicks();
        


    }

    private void implementOnClicks() {
        inputEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!input.getText().toString().equals("")){
                    amountMoney += Integer.parseInt(input.getText().toString());
                    amount.setText(String.valueOf(amountMoney));
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                    logArrayList.add(0,timeStamp + " Player added: " + input.getText().toString());
                    setAdapter();
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
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                    logArrayList.add(0,timeStamp + " Player subtracted: " + output.getText().toString());
                    setAdapter();
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

    private void setAdapter() {
        if (frameLayout.getVisibility() == View.VISIBLE){
            if (fragment != null) {
                fragment.setAdapter(logArrayList);
            }
        }
    }

    private void setArrayListFromSharedPreference() {
        if (gameInfo.getString("logArrayList" + gameName,null) != null){
            String logArrayString = gameInfo.getString("logArrayList" + gameName, gameName);
            LogArrayModel logArrayModel = new Gson().fromJson(logArrayString,LogArrayModel.class);
            logArrayList = logArrayModel.getArrayList();
        }
    }

    private void setFields() {
        intent = getIntent();
        gameName = intent.getStringExtra("name");
        input = findViewById(R.id.input_amount);
        output = findViewById(R.id.output_amount);
        amount = findViewById(R.id.money_amount);
        inputEnter = findViewById(R.id.input_enter);
        outputEnter = findViewById(R.id.output_enter);
        frameLayout = findViewById(R.id.fragment_container);
        cameraView = findViewById(R.id.camera_view);
        bundle = new Bundle();
        bundle.putString(Constants.LOGGING_FRAG_KEY, gameName);
        gameInfo = getApplicationContext().getSharedPreferences("Game Money Activity" + gameName,MODE_PRIVATE);
        logArrayList = new ArrayList<>();
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
        amountMoney = Integer.parseInt(gameInfo.getString(gameName,"0"));
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
                Intent intent = new Intent(MoneyActivity.this, CameraActivity.class);
                intent.putExtra("gameName", gameName);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
