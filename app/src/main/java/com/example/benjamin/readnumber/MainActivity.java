package com.example.benjamin.readnumber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.benjamin.readnumber.utils.CommonUtils;

import static com.example.benjamin.readnumber.utils.CommonUtils.info;

public class MainActivity extends AppCompatActivity {

    Button takePic;

    private String lastFileName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePic = findViewById(R.id.takePicButton);

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    private void takePicture(){
        Intent camera = new Intent(MainActivity.this, AndroidCamera.class);
        lastFileName = CommonUtils.APP_PATH + "capture" + System.currentTimeMillis() + ".jpg";
        camera.putExtra("output", lastFileName);
        info(lastFileName);
        startActivityForResult(camera, 1);
    }
}
