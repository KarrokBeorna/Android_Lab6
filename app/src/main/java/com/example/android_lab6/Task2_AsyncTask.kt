package com.example.android_lab6

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.View.INVISIBLE
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.task2_download.*
import java.io.InputStream
import java.net.URL


class Task2_AsyncTask : AppCompatActivity() {

    private val imageURL = "https://vsezhivoe.ru/wp-content/uploads/2017/09/%D0%A4%D0%BE%D1%82%D0%BE1-%D0%9B%D0%B8%D1%81%D0%B8%D1%86%D0%B0-%D0%BE%D0%B1%D1%8B%D0%BA%D0%BD%D0%BE%D0%B2%D0%B5%D0%BD%D0%BD%D0%B0%D1%8F.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task2_download)

        button.setOnClickListener {
            DownloadImageTask().execute(imageURL)
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class DownloadImageTask : AsyncTask<String?, Void?, Bitmap?>() {
        override fun doInBackground(vararg urls: String?): Bitmap? {
            val urldisplay = urls[0]
            var mIcon11: Bitmap? = null
            try {
                val stream: InputStream = URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(stream)
            } catch (e: Exception) { }
            return mIcon11
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            image.setImageBitmap(result)
            button.visibility = INVISIBLE
        }
    }

}