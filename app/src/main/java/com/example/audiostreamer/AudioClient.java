package com.example.audiostreamer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import java.io.InputStream;
import java.net.Socket;

public class AudioClient extends Thread {

    private final String serverIp;
    private final int port;

    public AudioClient(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(serverIp, port);
            InputStream inputStream = socket.getInputStream();

            int bufferSize = AudioTrack.getMinBufferSize(
                    8000,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
            );

            AudioTrack audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    8000,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize,
                    AudioTrack.MODE_STREAM
            );

            audioTrack.play();

            byte[] buffer = new byte[bufferSize];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) > 0) {
                audioTrack.write(buffer, 0, bytesRead);
            }

            audioTrack.stop();
            audioTrack.release();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}