package tr.com.huseyinaydin.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import tr.com.huseyinaydin.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int PROGRESS_DURATION_MS = 4000; // 4 saniye
    private ActivityMainBinding binding;
    private Handler progressHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ProgressBar'ı 0'dan 100'e animasyonla doldur
        try {
            animateProgressBar();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 4 saniye sonra MainActivity'e geç
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, EarthquakeActivity.class));
            finish();
        }, 5500);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.progressBar.setProgress(100, true);
        }
    }

    private void animateProgressBar() throws InterruptedException {
        final int maxProgress = 100;
        final int interval = 50; // Her 50ms'de bir güncelle
        final int totalSteps = PROGRESS_DURATION_MS / interval;
        final int progressIncrement = maxProgress / totalSteps;

        Thread thread = new Thread(() -> {
            int currentProgress = 0;
            while (currentProgress <= maxProgress) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final int finalProgress = currentProgress;
                runOnUiThread(() -> binding.progressBar.setProgress(finalProgress));
                currentProgress += progressIncrement;
            }
        });
        thread.start();
        //thread.join();
        /*
         ObjectAnimator.ofInt(binding.progressBar, "progress", 0, 100)
            .setDuration(PROGRESS_DURATION_MS)
            .start();
         */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressHandler.removeCallbacksAndMessages(null); // Memory leak önleme
    }
}