package com.playplexmatm.activity

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.adapter.PaymentSaleAdapter
import com.playplexmatm.adapter.PaymentsPurchaseAdapter
import com.playplexmatm.adapter.ProductPurchaseAdapter
import com.playplexmatm.adapter.ProductSaleAdapter
import com.playplexmatm.aeps.activities_aeps.AepsTransactionActivity
import com.playplexmatm.model.paymentmode.PaymentModeModel
import com.playplexmatm.model.purchasemodel.PurchaseModel
import com.playplexmatm.model.saleList.SaleListModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.activity_order_details.tvInvoiceNumber
import kotlinx.android.synthetic.main.activity_order_details.tvOrderStatus
import kotlinx.android.synthetic.main.layout_add_payment.*
import kotlinx.android.synthetic.main.layout_listview_bottomsheet.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailsActivity : AppCompatActivity() {

    lateinit var purchaseModel: PurchaseModel
    lateinit var saleListModel: SaleListModel
    lateinit var from: String
    lateinit var productAdapter: ProductSaleAdapter
    lateinit var productPurchaseAdapter: ProductPurchaseAdapter
    lateinit var paymentSaleAdapter: PaymentSaleAdapter
    lateinit var paymentPurchaseAdapter: PaymentsPurchaseAdapter

    lateinit var dialog: Dialog
    lateinit var bottomSheetDialog: BottomSheetDialog

    private var paymentModeList: ArrayList<PaymentModeModel> = arrayListOf()
    private var paymentModeStringList: ArrayList<String> = arrayListOf()

    lateinit var paymentModeId: String
    lateinit var apiInterface: ApiInterface


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        apiInterface = Apiclient(this).getClient()!!.create(ApiInterface::class.java)
        mNetworkCallGetPaymentModeAPI()

        tvAddNewPayment.setOnClickListener {
            editDialog()
        }

        val bundle = intent.extras
        if(bundle != null) {
            from = bundle.getString("from").toString()
            Log.e("from",from)
            if(from.equals("purchaseModel")) {
                purchaseModel = bundle.getSerializable("model") as PurchaseModel


                tvOrderDate.setText(convertDate(purchaseModel.order_date))
                tvInvoiceNumber.setText(purchaseModel.invoice_number)
                tvCustomer.setText(purchaseModel.user.name)
                tvOrderStatus.setText(purchaseModel.order_status)
                tvPaymentStatus.setText(purchaseModel.payment_status)
                tvOrderTakenBy.setText(purchaseModel.staff_member.name)
                tvTotalAmount.setText(resources.getString(R.string.Rupee)+" "+purchaseModel.subtotal)
                tvPaidAmount.setText(resources.getString(R.string.Rupee)+" "+purchaseModel.paid_amount)
                tvDueAmount.setText(resources.getString(R.string.Rupee)+" "+purchaseModel.due_amount)
                tvDiscount.setText(resources.getString(R.string.Rupee)+" "+purchaseModel.discount)
                tvShipping.setText(resources.getString(R.string.Rupee)+" "+purchaseModel.shipping)
                tvOrderTax.setText(resources.getString(R.string.Rupee)+" "+purchaseModel.tax_amount)

                rvProducts.layoutManager = LinearLayoutManager(this)
                rvProducts.setHasFixedSize(true)
                productPurchaseAdapter = ProductPurchaseAdapter(this, purchaseModel.items)
                rvProducts.adapter = productPurchaseAdapter


                rvPayments.layoutManager = LinearLayoutManager(this)
                rvPayments.setHasFixedSize(true)
                paymentPurchaseAdapter = PaymentsPurchaseAdapter(this, purchaseModel.order_payments)
                rvPayments.adapter = paymentPurchaseAdapter


                tvOrderItems.setOnClickListener {
                    tvOrderItems.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_green))
                    tvOrderItems.setTextColor(resources.getColor(R.color.white))
                    tvPayments.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_white))
                    tvPayments.setTextColor(resources.getColor(R.color.black))
                    tvAddNewPayment.visibility = View.GONE

                    rvProducts.visibility = View.VISIBLE
                    rvPayments.visibility  = View.GONE
                }

                tvPayments.setOnClickListener {
                    tvPayments.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_green))
                    tvPayments.setTextColor(resources.getColor(R.color.white))
                    tvOrderItems.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_white))
                    tvOrderItems.setTextColor(resources.getColor(R.color.black))
                    tvAddNewPayment.visibility = View.VISIBLE

                    rvProducts.visibility = View.GONE
                    rvPayments.visibility  = View.VISIBLE
                }

            } else {
                saleListModel = bundle.getSerializable("model") as SaleListModel

                tvOrderDate.setText(convertDate(saleListModel.order_date))
                tvInvoiceNumber.setText(saleListModel.invoice_number)
                tvCustomer.setText(saleListModel.user.name)
                tvOrderStatus.setText(saleListModel.order_status)
                tvPaymentStatus.setText(saleListModel.payment_status)
                tvOrderTakenBy.setText(saleListModel.staff_member.name)
                tvTotalAmount.setText(resources.getString(R.string.Rupee)+" "+saleListModel.subtotal)
                tvPaidAmount.setText(resources.getString(R.string.Rupee)+" "+saleListModel.paid_amount)
                tvDueAmount.setText(resources.getString(R.string.Rupee)+" "+saleListModel.due_amount)
                tvDiscount.setText(resources.getString(R.string.Rupee)+" "+saleListModel.discount)
                tvShipping.setText(resources.getString(R.string.Rupee)+" "+saleListModel.shipping)
                tvOrderTax.setText(resources.getString(R.string.Rupee)+" "+saleListModel.tax_amount)

                rvProducts.layoutManager = LinearLayoutManager(this)
                rvProducts.setHasFixedSize(true)
                productAdapter = ProductSaleAdapter(this, saleListModel.items)
                rvProducts.adapter = productAdapter


                rvPayments.layoutManager = LinearLayoutManager(this)
                rvPayments.setHasFixedSize(true)
                paymentSaleAdapter = PaymentSaleAdapter(this, saleListModel.order_payments)
                rvPayments.adapter = paymentSaleAdapter

                tvOrderItems.setOnClickListener {
                    tvOrderItems.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_green))
                    tvOrderItems.setTextColor(resources.getColor(R.color.white))
                    tvPayments.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_white))
                    tvPayments.setTextColor(resources.getColor(R.color.black))
                    tvAddNewPayment.visibility = View.GONE

                    rvProducts.visibility = View.VISIBLE
                    rvPayments.visibility  = View.GONE
                }

                tvPayments.setOnClickListener {
                    tvPayments.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_green))
                    tvPayments.setTextColor(resources.getColor(R.color.white))
                    tvOrderItems.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_white))
                    tvOrderItems.setTextColor(resources.getColor(R.color.black))
                    tvAddNewPayment.visibility = View.VISIBLE

                    rvProducts.visibility = View.GONE
                    rvPayments.visibility  = View.VISIBLE
                }
