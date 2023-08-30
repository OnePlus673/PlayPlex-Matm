package com.playplexmatm.activity.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.payplex.aeps.activities_aeps.aepshistory.AepsLedgerHistoryModel
import com.payplex.aeps.aeps_activities.AepsLedgerHistoryAdapter
import com.playplexmatm.R
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.microatm.MATMTestActivity
import com.playplexmatm.util.AppCommonMethods
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.AppPrefs
import com.playplexmatm.util.toast
import kotlinx.android.synthetic.main.activity_matmtest.*
import kotlinx.android.synthetic.main.activity_matmtest.view.*
import kotlinx.android.synthetic.main.fragment_sales.view.*
import kotlinx.android.synthetic.main.fragment_sales.view.progress_bar
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SalesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SalesFragment : Fragment(), AppApiCalls.OnAPICallCompleteListener, SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    
    lateinit var root : View

    lateinit var aepsHistoryAdapter: AepsLedgerHistoryAdapter
    var aepsHistoryModelArrayList = ArrayList<AepsLedgerHistoryModel>()
    private val AEPSHISTORY_REPORT: String = "AEPSHISTORY_REPORT"
    private val AEPS_LEDGER_HISTORY_REPORT: String = "AEPS_LEDGER_HISTORY_REPORT"
    lateinit var userModel: UserModel
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_sales, container, false)

        val gson = Gson()
        val json = AppPrefs.getStringPref("userModel", requireContext())
        userModel = gson.fromJson(json, UserModel::class.java)

