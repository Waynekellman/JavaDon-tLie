package com.nyc.javadontlie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;

import java.io.File;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class CameraActivity extends AppCompatActivity {

    private CameraView cameraView;
    private String gameName;
    private Fotoapparat fotoapparat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraView = findViewById(R.id.camera_view);

        takeFotoPic();
    }

    private void takeFotoPic() {
        Intent intent = getIntent();
        gameName = intent.getStringExtra("gameNames");
        fotoapparat = new Fotoapparat(CameraActivity.this,cameraView);
        PhotoResult gameBoard = fotoapparat.takePicture();
        gameBoard.toBitmap().whenAvailable(new Function1<BitmapPhoto, Unit>() {
            @Override
            public Unit invoke(BitmapPhoto bitmapPhoto) {
                String string = new Gson().toJson(bitmapPhoto);
                Intent intent1 = new Intent(CameraActivity.this,MoneyActivity.class);
                intent1.putExtra("photoTaken", string);
                startActivity(intent1);
                return null;

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        fotoapparat.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        fotoapparat.stop();
    }
}
