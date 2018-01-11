package com.nyc.javadontlie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MoneyActivity extends AppCompatActivity {
    private EditText input,output;
    private TextView amount;
    private Button inputEnter, outputEnter;
    private SharedPreferences gameInfo;
    private String gameName;
    private int amountMoney;

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
        gameInfo = getApplicationContext().getSharedPreferences("Game Model",MODE_PRIVATE);

        gameName = intent.getStringExtra("name");
        amountMoney = Integer.parseInt(gameInfo.getString(gameName,"0"));
        amount.setText(String.valueOf(amountMoney));
        inputEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getText() != null){
                    amountMoney += Integer.parseInt(input.getText().toString());
                    amount.setText(String.valueOf(amountMoney));
                    input.setText("");
                    SharedPreferences.Editor editor = gameInfo.edit();
                    editor.putString(gameName,String.valueOf(amountMoney));
                    editor.commit();
                }
            }
        });
        outputEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getText() != null){
                    amountMoney -= Integer.parseInt(output.getText().toString());
                    amount.setText(String.valueOf(amountMoney));
                    output.setText("");
                    SharedPreferences.Editor editor = gameInfo.edit();
                    editor.putString(gameName,String.valueOf(amountMoney));
                    editor.commit();
                }
            }
        });



    }
}
