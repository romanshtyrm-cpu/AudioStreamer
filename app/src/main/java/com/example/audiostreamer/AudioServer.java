package com.example.audiostreamer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AudioServer extends Thread {

    private static final int PORT = 50005; // Порт для приема звука
    private boolean running = true;

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Log.d("AudioServer", "Server started on port " + PORT);

            while (running) {
                Socket client = serverSocket.accept();
                Log.d("AudioServer", "Client connected");

                InputStream inputStream = client.getInputStream();

                int sampleRate = 16000;
                int bufferSize = AudioTrack.getMinBufferSize(sampleRate,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                AudioTrack audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        sampleRate,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize,
                        AudioTrack.MODE_STREAM
                );

                audioTrack.play();

                byte[] buffer = new byte[bufferSize];

                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    audioTrack.write(buffer, 0, bytesRead);
                }

                audioTrack.stop();
                audioTrack.release();
                client.close();
            }

        } catch (Exception e) {
            Log.e("AudioServer", "Error: " + e.getMessage());
        }
    }

    public void stopServer() {
        running = false;
    }
}
