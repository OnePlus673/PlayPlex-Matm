package com.playplexmatm.fragment.dashboard

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.activity.pos.saleList.AddSaleActivity
import com.playplexmatm.adapter.DashboardSaleListAdapter
import com.playplexmatm.model.saleList.SaleListModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.PaginationScrollListener
import com.playplexmatm.util.SimpleDividerItemDecoration
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.fragment_sale.*
import kotlinx.android.synthetic.main.fragment_sale.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class SaleFragment : Fragment(), View.OnClickListener, DashboardSaleListAdapter.onClick {

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

    lateinit var root: View

    // for our PDF file.
    var pageHeight = 1120
    var pagewidth = 792

    // creating a bitmap variable
    // for storing our images
    lateinit var bmp: Bitmap
    var scaledbmp: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_sale, container, false)

        initUI(root)
        return root
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


        val call = apiInterface.getSales("id desc", "", offset, 10,"")
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
        saleListAdapter = DashboardSaleListAdapter(requireContext(), saleList, this)
        rvSaleList.adapter = saleListAdapter
        rvSaleList.adapter = saleListAdapter
        rvSaleList.addItemDecoration(SimpleDividerItemDecoration(requireContext()))
        saleListAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvNewSaleList -> {
                startActivityForResult(Intent(context, AddSaleActivity::class.java), 201)

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

    override fun onDeleteClick(saleListModel: SaleListModel, position: Int) {
        openSaleDialog(saleListModel, position)
    }

    override fun onPrintClick(saleListModel: SaleListModel, position: Int) {
        if(saleListModel != null) {
            mNetworkCallOrderHtmlAPI(saleListModel.xid)
        }
    }

    override fun onShareClick(saleListModel: SaleListModel, position: Int) {
        if(saleListModel != null) {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, "URL")
            i.putExtra(Intent.EXTRA_TEXT, "https://pospayplex.com/api/v1/pdf/${saleListModel.unique_id}/en")
            startActivity(Intent.createChooser(i, "Share URL"))
        }
    }


    private fun openSaleDialog(saleListModel: SaleListModel, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to Delete Sale List?")
        builder.setPositiveButton("YES") { dialog, which ->
            if (InternetConnection.checkConnection(requireContext())) {
                deleteSaleList(dialog, saleListModel, position)
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


    private fun deleteSaleList(
        dialog: DialogInterface,
        saleListModel: SaleListModel,
        position: Int
    ) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.deleteSales(saleListModel.xid)
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
                            if (salesArrayList.size > 0) {
                                salesArrayList.removeAt(position)
                                currentPage=1
                                mNetworkCallSaleListAPI(currentPage)
                            } else {
                                rvSaleList.visibility = View.GONE
                                tvNoSaleList.visibility = View.VISIBLE
                            }
                        } else {
                            rvSaleList.visibility = View.GONE
                            tvNoSaleList.visibility = View.VISIBLE
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
        val temp: MutableList<SaleListModel> = ArrayList()
        for (d in salesArrayList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.user.name.contains(text,ignoreCase = true) || d.invoice_number.contains(text,ignoreCase = true)) {
                temp.add(d)
            }
        }
        //update recyclerview
        saleListAdapter.updateList(temp)
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
                            setData(data.optString("html"))
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

    private fun generatePDF() {
        // creating an object variable
        // for our PDF document.
        val pdfDocument = PdfDocument()


        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.applogo);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);

        // two variables for paint "paint" is used
        // for drawing shapes and we will use "title"
        // for adding text in our PDF file.
        val paint = Paint()
        val title = Paint()

        // we are adding page info to our PDF file
        // in which we will be passing our pageWidth,
        // pageHeight and number of pages and after that
        // we are calling it to create our PDF.
        val mypageInfo = PageInfo.Builder(pagewidth, pageHeight, 1).create()

        // below line is used for setting
        // start page for our PDF file.
        val myPage = pdfDocument.startPage(mypageInfo)

        // creating a variable for canvas
        // from our page of PDF.
        val canvas: Canvas = myPage.canvas

        // below line is used to draw our image on our PDF file.
        // the first parameter of our drawbitmap method is
        // our bitmap
        // second parameter is position from left
        // third parameter is position from top and last
        // one is our variable for paint.
        canvas.drawBitmap(scaledbmp!!, 56.0F, 40.0F, paint)

        // below line is used for adding typeface for
        // our text which we will be adding in our PDF file.
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))

        // below line is used for setting text size
        // which we will be displaying in our PDF file.
        title.setTextSize(15.0F)

        // below line is sued for setting color
        // of our text inside our PDF file.
        title.setColor(ContextCompat.getColor(requireContext(), R.color.purple_200))

        // below line is used to draw text in our PDF file.
        // the first parameter is our text, second parameter
        // is position from start, third parameter is position from top
        // and then we are passing our variable of paint which is title.
        canvas.drawText("A portal for IT professionals.", 209.0F, 100.0F, title)
        canvas.drawText("Geeks for Geeks", 209.0F, 80.0F, title)

        // similarly we are creating another text and in this
        // we are aligning this text to center of our PDF file.
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        title.setColor(ContextCompat.getColor(requireContext(), R.color.purple_200))
        title.setTextSize(15.0F)

        // below line is used for setting
        // our text to center of PDF.
        title.setTextAlign(Paint.Align.CENTER)
        canvas.drawText("This is sample document which we have created.", 396.0F, 560.0F, title)

        // after adding all attributes to our
        // PDF file we will be finishing our page.
        pdfDocument.finishPage(myPage)

        // below line is used to set the name of
        // our PDF file and its path.
        val file = File(Environment.getExternalStorageDirectory(), "GFG.pdf")
        try {
            // after creating a file name we will
            // write our PDF file to that location.
            pdfDocument.writeTo(FileOutputStream(file))

            // below line is to print toast message
            // on completion of PDF generation.
            Toast.makeText(
                requireContext(),
                "PDF file generated successfully.",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: IOException) {
            // below line is used
            // to handle error
            e.printStackTrace()
        }
        // after storing our pdf to that
        // location we are closing our PDF file.
        pdfDocument.close()
    }
}