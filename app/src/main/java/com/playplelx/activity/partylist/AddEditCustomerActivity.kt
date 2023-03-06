package com.playplelx.activity.partylist

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
import com.playplelx.model.customers.CustomerModel
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

class AddEditCustomerActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mContext: AddEditCustomerActivity
    lateinit var ivBack: AppCompatImageView
    lateinit var tvTitle: AppCompatTextView
    lateinit var pbLoadData: ProgressBar
    lateinit var tvSave: TextView
    lateinit var acWareHouseName: AutoCompleteTextView
    lateinit var acBalanceType: AutoCompleteTextView
    lateinit var apiInterface: ApiInterface
    private var wareHouseList: ArrayList<WareHouseModel> = arrayListOf()
    private var wareHouseStringList: ArrayList<String> = arrayListOf()
    private var blanceTypeList: ArrayList<String> = arrayListOf()
    lateinit var edtName: EditText
    lateinit var edtEmail: EditText
    lateinit var edtPhoneNumber: EditText
    lateinit var edtAddress: EditText
    lateinit var edtTaxNumber: EditText
    lateinit var edtOpeningBalance: EditText
    lateinit var edtCreditPeriod: EditText
    lateinit var edtCreditLimit: EditText
    lateinit var edtBillingAddress: EditText
    lateinit var frameImage: FrameLayout
    lateinit var ivProfileImage: ImageView
    private var wareHouseId: String = ""
    private var blanceType: String = ""
    private var profilepath: String = ""
    private var file: String = ""
    private var fileUrl: String = ""
    private var mFrom: String = ""
    lateinit var customerModel: CustomerModel

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
        supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        tvTitle = findViewById(R.id.tvTitle)
        pbLoadData = findViewById(R.id.pbLoadData)
        acWareHouseName = findViewById(R.id.acWareHouseName)
        acBalanceType = findViewById(R.id.acBalanceType)
        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber)
        edtAddress = findViewById(R.id.edtAddress)
        edtTaxNumber = findViewById(R.id.edtTaxNumber)
        edtOpeningBalance = findViewById(R.id.edtOpeningBalance)
        edtCreditPeriod = findViewById(R.id.edtCreditPeriod)
        edtCreditLimit = findViewById(R.id.edtCreditLimit)
        edtBillingAddress = findViewById(R.id.edtBillingAddress)
        tvSave = findViewById(R.id.tvSave)
        frameImage = findViewById(R.id.frameImage)
        ivProfileImage = findViewById(R.id.ivProfileImage)

        if (intent.extras != null) {
            mFrom = intent.getStringExtra(Constants.mFrom)!!
            if (mFrom == Constants.isEdit) {
                tvTitle.text = "Update Customer"
                customerModel = Gson().fromJson(
                    intent.getStringExtra(Constants.customerModel),
                    CustomerModel::class.java
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
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        tvSave.setOnClickListener(this)
        frameImage.setOnClickListener(this)

        acBalanceType.setOnTouchListener { v, event ->
            acBalanceType.showDropDown()
            false
        }


        acWareHouseName.setOnTouchListener { v, event ->
            acWareHouseName.showDropDown()
            false
        }

    }

    private fun setData(customerModel: CustomerModel) {
        Glide.with(mContext).load(customerModel.profile_image_url).into(ivProfileImage)
        acWareHouseName.setText(customerModel.details.warehouse.name)
        wareHouseId=customerModel.details.warehouse.xid

        edtName.setText(customerModel.name)
        edtEmail.setText(customerModel.email)
        edtPhoneNumber.setText(customerModel.phone)
        edtAddress.setText(customerModel.address)
        //edtTaxNumber.setText(customerModel.details.ta)
        edtOpeningBalance.setText(customerModel.details.opening_balance)
        acBalanceType.setText(customerModel.details.opening_balance_type)
        blanceType=customerModel.details.opening_balance_type
        edtCreditPeriod.setText(customerModel.details.credit_period)
        edtCreditLimit.setText(customerModel.details.credit_limit)
        edtBillingAddress.setText(customerModel.shipping_address)

        file=customerModel.profile_image
        fileUrl=customerModel.profile_image_url
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
                if (InternetConnection.checkConnection(mContext)) {
                    if (isValidate()) {
                        if (mFrom.equals(Constants.isEdit)) {
                            mNetworkCallUpdateCustomerAPI()

                        } else {
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
            ivProfileImage.setImageURI(uri)
            mNetworkCallUploadFileAPI()
        }
    }


    private fun mNetworkCallUploadFileAPI() {
        pbLoadData.visibility = View.VISIBLE
        val folder: RequestBody = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            "user"
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


    private fun isValidate(): Boolean {
        var isValid = true
        if (fileUrl.isEmpty()) {
            Toast.makeText(mContext, "Please upload file", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (wareHouseId.isEmpty()) {
            Toast.makeText(mContext, "Please select warehouse name", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (edtName.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter name", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (edtEmail.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter email", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (edtPhoneNumber.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter phone numbe", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (edtAddress.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter address", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (edtTaxNumber.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter tax number", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (edtOpeningBalance.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter opening balance", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (blanceType.isEmpty()) {
            Toast.makeText(mContext, "Please select balance type", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (edtCreditPeriod.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter credit period", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (edtCreditLimit.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter credit limit", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (edtBillingAddress.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter billing address", Toast.LENGTH_SHORT).show()
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
            edtAddress.text.toString().trim(),
            "enabled",
            edtBillingAddress.text.toString().trim(),
            edtOpeningBalance.text.toString().trim(),
            blanceType,
            edtCreditPeriod.text.toString().trim(),
            edtCreditLimit.text.toString().trim(),
            edtTaxNumber.text.toString().trim()
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonobject = JSONObject(response.body().toString())
                        if (jsonobject.optBoolean("status")) {
                            Toast.makeText(
                                mContext, jsonobject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent=Intent()
                            setResult(Activity.RESULT_OK,intent)
                            finish()

                        } else {
                            Toast.makeText(
                                mContext, jsonobject.optString("message"),
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
            edtAddress.text.toString().trim(),
            "enabled",
            edtBillingAddress.text.toString().trim(),
            edtOpeningBalance.text.toString().trim(),
            blanceType,
            edtCreditPeriod.text.toString().trim(),
            edtCreditLimit.text.toString().trim(),
            edtTaxNumber.text.toString().trim()
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonobject = JSONObject(response.body().toString())
                        if (jsonobject.optBoolean("status")) {
                            Toast.makeText(
                                mContext, jsonobject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent=Intent()
                            setResult(Activity.RESULT_OK,intent)
                            finish()

                        } else {
                            Toast.makeText(
                                mContext, jsonobject.optString("message"),
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


    private fun setBalanceData() {
        blanceTypeList.add("Receive")
        blanceTypeList.add("Pay")

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, blanceTypeList)

        acBalanceType.threshold = 0 //will start working from first character

        acBalanceType.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acBalanceType.setOnItemClickListener { parent, view, position, id ->
            blanceType = blanceTypeList[position]
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
                            setAdapter(wareHouseList)
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
}