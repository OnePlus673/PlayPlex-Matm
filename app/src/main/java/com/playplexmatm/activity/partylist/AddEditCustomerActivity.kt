package com.playplexmatm.activity.partylist

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.activity.settings.AddEditWareHouseSettingActivity
import com.playplexmatm.adapter.CustomerAdressAdpater
import com.playplexmatm.model.customers.CustomerModel
import com.playplexmatm.model.warehouse.WareHouseModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.Constants
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.activity_add_edit_customer.*
import kotlinx.android.synthetic.main.activity_add_edit_customer.tabLayout
import kotlinx.android.synthetic.main.activity_add_edit_customer.viewPager
import kotlinx.android.synthetic.main.activity_add_edit_supplier.*
import kotlinx.android.synthetic.main.layout_list_category.view.*
import kotlinx.android.synthetic.main.layout_listview_bottomsheet.view.*
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

class AddEditCustomerActivity : AppCompatActivity(), View.OnClickListener,
   CustomerAdresssFragment.OnTextChangeListener,
   CustomerGstFragment.OnTextChangeListener
   {

    lateinit var mContext: AddEditCustomerActivity
    lateinit var ivBack: AppCompatImageView
    lateinit var tvTitle: AppCompatTextView
    lateinit var pbLoadData: ProgressBar
    lateinit var tvSave: TextView
    lateinit var acWareHouseName: AutoCompleteTextView
    lateinit var apiInterface: ApiInterface
    private var wareHouseList: ArrayList<WareHouseModel> = arrayListOf()
    private var wareHouseStringList: ArrayList<String> = arrayListOf()
    private var blanceTypeList: ArrayList<String> = arrayListOf()
    lateinit var edtName: EditText
    lateinit var edtEmail: EditText
    lateinit var edtPhoneNumber: EditText
//    lateinit var edtAddress: EditText
//    lateinit var edtTaxNumber: EditText
//    lateinit var edtBillingAddress: EditText
//    lateinit var frameImage: FrameLayout
//    lateinit var ivProfileImage: ImageView
    private var wareHouseId: String = ""
    private var blanceType: String = ""
    private var profilepath: String = ""
    private var file: String = ""
    private var fileUrl: String = ""
    private var mFrom: String = ""
    lateinit var customerModel: CustomerModel
       var bottomSheetDialogCategory: BottomSheetDialog? = null


    var tax = ""
    var openingBalance = ""
    var balanceType = ""
    var creditPeriod = ""
    var creditLimit = ""
    var adress = ""
    var billingaddress = ""

    companion object {
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_customer)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        //supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        tvTitle = findViewById(R.id.tvTitle)
        pbLoadData = findViewById(R.id.pbLoadData)
        acWareHouseName = findViewById(R.id.acWareHouseCustomer)
        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber)
//        edtAddress = findViewById(R.id.edtAddress)
//        edtTaxNumber = findViewById(R.id.edtTaxNumber)
//        edtBillingAddress = findViewById(R.id.edtBillingAddress)
        tvSave = findViewById(R.id.tvSave)
