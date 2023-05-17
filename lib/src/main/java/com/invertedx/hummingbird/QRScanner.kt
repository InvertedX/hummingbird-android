package com.invertedx.hummingbird

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.zxing.Result
import com.sparrowwallet.hummingbird.LegacyURDecoder
import com.sparrowwallet.hummingbird.ResultType
import com.sparrowwallet.hummingbird.URDecoder
import com.sparrowwallet.hummingbird.registry.RegistryType
import kotlinx.coroutines.*
import kotlin.jvm.internal.Intrinsics.Kotlin
import kotlin.math.roundToInt


enum class QrDetectType {
    AUTO,
    QR_ONLY,
    UR_ONLY
}

class QRScanner : FrameLayout {

    private var _decodeURCallback: (result: kotlin.Result<URDecoder.Result>) -> Unit = { _ -> }
    private var _decodeQrCallback: (result: String) -> Unit = {}
    private var _type = QrDetectType.AUTO
    private val _decoder = URDecoder()
    private var _linearProgressIndicator: LinearProgressIndicator? = null
    private var _progressMessage: TextView? = null
    private var _mCodeScanner: CodeScanner? = null
    private var _enableURProgress = true
    private var _scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var _urTransmissionListener: (totalFrames: Int, processedFrames: Int, progress: Double) -> Unit =
        { _, _, _ -> }


    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }


    fun setUrTransmissionListener(callback: (totalFrames: Int, processedFrames: Int, progress: Double) -> Unit) {
        this._urTransmissionListener = callback;
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val attributeSet = context.obtainStyledAttributes(
            attrs, R.styleable.QRScanner, defStyle, 0
        )
        inflate(context, R.layout.layout_scanner_view, this);
        _type = QrDetectType.values()[attributeSet.getInt(R.styleable.QRScanner_enableQrMode, 0)]
        val codeScanner = findViewById<CodeScannerView>(R.id.codeScanner)
        _linearProgressIndicator = findViewById(R.id.progressBar)
        _progressMessage = findViewById(R.id.progressMessage)
        _mCodeScanner = CodeScanner(context, codeScanner)
        _linearProgressIndicator?.visibility = View.GONE
        _progressMessage?.visibility = View.GONE
        _mCodeScanner?.scanMode = ScanMode.CONTINUOUS
        _mCodeScanner?.decodeCallback = DecodeCallback {
            setScanResult(it)
        }
        attributeSet.recycle()
    }


    fun startScanner() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            _mCodeScanner?.startPreview()
        } else {
            Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopScanner() {
        _mCodeScanner?.releaseResources()
        _mCodeScanner?.stopPreview()
    }

    private fun setScanType(type: QrDetectType) {
        this._type = type;
    }

    fun setURDecodeListener(callback: (result: kotlin.Result<URDecoder.Result>) -> Unit) {
        this._decodeURCallback = callback
    }

    fun showURProgress(enable: Boolean) {
        this._enableURProgress = enable
        this.invalidate()
    }

    fun setQRDecodeListener(callback: (result: String) -> Unit) {
        this._decodeQrCallback = callback;
    }

    @SuppressLint("SetTextI18n")
    private fun setScanResult(it: Result) {
        val string = it.text
        _scope.launch(Dispatchers.Main) {
            if (_type == QrDetectType.QR_ONLY) {
                _decodeQrCallback.invoke(string)
            }
            if (_type == QrDetectType.AUTO) {
                if (!string.lowercase().startsWith("ur:") && _decoder.expectedPartCount == 0) {
                    _decodeQrCallback.invoke(string)
                    return@launch
                }
            }
            return@launch
        }
        _scope.launch {
            try {
                _decoder.receivePart(string)
                withContext(Dispatchers.Main) {
                    if (_enableURProgress) {
                        _linearProgressIndicator?.visibility = View.VISIBLE
                        _progressMessage?.visibility = View.VISIBLE
                        _linearProgressIndicator?.max = 100;
                        _linearProgressIndicator?.setProgressCompat(
                            (_decoder.estimatedPercentComplete * 100).roundToInt(),
                            false
                        )
                        _progressMessage?.text =
                            "${(_decoder.estimatedPercentComplete * 100).roundToInt()}%"
                    } else {
                        _linearProgressIndicator?.visibility = View.GONE
                        _progressMessage?.visibility = View.GONE
                    }
                    if (_decoder.expectedPartCount != 0) {
                        _urTransmissionListener.invoke(
                            _decoder.expectedPartCount,
                            _decoder.processedPartsCount,
                            _decoder.estimatedPercentComplete
                        )
                    }
                    if (_decoder.result != null && _decoder.result.type == ResultType.SUCCESS) {
                        _linearProgressIndicator?.setProgressCompat(
                           100,
                            false
                        )
                        _progressMessage?.text =
                            "100%"
                        val result = kotlin.Result.success(_decoder.result)
                        _decodeURCallback.invoke(result)
                        return@withContext
                    }
                }
            } catch (e: Exception) {
                _linearProgressIndicator?.setProgressCompat(
                    0,
                    false
                )
                _progressMessage?.text =
                    "0%"
                withContext(Dispatchers.Main){
                    val result = kotlin.Result.failure<URDecoder.Result>(Throwable(e))
                    _decodeURCallback.invoke(result)
                }
            }
        }

    }


    companion object {
        private const val TAG = "QRScanner"
    }

}