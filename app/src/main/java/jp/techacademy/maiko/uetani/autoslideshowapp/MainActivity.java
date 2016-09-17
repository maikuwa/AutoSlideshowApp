package jp.techacademy.maiko.uetani.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.os.Handler;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private List<Uri> uriList = new ArrayList<>();
    private int currentPosition = 0;
    private ImageView imageView;
    private Timer mainTimer = new Timer();
    private MainTimerTask mainTimerTask;
    private Handler mHandler = new Handler();
    float    mLaptime = 0.0f;
     Button button01;
     Button button02;
     Button button03;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        button01 = (Button) findViewById(R.id.button01);
        button02 = (Button) findViewById(R.id.button02);
        button03 = (Button) findViewById(R.id.button03);



        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }
        imageView.setImageURI(uriList.get(currentPosition));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                uriList.add(imageUri);
                Log.d("ANDROID", "URI : " + imageUri.toString());


            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("ANDROID", "URI : " + uriList);
    }

    public void onClick(View v) {
        // ボタン1が押された場合
        if (v.getId() == R.id.button01) {
            pageSegue(0);
        }
        if (v.getId() == R.id.button02) {
            pageSegue(1);
        }
        if (v.getId() == R.id.button03) {
            if(mainTimer == null){
                this.mainTimer = new Timer(true);
                this.mainTimerTask = new MainTimerTask();
                mainTimer.schedule(mainTimerTask, 2000, 2000);
                button01.setEnabled(false);
                button02.setEnabled(false);

            }
            else if(mainTimer != null){
                mLaptime = 0.0f;
                mainTimer.cancel();
                mainTimer = null;
                button01.setEnabled(true);
                button02.setEnabled(true);

            }
        }
    }


    public void pageSegue(int ope) {
        if (ope == 0) {
            if (currentPosition >= uriList.size() - 1) {
                currentPosition = 0;
            } else {
                currentPosition++;
            }
        } else if (ope == 1) {
            if (currentPosition <= 0) {
                currentPosition = uriList.size() - 1;
            } else {
                currentPosition--;
            }
        }
        Log.d("ANDROID", "URI : " + currentPosition + "yahoo" + uriList.size());
        Log.d("ANDROID", "URI : " + uriList.get(currentPosition));
        imageView.setImageURI(uriList.get(currentPosition));
    }

    public class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            //ここに定周期で実行したい処理を記述します
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    pageSegue(0);

                }
            });
        }
    }
}