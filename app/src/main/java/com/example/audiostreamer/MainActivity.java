package com.example.audiostreamer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final int PORT = 50005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editIp = findViewById(R.id.editIp);
        Button btnStartServer = findViewById(R.id.btnStartServer);
        Button btnStartClient = findViewById(R.id.btnStartClient);

        // Start Server
        btnStartServer.setOnClickListener(v -> {
            AudioServer server = new AudioServer(PORT);
            server.start();
        });

        // Start Client
        btnStartClient.setOnClickListener(v -> {
            String ip = editIp.getText().toString().trim();
            if (ip.isEmpty()) {
                editIp.setError("Введите IP!");
                return;
            }
            AudioClient client = new AudioClient(ip, PORT);
            client.start();
        });
    }
}