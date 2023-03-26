package com.playplelx.fragment.dashboard

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplelx.R
import com.playplelx.activity.pos.saleList.AddEditSaleListActivity
import com.playplelx.adapter.DashboardSaleListAdapter
import com.playplelx.adapter.SaleListAdapter
import com.playplelx.model.saleList.SaleListModel
import com.playplelx.network.ApiInterface
import com.playplelx.network.Apiclient
import com.playplelx.util.InternetConnection
import com.playplelx.util.PaginationScrollListener
import com.playplelx.util.Util
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class SaleFragment : Fragment(), View.OnClickListener {

    lateinit var rvSaleList: RecyclerView
    lateinit var pbLoadData: ProgressBar
    lateinit var pbBottomLoadData: ProgressBar
    lateinit var apiInterface: ApiInterface
    private var salesArrayList: ArrayList<SaleListModel> = arrayListOf()
    lateinit var saleListAdapter: DashboardSaleListAdapter

    private var isloading: Boolean = false
    private var islastpage: Boolean = false
    private var currentPage = 1
    private var lastPage = 1
    private var offset: Int = 0
    lateinit var tvNewSaleList: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sale, container, false)
        initUI(view)
        return view
    }


    private fun initUI(view: View) {
        apiInterface = Apiclient(requireContext()).getClient()!!.create(ApiInterface::class.java)
        pbLoadData = view.findViewById(R.id.pbLoadData)
        rvSaleList = view.findViewById(R.id.rvSaleList)
        pbBottomLoadData = view.findViewById(R.id.pbBottomLoadData)
        tvNewSaleList = view.findViewById(R.id.tvNewSaleList)

        if (InternetConnection.checkConnection(requireContext())) {
            mNetworkCallSaleListAPI(currentPage)
        } else {
            Toast.makeText(
                requireContext(),
                requireContext().resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        addListner()

        setAdapter(salesArrayList)

        setPaginationData()
    }

    private fun addListner() {
        tvNewSaleList.setOnClickListener(this)
    }


    private fun setPaginationData() {
        rvSaleList.addOnScrollListener(object :
            PaginationScrollListener(rvSaleList.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallSaleListAPI(currentPage)
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


    private fun mNetworkCallSaleListAPI(newcurrentPage: Int) {

        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            salesArrayList.clear()
        }


        val call = apiInterface.getSales("id", "", offset, 10)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    pbBottomLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            isloading = false
                            val data = jsonObject.optJSONArray("data")
                            salesArrayList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<SaleListModel?>?>() {}.type
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


                            if (salesArrayList.size > 0) {
                                rvSaleList.visibility = View.VISIBLE
                                saleListAdapter.notifyDataSetChanged()
                            } else {

                                if (currentPage == 1) {
                                    rvSaleList.visibility = View.GONE
                                }


                            }

                        } else {
                            if (currentPage == 1) {
                                rvSaleList.visibility = View.GONE
                                Toast.makeText(
                                    context!!,
                                    jsonObject.optString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    } else {
                        Toast.makeText(
                            context!!,
                            context!!.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } /*else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject = JSONObject(response.errorBody()!!.string())
                        Util(context!! as Activity).logOutAlertDialog(
                            context!!,
                            JsonObject.optString("message")
                        )
                    } catch (e: Exception) {

                    }
                }*/
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                pbBottomLoadData.visibility = View.GONE
                Toast.makeText(context!!, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setAdapter(saleList: ArrayList<SaleListModel>) {

        rvSaleList.layoutManager = LinearLayoutManager(requireContext())
        rvSaleList.setHasFixedSize(true)
        saleListAdapter = DashboardSaleListAdapter(requireContext(), saleList)
        rvSaleList.adapter = saleListAdapter
        saleListAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvNewSaleList -> {
                startActivityForResult(Intent(context, AddEditSaleListActivity::class.java), 201)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK) {
                currentPage = 1;
                mNetworkCallSaleListAPI(currentPage)
            }
        }
    }


}