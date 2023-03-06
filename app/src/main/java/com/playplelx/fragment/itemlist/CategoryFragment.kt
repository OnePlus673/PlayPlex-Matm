package com.playplelx.fragment.itemlist

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplelx.R
import com.playplelx.activity.itemlist.AddEditCategoryActivity
import com.playplelx.adapter.CategoryAdapter
import com.playplelx.model.category.CategoryModel
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

class CategoryFragment : Fragment(), View.OnClickListener, CategoryAdapter.onClick {

    lateinit var pbLoadData: ProgressBar
    lateinit var rvCategory: RecyclerView
    lateinit var tvNoCategory: TextView
    lateinit var apiInterface: ApiInterface
    lateinit var categoryAdapter: CategoryAdapter
    lateinit var tvNewCategory: TextView
    private var categoryArrayList: ArrayList<CategoryModel> = arrayListOf()
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
        val view = inflater.inflate(R.layout.fragment_category, container, false)
        initUI(view)
        return view
    }

    private fun initUI(view: View) {
        apiInterface = Apiclient(requireContext()).getClient()!!.create(ApiInterface::class.java)
        rvCategory = view.findViewById(R.id.rvCategory)
        tvNoCategory = view.findViewById(R.id.tvNoCategory)
        tvNewCategory = view.findViewById(R.id.tvNewCategory)
        pbLoadData = view.findViewById(R.id.pbLoadData)
        pbBottomLoadData = view.findViewById(R.id.pbBottomLoadData)


        addListner()

        if (InternetConnection.checkConnection(context)) {
            mNetworkCallCategoryAPI(currentPage)
        } else {
            Toast.makeText(
                context,
                requireContext().resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        setAdapter(categoryArrayList)

        setPaginationData()
    }


    private fun setPaginationData() {
        rvCategory.addOnScrollListener(object :
            PaginationScrollListener(rvCategory.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallCategoryAPI(currentPage)
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


    private fun addListner() {
        tvNewCategory.setOnClickListener(this)


    }

    private fun mNetworkCallCategoryAPI(newcurrentPage: Int) {
        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            categoryArrayList.clear()
        }

        val call = apiInterface.getCategory("id", "=", offset, 10)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    pbBottomLoadData.visibility = View.GONE
                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {
                        isloading = false
                        val data = jsonObject.optJSONArray("data")
                        categoryArrayList.addAll(
                            Gson().fromJson(
                                data.toString(),
                                object : TypeToken<java.util.ArrayList<CategoryModel?>?>() {}.type
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



                        if (categoryArrayList.size > 0) {
                            rvCategory.visibility = View.VISIBLE
                            tvNoCategory.visibility = View.GONE
                            setAdapter(categoryArrayList)
                        } else {
                            if (currentPage == 1) {
                                rvCategory.visibility = View.GONE
                                tvNoCategory.visibility = View.VISIBLE
                            }

                        }
                    } else {
                        if (currentPage == 1) {
                            rvCategory.visibility = View.GONE
                            tvNoCategory.visibility = View.VISIBLE
                        }

                    }
                } else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        pbBottomLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(context!! as Activity).logOutAlertDialog(context!!,JsonObject.optString("message"))
                    }catch (e:Exception){

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

    private fun setAdapter(categoryArrayList: ArrayList<CategoryModel>) {

        rvCategory.layoutManager = LinearLayoutManager(requireContext())
        rvCategory.setHasFixedSize(true)
        categoryAdapter = CategoryAdapter(requireContext(), categoryArrayList, this)
        rvCategory.adapter = categoryAdapter
        categoryAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvNewCategory -> {
                val intent = Intent(context, AddEditCategoryActivity::class.java)
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
                mNetworkCallCategoryAPI(currentPage)
            }
        }
    }

    override fun onItemClick(categoryModel: CategoryModel, position: Int) {
        startActivityForResult(
            Intent(context, AddEditCategoryActivity::class.java)
                .putExtra(Constants.mFrom, Constants.isEdit)
                .putExtra(Constants.categoryModel, Gson().toJson(categoryModel)), 201
        )
    }

    override fun onDeleteClick(categoryModel: CategoryModel, position: Int) {
        openCategoryDialog(categoryModel, position)
    }

    private fun openCategoryDialog(categoryModel: CategoryModel, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to Delete Category?")
        builder.setPositiveButton("YES") { dialog, which ->
            if (InternetConnection.checkConnection(requireContext())) {
                deleteCategory(dialog, categoryModel, position)
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

    private fun deleteCategory(
        dialog: DialogInterface,
        categoryModel: CategoryModel,
        position: Int
    ) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.deleteCategory(categoryModel.xid)
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
                            if (categoryArrayList.size > 0) {
                                categoryArrayList.removeAt(position)
                                currentPage = 1
                                mNetworkCallCategoryAPI(currentPage)
                            } else {
                                rvCategory.visibility = View.GONE
                                tvNoCategory.visibility = View.VISIBLE
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

}