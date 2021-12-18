package com.invertedx.hummingbird

import android.R.attr
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.sparrowwallet.hummingbird.URDecoder
import com.sparrowwallet.hummingbird.UREncoder


import com.sparrowwallet.hummingbird.UR
import android.graphics.*
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.invertedx.hummingbird.encoder.Contents
import com.invertedx.hummingbird.encoder.encode.QRCodeEncoder
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit
import android.os.SystemClock


class URQRView : View {
    val MIN_FRAGMENT_LENGTH = 10
    val MAX_FRAGMENT_LENGTH = 100
    var currentFrame = 0;
    private var qrRect = Rect(0, 0, width, height)
    private var job: Job? = null;
    val data =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Vitae aliquet nec ullamcorper sit amet risus. Diam quis enim lobortis scelerisque fermentum. Quis hendrerit dolor magna eget est lorem. Diam maecenas ultricies mi eget mauris pharetra et ultrices neque. Neque egestas congue quisque egestas diam in. Blandit volutpat maecenas volutpat blandit aliquam etiam erat velit scelerisque. Tempor id eu nisl nunc mi ipsum. Sit amet nisl purus in. Mauris pharetra et ultrices neque. Sed arcu non odio euismod lacinia.\n" +
                "\n" +
                "Sed turpis tincidunt id aliquet risus feugiat in. Ante in nibh mauris cursus mattis molestie a iaculis at. Nulla facilisi etiam dignissim diam quis enim lobortis scelerisque. Non enim praesent elementum facilisis leo vel fringilla. Ut enim blandit volutpat maecenas volutpat blandit. Mauris a diam maecenas sed enim ut sem. Egestas quis ipsum suspendisse ultrices. Curabitur vitae nunc sed velit dignissim sodales. Vel orci porta non pulvinar neque laoreet suspendisse interdum. In iaculis nunc sed augue lacus viverra vitae congue eu. Egestas integer eget aliquet nibh praesent tristique magna. Ultricies mi quis hendrerit dolor magna eget. Consequat mauris nunc congue nisi vitae suscipit tellus mauris. At tempor commodo ullamcorper a lacus vestibulum sed. Urna porttitor rhoncus dolor purus non enim. Eu consequat ac felis donec et odio pellentesque.\n" +
                "\n" +
                "Hendrerit dolor magna eget est lorem ipsum dolor sit. Aliquet sagittis id consectetur purus ut. Rhoncus mattis rhoncus urna neque viverra justo nec ultrices. Sagittis nisl rhoncus mattis rhoncus urna. Sit amet commodo nulla facilisi nullam vehicula ipsum a arcu. Arcu odio ut sem nulla pharetra diam sit amet. Velit dignissim sodales ut eu. Aliquet nec ullamcorper sit amet. Ut faucibus pulvinar elementum integer enim neque volutpat. Mi sit amet mauris commodo quis imperdiet massa tincidunt nunc. Dui faucibus in ornare quam viverra orci sagittis eu volutpat. Mattis ullamcorper velit sed ullamcorper morbi tincidunt ornare massa eget. A arcu cursus vitae congue mauris rhoncus aenean vel. Placerat duis ultricies lacus sed turpis tincidunt id aliquet risus. Ultrices neque ornare aenean euismod elementum nisi quis eleifend. Condimentum mattis pellentesque id nibh tortor id aliquet. Mauris a diam maecenas sed enim. Ut tristique et egestas quis. Maecenas ultricies mi eget mauris pharetra et ultrices. Fringilla urna porttitor rhoncus dolor purus non enim praesent elementum.\n" +
                "\n" +
                "Quam pellentesque nec nam aliquam sem et. Tristique senectus et netus et. Est pellentesque elit ullamcorper dignissim cras. Pretium viverra suspendisse potenti nullam ac tortor vitae purus faucibus. Tristique risus nec feugiat in fermentum. Amet aliquam id diam maecenas ultricies mi eget. Id diam vel quam elementum pulvinar etiam non. Risus quis varius quam quisque id diam vel quam. Magna etiam tempor orci eu lobortis. Rhoncus mattis rhoncus urna neque viverra."
    private val bitmapList = arrayListOf<Bitmap>()
    private var _fps = 10
    private var scope = CoroutineScope(Dispatchers.Unconfined) + SupervisorJob()


    var fps: Int?
        get() = _fps
        set(value) {
            if (value != null) {
                _fps = value
                restart()
            }
        }


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


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.URQRView, defStyle, 0
        )
        a.recycle()
        CoroutineScope(Dispatchers.Unconfined).launch {
            val ur = UR.fromBytes(data.toByteArray())
            val encoder = UREncoder(ur, MAX_FRAGMENT_LENGTH, MIN_FRAGMENT_LENGTH, 0)
            while (!encoder.isComplete) {
                val qrCodeEncoder = QRCodeEncoder(
                    encoder.nextPart(),
                    null,
                    Contents.Type.TEXT,
                    BarcodeFormat.QR_CODE.toString(),
                    width
                );
                bitmapList.add(qrCodeEncoder.encodeAsBitmap())
            }
            withContext(Dispatchers.Main) {
                startLoop()
            }
        }

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements()
    }

    private fun invalidateTextPaintAndMeasurements() {

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        qrRect = Rect(0, 0, width, height)
    }

    fun startLoop() {
        job = scope.launch {
            while (scope.isActive) {
                delay((1000 / _fps).toLong())
                if (bitmapList.size == currentFrame + 1) {
                    currentFrame = 0;
                } else {
                    currentFrame += 1;

                }
                withContext(Dispatchers.Main) {
                    invalidate()
                }
            }
        }
    }

    fun restart() {
        job?.cancel()
        currentFrame = 0
        startLoop()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bitmapList.isNotEmpty()) {
            canvas.drawBitmap(bitmapList[currentFrame], null, qrRect, null)
        }
    }

    companion object {
        private const val TAG = "URQRView"
    }
}