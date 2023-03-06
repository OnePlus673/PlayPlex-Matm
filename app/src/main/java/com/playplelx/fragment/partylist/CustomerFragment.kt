package com.playplelx.fragment.partylist

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
import com.playplelx.activity.partylist.AddEditCustomerActivity
import com.playplelx.adapter.BrandAdapter
import com.playplelx.adapter.CustomersAdapter
import com.playplelx.model.brand.BrandModel
import com.playplelx.model.customers.CustomerModel
import com.playplelx.network.ApiInterface
import com.playplelx.network.Apiclient
import com.playplelx.util.Constants
import com.playplelx.util.InternetConnection
import com.playplelx.util.PaginationScrollListener
import com.playplelx.util.Util
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.ArrayList


class CustomerFragment : Fragment(), CustomersAdapter.onClick, View.OnClickListener {

    lateinit var apiInterface: ApiInterface
    lateinit var pbLoadData: ProgressBar
    lateinit var rvCustomers: RecyclerView
    lateinit var tvNoCustomer: TextView
    lateinit var tvNewCustomer: TextView
    private var customerArrayList: ArrayList<CustomerModel> = arrayListOf()
    lateinit var customersAdapter: CustomersAdapter

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
        val view = inflater.inflate(R.layout.fragment_customer, container, false)
        initUI(view)
        return view
    }

    private fun initUI(view: View) {
        apiInterface = Apiclient(requireContext()).getClient()!!.create(ApiInterface::class.java)
        pbLoadData = view.findViewById(R.id.pbLoadData)
        rvCustomers = view.findViewById(R.id.rvCustomers)
        tvNewCustomer = view.findViewById(R.id.tvNewCustomer)
        tvNoCustomer = view.findViewById(R.id.tvNoCustomer)
        pbBottomLoadData = view.findViewById(R.id.pbBottomLoadData)

        if (InternetConnection.checkConnection(requireContext())) {
            mNetworkCallGetCustomerAPI(currentPage)
        } else {
            Toast.makeText(
                requireContext(),
                requireContext().resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        addListner()

        setAdapter(customerArrayList)

        setPaginationData()
    }

    private fun addListner() {
        tvNewCustomer.setOnClickListener(this)
    }


    private fun setPaginationData() {
        rvCustomers.addOnScrollListener(object :
            PaginationScrollListener(rvCustomers.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallGetCustomerAPI(currentPage)
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


    private fun mNetworkCallGetCustomerAPI(newcurrentPage: Int) {
        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            customerArrayList.clear()
        }
        val call = apiInterface.getCustomers("id","",offset,10)
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
                            customerArrayList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object : TypeToken<ArrayList<CustomerModel?>?>() {}.type
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


                            if (customerArrayList.size > 0) {
                                rvCustomers.visibility = View.VISIBLE
                                tvNoCustomer.visibility = View.GONE
                                setAdapter(customerArrayList)
                            } else {
                                if (currentPage==1){
                                    rvCustomers.visibility = View.GONE
                                    tvNoCustomer.visibility = View.VISIBLE
                                }

                            }
                        } else {
                            if (currentPage==1){
                                rvCustomers.visibility = View.GONE
                                tvNoCustomer.visibility = View.VISIBLE
                            }

                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            requireContext().resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
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
                pbBottomLoadData.visibility = View.GONE
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()

            }

        })

    }

    private fun setAdapter(customerArrayList: ArrayList<CustomerModel>) {
        rvCustomers.layoutManager = LinearLayoutManager(requireContext())
        rvCustomers.setHasFixedSize(true)
        customersAdapter = CustomersAdapter(requireContext(), customerArrayList, this)
        rvCustomers.adapter = customersAdapter
        customersAdapter!!.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK) {
                currentPage=1
                mNetworkCallGetCustomerAPI(currentPage)
            }
        }
    }

    override fun onItemClick(customerModel: CustomerModel, position: Int) {
        startActivityForResult(
            Intent(requireContext(), AddEditCustomerActivity::class.java)
                .putExtra(Constants.customerModel, Gson().toJson(customerModel))
                .putExtra(Constants.mFrom, Constants.isEdit), 201
        )

    }

    override fun onDeleteClick(customerModel: CustomerModel, position: Int) {
        openCustomerDialog(customerModel, position)
    }


    private fun openCustomerDialog(customerModel: CustomerModel, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to Delete Customer?")
        builder.setPositiveButton("YES") { dialog, which ->
            if (InternetConnection.checkConnection(requireContext())) {
                deleteCustomer(dialog, customerModel, position)
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

    private fun deleteCustomer(
        dialog: DialogInterface,
        customerModel: CustomerModel,
        position: Int
    ) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.deleteCustomers(customerModel.xid)
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
                            if (customerArrayList.size > 0) {
                                customerArrayList.removeAt(position)
                                currentPage=1
                                mNetworkCallGetCustomerAPI(currentPage)
                            } else {
                                rvCustomers.visibility = View.GONE
                                tvNoCustomer.visibility = View.VISIBLE
                            }
                        } else {

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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvNewCustomer -> {
                startActivityForResult(
                    Intent(requireContext(), AddEditCustomerActivity::class.java)
                        .putExtra(Constants.mFrom, Constants.isAdd), 201
                )
            }
        }
    }


}