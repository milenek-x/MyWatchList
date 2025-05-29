package com.milenekx.mywatchlist

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext // Import withContext
import kotlinx.coroutines.Dispatchers // Import Dispatchers

class SplashActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var progressPercentage: TextView
    private lateinit var loadingText: TextView

    private val SPLASH_DURATION_MS = 3000L // 3 seconds for progress animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        progressBar = findViewById(R.id.progressBar)
        progressPercentage = findViewById(R.id.progress_percentage)
        loadingText = findViewById(R.id.loading_text)

        // Use a coroutine to animate the progress bar
        lifecycleScope.launch {
            for (progress in 0..100 step 1) { // Update progress in small steps
                withContext(Dispatchers.Main) {
                    progressBar.progress = progress
                    progressPercentage.text = "$progress%"
                }
                // Calculate delay for smooth animation over SPLASH_DURATION_MS
                delay(SPLASH_DURATION_MS / 100) // Delay for each 1% increment
            }

            // Ensure progress is 100% at the end
            withContext(Dispatchers.Main) {
                progressBar.progress = 100
                progressPercentage.text = "100%"
                loadingText.text = "Loading Complete!" // Optional: change text
            }

            // Add a small final delay before starting MainActivity
            delay(500) // Give a moment after 100% is reached

            // Start the MainActivity
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)

            // Finish the SplashActivity so the user cannot navigate back to it
            finish()
        }
    }
}