package com.nyc.javadontlie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nyc.javadontlie.controller.Adapter;
import com.nyc.javadontlie.moneyModel.MoneyModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button newGame;
    private EditText newGameName,moneyAmount;
    private List<MoneyModel> moneyModels;
    private Adapter adapter;
    private SharedPreferences gameModels;
    public final String SHARED_KEY = "Game Model";
    Map<String, String> pastGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        recyclerView = findViewById(R.id.recyclerView);
        gameModels = getApplicationContext().getSharedPreferences(SHARED_KEY,MODE_PRIVATE);
        moneyModels = new ArrayList<>();
        if(gameModels.getAll() != null){
            Map<String, String> pastGame = new HashMap<>();
            pastGame.putAll((Map<? extends String, ? extends String>) gameModels.getAll());
            for (String s:pastGame.keySet()) {
                moneyModels.add(new MoneyModel(s,Integer.parseInt(pastGame.get(s))));
            }
        }
        newGame = findViewById(R.id.new_game);
        newGameName = findViewById(R.id.new_game_name);
        moneyAmount = findViewById(R.id.money_amount_start);
        adapter = new Adapter(moneyModels,this);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);



                newGame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!newGameName.getText().toString().equals("")&& !moneyAmount.getText().toString().equals("")) {
                            Boolean notCopy = true;

                            pastGame = new HashMap<>();
                            pastGame.putAll((Map<? extends String, ? extends String>) gameModels.getAll());
                            for (String s : pastGame.keySet()) {
                                if (newGameName.getText().toString().equals(s)) {
                                    notCopy = false;
                                }
                            }
                            if (notCopy) {
                                moneyModels.add(new MoneyModel(newGameName.getText().toString(), Integer.parseInt(moneyAmount.getText().toString())));
                                SharedPreferences.Editor editor = gameModels.edit();
                                editor.putString(newGameName.getText().toString(), moneyAmount.getText().toString());
                                editor.commit();
                                adapter.notifyDataSetChanged();
                                Intent intent = new Intent(StartActivity.this, MoneyActivity.class);
                                intent.putExtra("name", newGameName.getText().toString());
                                newGameName.setText("");
                                moneyAmount.setText("");
                                startActivity(intent);
                            } else {
                                Toast.makeText(StartActivity.this,"No Copy Names Allower",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });



    }
}
