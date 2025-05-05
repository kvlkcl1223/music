package com.example.music;

import jp.kshoji.javax.sound.midi.*;
import android.content.res.AssetManager;

import java.io.InputStream;

public class AndroidMidiPlayer {
    private Sequencer sequencer;
    private Sequence sequence;

    // 初始化播放器
    public void init(AssetManager assets, String midiFile) throws Exception {
        // 加载Assets中的MIDI文件
        InputStream is = assets.open(midiFile);
        sequence = MidiSystem.getSequence(is);

        // 创建MIDI音序器
        sequencer = MidiSystem.getSequencer(true);
//        sequencer.open();
        Synthesizer synth = MidiSystem.getSynthesizer();
        synth.open();
        sequencer.getTransmitter().setReceiver(synth.getReceiver()); // 绑定合成器

        // 加载SoundFont
        InputStream sfStream = assets.open("TimGM6mb.sf2");
        Soundbank soundbank = MidiSystem.getSoundbank(sfStream);
        ((Synthesizer)sequencer).loadAllInstruments(soundbank);
        sequencer.setSequence(sequence);

        // 设置循环监听
        sequencer.addMetaEventListener(meta -> {
            if (meta.getType() == 47) { // 播放结束标记
                onPlaybackComplete();
            }
        });
    }

    // 开始播放
    public void play() {
        if (sequencer != null && !sequencer.isRunning()) {
            sequencer.start();
        }
    }

    // 停止并释放资源
    public void release() {
        if (sequencer != null) {
            sequencer.stop();
            sequencer.close();
        }
    }

    // 播放进度控制
    public void seekTo(long microseconds) {
        if (sequencer != null) {
            sequencer.setMicrosecondPosition(microseconds);
        }
    }

    // 播放完成回调
    private void onPlaybackComplete() {
        // 处理播放完成逻辑
    }
}