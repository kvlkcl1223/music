package com.example.music;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.music.AndroidMidiPlayer;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import jp.kshoji.javax.sound.midi.MidiEvent;

public class MidiPlayerActivity extends AppCompatActivity {
    private AndroidMidiPlayer midiPlayer;
    private Button btnPlay;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_midi_player);

        btnPlay = findViewById(R.id.btn_play_a);
        mediaPlayer = MediaPlayer.create(this, R.raw.demo);  // 不需要加 .mid 后缀
        mediaPlayer.start();
        try {
//            midiPlayer = new AndroidMidiPlayer();
//            midiPlayer.init(getAssets(), "demo.mid");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "MIDI初始化失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        btnPlay.setOnClickListener(v -> {
            if (midiPlayer != null) {
//                if (midiPlayer.isPlaying()) {
//                    midiPlayer.pause();
//                    btnPlay.setText("继续");
//                } else {
//                    midiPlayer.play();
//                    btnPlay.setText("暂停");
//                }
                try {
                    if (midiPlayer != null) {
                        midiPlayer.play();
                        btnPlay.setText("暂停");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "播放失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (midiPlayer != null) {
            midiPlayer.release();
        }
    }
}