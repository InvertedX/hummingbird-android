package  com.invertedx.hummingbird.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.invertedx.hummingbird.URQRView
import com.sparrowwallet.hummingbird.UR


class QRActivity : AppCompatActivity() {

    var type = 0;
    var psbtHex = "70736274ff01009a020000000258e87a21b56daf0c23be8e7070456c336f7cbaa5c8757924f545887bb2abdd750000000000ffffffff838d0427d0ec650a68aa46bb0b098aea4422c071b2ca78352a077959d07cea1d0100000000ffffffff0270aaf00800000000160014d85c2b71d0060b09c9886aeb815e50991dda124d00e1f5050000000016001400aea9a2e5f0f876a588df5546e8742d1d87008f000000000000000000"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qractivity)
        val urView = findViewById<URQRView>(R.id.urView)
        setSupportActionBar(findViewById(R.id.toolbarQR));
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "QR "
        if (intent.extras?.containsKey("TYPE") == true) {
            intent.extras?.getInt("TYPE")?.let {
                type = it
            }
        }
        urView.qrMargin = 1
        val urFrameStatus = findViewById<TextView>(R.id.urFrameStatus);
        if (type == 0) {
            val hex = "Hello world"
            urView.content = hex
            findViewById<TextView>(R.id.qrContentText).text = urView.content
        } else if (type == 1) {
            urView.maxFragmentLength = 100
            urView.setUrTransmissionListener { totalFrames, currentFrame ->
                urFrameStatus.visibility = View.VISIBLE
                urFrameStatus.text = "Current Frame: ${currentFrame} - TotalFrames ${totalFrames} \n\n FPS: ${urView.fps}"
            }
            urView.setContent(UR.fromBytes(hexToBytes(psbtHex)));
            findViewById<TextView>(R.id.qrContentText).text =psbtHex
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}