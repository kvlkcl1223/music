package com.example.music;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class MeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false); // 加载你写的那个包含 CardView 的布局

        // 获取 CardView 控件
        CardView cardMidiUpload = view.findViewById(R.id.card_midi_upload);
        CardView cardMyDevice = view.findViewById(R.id.card_my_device);
        CardView cardMyUpload = view.findViewById(R.id.card_my_upload);

        // 设置监听
        cardMidiUpload.setOnClickListener(v -> {
            Log.d("MeFragment", "点击了 MIDI上传");
            Intent intent = new Intent(getActivity(), MidiUploadActivity.class);
            startActivity(intent);
        });

        cardMyDevice.setOnClickListener(v -> {
            Log.d("MeFragment", "点击了 我的设备");

        });

        cardMyUpload.setOnClickListener(v -> {
            Log.d("MeFragment", "点击了 我上传的");
            Intent intent = new Intent(getActivity(), MyUploadsActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
