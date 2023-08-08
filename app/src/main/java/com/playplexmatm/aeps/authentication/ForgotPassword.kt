package com.playplexmatm.aeps.authentication

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.playplexmatm.R
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.util.AppCommonMethods
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.toast
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_forgot_password.view.*
import kotlinx.android.synthetic.main.layout_dialog_confirmotp.*
import org.json.JSONObject
import java.util.*

class ForgotPassword : AppCompatActivity(), AppApiCalls.OnAPICallCompleteListener {

    private val SEND_OTP: String = "SEND_OTP"
    private val FORGOTPASSWORD = "FORGOTPASSWORD"
    private lateinit var otp: String
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)


        custToolbar.ivBackBtn.setOnClickListener {
            onBackPressed()
        }



        btnGetOtp.setOnClickListener {
            val r = Random()
            otp = java.lang.String.format("%06d", r.nextInt(999999))
            Log.d("OTP", otp)
            if (!AppCommonMethods.checkForMobile(etForgotMobileNumber)) {
                etForgotMobileNumber.requestFocus()
                etForgotMobileNumber.error = "Invalid Mobile number"
            } else {
                sendSmsForForgetPass(etForgotMobileNumber.text.toString(), otp)
            }
        }

    }


    fun sendSmsForForgetPass(
        mobile: String, otp: String
    ) {
        progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(this, SEND_OTP, this)
            mAPIcall.getForgetPassOtp(mobile, otp)
        } else {

            toast("No Internet Connection")

        }
    }


    private fun forgetPassword(
        mobile: String,
        deviceId: String,
        deviceNameDet: String

    ) {
        progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(this, FORGOTPASSWORD, this)
            mAPIcall.forgotPassword(mobile, deviceId, deviceNameDet)
        } else {
            Toast.makeText(this, "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(SEND_OTP)) {
            Log.e("SEND_OTP", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status)
            //Log.e(AppConstants.MESSAGE_CODE, messageCode);
            if (status.contains("true")) {
                progress_bar.visibility = View.INVISIBLE
                confirmOtp(otp)
            } else {

                progress_bar.visibility = View.INVISIBLE

            }
        }
        if (flag.equals(FORGOTPASSWORD)) {
            Log.e("FORGOT_PASSWORD", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status.toString())
            if (status.contains("true")) {
                progress_bar.visibility = View.GONE
                onBackPressed()
                Toast.makeText(this, "Password Changed Succesfully", Toast.LENGTH_SHORT)
                    .show()
            } else {
                progress_bar.visibility = View.GONE
                Toast.makeText(this, "Invalid User", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun confirmOtp(otp: String) {
        dialog = Dialog(this, com.google.android.material.R.style.Theme_MaterialComponents_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_confirmotp)

        dialog.etOtp.requestFocus()
        dialog.tvDialogCancel.setOnClickListener {
            dialog.dismiss()
        }


        dialog.getWindow()!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        dialog.tvConfirmOtp.setOnClickListener {
            if (!dialog.etOtp.text.toString().equals(otp)) {
                dialog.etOtp.requestFocus()
                dialog.etOtp.setError("Please Enter Valid OTP")
            } else {


                forgetPassword(
                    etForgotMobileNumber.text.toString(),
                    AppCommonMethods.getDeviceId(this),
                    AppCommonMethods.getDeviceName()
                )

                dialog.dismiss()

            }

        }


        dialog.show()
    }

}