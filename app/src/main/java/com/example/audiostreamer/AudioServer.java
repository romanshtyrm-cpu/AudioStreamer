package com.example.audiostreamer;

import android.media.AudioAttributes;
import android.media.AudioFormat;
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
                int bufferSize = AudioTrack.getMinBufferSize(
                        sampleRate,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT
                );

                AudioTrack audioTrack = new AudioTrack.Builder()
                        .setAudioAttributes(
                                new AudioAttributes.Builder()
                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                        .build()
                        )
                        .setAudioFormat(
                                new AudioFormat.Builder()
                                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                        .setSampleRate(sampleRate)
                                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                        .build()
                        )
                        .setBufferSizeInBytes(bufferSize)
                        .setTransferMode(AudioTrack.MODE_STREAM)
                        .build();

                audioTrack.play();

                byte[] buffer = new byte[bufferSize];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1 && running) {
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