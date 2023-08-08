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
import com.playplexmatm.activity.itemlist.AddEditProductActivity
import com.playplexmatm.adapter.ProductAdapter
import com.playplexmatm.model.product.ProductModel
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


class ProductFragment : Fragment(), View.OnClickListener, ProductAdapter.onClick {

    lateinit var pbLoadData: ProgressBar
    lateinit var rvProducts: RecyclerView
    lateinit var tvNoProducts: TextView
    lateinit var apiInterface: ApiInterface
    lateinit var productAdapter: ProductAdapter
    lateinit var tvNewProducts: TextView
    private var productArrayList: ArrayList<ProductModel> = arrayListOf()

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
        val view = inflater.inflate(R.layout.fragment_product, container, false)
        initUI(view)
        return view
    }

    private fun initUI(view: View) {
        apiInterface = Apiclient(requireContext()).getClient()!!.create(ApiInterface::class.java)
        pbLoadData = view.findViewById(R.id.pbLoadData)
        rvProducts = view.findViewById(R.id.rvProducts)
        tvNewProducts = view.findViewById(R.id.tvNewProducts)
        tvNoProducts = view.findViewById(R.id.tvNoProducts)
        pbBottomLoadData = view.findViewById(R.id.pbBottomLoadData)

        if (InternetConnection.checkConnection(requireContext())) {
            mNetworkCallProductsAPI(currentPage)
        } else {
            Toast.makeText(
                requireContext(),
                requireContext().resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        addListner()

        setAdapter(productArrayList)
        setPaginationData()
    }

    private fun addListner() {
        tvNewProducts.setOnClickListener(this)
    }

    private fun setPaginationData() {
        rvProducts.addOnScrollListener(object :
            PaginationScrollListener(rvProducts.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallProductsAPI(currentPage)
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


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvNewProducts -> {
                startActivityForResult(
                    Intent(requireContext(), AddEditProductActivity::class.java)
                        .putExtra(Constants.mFrom, Constants.isAdd), 201
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK) {
                currentPage = 1
                mNetworkCallProductsAPI(currentPage)
            }
        }
    }

    private fun mNetworkCallProductsAPI(newcurrentPage: Int) {
        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            productArrayList.clear()
        }
        val call = apiInterface.getProducts("id desc","",offset,10)
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
                            productArrayList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<ProductModel?>?>() {}.type
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

                            if (productArrayList.size > 0) {
                                rvProducts.visibility = View.VISIBLE
                                tvNoProducts.visibility = View.GONE
                                setAdapter(productArrayList)
                            } else {
                                if (currentPage == 1) {
                                    rvProducts.visibility = View.GONE
                                    tvNoProducts.visibility = View.VISIBLE
                                }

                            }

                        } else {
                            if (currentPage == 1) {
                                rvProducts.visibility = View.GONE
                                tvNoProducts.visibility = View.VISIBLE
                            }

                            Toast.makeText(
                                requireContext(), jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
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

    private fun setAdapter(productArrayList: ArrayList<ProductModel>) {
        rvProducts.layoutManager = LinearLayoutManager(requireContext())
        rvProducts.setHasFixedSize(true)
        productAdapter = ProductAdapter(requireContext(), productArrayList, this)
        rvProducts.adapter = productAdapter
        productAdapter!!.notifyDataSetChanged()
    }

    override fun onItemClick(productModel: ProductModel, position: Int) {
        startActivityForResult(
            Intent(requireContext(), AddEditProductActivity::class.java)
                .putExtra(Constants.mFrom, Constants.isEdit)
                .putExtra(Constants.productModel, Gson().toJson(productModel)), 201
        )
    }

    override fun onDeleteClick(productModel: ProductModel, position: Int) {
        openProductDialog(productModel, position)
    }

    private fun openProductDialog(productModel: ProductModel, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to Delete Product?")
        builder.setPositiveButton("YES") { dialog, which ->
            if (InternetConnection.checkConnection(requireContext())) {
                deleteProduct(dialog, productModel, position)
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

    private fun deleteProduct(
        dialog: DialogInterface,
        productModel: ProductModel,
        position: Int
    ) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.deleteProducts(productModel.xid)
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
                            if (productArrayList.size > 0) {
                                productArrayList.removeAt(position)
                                currentPage = 1
                                mNetworkCallProductsAPI(currentPage)
                            } else {
                                rvProducts.visibility = View.GONE
                                tvNoProducts.visibility = View.VISIBLE
                            }
                        } else {
                            rvProducts.visibility = View.GONE
                            tvNoProducts.visibility = View.VISIBLE
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