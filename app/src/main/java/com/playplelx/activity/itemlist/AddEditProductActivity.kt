package com.playplelx.activity.itemlist

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplelx.R
import com.playplelx.model.brand.BrandModel
import com.playplelx.model.category.CategoryModel
import com.playplelx.model.product.ProductModel
import com.playplelx.model.taxes.TaxesModel
import com.playplelx.model.units.UnitModel
import com.playplelx.model.warehouse.WareHouseModel
import com.playplelx.network.ApiInterface
import com.playplelx.network.Apiclient
import com.playplelx.util.Constants
import com.playplelx.util.InternetConnection
import com.playplelx.util.Util
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.concurrent.PriorityBlockingQueue
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddEditProductActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mContext: AddEditProductActivity
    lateinit var ivBack: AppCompatImageView
    lateinit var tvTitle: AppCompatTextView
    lateinit var apiInterface: ApiInterface
    lateinit var frameProductImage: FrameLayout
    lateinit var ivUploadImage: AppCompatImageView
    private var profilepath: String = ""
    private var file: String = ""
    private var fileUrl: String = ""
    lateinit var pbLoadData: ProgressBar
    lateinit var tvSave: AppCompatTextView
    lateinit var edtProductName: EditText
    lateinit var edtDescriptions: EditText
    lateinit var edtCurrentStock: EditText
    lateinit var edtMRP: EditText
    lateinit var edtPurchasePrice: EditText
    lateinit var edtSalePrice: EditText
    lateinit var edtOpeningStock: EditText
    lateinit var edtOpeningStockDate: EditText
    lateinit var edtWholesalePrice: EditText
    lateinit var edtWholesaleQuantity: EditText
    lateinit var acCategory: AutoCompleteTextView
    lateinit var acBrand: AutoCompleteTextView
    lateinit var acWareHouse: AutoCompleteTextView
    lateinit var acUnit: AutoCompleteTextView
    lateinit var acTax: AutoCompleteTextView
    private var categoryList: ArrayList<CategoryModel> = arrayListOf()
    private var categoryStringList: ArrayList<String> = arrayListOf()
    private var brandList: ArrayList<BrandModel> = arrayListOf()
    private var brandStringList: ArrayList<String> = arrayListOf()
    private var wareHouseList: ArrayList<WareHouseModel> = arrayListOf()
    private var wareHouseStringList: ArrayList<String> = arrayListOf()
    private var unitList: ArrayList<UnitModel> = arrayListOf()
    private var unitStringList: ArrayList<String> = arrayListOf()

    private var taxList: ArrayList<TaxesModel> = arrayListOf()
    private var taxStringList: ArrayList<String> = arrayListOf()
    private var categoryId: String = ""
    private var brandId: String = ""
    private var wareHouseId: String = ""
    private var unitId: String = ""
    private var taxId: String = ""
    private var selectedDate: String = ""

    private var mFrom: String = ""
    lateinit var productModel: ProductModel

    companion object {
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_product)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        tvTitle = findViewById(R.id.tvTitle)
        frameProductImage = findViewById(R.id.frameProductImage)
        ivUploadImage = findViewById(R.id.ivUploadImage)
        pbLoadData = findViewById(R.id.pbLoadData)
        edtProductName = findViewById(R.id.edtProductName)
        edtDescriptions = findViewById(R.id.edtDescriptions)
        edtMRP = findViewById(R.id.edtMRP)
        edtPurchasePrice = findViewById(R.id.edtPurchasePrice)
        edtSalePrice = findViewById(R.id.edtSalePrice)
        edtOpeningStock = findViewById(R.id.edtOpeningStock)
        edtOpeningStockDate = findViewById(R.id.edtOpeningStockDate)
        edtWholesalePrice = findViewById(R.id.edtWholesalePrice)
        edtWholesaleQuantity = findViewById(R.id.edtWholesaleQuantity)
        acCategory = findViewById(R.id.acCategory)
        acBrand = findViewById(R.id.acBrand)
        acTax = findViewById(R.id.acTax)
        acWareHouse = findViewById(R.id.acWareHouse)
        edtCurrentStock = findViewById(R.id.edtCurrentStock)
        acUnit = findViewById(R.id.acUnit)
        tvSave = findViewById(R.id.tvSave)


        if (intent.extras != null) {
            mFrom = intent.getStringExtra(Constants.mFrom)!!
            if (mFrom.equals(Constants.isEdit)) {
                tvTitle.setText("Update Product")
                productModel=Gson().fromJson(intent.getStringExtra(Constants.productModel),ProductModel::class.java)
                setData(productModel)

            } else {
                tvTitle.setText("Add Product")
            }
        }

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallCategoryAPI()
            mNetworkCallBrandAPI()
            mNetworkCallWareHouseAPI()
            mNetworkCallUnitAPI()
            mNetworkCallTaxAPI()
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        tvSave.setOnClickListener(this)
        edtOpeningStockDate.setOnClickListener(this)
        frameProductImage.setOnClickListener(this)


        acCategory.setOnTouchListener { v, event ->
            acCategory.showDropDown()
            false
        }


        acBrand.setOnTouchListener { v, event ->
            acBrand.showDropDown()
            false
        }



        acWareHouse.setOnTouchListener { v, event ->
            acWareHouse.showDropDown()
            false
        }

        acUnit.setOnTouchListener { v, event ->
            acUnit.showDropDown()
            false
        }

        acTax.setOnTouchListener { v, event ->
            acTax.showDropDown()
            false
        }
    }

    private fun setData(productModel: ProductModel) {
        Glide.with(mContext).load(productModel.image_url).into(ivUploadImage)
        file = productModel.image
        fileUrl = productModel.image_url
        edtProductName.setText(productModel.name)
        acCategory.setText(productModel.category.name)
        if (productModel.brand!= null){
            acBrand.setText(productModel.brand.name)
            brandId=productModel.brand.xid

        }
        acWareHouse.setText(productModel.details.warehouse.name)
        acUnit.setText(productModel.unit.name)
        acTax.setText(productModel.details.tax.name)

        categoryId=productModel.category.xid
        wareHouseId=productModel.details.warehouse.xid
        unitId=productModel.unit.xid
        taxId=productModel.details.tax.xid

        edtDescriptions.setText(productModel.description)

        edtCurrentStock.setText(productModel.details.current_stock.toString())
        edtMRP.setText(productModel.details.mrp.toString())
        edtPurchasePrice.setText(productModel.details.purchase_price.toString())
        edtSalePrice.setText(productModel.details.sales_price.toString())
        edtOpeningStock.setText(productModel.details.opening_stock.toString())
        edtOpeningStockDate.setText(productModel.details.opening_stock_date)
        if (productModel.details.wholesale_price!= null){
            edtWholesalePrice.setText(productModel.details.wholesale_price.toString())
        }
        if (productModel.details.wholesale_quantity!= null){
            edtWholesaleQuantity.setText(productModel.details.wholesale_quantity.toString())
        }


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.frameProductImage -> {
                if (checkPermission()) {
                    alertDialogForImagePicker()
                } else {
                    checkPermission()
                }
            }
            R.id.tvSave -> {
                if (InternetConnection.checkConnection(mContext)) {
                    if (isValidate()) {
                        if (mFrom.equals(Constants.isAdd)) {
                            mNetworkCallAddProductAPI()
                        } else {
                            mNetworkCallUpdateProductAPI()
                        }
                    }
                } else {
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.str_check_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            R.id.edtOpeningStockDate -> {
                showDatePicker(edtOpeningStockDate)
            }
        }
    }

    private fun showDatePicker(edtDate: EditText) {
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
                selectedDate =
                    year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString()
                edtOpeningStockDate.setText(year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString())
            },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }


    private fun checkPermission(): Boolean {
        val camerapermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val writepermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val listPermissionsNeeded = ArrayList<String>()
        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms = HashMap<String, Int>()
                perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] =
                    PackageManager.PERMISSION_GRANTED
                if (grantResults.size != 0) {
                    var i = 0
                    while (i <= permissions.size - 1) {
                        perms[permissions[i]] = grantResults[i]
                        if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                        ) {
                            alertDialogForImagePicker()
                        } else {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this, Manifest.permission.CAMERA
                                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            ) {
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.CAMERA),
                                    requestCode
                                )
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    requestCode
                                )
                            } else {
                                //  showSettingDialog()
                            }
                        }
                        i++
                    }
                }
            }
        }
    }

    private fun alertDialogForImagePicker() {
        val dialogView = Dialog(this)
        dialogView.setContentView(R.layout.image_picker_popup)
        dialogView.setCancelable(false)
        val txtcamera = dialogView.findViewById<TextView>(R.id.txtcamera)
        val txtGallery = dialogView.findViewById<TextView>(R.id.txtGallery)
        val txtCancel = dialogView.findViewById<TextView>(R.id.txtCancel)
        txtcamera.setOnClickListener { v: View? ->
            ImagePicker.with(mContext)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .cameraOnly()
                .start(Constants.REQ_PICK_IMAGE)
            dialogView.dismiss()
        }
        txtGallery.setOnClickListener { v: View? ->
            ImagePicker.with(mContext)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .galleryOnly()
                .start(Constants.REQ_PICK_IMAGE)
            dialogView.dismiss()
        }
        txtCancel.setOnClickListener { v: View? -> dialogView.dismiss() }
        dialogView.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val uri = data!!.data
            profilepath = uri!!.path.toString()
            ivUploadImage.setImageURI(uri)
            mNetworkCallUploadFileAPI()
        }
    }


    private fun mNetworkCallUploadFileAPI() {
        pbLoadData.visibility = View.VISIBLE
        val folder: RequestBody = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            "product"
        )
        var profilebody: MultipartBody.Part? = null
        if (!profilepath.isEmpty()) {
            val requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), File(profilepath))
            profilebody =
                MultipartBody.Part.createFormData("image", File(profilepath).name, requestFile)
        }
        val call: Call<JsonObject> = apiInterface.uploadFile(profilebody!!, folder)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        try {
                            val jsonObject = JSONObject(response.body().toString())
                            if (jsonObject.optBoolean("status")) {
                                file = jsonObject.optJSONObject("data")!!.optString("file")
                                fileUrl = jsonObject.optJSONObject("data")!!.optString("file_url")
                                Toast.makeText(
                                    mContext, jsonObject.optString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    mContext, jsonObject.optString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    pbLoadData.visibility = View.VISIBLE
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun mNetworkCallCategoryAPI() {
        categoryList.clear()
        categoryStringList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getCategoryDropDown()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONArray("data")
                            categoryList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<CategoryModel?>?>() {}.type
                                )
                            )
                            setCategoryAdapter(categoryList)
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
                }else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext,JsonObject.optString("message"))
                    }catch (e: Exception){

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setCategoryAdapter(categoryList: ArrayList<CategoryModel>) {
        for (i in 0 until categoryList.size) {
            categoryStringList.add(categoryList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, categoryStringList)

        acCategory.threshold = 0 //will start working from first character

        acCategory.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acCategory.setOnItemClickListener { parent, view, position, id ->
            categoryId = categoryList[position].xid
        }
    }


    private fun mNetworkCallBrandAPI() {
        brandList.clear()
        brandStringList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getBrandDropDown()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONArray("data")
                            brandList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<BrandModel?>?>() {}.type
                                )
                            )
                            setBrandAdapter(brandList)
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
                }else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext,JsonObject.optString("message"))
                    }catch (e: Exception){

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setBrandAdapter(brandList: ArrayList<BrandModel>) {

        for (i in 0 until brandList.size) {
            brandStringList.add(brandList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, brandStringList)

        acBrand.threshold = 0 //will start working from first character

        acBrand.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acBrand.setOnItemClickListener { parent, view, position, id ->
            brandId = brandList[position].xid
        }
    }


    private fun mNetworkCallWareHouseAPI() {
        wareHouseList.clear()
        wareHouseStringList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getWareHouseDropDown()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONArray("data")
                            wareHouseList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<WareHouseModel?>?>() {}.type
                                )
                            )
                            setWareHouseAdapter(wareHouseList)
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
                }else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext,JsonObject.optString("message"))
                    }catch (e: Exception){

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setWareHouseAdapter(wareHouseList: ArrayList<WareHouseModel>) {
        for (i in 0 until wareHouseList.size) {
            wareHouseStringList.add(wareHouseList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, wareHouseStringList)

        acWareHouse.threshold = 0 //will start working from first character

        acWareHouse.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acWareHouse.setOnItemClickListener { parent, view, position, id ->
            wareHouseId = wareHouseList[position].xid
        }
    }


    private fun mNetworkCallUnitAPI() {
        unitList.clear()
        unitStringList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getUnitDropDown()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONArray("data")
                            unitList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<UnitModel?>?>() {}.type
                                )
                            )
                            setUnitAdapter(unitList)
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
                }else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext,JsonObject.optString("message"))
                    }catch (e: Exception){

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setUnitAdapter(unitList: ArrayList<UnitModel>) {
        for (i in 0 until unitList.size) {
            unitStringList.add(unitList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, unitStringList)

        acUnit.threshold = 0 //will start working from first character

        acUnit.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acUnit.setOnItemClickListener { parent, view, position, id ->
            unitId = unitList[position].xid
        }
    }


    private fun mNetworkCallTaxAPI() {
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
                }else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext,JsonObject.optString("message"))
                    }catch (e: Exception){

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setTaxAdapter(taxList: ArrayList<TaxesModel>) {
        for (i in 0 until taxList.size) {
            taxStringList.add(taxList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, taxStringList)

        acTax.threshold = 0 //will start working from first character

        acTax.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acTax.setOnItemClickListener { parent, view, position, id ->
            taxId = taxList[position].xid
        }
    }

    private fun isValidate(): Boolean {
        var isvalid = true
        if (fileUrl.isEmpty()) {
            Toast.makeText(mContext, "Please upload file", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (edtProductName.text.toString()
                .trim().isEmpty()
        ) {
            Toast.makeText(mContext, "Please enter product name", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (categoryId.isEmpty()) {
            Toast.makeText(mContext, "Please select category", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (brandId.isEmpty()) {
            Toast.makeText(mContext, "Please select brand", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (wareHouseId.isEmpty()) {
            Toast.makeText(mContext, "Please select warehouse", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (unitId.isEmpty()) {
            Toast.makeText(mContext, "Please select units", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (taxId.isEmpty()) {
            Toast.makeText(mContext, "Please select tax", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (edtDescriptions.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter descriptions", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (edtCurrentStock.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter current stock", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (edtMRP.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter MRP", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (edtPurchasePrice.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter purchase price", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (edtSalePrice.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter sale price", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (edtOpeningStock.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter opening stock", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (edtOpeningStockDate.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter opening stock date", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (edtWholesalePrice.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter wholesale price", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (edtWholesaleQuantity.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter wholesale quantity", Toast.LENGTH_SHORT).show()
            isvalid = false
        }

        return isvalid
    }

    private fun mNetworkCallAddProductAPI() {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.addProducts(
            wareHouseId,
            edtProductName.text.toString().trim(),
            file,
            fileUrl,
            edtProductName.text.toString().trim().toLowerCase(),
            "CODE128",
            "3574363549",
            edtCurrentStock.text.toString().trim(),
            categoryId,
            brandId,
            edtMRP.text.toString().trim(),
            edtPurchasePrice.text.toString().trim(),
            edtSalePrice.text.toString().trim(),
            taxId,
            unitId,
            "exclusive",
            edtDescriptions.text.toString().trim(),
            "exclusive",
            edtOpeningStock.text.toString().trim(),
            edtOpeningStockDate.text.toString().trim(),
            edtWholesalePrice.text.toString().trim(),
            edtWholesaleQuantity.text.toString().trim()
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent)
                            finish()
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
                }else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext,JsonObject.optString("message"))
                    }catch (e: Exception){

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun mNetworkCallUpdateProductAPI() {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.updateProducts(
            productModel.xid,
            wareHouseId,
            edtProductName.text.toString().trim(),
            file,
            fileUrl,
            edtProductName.text.toString().trim().toLowerCase(),
            "CODE128",
            "3574363549",
            edtCurrentStock.text.toString().trim(),
            categoryId,
            brandId,
            edtMRP.text.toString().trim(),
            edtPurchasePrice.text.toString().trim(),
            edtSalePrice.text.toString().trim(),
            taxId,
            unitId,
            "exclusive",
            edtDescriptions.text.toString().trim(),
            "exclusive",
            edtOpeningStock.text.toString().trim(),
            edtOpeningStockDate.text.toString().trim(),
            edtWholesalePrice.text.toString().trim(),
            edtWholesaleQuantity.text.toString().trim()
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent)
                            finish()
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
                }else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext,JsonObject.optString("message"))
                    }catch (e: Exception){

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

}