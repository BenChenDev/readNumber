package com.example.benjamin.readnumber;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.benjamin.readnumber.utils.CommonUtils;
import com.example.benjamin.readnumber.views.TouchImageView;


import static com.example.benjamin.readnumber.utils.CommonUtils.info;

public class MainActivity extends AppCompatActivity {

    Button takePic;
    EditText recognizeResult;
    static ProcessImage processImg = new ProcessImage();
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String lastFileName = "";
    private boolean isRecognized = false;
    private TouchImageView image;

    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePic = findViewById(R.id.takePicButton);
        recognizeResult = findViewById(R.id.recognize_result);
        image = findViewById(R.id.grid_img);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    private void takePicture(){
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        lastFileName = CommonUtils.APP_PATH + "capture" + System.currentTimeMillis() + ".jpg";
        camera.putExtra("output", lastFileName);
        info(lastFileName);
        if (camera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(camera, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap imageBitmap = BitmapFactory.decodeFile(lastFileName, options);

            if (imageBitmap == null) {
                // Try again
                isRecognized = false;
                image.setImageBitmap(imageBitmap);
                hideProcessBar();
                dialogBox("Can not recognize sheet. Please try again", "Retry", "Exist", true);
                return;
            }
            final Bitmap finalImageBitmap = imageBitmap.getWidth() > imageBitmap.getHeight()
                    ? rotateBitmap(imageBitmap, 90) : imageBitmap;

            int top = data.getIntExtra("top", 0);
            int bot = data.getIntExtra("bot", 0);
            int right = data.getIntExtra("right", 0);
            int left = data.getIntExtra("left", 0);

            image.setImageBitmap(finalImageBitmap);
            displayResult(finalImageBitmap, top, bot, right, left);

        }
    }

    public void hideProcessBar() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (progressBar != null && progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }
        });
    }

    public void displayResult(Bitmap imageBitmap, int top, int bot, int right, int left) {
        info("Origin size: " + imageBitmap.getWidth() + ":" + imageBitmap.getHeight());
        // Parser
        recognizeResult.setText("");
        if (processImg.parseBitmap(imageBitmap, top, bot, right, left)) {
            // TODO: set result
            recognizeResult.setText(processImg.recognizeResult);
            // TODO: write result to image
            // image.setImageBitmap(toBitmap(processImg.drawAnswered(numberAnswer)));
            isRecognized = true;
            hideProcessBar();
        } else {
            // Try again
            isRecognized = false;
            image.setImageBitmap(imageBitmap);
            hideProcessBar();
            dialogBox("Can not recognize sheet. Please try again", "Retry", "Exist", true);
        }
    }

    public Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void dialogBox(String message, String bt1, String bt2, final boolean flagContinue) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(bt1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (flagContinue) {
                    takePicture();
                }
            }
        });

        if (bt2 != "") {
            alertDialogBuilder.setNegativeButton(bt2, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    existApp();
                    // return false;
                }
            });
        }

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void existApp() {
        CommonUtils.cleanFolder();
        this.finish();
    }

}
