package com.invertedx.hummingbird.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.invertedx.hummingbird.QRScanner

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.viewQR).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    QRActivity::class.java
                )
            )
        }
    }
}