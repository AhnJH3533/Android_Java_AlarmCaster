package com.dsuapp.alarmtest001;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Demo extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        mediaPlayer = MediaPlayer.create(Demo.this, R.raw.sunny);
        mediaPlayer.start();
        findViewById(R.id.btnClose).setOnClickListener(mClickListener);
    }
    private void close() {
        if (this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }

        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnClose:
                    // 알람 종료
                    close();
                    break;
            }
        }
    };
}