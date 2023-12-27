package com.example.gamecolors;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Wyświetlanie wyniku, na przykład w TextView
        TextView scoreTextView = findViewById(R.id.scoreTextView); // Załóżmy, że masz TextView o ID scoreTextView
        int score = getIntent().getIntExtra("score", 0); // Użyj wartości innej niż zero jako domyślnej
        scoreTextView.setText("Wynik: " + score);

        Button startGameButton = findViewById(R.id.startGameButton);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button historyButton = findViewById(R.id.historyButton);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScoreHistory();
            }

            private void showScoreHistory() {
                SharedPreferences prefs = getSharedPreferences("game_scores", Context.MODE_PRIVATE);
                String scores = prefs.getString("scores", "");

                AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this); // Użyj MainMenuActivity.this
                builder.setTitle("Historia Wyników")
                        .setMessage(scores.replace(";", "\n"))
                        .setPositiveButton("OK", null)
                        .show();
            }
        });




    }
}
