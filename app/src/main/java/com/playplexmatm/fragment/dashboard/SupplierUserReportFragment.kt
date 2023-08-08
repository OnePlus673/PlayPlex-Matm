package com.playplexmatm.fragment.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.adapter.UserReportAdapter
import com.playplexmatm.model.UserReportModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.PaginationScrollListener
import com.playplexmatm.util.SimpleDividerItemDecoration
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.fragment_supplier_user_report.view.edtSearch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class SupplierUserReportFragment : Fragment(),View.OnClickListener {

    lateinit var rvUserReport: RecyclerView
    lateinit var pbLoadData: ProgressBar
    lateinit var apiInterface: ApiInterface
    private var userReportArrayList: ArrayList<UserReportModel> = arrayListOf()
    lateinit var userReportAdapter: UserReportAdapter
    lateinit var tvNoPurchaseList: TextView
    lateinit var pbBottomLoadData: ProgressBar
    private var isloading: Boolean = false
    private var islastpage: Boolean = false
    private var currentPage = 1
    private var lastPage = 1
    private var offset: Int = 0
    lateinit var tvNewPurchaseList: TextView

    lateinit var root: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_supplier_user_report, container, false)
        initUI(root)
        return root
    }

    private fun initUI(view: View) {
        apiInterface = Apiclient(requireContext()).getClient()!!.create(ApiInterface::class.java)
        pbLoadData = view.findViewById(R.id.pbLoadData)
        rvUserReport = view.findViewById(R.id.rvUserReport)
        pbBottomLoadData = view.findViewById(R.id.pbBottomLoadData)
        tvNoPurchaseList = view.findViewById(R.id.tvNoPurchaseList)
        tvNewPurchaseList = view.findViewById(R.id.tvNewPurchaseList)

        if (InternetConnection.checkConnection(requireContext())) {
            mNetworkCallUserReportAPI(currentPage)
        } else {
            Toast.makeText(
                requireContext(),
                requireContext().resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        addListner()
        setAdapter(userReportArrayList)

        setPaginationData()

    }
    private fun addListner(){
        tvNewPurchaseList.setOnClickListener(this)
    }


    private fun setPaginationData() {
        rvUserReport.addOnScrollListener(object :
            PaginationScrollListener(rvUserReport.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallUserReportAPI(currentPage)
                }
            }

            override fun isLoading(): Boolean {
                return isloading
            }

            override fun isLastPage(): Boolean {
                return islastpage
            }
        })
    }


    private fun mNetworkCallUserReportAPI(newcurrentPage: Int) {
        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            userReportArrayList.clear()
        }
        val call = apiInterface.getUserReportSupplier("id desc","",offset,10)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    Log.e("Response",response.toString())
                    pbLoadData.visibility = View.GONE
                    pbBottomLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            isloading = false
                            val data = jsonObject.optJSONArray("data")
                            userReportArrayList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<UserReportModel?>?>() {}.type
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

                            if (userReportArrayList.size > 0) {
                                rvUserReport.visibility = View.VISIBLE
                                tvNoPurchaseList.visibility = View.GONE
                                setAdapter(userReportArrayList)
                            } else {
                                if (currentPage == 1) {
                                    rvUserReport.visibility = View.GONE
                                    tvNoPurchaseList.visibility = View.VISIBLE

                                }
                            }
                        } else {
                            if (currentPage == 1) {
                                rvUserReport.visibility = View.GONE
                                tvNoPurchaseList.visibility = View.VISIBLE

                                Toast.makeText(
                                    requireContext(),
                                    jsonObject.optString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }

                        root.edtSearch.addTextChangedListener(object : TextWatcher {
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
                        Toast.makeText(
                            requireContext(),
                            requireContext().resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject = JSONObject(response.errorBody()!!.string())
                        Util(requireContext() as Activity).logOutAlertDialog(
                            requireContext(),
                            JsonObject.optString("message")
                        )
                    } catch (e: Exception) {

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setAdapter(userReportArrayList: ArrayList<UserReportModel>) {

        rvUserReport.layoutManager = LinearLayoutManager(requireContext())
        rvUserReport.setHasFixedSize(true)
        userReportAdapter = UserReportAdapter(requireContext(), userReportArrayList)
        rvUserReport.adapter = userReportAdapter
        rvUserReport.addItemDecoration(SimpleDividerItemDecoration(requireContext()))
        userReportAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK) {
                currentPage = 1
                mNetworkCallUserReportAPI(currentPage)
            }
        }
    }

    fun filter(text: String) {
        val temp: MutableList<UserReportModel> = ArrayList()
        for (d in userReportArrayList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.name.contains(text,ignoreCase = true) || d.phone.contains(text,ignoreCase = true)) {
                temp.add(d)
            }
        }
        //update recyclerview
        userReportAdapter.updateList(temp)
    }

    override fun onClick(p0: View?) {

    }

}