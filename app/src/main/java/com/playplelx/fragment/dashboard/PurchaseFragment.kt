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
import com.playplelx.activity.AddEditPurchaseListActivity
import com.playplelx.activity.PurchaseListActivity
import com.playplelx.adapter.DashboardPurchaseAdapter
import com.playplelx.adapter.PurchaseAdapter
import com.playplelx.model.purchasemodel.PurchaseModel
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


class PurchaseFragment : Fragment() {


    lateinit var rvPurchase: RecyclerView
    lateinit var pbLoadData: ProgressBar
    lateinit var apiInterface: ApiInterface
    private var purchaseAraryList: ArrayList<PurchaseModel> = arrayListOf()
    lateinit var purchaseAdapter: DashboardPurchaseAdapter
    lateinit var tvNoPurchaseList: TextView
    lateinit var pbBottomLoadData: ProgressBar
    private var isloading: Boolean = false
    private var islastpage: Boolean = false
    private var currentPage = 1
    private var lastPage = 1
    private var offset: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_purchase, container, false)
        initUI(view)
        return view
    }

    private fun initUI(view: View) {
        apiInterface = Apiclient(requireContext()).getClient()!!.create(ApiInterface::class.java)
        pbLoadData = view.findViewById(R.id.pbLoadData)
        rvPurchase = view.findViewById(R.id.rvPurchase)
        pbBottomLoadData = view.findViewById(R.id.pbBottomLoadData)
        tvNoPurchaseList = view.findViewById(R.id.tvNoPurchaseList)

        if (InternetConnection.checkConnection(requireContext())) {
            mNetworkCallPurchaseListAPI(currentPage)
        } else {
            Toast.makeText(
                requireContext(),
                requireContext().resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        setAdapter(purchaseAraryList)

        setPaginationData()

    }


    private fun setPaginationData() {
        rvPurchase.addOnScrollListener(object :
            PaginationScrollListener(rvPurchase.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallPurchaseListAPI(currentPage)
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


    private fun mNetworkCallPurchaseListAPI(newcurrentPage: Int) {
        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            purchaseAraryList.clear()
        }
        val call = apiInterface.getPurchase()
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
                            purchaseAraryList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<PurchaseModel?>?>() {}.type
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

                            if (purchaseAraryList.size > 0) {
                                rvPurchase.visibility = View.VISIBLE
                                tvNoPurchaseList.visibility = View.GONE
                                setAdapter(purchaseAraryList)
                            } else {
                                if (currentPage == 1) {
                                    rvPurchase.visibility = View.GONE
                                    tvNoPurchaseList.visibility = View.VISIBLE

                                }

                            }

                        } else {
                            if (currentPage == 1) {
                                rvPurchase.visibility = View.GONE
                                tvNoPurchaseList.visibility = View.VISIBLE

                                Toast.makeText(
                                    requireContext(),
                                    jsonObject.optString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
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

    private fun setAdapter(purchaseAraryList: ArrayList<PurchaseModel>) {

        rvPurchase.layoutManager = LinearLayoutManager(requireContext())
        rvPurchase.setHasFixedSize(true)
        purchaseAdapter = DashboardPurchaseAdapter(requireContext(), purchaseAraryList)
        rvPurchase.adapter = purchaseAdapter
        purchaseAdapter.notifyDataSetChanged()
    }


}