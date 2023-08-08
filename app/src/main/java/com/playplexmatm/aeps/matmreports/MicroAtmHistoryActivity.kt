package com.playplexmatm.microatm

import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.playplexmatm.R
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.util.AppCommonMethods
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.AppPrefs
import com.playplexmatm.util.toast
import com.sg.swapnapay.model.MicroAtmHistoryModel
import com.sg.swapnapay.model.UserModel
import kotlinx.android.synthetic.main.activity_micro_atm_history.*
import kotlinx.android.synthetic.main.activity_micro_atm_history.rvDisputeHistory
import kotlinx.android.synthetic.main.activity_micro_atm_history.view.*
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MicroAtmHistoryActivity : AppCompatActivity() , AppApiCalls.OnAPICallCompleteListener,
    PopupMenu.OnMenuItemClickListener,SwipeRefreshLayout.OnRefreshListener {

    lateinit var aepsHistoryAdapter: MicroAtmHistoryAdapter
    var aepsHistoryModelArrayList = ArrayList<MicroAtmHistoryModel>()
    private val AEPSHISTORY_REPORT: String = "AEPSHISTORY_REPORT"
    lateinit var userModel: UserModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.black, this.theme)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        setContentView(R.layout.activity_micro_atm_history)
        custToolbar.ivBackBtn.setOnClickListener { onBackPressed() }


        val gson = Gson()
        val json = AppPrefs.getStringPref("userModel", this)
        userModel = gson.fromJson(json, UserModel::class.java)

        val date = getCurrentDateTime()
        val dateInString = date.toString("dd MMM yyyy")
        tvFromDateRecHistory.text = dateInString
        tvToDateRecHistory.text = dateInString


        mSwipeRefresh.setOnRefreshListener(this);

        mSwipeRefresh.post(Runnable {
            if (mSwipeRefresh != null) {
                mSwipeRefresh.setRefreshing(true)
            }
            aepsHistory(
                userModel.cus_id, AppCommonMethods.convertDateFormat(
                    "dd MMM yyyy",
                    "yyyy-MM-dd", tvFromDateRecHistory.text.toString()
                ).toString(),
                AppCommonMethods.convertDateFormat(
                    "dd MMM yyyy",
                    "yyyy-MM-dd", tvToDateRecHistory.text.toString()
                ).toString(),
                AppPrefs.getStringPref("deviceId", this).toString(),
                AppPrefs.getStringPref("deviceName", this).toString(),
                userModel.cus_pin,
                userModel.cus_pass,
                userModel.cus_mobile, userModel.cus_type
            )
            mSwipeRefresh.setRefreshing(false)

        })


        custToolbar.ivMore.setOnClickListener {
            val popup = PopupMenu(this@MicroAtmHistoryActivity, ivMore)
            popup.setOnMenuItemClickListener(this@MicroAtmHistoryActivity)
            popup.inflate(R.menu.menu_main)
            popup.show()
        }

        rvDisputeHistory.apply {

            layoutManager = LinearLayoutManager(this@MicroAtmHistoryActivity)
            aepsHistoryAdapter = MicroAtmHistoryAdapter(
                context, aepsHistoryModelArrayList
            )
            rvDisputeHistory.adapter = aepsHistoryAdapter
        }

        tvFromDateRecHistory.setOnClickListener {
            FromdatePicker()
        }

        tvToDateRecHistory.setOnClickListener {
            TodatePicker()
        }
    }

    private fun aepsHistory(
        cus_id: String,
        fromDate: String,
        toDate: String,
        deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {
        progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(this, AEPSHISTORY_REPORT, this)
            mAPIcall.microatmHistory(cus_id, fromDate,toDate,deviceId,deviceName, pin, pass, cus_mobile, cus_type)
        } else {

            Toast.makeText(this, "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(AEPSHISTORY_REPORT)) {
            aepsHistoryModelArrayList.clear()
            Log.e("AEPSHISTORY_REPORT", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status)
            if (status.contains("true")) {
                progress_bar.visibility = View.INVISIBLE
                val cast = jsonObject.getJSONArray("result")
                for (i in 0 until cast.length()) {
                    val notifyObjJson = cast.getJSONObject(i)
                    val aeps_id = notifyObjJson.getString("ma_id")
                    Log.e("aeps_id ", aeps_id)
                    val aepsmodel = Gson()
                        .fromJson(
                            notifyObjJson.toString(),
                            MicroAtmHistoryModel::class.java
                        )
                    aepsHistoryModelArrayList.add(aepsmodel)
                }
                rvDisputeHistory.adapter!!.notifyDataSetChanged()
            } else {
                progress_bar.visibility = View.INVISIBLE
                toast(jsonObject.getString("result"))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.action_refresh -> {
                aepsHistory(
                    userModel.cus_id, AppCommonMethods.convertDateFormat(
                        "dd MMM yyyy",
                        "yyyy-MM-dd", tvFromDateRecHistory.text.toString()
                    ).toString(),
                    AppCommonMethods.convertDateFormat(
                        "dd MMM yyyy",
                        "yyyy-MM-dd", tvToDateRecHistory.text.toString()
                    ).toString(),
                    AppPrefs.getStringPref("deviceId", this).toString(),
                    AppPrefs.getStringPref("deviceName", this).toString(),
                    userModel.cus_pin,
                    userModel.cus_pass,
                    userModel.cus_mobile, userModel.cus_type
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }



    fun FromdatePicker() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var dpd =
            DatePickerDialog(this, { view, mYear, mMonth, mDay ->
                val mmMonth = mMonth + 1
                val date = "$mDay/$mmMonth/$mYear"

                tvFromDateRecHistory.setText(
                    AppCommonMethods.convertDateFormat(
                        "dd/MM/yyyy",
                        "dd MMM yyyy", date
                    ).toString()
                )
                compareTwoDates(
                    tvFromDateRecHistory.text.toString(),
                    tvToDateRecHistory.text.toString()
                )

            }, year, month, day)
        dpd.show()
    }

    fun TodatePicker() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var dpd =
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val mmMonth = mMonth + 1
                val date = "$mDay/$mmMonth/$mYear"

                tvToDateRecHistory.setText(
                    AppCommonMethods.convertDateFormat(
                        "dd/MM/yyyy",
                        "dd MMM yyyy", date
                    ).toString()
                )


                compareTwoDates(
                    tvFromDateRecHistory.text.toString(),
                    tvToDateRecHistory.text.toString()
                )


            }, year, month, day)
        dpd.show()
    }

    private fun compareTwoDates(date: String, dateafter: String) {


        val dateFormat = SimpleDateFormat(
            "dd MMM yyyy"
        )
        var convertedDate: Date? = Date()
        var convertedDate2 = Date()
        try {
            convertedDate = dateFormat.parse(date)
            convertedDate2 = dateFormat.parse(dateafter)
            if (convertedDate2.after(convertedDate) || convertedDate2.equals(convertedDate)) {
//                recentRechargeHistoryModalArrayList.clear()
                aepsHistory(
                    userModel.cus_id, AppCommonMethods.convertDateFormat(
                        "dd MMM yyyy",
                        "yyyy-MM-dd", tvFromDateRecHistory.text.toString()
                    ).toString(),
                    AppCommonMethods.convertDateFormat(
                        "dd MMM yyyy",
                        "yyyy-MM-dd", tvToDateRecHistory.text.toString()
                    ).toString(),
                    AppPrefs.getStringPref("deviceId", this).toString(),
                    AppPrefs.getStringPref("deviceName", this).toString(),
                    userModel.cus_pin,
                    userModel.cus_pass,
                    userModel.cus_mobile, userModel.cus_type
                )


            } else {
                toast("Invalid Date")
            }
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }


    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    override fun onRefresh() {
        aepsHistory(
            userModel.cus_id, AppCommonMethods.convertDateFormat(
                "dd MMM yyyy",
                "yyyy-MM-dd", tvFromDateRecHistory.text.toString()
            ).toString(),
            AppCommonMethods.convertDateFormat(
                "dd MMM yyyy",
                "yyyy-MM-dd", tvToDateRecHistory.text.toString()
            ).toString(),
            AppPrefs.getStringPref("deviceId", this).toString(),
            AppPrefs.getStringPref("deviceName", this).toString(),
            userModel.cus_pin,
            userModel.cus_pass,
            userModel.cus_mobile, userModel.cus_type
        )
        mSwipeRefresh.setRefreshing(false)

    }


}