package com.playplexmatm.aeps.activities_aeps

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
//import com.paysprint.onboardinglib.activities.HostActivity
import com.playplexmatm.R
import com.playplexmatm.activity.MainActivity
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.util.AppPrefs
import com.playplexmatm.util.toast
import kotlinx.android.synthetic.main.activity_paysprints_onboarding.*

class PaysprintsOnboardingActivity : AppCompatActivity() {

    var latitude: String=""
    var longitude: String=""
    lateinit var userModel: UserModel
    var intent1: Intent? = null
    var gpsStatus = false

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paysprints_onboarding)

        val gson = Gson()
        val json = AppPrefs.getStringPref("userModel", this)
        userModel = gson.fromJson(json, UserModel::class.java)


        btnSubmit.setOnClickListener {
            Log.e("LALO", "$latitude $longitude")
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("pId", "PS003715")//partner Id provided in credential
            intent.putExtra("pApiKey", "UFMwMDM3MTVmOTU2MDg0MDFjNDdkNmYxZWI5NTRlNmZiNmQ1OTA2OA==")//JWT API Key provided in credential
            intent.putExtra("mCode", "FP"+userModel.cus_mobile)//Merchant Code
            intent.putExtra("mobile", userModel.cus_mobile)// merchant mobile number
            intent.putExtra("lat", latitude)
            intent.putExtra("lng", longitude)
            intent.putExtra("firm", userModel.cus_name)
            intent.putExtra("email", userModel.cus_email)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivityForResult(intent, 999)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999) {
            if (resultCode == Activity.RESULT_OK) {
                val status = data?.getBooleanExtra("status", false)
                val response = data?.getIntExtra("response", 0)
                val message = data?.getStringExtra("message")
                val detailedResponse = "Status: $status,  " +
                        "Response: $response, " +
                        "Message: $message "

                val builder = AlertDialog.Builder(this@PaysprintsOnboardingActivity)
                builder.setTitle("Info")
                builder.setMessage(message)
                builder.setCancelable(false)
                builder.setPositiveButton("Ok"){dialogInterface, which ->
                    dialogInterface.dismiss()
                }
                builder.show();
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkGpsStatus()
    }

    private fun checkGpsStatus() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (gpsStatus) {
            getLocation()
        } else {
            gpsStatus()
            toast("GPS is Disabled")
        }
    }

    fun gpsStatus() {
        intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent1)
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode)
        }

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            5000, 5f, locationListener)

    }

    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            Log.d("TAG", "onLocationChanged: "+"Latitude: " + location.latitude)
            Log.d("TAG", "onLocationChanged: "+"Longitude: " + location.longitude)

            latitude = location.latitude.toString()
            longitude = location.longitude.toString()
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}