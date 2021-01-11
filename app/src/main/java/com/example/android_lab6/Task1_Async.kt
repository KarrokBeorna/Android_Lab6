package com.example.android_lab6

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.continuewatch.*

private const val SECONDS_EL = "seconds_el"

class Task1_Async : AppCompatActivity() {
    var secondsElapsed = 0
    private var backgroundTask: NoTimer? = null

    @SuppressLint("StaticFieldLeak")
    inner class NoTimer : AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {
            while (!isCancelled) {
                Thread.sleep(1000)
                publishProgress()
            }
        }

        override fun onProgressUpdate(vararg values: Unit?) {
            super.onProgressUpdate(*values)
            textSecondsElapsed.post {
                textSecondsElapsed.text = "Seconds elapsed: " + secondsElapsed++
            }
            Log.i("Task", "Time = $secondsElapsed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            secondsElapsed = savedInstanceState.getInt(SECONDS_EL)
        }
        setContentView(R.layout.continuewatch)
    }

    override fun onPause() {
        backgroundTask?.cancel(false)
        Log.i("Task", "Task paused")
        super.onPause()
    }

    override fun onResume() {
        backgroundTask = NoTimer()
        backgroundTask?.execute()
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