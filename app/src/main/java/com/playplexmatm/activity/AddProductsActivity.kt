package com.playplexmatm.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.model.product.ProductModel
import com.playplexmatm.model.productfilter.ProductFilterModel
import com.playplexmatm.model.taxes.TaxesModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.activity_add_products.*
import kotlinx.android.synthetic.main.activity_add_products.acTaxExclusive
import kotlinx.android.synthetic.main.activity_add_products.edtBasicAmount
import kotlinx.android.synthetic.main.layout_listview_bottomsheet.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddProductsActivity : AppCompatActivity() {

    private var productId: String = ""
    private var productName: String = ""
    private var productImage: String = ""
    private var taxId: String = ""
    private var tax_type: String = ""
    private var taxRate= 0
    private var taxAmount: Double = 0.0
    private var productList: ArrayList<ProductModel> = arrayListOf()
    private var productStringList: ArrayList<String> = arrayListOf()
    private var filterProductList: ArrayList<ProductFilterModel> = arrayListOf()
    private var taxList: ArrayList<TaxesModel> = arrayListOf()
    private var taxStringList: ArrayList<String> = arrayListOf()
    private var taxIntList: ArrayList<Int> = arrayListOf()
    lateinit var mContext: AddProductsActivity
    lateinit var apiInterface: ApiInterface
    lateinit var productFilterModel: ProductFilterModel

    var bottomSheetDialog: BottomSheetDialog? = null

    private var total = 0.0
    private var discountTotal = 0
    private var discountNewTotal = 0
    private var shippingTotal = 0
    private var currentStock = 0
    private var unitId = ""
    private var itemId = ""
    var from: String = ""
    var position: String = ""

    private var taxExlusiveArray : ArrayList<String>  = arrayListOf("Exclusive","Inclusive")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_products)
        mContext = this

        val bundle = intent.extras
        if(bundle != null) {
            productFilterModel = bundle.getSerializable("filterModel") as ProductFilterModel
            from = "edit"

            position = bundle.getString("position").toString()

            acProduct.setText(productFilterModel.name)
            edtBasicAmount.setText(productFilterModel.single_unit_price.toString())
            acOrderTax.setText(productFilterModel.tax_rate.toString())
            edtQuantity.setText(productFilterModel.quantity.toString())
            edtDiscount.setText(productFilterModel.discount_rate.toString())
            acTaxExclusive.setText(productFilterModel.tax_type)

            tax_type = (productFilterModel.tax_type)
            taxId = productFilterModel.x_tax_id
            taxRate = productFilterModel.tax_rate

            itemId = productFilterModel.item_id

            productId = productFilterModel.xid
            productName = productFilterModel.name
            productImage = productFilterModel.image_url
            currentStock = productFilterModel.current_stock
            unitId = productFilterModel.x_unit_id

        }

        init()
        listener()
    }

    fun listener() {

        btnSave.setOnClickListener {
            if(acProduct.text.toString().isNullOrEmpty()) {
                acProduct.requestFocus()
                acProduct.setError("Invalid Product")
            } else if(edtBasicAmount.text.toString().isNullOrEmpty()){
                edtBasicAmount.requestFocus()
                edtBasicAmount.setError("Invalid Product")
            } else if(edtQuantity.text.toString().isNullOrEmpty()){
                edtQuantity.requestFocus()
                edtQuantity.setError("Invalid Quantity")
            } else if(edtDiscount.text.toString().isNullOrEmpty()){
                edtDiscount.requestFocus()
                edtDiscount.setError("Invalid Discount")
            } else if(acOrderTax.text.toString().isNullOrEmpty()){
                acOrderTax.requestFocus()
                acOrderTax.setError("Invalid Tax")
            }else {
                addInArrayList()
                if (from.equals("edit")) {
                    val extra = Bundle()
                    extra.putSerializable("model", productFilterModel)
                    extra.putString("position", position)
                    val resultIntent = Intent()
                    resultIntent.putExtra("extra", extra)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else {
                    val extra = Bundle()
                    extra.putSerializable("model", productFilterModel)
                    val resultIntent = Intent()
                    resultIntent.putExtra("extra", extra)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
        }

        acProduct.setOnTouchListener { v, event ->
            acProduct.showDropDown()
            false
        }

        acOrderTax.setOnTouchListener { v, event ->
            acOrderTax.showDropDown()
            false
        }

        acTaxExclusive.setOnClickListener {
            showTaxExclusiveBottomSheet()
        }
    }

    fun init() {
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallGetProductAPI()
            mNetworkCallGetTaxAPI()
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections), Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun mNetworkCallGetProductAPI() {
        productList.clear()
        productStringList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getProductDropDown()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())

                        Log.e("Product List",jsonObject.toString())

                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONArray("data")
                            productList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<ArrayList<ProductModel?>?>() {}.type
                                )
                            )

                            setProductAdapter(productList)
                        } else {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject = JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext, JsonObject.optString("message"))
                    } catch (e: Exception) {

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setProductAdapter(productList: ArrayList<ProductModel>) {
        filterProductList.clear()
        for (i in 0 until productList.size) {
            productStringList.add(productList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, productStringList)

        acProduct.threshold = 0 //will start working from first character

        acProduct.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acProduct.setOnItemClickListener { parent, view, position, id ->

            edtBasicAmount.setText(productList[position].details.sales_price.toString())
            acOrderTax.setText(productList[position].details.tax.rate.toString())
            tax_type = productList[position].details.purchase_tax_type
            taxId = productList[position].details.tax.xid
            taxRate = productList[position].details.tax.rate

            productId = productList[position].xid
            productName = productList[position].name
            productImage = productList[position].image_url
            currentStock = productList[position].details.current_stock
            unitId = productList[position].x_unit_id

        }
    }

    private fun mNetworkCallGetTaxAPI() {
        taxList.clear()
        taxStringList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getTaxesDropDown()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())

                        Log.e("TAX API",jsonObject.toString())

                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONArray("data")
                            taxList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<TaxesModel?>?>() {}.type
                                )
                            )

                            setTaxAdapter(taxList)
                        } else {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject = JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext, JsonObject.optString("message"))
                    } catch (e: Exception) {

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setTaxAdapter(taxList: java.util.ArrayList<TaxesModel>) {

        for (i in 0 until taxList.size) {
            taxStringList.add(taxList[i].rate)
            taxIntList.add(taxList[i].rate.toInt())
        }


        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, taxStringList)

        acOrderTax.threshold = 0 //will start working from first character

        acOrderTax.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acOrderTax.setOnItemClickListener { parent, view, position, id ->
            taxId = taxList[position].xid
            taxRate = taxList[position].rate.toInt()
            taxAmount = taxList[position].rate.toDouble()
        }
    }


    fun addInArrayList() {

        val discountOnOneItem = (edtDiscount.text.toString().toDouble() * edtBasicAmount.text.toString().toDouble()) / 100
        val discountOnAllItems = (((edtDiscount.text.toString().toDouble() * edtBasicAmount.text.toString().toDouble())) / 100) * edtQuantity.text.toString().toDouble()

        val basicAmount = edtBasicAmount.text.toString().toDouble() * edtQuantity.text.toString().toInt()
        val basciAmountWithDiscount = basicAmount - (basicAmount * edtDiscount.text.toString().toDouble()) / 100


        var tax: Double = 0.0

        Log.e("taxType",tax_type)

        if(tax_type.equals("inclusive",ignoreCase = true)) {
            val taxOnBasicAmount = (basciAmountWithDiscount / (1 + 100/acOrderTax.text.toString().toDouble()))
            tax = taxOnBasicAmount
            Log.e("Tax",tax.toString())
        } else {
            val taxOnBasicAmount = (basciAmountWithDiscount * acOrderTax.text.toString().toInt()) / 100
            tax = taxOnBasicAmount
            Log.e("Tax 1",tax.toString())
        }


        if(from.equals("edit")) {
            productFilterModel = ProductFilterModel(
                productName,
                productImage,
                itemId,
                productId,
                edtDiscount.text.toString().toInt(),
                discountOnOneItem,
                discountOnAllItems,
                taxId,
                tax_type,
                acOrderTax.text.toString().toInt(),
                tax,
                unitId,
                edtBasicAmount.text.toString().toDouble(),
                edtBasicAmount.text.toString().toDouble(),
                edtQuantity.text.toString().toDouble() * edtBasicAmount.text.toString().toDouble(),
                currentStock,
                edtQuantity.text.toString().toInt()
            )
        } else {
            productFilterModel = ProductFilterModel(
                productName,
                productImage,
                "",
                productId,
                edtDiscount.text.toString().toInt(),
                discountOnOneItem,
                discountOnAllItems,
                taxId,
                tax_type,
                acOrderTax.text.toString().toInt(),
                tax,
                unitId,
                edtBasicAmount.text.toString().toDouble(),
                edtBasicAmount.text.toString().toDouble(),
                edtQuantity.text.toString().toDouble() * edtBasicAmount.text.toString().toDouble(),
                currentStock,
                edtQuantity.text.toString().toInt()
            )
        }

    }


    private fun showTaxExclusiveBottomSheet() {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.layout_listview_bottomsheet, null)
        val  listAdapter = ArrayAdapter<String>(mContext, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, taxExlusiveArray)

        view.lvCategory.adapter = listAdapter

        bottomSheetDialog = BottomSheetDialog(mContext)
        bottomSheetDialog!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        view.lvCategory.setOnItemClickListener { parent, view, position, id ->
            val element = listAdapter.getItem(position) // The item that was clicked
//            Toast.makeText(this,element,Toast.LENGTH_SHORT).show()
            acTaxExclusive.setText(element.toString())
            tax_type = element.toString()
            bottomSheetDialog!!.dismiss()
        }
        bottomSheetDialog!!.show()

    }
}