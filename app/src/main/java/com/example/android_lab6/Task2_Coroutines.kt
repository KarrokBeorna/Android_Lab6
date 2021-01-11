package com.example.android_lab6

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.task2_download.*
import kotlinx.coroutines.*
import java.io.InputStream
import java.net.URL

@Suppress("BlockingMethodInNonBlockingContext")
class Task2_Coroutines : AppCompatActivity() {

    private val imageURL = "https://vsezhivoe.ru/wp-content/uploads/2017/09/%D0%A4%D0%BE%D1%82%D0%BE1-%D0%9B%D0%B8%D1%81%D0%B8%D1%86%D0%B0-%D0%BE%D0%B1%D1%8B%D0%BA%D0%BD%D0%BE%D0%B2%D0%B5%D0%BD%D0%BD%D0%B0%D1%8F.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task2_download)

        button.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                var mIcon11: Bitmap? = null
                withContext(Dispatchers.IO) {
                    try {
                        val stream: InputStream = URL(imageURL).openStream()
                        mIcon11 = BitmapFactory.decodeStream(stream)
                    } catch (e: Exception) { }
                }
                withContext(Dispatchers.Main) {
                    image.setImageBitmap(mIcon11)
                    button.visibility = View.INVISIBLE
                }
            }
        }
    }
}