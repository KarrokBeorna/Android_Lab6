package com.example.android_lab6

import android.os.Bundle
import android.view.View.INVISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.task2_download.*

class Task2_Picasso : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task2_download)

        button.setOnClickListener {
            Picasso.get().load("https://vsezhivoe.ru/wp-content/uploads/2017/09/%D0%A4%D0%BE%D1%82%D0%BE1-%D0%9B%D0%B8%D1%81%D0%B8%D1%86%D0%B0-%D0%BE%D0%B1%D1%8B%D0%BA%D0%BD%D0%BE%D0%B2%D0%B5%D0%BD%D0%BD%D0%B0%D1%8F.jpg")
                .into(image)
            button.visibility = INVISIBLE
        }
    }
}