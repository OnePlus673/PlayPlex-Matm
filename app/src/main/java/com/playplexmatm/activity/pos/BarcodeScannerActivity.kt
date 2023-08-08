package com.playplexmatm.activity.pos

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.playplexmatm.R
import kotlinx.android.synthetic.main.activity_barcode_scanner.*

class BarcodeScannerActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 200
    private lateinit var codeScanner: CodeScanner
    var name: String =""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner)
        requestPermission()


//        try {
            codeScanner = CodeScanner(this, scanner_view_upi)

            // Parameters (default values)
            codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
            codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
            // ex. listOf(BarcodeFormat.QR_CODE)
            codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
            codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
            codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
            codeScanner.isFlashEnabled = false // Whether to enable flash or not

            // Callbacks
            codeScanner.decodeCallback = DecodeCallback {
                runOnUiThread {
                    //Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()

                    barReadCode(it.text.toString())

                }
            }

            codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
                runOnUiThread {
                    Toast.makeText(this, "Camera initialization error: ${it.message}",
                        Toast.LENGTH_LONG).show()
                }
            }

            scanner_view_upi.setOnClickListener {
                codeScanner.startPreview()
            }
//        }
//        catch (e: Exception)
//        {
//            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
//        }


    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@BarcodeScannerActivity,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            PERMISSION_REQUEST_CODE
        )
    }


    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun barReadCode(barcode : String)
    {
        val extra = Bundle()
        extra.putString("barcode", barcode)
        val resultIntent = Intent()
        resultIntent.putExtra("extra", extra)
        setResult(RESULT_OK, resultIntent)
        finish()
    }


}