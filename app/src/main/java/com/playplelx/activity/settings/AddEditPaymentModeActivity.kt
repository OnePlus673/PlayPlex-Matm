package com.playplelx.activity.settings

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.playplelx.R
import com.playplelx.model.paymentmode.PaymentModeModel
import com.playplelx.model.taxes.TaxesModel
import com.playplelx.network.ApiInterface
import com.playplelx.network.Apiclient
import com.playplelx.util.Constants
import com.playplelx.util.InternetConnection
import com.playplelx.util.Util
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class AddEditPaymentModeActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mContext: AddEditPaymentModeActivity
    lateinit var ivBack: ImageView

    lateinit var apiInterface: ApiInterface
    lateinit var edtName: EditText
    lateinit var edtModeType: EditText
    lateinit var tvSave: TextView
    lateinit var pbLoadData: ProgressBar
    private var mFrom: String = ""
    lateinit var paymentModeModel: PaymentModeModel
    lateinit var tvTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_payment_mode)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        tvTitle = findViewById(R.id.tvTitle)
        edtName = findViewById(R.id.edtName)
        edtModeType = findViewById(R.id.edtModeType)
        tvSave = findViewById(R.id.tvSave)
        pbLoadData = findViewById(R.id.pbLoadData)

        if (intent.extras != null) {
            mFrom = intent.getStringExtra(Constants.mFrom)!!
            if (mFrom == Constants.isEdit) {
                tvTitle.text = "Update Payment Mode"
                paymentModeModel = Gson().fromJson(
                    intent.getStringExtra(Constants.paymentModel),
                    PaymentModeModel::class.java
                )
                setData(paymentModeModel)
            } else {
                tvTitle.text = "Add Payment Mode"
            }
        }
    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        tvSave.setOnClickListener(this)
    }

    private fun setData(paymentModeModel: PaymentModeModel) {
        edtName.setText(paymentModeModel.name)
        edtModeType.setText(paymentModeModel.mode_type)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvSave -> {
                if (InternetConnection.checkConnection(mContext)) {
                    if (isValidate()) {
                        if (mFrom == Constants.isEdit){
                            mNetworkCallUpdatePaymentModeAPI()
                        }else{
                            mNetworkCallAddPaymentModeAPI()
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


    private fun mNetworkCallAddPaymentModeAPI() {
        pbLoadData.visibility = View.VISIBLE
        val call =
            apiInterface.addPaymentModes(edtName.text.toString().trim(), edtModeType.text.toString().trim())
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


    private fun mNetworkCallUpdatePaymentModeAPI() {
        pbLoadData.visibility = View.VISIBLE
        val call =
            apiInterface.updatePaymentModes(paymentModeModel.xid,edtName.text.toString().trim(), edtModeType.text.toString().trim())
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
        } else if (edtModeType.text.toString().trim().isEmpty()) {
            edtModeType.requestFocus()
            Toast.makeText(mContext, "Please enter mode type", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        return isValid
    }
}