package com.invertedx.hummingbird.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.invertedx.hummingbird.QRScanner


class ScanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)


        val scanner = findViewById<QRScanner>(R.id.qrScanner)

        scanner.startScanner()

        scanner.setUrTransmissionListener { _, processedFrames, progress ->
            //noop
        }

        scanner.setQRDecodeListener {
            scanner.stopScanner()
            MaterialAlertDialogBuilder(this@ScanActivity)
                .setTitle("QR Result")
                .setMessage(it)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
        scanner.setOnClickListener {
            scanner.startScanner()
        }
        scanner.setURDecodeListener { result ->
            scanner.stopScanner()
            result.fold(
                onSuccess = {
                    val urResult = result.getOrThrow()
                    //try to get the bytes from the UR, if it fails, get the CBOR bytes
                    val bytes = try {
                        urResult.ur.toBytes()
                    } catch (e: Exception) {
                        urResult.ur.cborBytes
                    }
                    MaterialAlertDialogBuilder(this@ScanActivity)
                        .setTitle("UR Result")
                        .setMessage(bytesToHex(bytes))
                        .setPositiveButton("Ok") { dialog, which ->
                            dialog.dismiss()
                        }.show()
                },
                onFailure = {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Error decoding UR")
                        .setMessage("Exception: ${it.message}")
                        .setPositiveButton("Ok") { dialog, _ ->
                            dialog.dismiss()
                            scanner.stopScanner()
                        }.show()
                }
            )

        }


    }


}