package com.playplexmatm.activity.itemlist

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.model.brand.BrandModel
import com.playplexmatm.model.category.CategoryModel
import com.playplexmatm.model.product.ProductModel
import com.playplexmatm.model.taxes.TaxesModel
import com.playplexmatm.model.units.UnitModel
import com.playplexmatm.model.warehouse.WareHouseModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.Constants
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.activity_add_edit_product.*
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
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.playplexmatm.activity.pos.BarcodeScannerActivity
import com.playplexmatm.activity.settings.AddEditUnitActivity
import com.playplexmatm.activity.settings.AddEditWareHouseSettingActivity
import com.playplexmatm.adapter.*
import com.playplexmatm.util.SimpleDividerItemDecoration
import kotlinx.android.synthetic.main.activity_add_edit_product.ivBarCode
import kotlinx.android.synthetic.main.activity_add_purchase_list.*
import kotlinx.android.synthetic.main.activity_pos_setting.*
import kotlinx.android.synthetic.main.layout_list_bottomsheet.view.*
import kotlinx.android.synthetic.main.layout_list_bottomsheet_customer.view.*
import kotlinx.android.synthetic.main.layout_list_category.view.*


class AddEditProductActivity : AppCompatActivity(), View.OnClickListener,
    PricingFragment.onSomeEventListener,
   StockFragment.onSomeEventListener, BrandListAdapter.ListAdapterListener,
    CategoryListAdapter.ListAdapterListener,
    UnitListAdapter.ListAdapterListener,
    WarehouseListAdapter.ListAdapterListener {

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
    lateinit var edtItemCode: EditText
    lateinit var edtSlug: EditText
//    lateinit var edtPurchasePrice: EditText
//    lateinit var edtSalePrice: EditText
//    lateinit var edtWholesalePrice: EditText
//    lateinit var edtWholesaleQuantity: EditText
    lateinit var acCategory: AutoCompleteTextView
    lateinit var acBrand: AutoCompleteTextView
    lateinit var acWareHouse: AutoCompleteTextView
    lateinit var acUnit: AutoCompleteTextView
//    lateinit var acTax: AutoCompleteTextView
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
    private var mrp: String = ""
    private var tax: String = ""


    var bottomSheetDialogCategory: BottomSheetDialog? = null

    lateinit var categoryListAdapter: CategoryListAdapter
    lateinit var brandListAdapter: BrandListAdapter
    lateinit var unitListAdapter: UnitListAdapter
    lateinit var warehouseListAdapter: WarehouseListAdapter


    private var mFrom: String = ""
    lateinit var productModel: ProductModel

    var currentStock = ""
    var openingStock = ""
    var openingDate = ""
    var purchasePrice = ""
    var salePrice = ""

    private val ITEMCODE_SCAN = 12

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
        //supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        tvTitle = findViewById(R.id.tvTitle)
        frameProductImage = findViewById(R.id.frameProductImage)
        ivUploadImage = findViewById(R.id.ivUploadImage)
        pbLoadData = findViewById(R.id.pbLoadData)
        edtProductName = findViewById(R.id.edtProductName)
        edtDescriptions = findViewById(R.id.edtDescriptions)
        edtItemCode = findViewById(R.id.edtItemCode)
        edtSlug = findViewById(R.id.edtSlug)
//        edtMRP = findViewById(R.id.edtMRP)
//        edtPurchasePrice = findViewById(R.id.edtPurchasePrice)
//        edtSalePrice = findViewById(R.id.edtSalePrice)
//        edtOpeningStock = findViewById(R.id.edtOpeningStock)
//        edtOpeningStockDate = findViewById(R.id.edtOpeningStockDate)
//        edtWholesalePrice = findViewById(R.id.edtWholesalePrice)
//        edtWholesaleQuantity = findViewById(R.id.edtWholesaleQuantity)
        acCategory = findViewById(R.id.acCategory)
        acBrand = findViewById(R.id.acBrand)
//        acTax = findViewById(R.id.acTax)
        acWareHouse = findViewById(R.id.acWareHouse)
//        edtCurrentStock = findViewById(R.id.edtCurrentStock)
        acUnit = findViewById(R.id.acUnit)
        tvSave = findViewById(R.id.tvSave)

        tvAddProductImage.setOnClickListener {
            tvRemoveProductImage.visibility = View.VISIBLE
            tvAddProductImage.visibility = View.GONE
            llProductImage.visibility = View.VISIBLE
        }

        tvRemoveProductImage.setOnClickListener {
            tvRemoveProductImage.visibility = View.GONE
            tvAddProductImage.visibility = View.VISIBLE
            llProductImage.visibility = View.GONE
        }

        ivAddBrand.setOnClickListener {
            val intent = Intent(this,AddEditBrandActivity::class.java)
            startActivity(intent)
        }

        ivAddCategory.setOnClickListener {
            val intent = Intent(this,AddEditCategoryActivity::class.java)
            startActivity(intent)
        }

        ivAddUnit.setOnClickListener {

            val intent = Intent(this,AddEditUnitActivity::class.java)
            startActivity(intent)
        }

        ivAddWareHouse.setOnClickListener {
            val intent = Intent(this,AddEditWareHouseSettingActivity::class.java)
            startActivity(intent)
        }

        ivBarCode.setOnClickListener {
            val intent = Intent(mContext, BarcodeScannerActivity::class.java)
            startActivityForResult(intent, ITEMCODE_SCAN)
        }


        edtProductName.addTextChangedListener(object : TextWatcher {
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
                if(!s.toString().isNullOrEmpty())
                {
                    edtSlug.setText(edtProductName.text.toString())
                    llAddProduct.visibility = View.VISIBLE
                }
                else
                {
                    edtSlug.setText(edtProductName.text.toString())
                    llAddProduct.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        title = "Product"

        tabLayout.addTab(tabLayout.newTab().setText("Price & Tax"))
        tabLayout.addTab(tabLayout.newTab().setText("Stock"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = AddProductAdapter(
            this, supportFragmentManager,
            tabLayout.tabCount
        )
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })






        if (intent.extras != null) {
            mFrom = intent.getStringExtra(Constants.mFrom)!!
            if (mFrom.equals(Constants.isEdit)) {
                tvTitle.setText("Update Product")
                productModel = Gson().fromJson(
                    intent.getStringExtra(Constants.productModel),
                    ProductModel::class.java
                )
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
//        edtOpeningStockDate.setOnClickListener(this)
        frameProductImage.setOnClickListener(this)



        acCategory.setOnClickListener {
            showCategoryBottomSheet()

        }

      /*
        acCategory.setOnTouchListener { v, event ->
//            acCategory.showDropDown()

            showCategoryBottomSheet()
            false
        }

       */


//        acBrand.setOnTouchListener { v, event ->
//            acBrand.showDropDown()
//            false
//        }

          acBrand.setOnClickListener {
              showBrandBottomSheet()
          }


//        acWareHouse.setOnTouchListener { v, event ->
//            acWareHouse.showDropDown()
//            false
//        }

        acWareHouse.setOnClickListener {
            showWareHouseBottomSheet()
        }

//        acUnit.setOnTouchListener { v, event ->
//            acUnit.showDropDown()
//            false
//        }

        acUnit.setOnClickListener {
            showUnitBottomSheet()
        }

//        acTax.setOnClickListener {
//            showTaxBottomSheet()
//        }

//        acTax.setOnTouchListener { v, event ->
//            acTax.showDropDown()
//            false
//        }
    }

    private fun setData(productModel: ProductModel) {
        Glide.with(mContext).load(productModel.image_url).into(ivUploadImage)
        file = productModel.image
        fileUrl = productModel.image_url
        edtProductName.setText(productModel.name)
        acCategory.setText(productModel.category.name)
        if (productModel.brand != null) {
            acBrand.setText(productModel.brand.name)
            brandId = productModel.brand.xid

        }
        acWareHouse.setText(productModel.details.warehouse.name)
        acUnit.setText(productModel.unit.name)
//        acTax.setText(productModel.details.tax.name)

        categoryId = productModel.category.xid
        wareHouseId = productModel.details.warehouse.xid
        unitId = productModel.unit.xid
        taxId = productModel.details.tax.xid

        edtDescriptions.setText(productModel.description)

//        edtCurrentStock.setText(productModel.details.current_stock.toString())
//        edtMRP.setText(productModel.details.mrp.toString())
//        edtPurchasePrice.setText(productModel.details.purchase_price.toString())
//        edtSalePrice.setText(productModel.details.sales_price.toString())
//        edtOpeningStock.setText(productModel.details.opening_stock.toString())
//        edtOpeningStockDate.setText(productModel.details.opening_stock_date)
//        if (productModel.details.wholesale_price != null) {
//            edtWholesalePrice.setText(productModel.details.wholesale_price.toString())
//        }
//        if (productModel.details.wholesale_quantity != null) {
//            edtWholesaleQuantity.setText(productModel.details.wholesale_quantity.toString())
//        }

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

                        Log.e("checkpoint","inside vaildate")

                        if (mFrom.equals(Constants.isEdit)) {
                            mNetworkCallUpdateProductAPI()
                        } else {
                            mNetworkCallAddProductAPI()
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
//                showDatePicker(edtOpeningStockDate)
            }
        }
    }

//    private fun showDatePicker(edtDate: EditText) {
//        var mYear = 0
//        var mMonth = 0
//        var mDay = 0
//        // Get Current Date
//        val c = Calendar.getInstance()
//        mYear = c[Calendar.YEAR]
//        mMonth = c[Calendar.MONTH]
//        mDay = c[Calendar.DAY_OF_MONTH]
//        val datePickerDialog = DatePickerDialog(
//            this,
//            { view, year, monthOfYear, dayOfMonth ->
//                selectedDate =
//                    year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString()
//                edtOpeningStockDate.setText(year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString())
//            },
//            mYear,
//            mMonth,
//            mDay
//        )
//        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
//        datePickerDialog.show()
//    }


    private fun showCategoryBottomSheet()
    {
        val view: View = layoutInflater.inflate(R.layout.layout_list_bottomsheet, null)


        view.rvCategory.apply {

            layoutManager = LinearLayoutManager(this@AddEditProductActivity)
            categoryListAdapter = CategoryListAdapter(
                context, categoryList, this@AddEditProductActivity
            )
            view.rvCategory.addItemDecoration(SimpleDividerItemDecoration(this@AddEditProductActivity))
            view.rvCategory.adapter = categoryListAdapter
        }

        bottomSheetDialogCategory = BottomSheetDialog(this)
        bottomSheetDialogCategory!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);



       bottomSheetDialogCategory!!.show()

    }

    private fun showBrandBottomSheet()
    {
        val view: View = layoutInflater.inflate(R.layout.layout_list_bottomsheet, null)


        view.rvCategory.apply {

            layoutManager = LinearLayoutManager(this@AddEditProductActivity)
            brandListAdapter = BrandListAdapter(
                context, brandList, this@AddEditProductActivity
            )
            view.rvCategory.addItemDecoration(SimpleDividerItemDecoration(this@AddEditProductActivity))
            view.rvCategory.adapter = brandListAdapter
        }

        bottomSheetDialogCategory = BottomSheetDialog(this)
        bottomSheetDialogCategory!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);



        bottomSheetDialogCategory!!.show()

    }

    private fun showUnitBottomSheet()
    {
        val view: View = layoutInflater.inflate(R.layout.layout_list_bottomsheet, null)


        view.rvCategory.apply {

            layoutManager = LinearLayoutManager(this@AddEditProductActivity)
            unitListAdapter = UnitListAdapter(
                context, unitList, this@AddEditProductActivity
            )
            view.rvCategory.addItemDecoration(SimpleDividerItemDecoration(this@AddEditProductActivity))
            view.rvCategory.adapter = unitListAdapter
        }

        bottomSheetDialogCategory = BottomSheetDialog(this)
        bottomSheetDialogCategory!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetDialogCategory!!.show()

    }




    private fun showWareHouseBottomSheet()
    {
        val view: View = layoutInflater.inflate(R.layout.layout_list_bottomsheet, null)


        view.rvCategory.apply {

            layoutManager = LinearLayoutManager(this@AddEditProductActivity)
            warehouseListAdapter = WarehouseListAdapter(
                context, wareHouseList, this@AddEditProductActivity
            )
            view.rvCategory.addItemDecoration(SimpleDividerItemDecoration(this@AddEditProductActivity))
            view.rvCategory.adapter = warehouseListAdapter
        }

        bottomSheetDialogCategory = BottomSheetDialog(this)
        bottomSheetDialogCategory!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetDialogCategory!!.show()

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
        if(requestCode == ITEMCODE_SCAN) {
            if (resultCode === RESULT_OK){
                val extra: Bundle = data!!.getBundleExtra("extra")!!
                edtItemCode.setText(extra.getString("barcode").toString())
            }
        } else {
            if (resultCode == RESULT_OK) {
                val uri = data!!.data
                profilepath = uri!!.path.toString()
                ivUploadImage.setImageURI(uri)
                mNetworkCallUploadFileAPI()
            }
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

                            Log.e("Response",jsonObject.toString())

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

    private fun setCategoryAdapter(categoryList: ArrayList<CategoryModel>) {
        for (i in 0 until categoryList.size) {
            categoryStringList.add(categoryList[i].name)
        }

//        val adapter: ArrayAdapter<String> =
//            ArrayAdapter<String>(this, R.layout.dropdown, categoryStringList)

//        acCategory.threshold = 0 //will start working from first character

//        acCategory.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

//        acCategory.setOnItemClickListener { parent, view, position, id ->
//            categoryId = categoryList[position].xid
//        }
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

    private fun setTaxAdapter(taxList: ArrayList<TaxesModel>) {
        for (i in 0 until taxList.size) {
            taxStringList.add(taxList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, taxStringList)

//        acTax.threshold = 0 //will start working from first character
//
//        acTax.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView
//
//        acTax.setOnItemClickListener { parent, view, position, id ->
//            taxId = taxList[position].xid
//        }
    }

    private fun isValidate(): Boolean {
        var isvalid = true

//        Log.e("Purchase price",PricingFragment().getPurchasePrice())
//        Toast.makeText(this,PricingFragment().getPurchasePrice(),Toast.LENGTH_SHORT ).show()

        if (edtProductName.text.toString()
                .trim().isEmpty()
        ) {
            Toast.makeText(mContext, "Please enter product name", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (categoryId.isEmpty()) {
            Toast.makeText(mContext, "Please select category", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (unitId.isEmpty()) {
            Toast.makeText(mContext, "Please select units", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (openingStock.isNullOrEmpty()) {
            Toast.makeText(mContext, "Please enter opening stock", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (mrp.trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter MRP", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (purchasePrice.isNullOrEmpty()) {
            Toast.makeText(mContext, "Please enter purchase price", Toast.LENGTH_SHORT).show()
            isvalid = false
        } else if (salePrice.isNullOrEmpty()) {
            Toast.makeText(mContext, "Please enter sale price", Toast.LENGTH_SHORT).show()
            isvalid = false
        }

        return isvalid
    }

    private fun mNetworkCallAddProductAPI() {
        pbLoadData.visibility = View.VISIBLE

        Log.e("Add Purchase",
            wareHouseId+":"
                +edtProductName.text.toString().trim()+":"
                +file+":"
                +fileUrl+":"
                +edtSlug.text.toString().trim()+":"
                +"CODE128"+":"
                +edtItemCode.text.toString()+":"
                +currentStock+":"
                +categoryId+":"
                +brandId+":"
                +mrp+":"
                +purchasePrice+":"
                +salePrice+":"
                +taxId+":"
                +unitId+":"
                +"exclusive"+":"
                +edtDescriptions.text.toString().trim()+":"
                +"exclusive"+":"
                +openingStock+":"
                +openingDate+":"
                +"0"+":"
                +"0"+":")

        val call = apiInterface.addProducts(
            wareHouseId,
            edtProductName.text.toString().trim(),
            file,
            fileUrl,
            edtSlug.text.toString(),
            "CODE128",
            edtItemCode.text.toString(),
            currentStock,
            categoryId,
            brandId,
            mrp,
            purchasePrice,
            salePrice,
            taxId,
            unitId,
            "exclusive",
            edtDescriptions.text.toString().trim(),
            "exclusive",
            openingStock,
            openingDate,
            "0",
            "0"
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
            edtItemCode.text.toString(),
            currentStock,
            categoryId,
            brandId,
            edtItemCode.text.toString().trim(),
            purchasePrice,
            salePrice,
            taxId,
            unitId,
            "exclusive",
            edtDescriptions.text.toString().trim(),
            "exclusive",
            openingStock,
            openingDate,
            "",
            ""
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {

                        val jsonObject = JSONObject(response.body().toString())

                        Log.e("response",jsonObject.toString() )

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



    override fun someEvent(purchasePriceP: String?, salePriceP: String?, mrpP: String?, taxP: String?) {
        Log.e("PurchasePrice",purchasePrice.toString())
        Log.e("SalePrice",salePrice.toString())
        Log.e("TaxId",taxP.toString())

        purchasePrice = purchasePriceP.toString()
        mrp = mrpP.toString()
        taxId = taxP.toString()
        salePrice = salePriceP.toString()


    }

    override fun stockEvent(
        currentStockP: String?,
        openingStockP: String?,
        openingStockDateP: String?
    ) {

        Log.e("currentstock",currentStockP.toString())
        Log.e("openingstock",openingStockP.toString())
        Log.e("openingstockDate",openingStockDateP.toString())

        currentStock = currentStockP.toString()
        openingStock = openingStockP.toString()

        openingDate = openingStockDateP.toString()

    }


    override fun onClickAtButton(brandModel: BrandModel) {
        if(brandModel != null) {
            acBrand.setText(brandModel.name)
            brandId = brandModel.xid
            bottomSheetDialogCategory!!.dismiss()

        }
    }

    override fun onClickAtButton(categoryModel: CategoryModel) {
        if(categoryModel != null) {
            acCategory.setText(categoryModel.name)
            categoryId = categoryModel.xid
            bottomSheetDialogCategory!!.dismiss()
        }
    }

    override fun onClickAtButton(unitModel: UnitModel) {
        if(unitModel != null) {
            acUnit.setText(unitModel.name)
            unitId = unitModel.xid
            bottomSheetDialogCategory!!.dismiss()

        }
    }

    override fun onClickAtButton(wareHouseModel: WareHouseModel) {
        if(wareHouseModel != null) {
            acWareHouse.setText(wareHouseModel.name)
            wareHouseId = wareHouseModel.xid
            bottomSheetDialogCategory!!.dismiss()

        }
    }

    override fun onResume() {
        super.onResume()
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

}