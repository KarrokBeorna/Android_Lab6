package com.example.android_lab6

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.continuewatch.*
import kotlinx.coroutines.*

private const val SECONDS_EL = "seconds_el"

class Task1_Coroutines : AppCompatActivity() {
    var secondsElapsed = 0
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            secondsElapsed = savedInstanceState.getInt(SECONDS_EL)
        }
        setContentView(R.layout.continuewatch)
    }

    override fun onPause() {
        job?.cancel()
        Log.i("Coroutines", "Coroutines paused")
        super.onPause()
    }

    override fun onResume() {
        job = lifecycleScope.launchWhenResumed {
            while (isActive) {
                delay(1000)
                textSecondsElapsed.post {
                    textSecondsElapsed.text = "Seconds elapsed: " + secondsElapsed++
                }
                Log.i("Coroutines", "Time = $secondsElapsed")
            }
        }
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SECONDS_EL, secondsElapsed)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        secondsElapsed = savedInstanceState.getInt(SECONDS_EL)
        super.onRestoreInstanceState(savedInstanceState)
    }
}