package com.example.audiostreamer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class AudioClient extends Thread {

    private String serverIP;
    private static final int PORT = 50005;
    private volatile boolean running = true;

    public AudioClient(String serverIP) {
        this.serverIP = serverIP;
    }

    @Override
    public void run() {
        try {
            InetAddress serverAddr = InetAddress.getByName(serverIP);
            Socket socket = new Socket(serverAddr, PORT);
            OutputStream outputStream = socket.getOutputStream();

            int sampleRate = 16000;

            int bufferSize = AudioRecord.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
            );

            AudioRecord audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize
            );

            audioRecord.startRecording();
            Log.d("AudioClient", "Recording started…");

            byte[] buffer = new byte[bufferSize];

            while (running) {
                int bytesRead = audioRecord.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            audioRecord.stop();
            audioRecord.release();
            socket.close();

        } catch (Exception e) {
            Log.e("AudioClient", "Error: " + e.getMessage());
        }
    }

    public void stopClient() {
        running = false;
    }
}
