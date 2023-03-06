package com.playplelx.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.playplelx.R
import com.playplelx.network.ApiInterface
import com.playplelx.network.Apiclient
import com.playplelx.util.Constants
import com.playplelx.util.InternetConnection
import com.playplelx.util.PrefManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mContext: LoginActivity
    lateinit var edtEmailAddress: EditText
    lateinit var edtPassword: EditText
    lateinit var tvLogin: TextView
    lateinit var pbLoadData: ProgressBar
    lateinit var apiInterface: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        edtEmailAddress = findViewById(R.id.edtEmailAddress)
        edtPassword = findViewById(R.id.edtPassword)
        tvLogin = findViewById(R.id.tvLogin)
        pbLoadData = findViewById(R.id.pbLoadData)
    }

    private fun addListner() {
        tvLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvLogin -> {
                if (InternetConnection.checkConnection(mContext)) {
                    if (isValidate()) {
                        mNetworkCallLoginAPI(
                            edtEmailAddress.text.toString().trim(),
                            edtPassword.text.toString().trim()
                        )
                    }
                } else {
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.str_check_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun isValidate(): Boolean {
        var isValid = true
        if (edtEmailAddress.text.toString().trim().isEmpty()) {
            edtEmailAddress.requestFocus()
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_error_email_address),
                Toast.LENGTH_SHORT
            ).show()
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmailAddress.text.toString().trim())
                .matches()
        ) {
            edtEmailAddress.requestFocus()
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_error_valid_email_address),
                Toast.LENGTH_SHORT
            ).show()
            isValid = false
        } else if (edtPassword.text.toString().trim().isEmpty()) {
            edtPassword.requestFocus()
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_error_password),
                Toast.LENGTH_SHORT
            ).show()
            isValid = false
        }
        return isValid
    }

    private fun mNetworkCallLoginAPI(email: String, password: String) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.doLogin(email, password)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (response.isSuccessful){
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonobject = JSONObject(response.body().toString())
                        if (jsonobject.optBoolean("status")) {
                            val data = jsonobject.optJSONObject("data")
                            val app = data?.optJSONObject("app")
                            if (app != null) {
                                setPrefData(data, app)
                            }
                            Toast.makeText(
                                mContext, jsonobject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                mContext, jsonobject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }else{
                    pbLoadData.visibility=View.GONE
                    Toast.makeText(mContext,mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                    Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
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
}