package ru.netology.singlealbumapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.singlealbumapp.R

class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
    }
}