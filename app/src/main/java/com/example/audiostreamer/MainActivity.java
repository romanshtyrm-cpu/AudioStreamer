package com.example.audiostreamer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private AudioClient audioClient;
    private ActivityResultLauncher<String> reqPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText ipEdit = findViewById(R.id.etIp);
        Button btnStart = findViewById(R.id.btnStart);
        Button btnStop = findViewById(R.id.btnStop);

        reqPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {});

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            reqPermission.launch(Manifest.permission.RECORD_AUDIO);
        }

        btnStart.setOnClickListener(v -> {
            String ip = ipEdit.getText().toString().trim();
            if (ip.isEmpty()) {
                Toast.makeText(this, "Enter server IP", Toast.LENGTH_SHORT).show();
                return;
            }
            if (audioClient == null) {
                audioClient = new AudioClient(ip);
                audioClient.start();
                Toast.makeText(this, "Client started", Toast.LENGTH_SHORT).show();
            }
        });

        btnStop.setOnClickListener(v -> {
            if (audioClient != null) {
                audioClient.stopClient();
                audioClient = null;
                Toast.makeText(this, "Client stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