//        frameImage = findViewById(R.id.frameImage)
//        ivProfileImage = findViewById(R.id.ivProfileImage)


        ivAddWareHouse.setOnClickListener {
            val intent = Intent(this,AddEditWareHouseSettingActivity::class.java)
            startActivity(intent)
        }

        if (intent.extras != null) {
            mFrom = intent.getStringExtra(Constants.mFrom)!!
            if (mFrom == Constants.isEdit) {
                tvTitle.text = "Update Customer"
                customerModel = Gson().fromJson(
                    intent.getStringExtra(Constants.customerModel), CustomerModel::class.java
                )
                setData(customerModel)
            } else {
                tvTitle.text = "Add Customer"
            }
        }

        if (InternetConnection.checkConnection(mContext)) {
            setBalanceData()
            mNetworkCallWareHouseAPI()
        } else {
            Toast.makeText(
                mContext,
                mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }


        title = "Address"

        if (mFrom != Constants.isEdit) {
            tabLayout.addTab(tabLayout.newTab().setText("Addresses"))
            tabLayout.addTab(tabLayout.newTab().setText("GST Details"))
            tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            val adapter = CustomerAdressAdpater(
                this, supportFragmentManager,
                tabLayout.tabCount, adress, billingaddress,
                openingBalance, balanceType, creditPeriod, creditLimit
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
        }

    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        tvSave.setOnClickListener(this)
//        frameImage.setOnClickListener(this)

//        acBalanceType.setOnTouchListener { v, event ->
//            acBalanceType.showDropDown()
//            false
//        }

        acWareHouseName.setOnTouchListener { v, event ->
            acWareHouseName.showDropDown()
            false
        }

//        edtName.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(
//                s: CharSequence?,
//                start: Int,
//                count: Int,
//                after: Int
//            ) {
//            }
//
//            override fun onTextChanged(
//                s: CharSequence,
//                start: Int,
//                before: Int,
//                count: Int
//            ) {
//
//                if(!s.toString().isNullOrEmpty())
//                {
//                    llAddForm.visibility = View.VISIBLE
//                    tvSave.isEnabled = true
//                }
//                else
//                {
//                    llAddForm.visibility = View.GONE
//                    tvSave.isEnabled = false
//
//                }
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//
//            }
//        })




    }

    private fun setData(customerModel: CustomerModel) {
//        Glide.with(mContext).load(customerModel.profile_image_url).into(ivProfileImage)
        acWareHouseName.setText(customerModel.details.warehouse.name)
        wareHouseId = customerModel.details.warehouse.xid

        edtName.setText(customerModel.name)
        edtEmail.setText(customerModel.email)
        edtPhoneNumber.setText(customerModel.phone)
        blanceType = customerModel.details.opening_balance_type
        adress = customerModel.address
        billingaddress = customerModel.shipping_address
        openingBalance = customerModel.details.opening_balance
        balanceType = customerModel.details.opening_balance_type
        creditPeriod = customerModel.details.credit_period
        creditLimit = customerModel.details.credit_limit

        file = customerModel.profile_image
        fileUrl = customerModel.profile_image_url

        tabLayout.addTab(tabLayout.newTab().setText("Address"))
        tabLayout.addTab(tabLayout.newTab().setText("GST Details"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = CustomerAdressAdpater(
            this, supportFragmentManager,
            tabLayout.tabCount,customerModel.address,customerModel.shipping_address,
            customerModel.details.opening_balance,
            customerModel.details.opening_balance_type,
            customerModel.details.credit_period,
            customerModel.details.credit_limit)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.frameImage -> {
                if (checkPermission()) {
                    alertDialogForImagePicker()
                } else {
                    checkPermission()
                }

            }
            R.id.tvSave -> {

//                Toast.makeText(this,"Button Clicked" ,Toast.LENGTH_SHORT).show()

                Log.e("CheckPoint","Inside clicklistener")


                if (InternetConnection.checkConnection(mContext)) {

                    Log.e("CheckPoint","Inside InternetConnection")

                    if (isValidate()) {
                        if (mFrom.equals(Constants.isEdit)) {
                            mNetworkCallUpdateCustomerAPI()
                            Log.e("CheckPoint","Inside update Customer")
                        } else {
                            Log.e("CheckPoint","Inside Add Customer")
                            mNetworkCallAddCustomerAPI()
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
        }
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
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
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
                        if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {
                            alertDialogForImagePicker()
                        } else {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this, Manifest.permission.CAMERA
                                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            ) {
                                ActivityCompat.requestPermissions(
                                    this, arrayOf(Manifest.permission.CAMERA), requestCode
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
//            ImagePicker.with(mContext).compress(1024).maxResultSize(1080, 1080).cameraOnly()
//                .start(Constants.REQ_PICK_IMAGE)
            dialogView.dismiss()
        }
        txtGallery.setOnClickListener { v: View? ->
//            ImagePicker.with(mContext).compress(1024).maxResultSize(1080, 1080).galleryOnly()
//                .start(Constants.REQ_PICK_IMAGE)
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
//            ivProfileImage.setImageURI(uri)
            mNetworkCallUploadFileAPI()
        }
    }


    private fun mNetworkCallUploadFileAPI() {
        pbLoadData.visibility = View.VISIBLE
        val folder: RequestBody = RequestBody.create(
            MediaType.parse("multipart/form-data"), "user"
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
                                    mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT
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


    private fun isValidate(): Boolean {
        Log.e("CheckPoint","Inside isValidate")

        var isValid = true
        if (wareHouseId.isEmpty()) {
            Toast.makeText(mContext, "Please select warehouse name", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (edtName.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter name", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (edtPhoneNumber.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter phone number", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun mNetworkCallAddCustomerAPI() {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.addCustomers(
            "customers",
            wareHouseId,
            edtName.text.toString().trim(),
            edtEmail.text.toString().trim(),
            file,
            fileUrl,
            edtPhoneNumber.text.toString().trim(),
             adress,
            "enabled",
            billingaddress,
            openingBalance,
            balanceType,
            creditPeriod,
            creditLimit,
            tax
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {


                        val jsonobject = JSONObject(response.body().toString())

                        Log.e("Response",jsonobject.toString())


                        if (jsonobject.optBoolean("status")) {
                            Toast.makeText(
                                mContext, jsonobject.optString("message"), Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent)
                            finish()

                        } else {
                            Toast.makeText(
                                mContext, jsonobject.optString("message"), Toast.LENGTH_SHORT
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


    private fun mNetworkCallUpdateCustomerAPI() {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.updateCustomers(
            customerModel.xid,
            "customers",
            wareHouseId,
            edtName.text.toString().trim(),
            edtEmail.text.toString().trim(),
            file,
            fileUrl,
            edtPhoneNumber.text.toString().trim(),
            adress,
            "enabled",
            billingaddress,
            openingBalance,
            blanceType,
            creditPeriod,
            creditLimit,
            tax
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonobject = JSONObject(response.body().toString())
                        if (jsonobject.optBoolean("status")) {
                            Toast.makeText(
                                mContext, jsonobject.optString("message"), Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent)
                            finish()

                        } else {
                            Toast.makeText(
                                mContext, jsonobject.optString("message"), Toast.LENGTH_SHORT
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


    private fun setBalanceData() {
        blanceTypeList.add("Receive")
        blanceTypeList.add("Pay")

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, blanceTypeList)

//        acBalanceType.threshold = 0 //will start working from first character
//
//        acBalanceType.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView
//
//        acBalanceType.setOnItemClickListener { parent, view, position, id ->
//            blanceType = blanceTypeList[position]
//        }
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
                            setAdapter(wareHouseList)
                        } else {
                            Toast.makeText(
                                mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT
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

    private fun setAdapter(wareHouseList: ArrayList<WareHouseModel>) {
        for (i in 0 until wareHouseList.size) {
            wareHouseStringList.add(wareHouseList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, wareHouseStringList)

        acWareHouseName.threshold = 0 //will start working from first character

        acWareHouseName.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acWareHouseName.setOnItemClickListener { parent, view, position, id ->
            wareHouseId = wareHouseList[position].xid
        }
    }

    override fun address(billingAddressP: String?, addressP: String?) {
        adress = addressP.toString()
        billingaddress = billingAddressP.toString()
    }

   override fun tax(taxNumberP: String?, openingBalanceP: String?,
            balanceTypeP: String?, creditPeriodP: String?,
            creditLimitP: String?) {
       tax = taxNumberP.toString()
       openingBalance = openingBalanceP.toString()
       balanceType = balanceTypeP.toString()
       creditPeriod = creditPeriodP.toString()
       creditLimit = creditLimitP.toString()

   }


   }