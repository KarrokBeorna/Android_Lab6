package com.example.android_lab6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.continuewatch.*

private const val SECONDS_EL = "seconds_el"
//const val SLEEP = "sleep"

class Task1_Threads : AppCompatActivity() {
    private var secondsElapsed = 0
    //var sleep = 1000
    private var backgroundThread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            secondsElapsed = savedInstanceState.getInt(SECONDS_EL)
            //sleep = savedInstanceState.getInt(SLEEP)
        }
        setContentView(R.layout.continuewatch)
    }

    override fun onPause() {
        backgroundThread?.interrupt()
        super.onPause()
    }

    override fun onResume() {
        backgroundThread = Thread {
            try {
                while (backgroundThread?.isInterrupted == false) {
                    /**while(sleep != 0) {
                        Thread.sleep(1)
                        sleep--
                    }
                    sleep = 1000 */
                    Thread.sleep(1000)
                    textSecondsElapsed.post {
                        textSecondsElapsed.text = "Seconds elapsed: " + secondsElapsed++
                    }
                    Log.i("Thread", "Time = $secondsElapsed")
                }
            } catch (e: InterruptedException) {
                /** Возможно, как-то здесь стоит брать время прошлого post()
                 *  после этого смотреть на время сообщения "Thread paused",
                 *  вычитать эту разницу из 1000 и сохранять полученное значение
                 *  в глобальную переменную SLEEP
                 */
                Log.i("Thread", "Thread paused")
            }
        }

        backgroundThread?.start()
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SECONDS_EL, secondsElapsed)
        //outState.putInt(SLEEP, sleep)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        secondsElapsed = savedInstanceState.getInt(SECONDS_EL)
        //sleep = savedInstanceState.getInt(SLEEP)
        super.onRestoreInstanceState(savedInstanceState)
    }
}