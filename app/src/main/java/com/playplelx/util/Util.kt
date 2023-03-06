package com.playplelx.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import com.google.gson.JsonObject
import com.playplelx.R
import com.playplelx.activity.LoginActivity
import com.playplelx.network.ApiInterface
import com.playplelx.network.Apiclient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Util {
    var _context: Activity? = null

    constructor(context: Activity) {
        this._context = context
    }


    fun logOutAlertDialog(context: Context, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setPositiveButton("Yes") { dialog, which ->
            if (InternetConnection.checkConnection(context)) {
                mNetworkCallLogoutAPI(dialog)
            } else {
                Toast.makeText(
                    context, context.resources.getString(R.string.str_check_internet_connections),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        builder.setNegativeButton(
            "No"
        ) { dialog, which -> // Do nothing
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun mNetworkCallLogoutAPI(dialog: DialogInterface) {
        val apiInterface = Apiclient(_context!!).getClient()!!.create(ApiInterface::class.java)
        val call: Call<JsonObject> = apiInterface.doLogout()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            dialog.dismiss()
                            PrefManager(_context!!).clear()
                            Toast.makeText(
                                _context!!,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            _context!!.startActivity(Intent(_context!!, LoginActivity::class.java))
                            _context!!.finishAffinity()
                        } else {
                            Toast.makeText(
                                _context!!,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        _context!!,
                        _context!!.resources.getString(R.string.str_something_went_wrong_on_server),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(_context!!, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

}