package com.playplexmatm.activity.reports

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.JsonObject
import com.playplexmatm.R
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.activity_profitand_loss.*
import kotlinx.android.synthetic.main.activity_profitand_loss.pbLoadData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class ProfitandLossActivity : AppCompatActivity() {

    lateinit var mContext: ProfitandLossActivity
    lateinit var apiInterface: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profitand_loss)
        mContext = this
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallProfitLossyReportAPI()
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    private fun mNetworkCallProfitLossyReportAPI() {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getProfitLoss("daily_income")
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            Log.e("response", jsonObject.toString())

                            val data = jsonObject.getJSONObject("data")

                            tvSales.setText(resources.getString(R.string.Rupee)+" "+data.getString("sales"))
                            tvPurchases.setText(resources.getString(R.string.Rupee)+" "+data.getString("purchases"))
                            tvSaleReturn.setText(resources.getString(R.string.Rupee)+" "+data.getString("sales_returns"))
                            tvPurchaseReturn.setText(resources.getString(R.string.Rupee)+" "+data.getString("purchase_returns"))
                            tvStockTransferTransferred.setText(resources.getString(R.string.Rupee)+" "+data.getString("stock_transfer_transfered"))
                            tvStockTransferReceived.setText(resources.getString(R.string.Rupee)+" "+data.getString("stock_transfer_received"))
                            tvExpenses.setText(resources.getString(R.string.Rupee)+" "+data.getString("expenses"))
                            tvProfit.setText(resources.getString(R.string.Rupee)+" "+data.getString("profit"))
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
                        val JsonObject= JSONObject(response.errorBody()!!.string())
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

}