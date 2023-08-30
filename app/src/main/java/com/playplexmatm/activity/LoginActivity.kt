package com.playplexmatm.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.playplexmatm.R
import com.playplexmatm.aeps.DashboardActivity
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.microatm.MATMTestActivity
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.*
import com.playplexmatm.util.AppConstants.Companion.LOGIN_API
import com.playplexmatm.util.AppConstants.Companion.MESSAGE
import com.playplexmatm.util.AppConstants.Companion.RESULT
import com.playplexmatm.util.AppConstants.Companion.TOKEN
import com.playplexmatm.util.AppConstants.Companion.TRUE
import com.playplexmatm.util.AppConstants.Companion.USER_MODEL
import com.playplexmatm.util.Constants.Companion.email
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import pl.droidsonroids.gif.GifTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginActivity : AppCompatActivity(), View.OnClickListener,
    AppApiCalls.OnAPICallCompleteListener {

    lateinit var mContext: LoginActivity
    lateinit var etLoginMobile: EditText
    lateinit var etLoginPassword: EditText
    lateinit var tvLogin: Button
    lateinit var pbLoadData: GifTextView
    lateinit var apiInterface: ApiInterface
    private lateinit var otp: String
    lateinit var userModel: UserModel
    var token = ""
    val auth = FirebaseAuth.getInstance()

    //    lateinit var tvSignUp: TextView
    var remember = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mContext = this
        initUI()
        addListner()

        remember = AppPrefs.getBooleanPref("remember", this)

        if (remember) {
            etLoginMobile.setText(AppPrefs.getStringPref("email", this))
            etLoginPassword.setText(AppPrefs.getStringPref("password", this))

            etLoginPassword.transformationMethod = PasswordTransformationMethod()
        }


    }

    private fun initUI() {
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        etLoginMobile = findViewById(R.id.etLoginMb)
        etLoginPassword = findViewById(R.id.etLoginPass)
        tvLogin = findViewById(R.id.tvLogin)
        pbLoadData = findViewById(R.id.progress_bar)
//        tvSignUp = findViewById(R.id.tvSignUp)

        etLoginPassword.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    private fun addListner() {
        tvLogin.setOnClickListener(this)
//        tvSignUp.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvLogin -> {

                if (!AppCommonMethods.checkForMobile(etLoginMobile)) {
                    etLoginMobile.requestFocus()
                    etLoginMobile.error = "Invalid Mobile"
                } else if (etLoginPassword.text.toString().isNullOrEmpty()) {
                    etLoginPassword.requestFocus()
                    etLoginPassword.error = "Invalid Password"
                } else {
                    loginOrRegister("${etLoginMobile.text}@gmail.com","${etLoginPassword.text}1234")
                }
                /*
                                val email = etLoginMobile.text.toString()
                                val password = etLoginPassword.text.toString()

                                AppPrefs.putStringPref("email",email,this)
                                AppPrefs.putStringPref("password",password,this)
                                if(cbRemember.isChecked)
                                {
                                    AppPrefs.putBooleanPref("remember",true,this)
                                }
                                else
                                {
                                    AppPrefs.putBooleanPref("remember",false,this)
                                }

                                val intent = Intent(this,MATMTestActivity::class.java)
                                startActivity(intent)
                                */
            }
//            R.id.tvSignUp->{
//              setLinkData()
//            }
        }
    }
    private fun loginOrRegister(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful
                    val user = auth.currentUser
                    if (user != null) {
                        // User is logged in
//                        Toast.makeText(this,"Signed in",Toast.LENGTH_SHORT).show()

                    } else {
                        // User data not found (should not happen in normal flow)
                        Toast.makeText(this,"Signed in data not found",Toast.LENGTH_SHORT).show()

                    }
                } else {
                    // Login failed
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidUserException) {
                        // User with this email doesn't exist, so let's register them
                        register(email, password)
                    } else {
                        // Other login errors
                    }
                }
                val r = Random()
                otp = java.lang.String.format("%06d", r.nextInt(999999))
                Log.d("OTP", otp)
                loginApi(
                    etLoginMobile.text.toString(), etLoginPassword.text.toString(),
                    AppCommonMethods.getDeviceId(this),
                    AppCommonMethods.getDeviceName(),
                    otp
                )
            }
    }

    private fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
//                    Toast.makeText(this,"Registered",Toast.LENGTH_SHORT).show()
                    // Registration successful after login attempt
                } else {
                    Toast.makeText(this,"Registered failed",Toast.LENGTH_SHORT).show()
                    // Registration failed after login attempt
                }
            }
    }


    //API CALL FUNCTION DEFINITION
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


    private fun setPrefData(data: JSONObject, app: JSONObject) {
        PrefManager(mContext).setvalue(Constants.Is_Login, true)
        PrefManager(mContext).setValue(
            Constants.ACCESS_TOKEN,
            data.optString(Constants.ACCESS_TOKEN)
        )
        PrefManager(mContext).setValue(Constants.name, app.optString(Constants.name))
        PrefManager(mContext).setValue(Constants.email, app.optString(Constants.email))
        PrefManager(mContext).setValue(Constants.phone, app.optString(Constants.phone))

        startActivity(Intent(mContext, MainActivity::class.java))
        finishAffinity()
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

//                confirmOtp(otp,userModel,token)
                val email = etLoginMobile.text.toString()
                val password = etLoginPassword.text.toString()
                AppPrefs.putStringPref("email", email, this)
                AppPrefs.putStringPref("password", password, this)
                if (cbRemember.isChecked) {
                    AppPrefs.putBooleanPref("remember", true, this)
                } else {
                    AppPrefs.putBooleanPref("remember", false, this)
                }
//                confirmPinDialog()
                val gson = Gson()
                val json = gson.toJson(userModel)
                AppPrefs.putStringPref(USER_MODEL, json, this)
                AppPrefs.putBooleanPref(AppConstants.IS_LOGIN, true, this)
                AppPrefs.putStringPref(TOKEN, token, this)
                AppPrefs.putStringPref("deviceId", AppCommonMethods.getDeviceId(this), this)
                AppPrefs.putStringPref("deviceName", AppCommonMethods.getDeviceName(), this)
                val intent = Intent(this, MATMTestActivity::class.java)
                startActivity(intent)
                finish()

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
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            } else {

                progress_bar.visibility = View.GONE
                toast(message)

            }
        }
    }
}