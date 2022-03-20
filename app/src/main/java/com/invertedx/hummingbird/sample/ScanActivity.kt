package com.invertedx.hummingbird.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.invertedx.hummingbird.QRScanner


class ScanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)


        val scanner = findViewById<QRScanner>(R.id.qrScanner);

        scanner.startScanner()

        scanner.setUrTransmissionListener { totalFrames, processedFrames, progress ->
                //noop
         }
        scanner.setURDecodeListener { bytes, _ ->
            MaterialAlertDialogBuilder(this)
                    .setTitle("UR Result")
                    .setMessage(bytesToHex(bytes))
                    .setPositiveButton("Ok") { dialog, which ->
                        dialog.dismiss()
                    }.show();
            scanner.stopScanner()
        }
        scanner.setQRDecodeListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Result")
                .setMessage(it)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show();
            scanner.stopScanner();
        }

    }



}