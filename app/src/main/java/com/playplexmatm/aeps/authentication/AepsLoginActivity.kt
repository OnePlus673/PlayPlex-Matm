package com.playplexmatm.aeps.authentication

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.playplexmatm.R
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.util.AppCommonMethods
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.AppConstants.Companion.LOGIN_API
import com.playplexmatm.util.AppConstants.Companion.MESSAGE
import com.playplexmatm.util.AppConstants.Companion.RESULT
import com.playplexmatm.util.AppConstants.Companion.TOKEN
import com.playplexmatm.util.AppConstants.Companion.TRUE
import com.playplexmatm.util.AppConstants.Companion.USER_MODEL
import com.playplexmatm.util.AppPrefs
import com.playplexmatm.util.toast
import kotlinx.android.synthetic.main.activity_aeps_login.*
import kotlinx.android.synthetic.main.layout_dialog_confirmpin.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class AepsLoginActivity : AppCompatActivity(), AppApiCalls.OnAPICallCompleteListener {

    var dialogBuilder: AlertDialog? = null
    var editText: EditText? = null
    private lateinit var otp: String
    lateinit var dialog: Dialog

    lateinit var token: String

    lateinit var userModel: UserModel
    lateinit var from: String

    lateinit var jsonArray: JSONArray
    lateinit var paymentArray: JSONArray
    lateinit var detailsArray: JSONObject
    lateinit var amount: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aeps_login)

        val bundle = intent.extras
        if(bundle != null) {
            from = bundle.getString("from").toString()
            amount = bundle.getString("amount").toString()
            jsonArray = JSONArray(bundle.getString("jsonArray"))
            paymentArray = JSONArray(bundle.getString("paymentArray"))
            detailsArray = JSONObject(bundle.getString("detailsArray"))
        }

        btnLogin.setOnClickListener {
            if (!AppCommonMethods.checkForMobile(etLoginMobile)) {
                etLoginMobile.requestFocus()
                etLoginMobile.error = "Invalid Mobile Number"
            } else if (etLoginPassword.text.toString().isNullOrEmpty()) {
                etLoginPassword.requestFocus()
                etLoginPassword.error = "Invalid Password"
            } else {
                val r = Random()
                otp = java.lang.String.format("%06d", r.nextInt(999999))
                Log.d("OTP", otp)
                loginApi(
                    etLoginMobile.text.toString(), etLoginPassword.text.toString(),
                    AppCommonMethods.getDeviceId(this),
                    AppCommonMethods.getDeviceName(),
                    otp
                )
//                val intent = Intent(this, DashboardActivity::class.java)
//                startActivity(intent)
            }

        }

        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

//        tvRegisterHere.setOnClickListener {
//            val intent = Intent(this, VerifyNumberActivity::class.java)
//            startActivity(intent)
//        }



    }

    private fun loginApi(
        mobile: String, password: String, deviceId: String, deviceNameDet: String,
        otp: String
    ) {
        progress_bar.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                LOGIN_API,
                this
            )
            mAPIcall.login(mobile, password, deviceId, deviceNameDet, otp)

        } else {
            toast(getString(R.string.error_internet))
        }
    }

    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(LOGIN_API)) {
            progress_bar.visibility = View.GONE
            Log.e(LOGIN_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(MESSAGE)
            Log.e(AppConstants.STATUS, status)
            Log.e(MESSAGE, messageCode)
            if (status.contains(TRUE)) {
                token = jsonObject.getString(TOKEN)
                val cast = jsonObject.getJSONArray(RESULT)
                for (i in 0 until cast.length()) {
                    val notifyObjJson = cast.getJSONObject(i)
                    userModel = Gson()
                        .fromJson(notifyObjJson.toString(), UserModel::class.java)
                }
                val gson = Gson()
                val json = gson.toJson(userModel)
//                AppPrefs.putStringPref(USER_MODEL, json, this)
                AppPrefs.putStringPref(TOKEN, token, this)

                confirmPinDialog()

            } else {
                toast(messageCode.trim())
            }
        }
        if (flag.equals(AppConstants.VERFY_PIN_API)) {
            Log.e(AppConstants.VERFY_PIN_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val message = jsonObject.getString(AppConstants.MESSAGE)
            Log.e(AppConstants.STATUS, status)
            if (status.contains(AppConstants.TRUE)) {
                progress_bar.visibility = View.GONE
                val gson = Gson()
                val json = gson.toJson(userModel)
                AppPrefs.putStringPref(USER_MODEL, json, this)
                AppPrefs.putBooleanPref(AppConstants.IS_LOGIN, true, this)
                AppPrefs.putStringPref(TOKEN, token, this)
                AppPrefs.putStringPref("deviceId", AppCommonMethods.getDeviceId(this), this)
                AppPrefs.putStringPref("deviceName", AppCommonMethods.getDeviceName(), this)

                onBackPressed()
            } else {
                progress_bar.visibility = View.GONE
                toast( message)
            }
        }

    }

    fun confirmPinDialog() {
        dialog = Dialog(this, com.google.android.material.R.style.Theme_MaterialComponents_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_confirmpin)

        dialog.etPin.requestFocus()
        dialog.tvDialogCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.getWindow()!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        dialog.tvConfirmPin.setOnClickListener {
            if (dialog.etPin.text.toString().isEmpty()) {
                dialog.etPin.requestFocus()
                dialog.etPin.setError("Please Enter Pin")
            } else {

                verifyPin(userModel.cus_mobile, dialog.etPin.text.toString())
                dialog.dismiss()
            }

        }


        dialog.show()
    }

    private fun verifyPin(
        cus_mobile: String,
        pin: String
    ) {
        progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                AppConstants.VERFY_PIN_API,
                this
            )
            mAPIcall.verifyPin(cus_mobile, pin)

        } else {
            this.toast(getString(R.string.error_internet))
        }
    }
}