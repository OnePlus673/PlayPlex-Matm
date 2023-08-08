package com.playplexmatm.activity.reports

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.adapter.report.PaymentsReportAdapter
import com.playplexmatm.model.report.PaymentsReportModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.activity_payment_reports.*
import kotlinx.android.synthetic.main.activity_payment_reports.edtFromDate
import kotlinx.android.synthetic.main.activity_payment_reports.edtToDate
import kotlinx.android.synthetic.main.activity_payment_reports.ivBack
import kotlinx.android.synthetic.main.activity_payment_reports.pbBottomLoadData
import kotlinx.android.synthetic.main.activity_payment_reports.pbLoadData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class PaymentReportsActivity : AppCompatActivity() {

    lateinit var mContext: PaymentReportsActivity
    lateinit var apiInterface: ApiInterface
    private var paymentsReportModelArrayList: ArrayList<PaymentsReportModel> = arrayListOf()
    lateinit var paymentsReportAdapter: PaymentsReportAdapter

    private var isloading: Boolean = false
    private var islastpage: Boolean = false
    private var currentPage = 1
    private var lastPage = 1
    private var offset: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_reports)
        mContext = this
        //supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)


        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallPaymentReportAPI(currentPage, "")
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        setAdapter(paymentsReportModelArrayList)

        ivBack.setOnClickListener {
            onBackPressed()
        }

        edtFromDate.setOnClickListener {
            showFromDatePicker(edtFromDate)
        }

        edtToDate.setOnClickListener {
            showToDatePicker(edtToDate)
        }
    }

    private fun mNetworkCallPaymentReportAPI(newcurrentPage: Int, date: String) {

        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            paymentsReportModelArrayList.clear()
        }


        val call = apiInterface.getPaymentReport("id","", offset, 10, date)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    pbBottomLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            Log.e("response",jsonObject.toString())

                            isloading = false
                            val data = jsonObject.optJSONArray("data")
                            paymentsReportModelArrayList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<PaymentsReportModel?>?>() {}.type
                                )
                            )
                            currentPage = newcurrentPage
                            lastPage = Math.ceil(
                                (jsonObject.optJSONObject("meta")!!.optJSONObject("paging")!!
                                    .optString("total").toDouble() / 10.00)
                            ).toInt()

                            Log.e("lastPage", "=" + lastPage)

                            if (currentPage == lastPage) {
                                islastpage = true
                            }


                            if (paymentsReportModelArrayList.size > 0) {
                                rvPaymentReport.visibility = View.VISIBLE
                                tvNoPaymentReport.visibility = View.GONE
                                paymentsReportAdapter.notifyDataSetChanged()
                            } else {

                                if (currentPage == 1) {
                                    rvPaymentReport.visibility = View.GONE
                                    tvNoPaymentReport.visibility = View.VISIBLE
                                }
                            }
                            edtSearch.addTextChangedListener(object : TextWatcher {
                                override fun beforeTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    count: Int,
                                    after: Int
                                ) {
                                }

                                override fun onTextChanged(
                                    s: CharSequence,
                                    start: Int,
                                    before: Int,
                                    count: Int
                                ) {
                                }

                                override fun afterTextChanged(s: Editable?) {
                                    filter(s.toString())
                                }
                            })

                        } else {
                            if (currentPage == 1) {
                                rvPaymentReport.visibility = View.GONE
                                tvNoPaymentReport.visibility = View.VISIBLE
                                Toast.makeText(
                                    mContext,
                                    jsonObject.optString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

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
                pbBottomLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setAdapter(paymentsReportModelArrayList: ArrayList<PaymentsReportModel>) {

        rvPaymentReport.layoutManager = LinearLayoutManager(mContext)
        rvPaymentReport.setHasFixedSize(true)
        paymentsReportAdapter = PaymentsReportAdapter(mContext, paymentsReportModelArrayList)
        rvPaymentReport.adapter = paymentsReportAdapter
        paymentsReportAdapter.notifyDataSetChanged()
    }


    private fun showFromDatePicker(edtDate: EditText) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var datePickerDialog =
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val mmMonth = mMonth + 1
                val date = "$mYear-$mmMonth-$mDay"
                edtFromDate.setText(date)
                if(edtToDate.text.toString().isNullOrEmpty()) {
                    Toast.makeText(this, "Select To Date", Toast.LENGTH_SHORT).show()
                } else {
                    mNetworkCallPaymentReportAPI(currentPage,edtFromDate.text.toString()+","+edtToDate.text.toString())
                }
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun showToDatePicker(edtDate: EditText) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var datePickerDialog =
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val mmMonth = mMonth + 1
                val date = "$mYear-$mmMonth-$mDay"
                edtToDate.setText(date)
                if(edtFromDate.text.toString().isNullOrEmpty()) {
                    Toast.makeText(this, "Select From Date", Toast.LENGTH_SHORT).show()
                } else {
                    mNetworkCallPaymentReportAPI(currentPage,edtFromDate.text.toString()+","+edtToDate.text.toString())
                }
            },
                year,
                month,
                day
            )
        datePickerDialog.show()
    }

    fun filter(text: String) {
        val temp: MutableList<PaymentsReportModel> = ArrayList()
        for (d in paymentsReportModelArrayList) {


            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.user.name.contains(text,ignoreCase = true)) {
                temp.add(d)
            }
        }
        //update recyclerview
        paymentsReportAdapter.updateList(temp)
    }


}