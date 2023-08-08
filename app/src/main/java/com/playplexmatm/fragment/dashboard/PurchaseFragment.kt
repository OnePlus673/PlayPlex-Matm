package com.playplexmatm.fragment.dashboard

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.activity.AddEditPurchaseListActivity
import com.playplexmatm.adapter.DashboardPurchaseAdapter
import com.playplexmatm.adapter.PurchaseAdapter
import com.playplexmatm.model.purchasemodel.PurchaseModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.PaginationScrollListener
import com.playplexmatm.util.SimpleDividerItemDecoration
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.fragment_purchase.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PurchaseFragment : Fragment(),View.OnClickListener, PurchaseAdapter.onClick,
    DashboardPurchaseAdapter.onClick {


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
    lateinit var tvNewPurchaseList: TextView

    lateinit var root: View

    var save = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_purchase, container, false)
        initUI(root)
        return root
    }

    private fun initUI(view: View) {
        apiInterface = Apiclient(requireContext()).getClient()!!.create(ApiInterface::class.java)
        pbLoadData = view.findViewById(R.id.pbLoadData)
        rvPurchase = view.findViewById(R.id.rvPurchase)
        pbBottomLoadData = view.findViewById(R.id.pbBottomLoadData)
        tvNoPurchaseList = view.findViewById(R.id.tvNoPurchaseList)
        tvNewPurchaseList = view.findViewById(R.id.tvNewPurchaseList)

        if (InternetConnection.checkConnection(requireContext())) {
            mNetworkCallPurchaseListAPI(currentPage)
        } else {
            Toast.makeText(
                requireContext(),
                requireContext().resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        addListner()
        setAdapter(purchaseAraryList)

        setPaginationData()

    }
    private fun addListner(){
        tvNewPurchaseList.setOnClickListener(this)
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
        val call = apiInterface.getPurchase("id desc","",offset,10,"")
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
        purchaseAdapter = DashboardPurchaseAdapter(requireContext(), purchaseAraryList,this)
        rvPurchase.adapter = purchaseAdapter
        rvPurchase.addItemDecoration(SimpleDividerItemDecoration(requireContext()))
        purchaseAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK) {
                currentPage = 1
                mNetworkCallPurchaseListAPI(currentPage)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tvNewPurchaseList->{
                startActivityForResult(
                    Intent(context, AddEditPurchaseListActivity::class.java),
                    201
                )

            }
        }
    }

    override fun onDeleteClick(purchaseModel: PurchaseModel, position: Int) {
        openSaleDialog(purchaseModel, position)

    }

    override fun onPrintClick(purchaseModel: PurchaseModel, position: Int) {
        if(purchaseModel != null) {
            save = false
            mNetworkCallOrderHtmlAPI(purchaseModel.xid)
        }
    }

    override fun onShareClick(purchaseModel: PurchaseModel, position: Int) {
        if(purchaseModel != null) {
            save = true
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, "URL")
            i.putExtra(Intent.EXTRA_TEXT, "https://pospayplex.com/api/v1/pdf/${purchaseModel.unique_id}/en")
            startActivity(Intent.createChooser(i, "Share URL"))
        }
    }

    private fun openSaleDialog(purchaseModel: PurchaseModel, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to Delete Purchase?")
        builder.setPositiveButton("YES") { dialog, which ->
            if (InternetConnection.checkConnection(requireContext())) {
                deletepurchaseList(dialog, purchaseModel, position)
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.str_check_internet_connections),
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

    private fun deletepurchaseList(
        dialog: DialogInterface,
        purchaseModel: PurchaseModel,
        position: Int
    ) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.deletePurchase(purchaseModel.xid)
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
                            if (purchaseAraryList.size > 0) {
                                purchaseAraryList.removeAt(position)
                                currentPage = 1
                                mNetworkCallPurchaseListAPI(currentPage)
                            } else {
                                rvPurchase.visibility = View.GONE
                                tvNoPurchaseList.visibility = View.VISIBLE
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
                            rvPurchase.visibility = View.GONE
                            tvNoPurchaseList.visibility = View.VISIBLE
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
                        Util(requireActivity()).logOutAlertDialog(requireContext(),JsonObject.optString("message"))
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

    fun filter(text: String) {
        val temp: MutableList<PurchaseModel> = ArrayList()
        for (d in purchaseAraryList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.user.name.contains(text,ignoreCase = true) || d.invoice_number.contains(text,ignoreCase = true)) {
                temp.add(d)
            }
        }
        //update recyclerview
        purchaseAdapter.updateList(temp)
    }

    private fun mNetworkCallOrderHtmlAPI(xid: String) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.orderHtml(xid)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONObject("data")
                            Log.e("data",data.getString("html"))
//                            startActivity(Intent(mContext,WebviewActivity::class.java).putExtra("data",data.optString("html")))
                            if(save == false) {
                                setData(data.optString("html"))
                            } else {
//                                saveFun(data.getString("html"))

                            }
                        } else {
                            Toast.makeText(
                                requireContext(), jsonObject.optString("message"), Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        pbLoadData.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun createWebPrintJob(webView: WebView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val printManager = requireActivity().getSystemService(Context.PRINT_SERVICE) as PrintManager
            val printAdapter = webView.createPrintDocumentAdapter("MyDocument")
            printManager.print("My Print Job", printAdapter, PrintAttributes.Builder().build())
        } else {
            // SHOW MESSAGE or UPDATE UI
        }
    }

    private fun setData(data: String) {
        val webView = WebView(requireContext())
        webView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?, request: WebResourceRequest?
            ): Boolean {
                return false;
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                createWebPrintJob(view!!)

            }
        })
        val myHtml: String = data
        webView.loadDataWithBaseURL(null, myHtml, "text/HTML", "UTF-8", null)
    }






}