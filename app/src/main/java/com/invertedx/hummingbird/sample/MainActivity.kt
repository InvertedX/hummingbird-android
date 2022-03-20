package com.invertedx.hummingbird.sample

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      setSupportActionBar( findViewById(R.id.mainActivityToolBar))
        findViewById<Button>(R.id.showQR).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    QRActivity::class.java
                ).apply {
                    putExtra("TYPE",0)
                }
            )
        }
        findViewById<Button>(R.id.showPSBTQR).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    QRActivity::class.java
                ).apply {
                    putExtra("TYPE",1)
                }
            )
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            12
        )
//
        findViewById<Button>(R.id.scanQRBtn).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ScanActivity::class.java
                )
            )
        }
    }
}