package com.playplexmatm.fragment.itemlist

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
import com.playplexmatm.R
import com.playplexmatm.activity.itemlist.AddEditBrandActivity
import com.playplexmatm.adapter.BrandAdapter
import com.playplexmatm.model.brand.BrandModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.Constants
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.PaginationScrollListener
import com.playplexmatm.util.Util
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class BrandFragment : Fragment(), View.OnClickListener, BrandAdapter.onClick {

    lateinit var pbLoadData: ProgressBar
    lateinit var rvBrand: RecyclerView
    lateinit var tvNoBrand: TextView
    lateinit var apiInterface: ApiInterface
    lateinit var brandAdapter: BrandAdapter
    lateinit var tvNewBrand: TextView
    private var brandArrayList: ArrayList<BrandModel> = arrayListOf()

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
        val view = inflater.inflate(R.layout.fragment_brand, container, false)
        initUI(view)
        return view
    }

    private fun initUI(view: View) {
        apiInterface = Apiclient(requireContext()).getClient()!!.create(ApiInterface::class.java)
        rvBrand = view.findViewById(R.id.rvBrand)
        tvNoBrand = view.findViewById(R.id.tvNoBrand)
        pbLoadData = view.findViewById(R.id.pbLoadData)
        tvNewBrand = view.findViewById(R.id.tvNewBrand)
        pbBottomLoadData = view.findViewById(R.id.pbBottomLoadData)
        addListner()

        if (InternetConnection.checkConnection(context)) {
            mNetworkCallBrandAPI(currentPage)
        } else {
            Toast.makeText(
                context,
                requireContext().resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        setAdapter(brandArrayList)

        setPaginationData()
    }

    private fun addListner() {
        tvNewBrand.setOnClickListener(this)
    }


    private fun setPaginationData() {
        rvBrand.addOnScrollListener(object :
            PaginationScrollListener(rvBrand.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallBrandAPI(currentPage)
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


    private fun mNetworkCallBrandAPI(newcurrentPage: Int) {
        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            brandArrayList.clear()
        }
        val call = apiInterface.getBrand("id desc", "", offset, 10)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    pbBottomLoadData.visibility = View.GONE
                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {
                        isloading = false
                        val data = jsonObject.optJSONArray("data")
                        brandArrayList.addAll(
                            Gson().fromJson(
                                data.toString(),
                                object : TypeToken<java.util.ArrayList<BrandModel?>?>() {}.type
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

                        if (brandArrayList.size > 0) {
                            rvBrand.visibility = View.VISIBLE
                            tvNoBrand.visibility = View.GONE
                            setAdapter(brandArrayList)
                        } else {
                            if (newcurrentPage == 1) {
                                rvBrand.visibility = View.GONE
                                tvNoBrand.visibility = View.VISIBLE
                            }

                        }
                    } else {
                        if (newcurrentPage == 1) {
                            rvBrand.visibility = View.GONE
                            tvNoBrand.visibility = View.VISIBLE
                        }

                    }
                }else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        pbBottomLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(context!! as Activity).logOutAlertDialog(context!!,JsonObject.optString("message"))
                    }catch (e: Exception){

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                pbBottomLoadData.visibility = View.GONE
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun setAdapter(brandArrayList: ArrayList<BrandModel>) {

        rvBrand.layoutManager = LinearLayoutManager(requireContext())
        rvBrand.setHasFixedSize(true)
        brandAdapter = BrandAdapter(requireContext(), brandArrayList, this)
        rvBrand.adapter = brandAdapter
        brandAdapter!!.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvNewBrand -> {
                val intent = Intent(context, AddEditBrandActivity::class.java)
                intent.putExtra(Constants.mFrom, Constants.isAdd)
                startActivityForResult(intent, 201)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK) {
                currentPage = 1
                mNetworkCallBrandAPI(currentPage)
            }
        }
    }


    override fun onItemClick(brandModel: BrandModel, position: Int) {
        startActivityForResult(
            Intent(context, AddEditBrandActivity::class.java)
                .putExtra(Constants.mFrom, Constants.isEdit)
                .putExtra(Constants.brandModel, Gson().toJson(brandModel)), 201
        )
    }

    override fun onDeleteClick(brandModel: BrandModel, position: Int) {
        openBrandDialog(brandModel, position)
    }


    private fun openBrandDialog(brandModel: BrandModel, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to Delete Brand?")
        builder.setPositiveButton("YES") { dialog, which ->
            if (InternetConnection.checkConnection(requireContext())) {
                deleteBrand(dialog, brandModel, position)
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().resources.getString(R.string.str_check_internet_connections),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        builder.setNegativeButton(
            "NO"
        ) { dialog, which -> // Do nothing
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun deleteBrand(
        dialog: DialogInterface,
        brandModel: BrandModel,
        position: Int
    ) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.deleteBrand(brandModel.xid)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonobject = JSONObject(response.body().toString())
                        if (jsonobject.optBoolean("status")) {
                            Toast.makeText(
                                requireContext(),
                                jsonobject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                            if (brandArrayList.size > 0) {
                                brandArrayList.removeAt(position)
                                currentPage = 1
                                mNetworkCallBrandAPI(currentPage)
                            } else {
                                rvBrand.visibility = View.GONE
                                tvNoBrand.visibility = View.VISIBLE
                            }
                        } else {
                            rvBrand.visibility = View.GONE
                            tvNoBrand.visibility = View.VISIBLE
                            Toast.makeText(
                                requireContext(), jsonobject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(context!! as Activity).logOutAlertDialog(context!!,JsonObject.optString("message"))
                    }catch (e: Exception){

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }


}