//            posProductAdapter = PosProductAdapter(this, categoryModel.items, this)
            }
        }
    }

    fun convertDate(date: String): String {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SS:SS")
        val output = SimpleDateFormat("dd-MM-yyyy")

        var d: Date? = null
        try {
            d = input.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val formatted = output.format(d)
        Log.i("DATE", "" + formatted)
        return formatted
    }

    fun editDialog() {
        dialog = Dialog(this, com.google.android.material.R.style.Theme_MaterialComponents_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_add_payment)

        dialog.etDate.setOnClickListener {
            showDatePicker()
        }

        dialog.acPaymentMode.setOnClickListener {
            showpaymentModeBottomSheet()
        }

        dialog.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.btnUpdate.setOnClickListener {
            if(dialog.etDate.text.toString().isNullOrEmpty()) {
                Toast.makeText(this, "Select Date", Toast.LENGTH_SHORT).show()
            } else if(dialog.etAmount.text.toString().isNullOrEmpty()) {
                Toast.makeText(this, "Enter Amount", Toast.LENGTH_SHORT).show()
            } else if(dialog.acPaymentMode.text.toString().isNullOrEmpty()) {
                Toast.makeText(this, "Enter Amount", Toast.LENGTH_SHORT).show()
            } else {
                if(from.equals("purchase")) {
                    if(dialog.acPaymentMode.text.toString().equals("aeps",ignoreCase = true)) {
                        val intent = Intent(this,AepsTransactionActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString("amount",dialog.etAmount.text.toString())
                        intent.putExtras(bundle)
                        startActivityForResult(intent,108)
                    } else {
                        mNetworkCallAddPaymentAPI(
                            dialog.etDate.text.toString(),
                            paymentModeId,
                            dialog.etAmount.text.toString(),
                            dialog.etNotes.text.toString(),
                            purchaseModel.xid
                        )
                    }
                } else {
                    if(dialog.acPaymentMode.text.toString().equals("aeps",ignoreCase = true)) {
                        val intent = Intent(this,AepsTransactionActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString("amount",dialog.etAmount.text.toString())
                        intent.putExtras(bundle)
                        startActivityForResult(intent,108)
                    } else {
                        mNetworkCallAddPaymentAPI(
                            dialog.etDate.text.toString(),
                            paymentModeId,
                            dialog.etAmount.text.toString(),
                            dialog.etNotes.text.toString(),
                            saleListModel.xid
                        )
                    }
                }
            }
            dialog.dismiss()
        }
        dialog.getWindow()!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show()
    }

    private fun showpaymentModeBottomSheet() {
        val view: View = LayoutInflater.from(this).inflate(R.layout.layout_listview_bottomsheet, null)
        val  listAdapter = ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, paymentModeStringList)

        view.lvCategory.adapter = listAdapter

        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        view.lvCategory.setOnItemClickListener { parent, view, position, id ->
            val element = listAdapter.getItem(position) // The item that was clicked
//            Toast.makeText(this,element,Toast.LENGTH_SHORT).show()
            dialog.acPaymentMode.setText(element.toString())
            paymentModeId = paymentModeList[position].xid
            bottomSheetDialog!!.dismiss()
        }
        bottomSheetDialog!!.show()

    }

    private fun mNetworkCallAddPaymentAPI(
        date: String,
        payment_mode: String,
        amount: String,
        notes: String,
        order_id: String
    ) {
        Log.e("Data",date+payment_mode+amount+notes+order_id)
        pbLoadData.visibility = View.VISIBLE
        val call = Apiclient(this).getClientOrderPayments()!!.create(ApiInterface::class.java)
            .orderPayments(
            date,
            payment_mode,
            amount,
            notes,
            order_id
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e("Response",response.toString())
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            Toast.makeText(
                                this@OrderDetailsActivity,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            Toast.makeText(
                                this@OrderDetailsActivity,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        onBackPressed()
                    } else {
                        Toast.makeText(
                            this@OrderDetailsActivity,
                            resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject = JSONObject(response.errorBody()!!.string())
                        Util(this@OrderDetailsActivity).logOutAlertDialog(this@OrderDetailsActivity, JsonObject.optString("message"))
                    } catch (e: Exception) {

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(this@OrderDetailsActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showDatePicker() {
        var mYear = 0
        var mMonth = 0
        var mDay = 0
        // Get Current Date
        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            this,
            { view, year, monthOfYear, dayOfMonth ->
               dialog.etDate.setText(year.toString()+"-"+ (monthOfYear + 1)+"-"+dayOfMonth.toString())
            },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.show()
    }

    private fun mNetworkCallGetPaymentModeAPI() {
        paymentModeList.clear()
        paymentModeStringList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getPaymentModeDropDown()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONArray("data")
                            paymentModeList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<ArrayList<PaymentModeModel?>?>() {}.type
                                )
                            )

                            setPaymentAdapter(paymentModeList)
                        } else {
                            Toast.makeText(
                                this@OrderDetailsActivity,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            this@OrderDetailsActivity,
                            resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject = JSONObject(response.errorBody()!!.string())
                        Util(this@OrderDetailsActivity).logOutAlertDialog(this@OrderDetailsActivity, JsonObject.optString("message"))
                    } catch (e: Exception) {

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(this@OrderDetailsActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun setPaymentAdapter(paymentList: ArrayList<PaymentModeModel>) {
        for (i in 0 until paymentList.size) {
            paymentModeStringList.add(paymentList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, paymentModeStringList)

//        dialog.acPaymentMode.threshold = 0 //will start working from first character
//
//        dialog.acPaymentMode.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView
//
//        dialog.acPaymentMode.setOnItemClickListener { parent, view, position, id ->
//            paymentModeId = paymentList[position].xid
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode)  {
            108 -> {
                if(from.equals("purchase")) {

                    mNetworkCallAddPaymentAPI(
                        dialog.etDate.text.toString(),
                        paymentModeId,
                        dialog.etAmount.text.toString(),
                        dialog.etNotes.text.toString(),
                        purchaseModel.xid
                    )

                } else {
                    mNetworkCallAddPaymentAPI(
                        dialog.etDate.text.toString(),
                        paymentModeId,
                        dialog.etAmount.text.toString(),
                        dialog.etNotes.text.toString(),
                        saleListModel.xid
                    )
                }
            }

        }
    }
}