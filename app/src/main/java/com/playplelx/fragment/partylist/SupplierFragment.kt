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
import com.playplelx.activity.partylist.AddEditSupplierActivity
import com.playplelx.adapter.CustomersAdapter
import com.playplelx.adapter.SupplierAdapter
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

class SupplierFragment : Fragment(), View.OnClickListener, SupplierAdapter.onClick {


    lateinit var apiInterface: ApiInterface
    lateinit var pbLoadData: ProgressBar
    lateinit var rvSuppliers: RecyclerView
    lateinit var tvNoSuppliers: TextView
    lateinit var tvNewSuppliers: TextView
    private var supplierArrayList: ArrayList<CustomerModel> = arrayListOf()
    lateinit var supplierAdapter: SupplierAdapter
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
        val view = inflater.inflate(R.layout.fragment_supplier, container, false)
        initUI(view)
        return view
    }

    private fun initUI(view: View) {
        apiInterface = Apiclient(requireContext()).getClient()!!.create(ApiInterface::class.java)
        pbLoadData = view.findViewById(R.id.pbLoadData)
        rvSuppliers = view.findViewById(R.id.rvSuppliers)
        tvNewSuppliers = view.findViewById(R.id.tvNewSuppliers)
        tvNoSuppliers = view.findViewById(R.id.tvNoSuppliers)
        pbBottomLoadData = view.findViewById(R.id.pbBottomLoadData)


        if (InternetConnection.checkConnection(requireContext())) {
            mNetworkCallGetSuppliersAPI(currentPage)
        } else {
            Toast.makeText(
                requireContext(),
                requireContext().resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        addListner()

        setAdapter(supplierArrayList)
        setPaginationData()
    }

    private fun addListner() {
        tvNewSuppliers.setOnClickListener(this)
    }

    private fun setPaginationData() {
        rvSuppliers.addOnScrollListener(object :
            PaginationScrollListener(rvSuppliers.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallGetSuppliersAPI(currentPage)
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


    private fun mNetworkCallGetSuppliersAPI(newcurrentPage: Int) {
        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            supplierArrayList.clear()
        }
        val call = apiInterface.getSuppliers("id", "", offset, 10)
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
                            supplierArrayList.addAll(
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




                            if (supplierArrayList.size > 0) {
                                rvSuppliers.visibility = View.VISIBLE
                                tvNoSuppliers.visibility = View.GONE
                                setAdapter(supplierArrayList)
                            } else {
                                if (currentPage == 1) {
                                    rvSuppliers.visibility = View.GONE
                                    tvNoSuppliers.visibility = View.VISIBLE
                                }

                            }
                        } else {
                            if (currentPage == 1) {
                                rvSuppliers.visibility = View.GONE
                                tvNoSuppliers.visibility = View.VISIBLE
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

    private fun setAdapter(supplierArrayList: ArrayList<CustomerModel>) {
        rvSuppliers.layoutManager = LinearLayoutManager(requireContext())
        rvSuppliers.setHasFixedSize(true)
        supplierAdapter = SupplierAdapter(requireContext(), supplierArrayList, this)
        rvSuppliers.adapter = supplierAdapter
        supplierAdapter!!.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK) {
                currentPage = 1
                mNetworkCallGetSuppliersAPI(currentPage)
            }
        }
    }

    override fun onItemClick(customerModel: CustomerModel, position: Int) {
        startActivityForResult(
            Intent(requireContext(), AddEditSupplierActivity::class.java)
                .putExtra(Constants.customerModel, Gson().toJson(customerModel))
                .putExtra(Constants.mFrom, Constants.isEdit), 201
        )

    }

    override fun onDeleteClick(customerModel: CustomerModel, position: Int) {
        openCustomerDialog(customerModel, position)
    }


    private fun openCustomerDialog(customerModel: CustomerModel, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to Delete Supplier?")
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
        val call = apiInterface.deleteSuppliers(customerModel.xid)
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
                            if (supplierArrayList.size > 0) {
                                supplierArrayList.removeAt(position)
                                currentPage = 1
                                mNetworkCallGetSuppliersAPI(currentPage)
                            } else {
                                rvSuppliers.visibility = View.GONE
                                tvNoSuppliers.visibility = View.VISIBLE
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
            R.id.tvNewSuppliers -> {
                startActivityForResult(
                    Intent(requireContext(), AddEditSupplierActivity::class.java)
                        .putExtra(Constants.mFrom, Constants.isAdd), 201
                )
            }
        }
    }

}