//        (activity as MATMTestActivity).custToolbar.tvTitle.setText("Sales")

        val date = getCurrentDateTime()
        val dateInString = date.toString("dd MMM yyyy")
        root.tvFromDateRecHistory.text = dateInString
        root.tvToDateRecHistory.text = dateInString


        root.mSwipeRefresh.setOnRefreshListener(this);

        root.mSwipeRefresh.post(Runnable {
            if (root.mSwipeRefresh != null) {
                root.mSwipeRefresh.setRefreshing(true)
            }
            aepsLedgerHistory(
                userModel.cus_id,
                AppCommonMethods.convertDateFormat(
                    "dd MMM yyyy",
                    "yyyy-MM-dd", root.tvFromDateRecHistory.text.toString()
                ).toString(),
                AppCommonMethods.convertDateFormat(
                    "dd MMM yyyy",
                    "yyyy-MM-dd", root.tvToDateRecHistory.text.toString()
                ).toString(),
                AppPrefs.getStringPref("deviceId", requireContext()).toString(),
                AppPrefs.getStringPref("deviceName", requireContext()).toString(),
                userModel.cus_pin,
                userModel.cus_pass,
                userModel.cus_mobile, userModel.cus_type
            )
            root.mSwipeRefresh.setRefreshing(false)

        })



        aepsLedgerHistory(
            userModel.cus_id,
            AppCommonMethods.convertDateFormat(
                "dd MMM yyyy",
                "yyyy-MM-dd", root.tvFromDateRecHistory.text.toString()
            ).toString(),
            AppCommonMethods.convertDateFormat(
                "dd MMM yyyy",
                "yyyy-MM-dd", root.tvToDateRecHistory.text.toString()
            ).toString(),
            AppPrefs.getStringPref("deviceId", requireContext()).toString(),
            AppPrefs.getStringPref("deviceName", requireContext()).toString(),
            userModel.cus_pin,
            userModel.cus_pass,
            userModel.cus_mobile, userModel.cus_type
        )

        root.rvDisputeHistory.apply {

            layoutManager = LinearLayoutManager(requireContext())
            aepsHistoryAdapter = AepsLedgerHistoryAdapter(
                context, aepsHistoryModelArrayList
            )
            root.rvDisputeHistory.adapter = aepsHistoryAdapter
        }

        root.tvFromDateRecHistory.setOnClickListener {
            FromdatePicker()
        }

        root.tvToDateRecHistory.setOnClickListener {
            TodatePicker()
        }
      
      
        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SalesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SalesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun aepsLedgerHistory(
        cus_id: String,
        fromDate: String,
        toDate : String,
        deviceId : String, deviceName : String,pin : String,
        pass : String, cus_mobile : String, cus_type : String
    ) {
        root.progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(requireContext()).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(requireContext(), AEPS_LEDGER_HISTORY_REPORT, this)
            mAPIcall.aepsLedgerHistory(cus_id, fromDate, toDate,deviceId,deviceName, pin, pass, cus_mobile, cus_type)
        } else {

            Toast.makeText(requireContext(), "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(AEPS_LEDGER_HISTORY_REPORT)) {
            aepsHistoryModelArrayList.clear()
            Log.e("AEPS_LEDGER_HISTORY", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status)
            if (status.contains("true")) {

                root.progress_bar.visibility = View.INVISIBLE

                val cast = jsonObject.getJSONArray("result")

                aepsHistoryModelArrayList.clear()

                for (i in 0 until cast.length()) {
                    val notifyObjJson = cast.getJSONObject(i)
                    val aeps_id = notifyObjJson.getString("aeps_txn_id")
                    Log.e("aeps_id ", aeps_id)
                    val aepsmodel = Gson()
                        .fromJson(
                            notifyObjJson.toString(),
                            AepsLedgerHistoryModel::class.java
                        )


                    aepsHistoryModelArrayList.add(aepsmodel)
                }

                root.rvDisputeHistory.adapter!!.notifyDataSetChanged()


            } else {
                root.progress_bar.visibility = View.INVISIBLE


            }
        }
    }



    fun FromdatePicker() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var dpd =
            DatePickerDialog(requireContext(), { view, mYear, mMonth, mDay ->
                val mmMonth = mMonth + 1
                val date = "$mDay/$mmMonth/$mYear"

                root.tvFromDateRecHistory.setText(
                    AppCommonMethods.convertDateFormat(
                        "dd/MM/yyyy",
                        "dd MMM yyyy", date
                    ).toString()
                )
                compareTwoDates(
                    root.tvFromDateRecHistory.text.toString(),
                    root.tvToDateRecHistory.text.toString()
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
            DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val mmMonth = mMonth + 1
                val date = "$mDay/$mmMonth/$mYear"

                root.tvToDateRecHistory.setText(
                    AppCommonMethods.convertDateFormat(
                        "dd/MM/yyyy",
                        "dd MMM yyyy", date
                    ).toString()
                )


                compareTwoDates(
                    root.tvFromDateRecHistory.text.toString(),
                    root.tvToDateRecHistory.text.toString()
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
                aepsLedgerHistory(
                    userModel.cus_id,
                    AppCommonMethods.convertDateFormat(
                        "dd MMM yyyy",
                        "yyyy-MM-dd", root.tvFromDateRecHistory.text.toString()
                    ).toString(),
                    AppCommonMethods.convertDateFormat(
                        "dd MMM yyyy",
                        "yyyy-MM-dd", root.tvToDateRecHistory.text.toString()
                    ).toString(),
                    AppPrefs.getStringPref("deviceId", requireContext()).toString(),
                    AppPrefs.getStringPref("deviceName", requireContext()).toString(),
                    userModel.cus_pin,
                    userModel.cus_pass,
                    userModel.cus_mobile, userModel.cus_type
                )


            } else {
                requireActivity().toast("Invalid Date")
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
        aepsLedgerHistory(
            userModel.cus_id,
            AppCommonMethods.convertDateFormat(
                "dd MMM yyyy",
                "yyyy-MM-dd", root.tvFromDateRecHistory.text.toString()
            ).toString(),
            AppCommonMethods.convertDateFormat(
                "dd MMM yyyy",
                "yyyy-MM-dd", root.tvToDateRecHistory.text.toString()
            ).toString(),
            AppPrefs.getStringPref("deviceId", requireContext()).toString(),
            AppPrefs.getStringPref("deviceName", requireContext()).toString(),
            userModel.cus_pin,
            userModel.cus_pass,
            userModel.cus_mobile, userModel.cus_type
        )
        root.mSwipeRefresh.setRefreshing(false)

    }



}