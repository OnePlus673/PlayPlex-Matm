package com.playplexmatm.activity.settings

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.playplexmatm.R
import com.playplexmatm.model.units.UnitModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.Constants
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.Util
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class AddEditUnitActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mContext: AddEditUnitActivity
    lateinit var ivBack: ImageView
    lateinit var apiInterface: ApiInterface
    lateinit var edtName: EditText
    lateinit var edtShortName: EditText
    lateinit var tvSave: TextView
    lateinit var pbLoadData: ProgressBar
    private var mFrom: String = ""
    lateinit var unitModel: UnitModel
    lateinit var tvTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_unit)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        tvTitle = findViewById(R.id.tvTitle)
        edtName = findViewById(R.id.edtName)
        edtShortName = findViewById(R.id.edtShortName)
        tvSave = findViewById(R.id.tvSave)
        pbLoadData = findViewById(R.id.pbLoadData)

        if (intent.extras != null) {
            mFrom = intent.getStringExtra(Constants.mFrom)!!
            if (mFrom == Constants.isEdit) {
                tvTitle.text = "Update Units"
                unitModel = Gson().fromJson(
                    intent.getStringExtra(Constants.unitModel),
                    UnitModel::class.java
                )
                setData(unitModel)
            } else {
                tvTitle.text = "Add Units"
            }
        }
    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        tvSave.setOnClickListener(this)
    }

    private fun setData(unitModel: UnitModel) {
        edtName.setText(unitModel.name)
        edtShortName.setText(unitModel.short_name)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvSave -> {
                if (InternetConnection.checkConnection(mContext)) {
                    if (isValidate()) {
                        if (mFrom == Constants.isEdit) {
                            mNetworkCallUpdateUnitAPI()
                        } else {
                            mNetworkCallAddUnitAPI()
                        }
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

    private fun mNetworkCallAddUnitAPI() {
        pbLoadData.visibility = View.VISIBLE
        val call =
            apiInterface.addUnits(
                edtName.text.toString().trim(),
                edtShortName.text.toString().trim(),
                "multiply",
                "1"
            )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        } else {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext,JsonObject.optString("message"))
                    }catch (e: Exception){

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun mNetworkCallUpdateUnitAPI() {
        pbLoadData.visibility = View.VISIBLE
        val call =
            apiInterface.updateUnits(
                unitModel.xid,
                edtName.text.toString().trim(),
                edtShortName.text.toString().trim(), "multiply",
                "1"
            )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        } else {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext,JsonObject.optString("message"))
                    }catch (e: Exception){

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun isValidate(): Boolean {
        var isValid = true
        if (edtName.text.toString().trim().isEmpty()) {
            edtName.requestFocus()
            Toast.makeText(mContext, "Please enter name", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (edtShortName.text.toString().trim().isEmpty()) {
            edtShortName.requestFocus()
            Toast.makeText(mContext, "Please enter short name", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        return isValid
    }
}