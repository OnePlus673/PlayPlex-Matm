package com.playplexmatm.activity.itemlist

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.playplexmatm.R
import com.playplexmatm.model.brand.BrandModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.Constants
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.activity_add_edit_brand.*
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

class AddEditBrandActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mContext: AddEditBrandActivity
    lateinit var apiInterface: ApiInterface
    lateinit var ivBack: ImageView
    lateinit var frameCategoryImage: FrameLayout
    lateinit var ivUplaodImage: ImageView
    lateinit var edtBrandName: TextView
    lateinit var tvSave: TextView
    lateinit var pbLoadData: ProgressBar
    private var profilepath: String = ""
    private var file: String = ""
    private var fileUrl: String = ""
    lateinit var brandModel: BrandModel
    private var mFrom: String = ""
    lateinit var tvTitle: TextView
    private val PERMISSION_REQUEST_CODE = 200
    private var mUri: Uri? = null
    private val OPERATION_CAPTURE_PHOTO = 1
    private val OPERATION_CHOOSE_PHOTO = 2

    companion object {
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_brand)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        //supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        pbLoadData = findViewById(R.id.pbLoadData)
        ivBack = findViewById(R.id.ivBack)
        tvTitle = findViewById(R.id.tvTitle)
        frameCategoryImage = findViewById(R.id.frameCategoryImage)
        ivUplaodImage = findViewById(R.id.ivUploadImage)
        edtBrandName = findViewById(R.id.edtBrandName)
        tvSave = findViewById(R.id.tvSave)

        if (intent.extras != null) {
            mFrom = intent.getStringExtra(Constants.mFrom)!!
            if (mFrom == Constants.isEdit) {
                tvTitle.text = mContext.resources.getString(R.string.str_update_brand)

                brandModel = Gson().fromJson(
                    intent.getStringExtra(Constants.brandModel), BrandModel::class.java
                )
                setData(brandModel)
            } else {
                tvTitle.text = mContext.resources.getString(R.string.str_add_brand)
            }


        }
    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        frameCategoryImage.setOnClickListener(this)
        tvSave.setOnClickListener(this)

        edtBrandName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    edtSlug.setText(edtBrandName.text.toString())
                } else {
                    edtSlug.setText(edtBrandName.text.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun setData(brandModel: BrandModel) {
        edtBrandName.text = brandModel.name
        file = brandModel.image
        fileUrl = brandModel.image_url
        if (brandModel.image_url.isNotEmpty()) {
            Glide.with(mContext).load(brandModel.image_url).into(ivUplaodImage)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.frameCategoryImage -> {
                if (checkPermission()) {
                    alertDialogForImagePicker()
                } else {
                    checkPermission()
                }
            }
            R.id.tvSave -> {
                if (InternetConnection.checkConnection(mContext)) {
                    if (isValidate()) {
                        if (mFrom == Constants.isEdit) {
                            updateBrand()
                        } else {
                            addBrand()
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
            ImagePicker.with(mContext).compress(1024).maxResultSize(1080, 1080).cameraOnly()
                .start(Constants.REQ_PICK_IMAGE)
            dialogView.dismiss()
        }
        txtGallery.setOnClickListener { v: View? ->
            ImagePicker.with(mContext).compress(1024).maxResultSize(1080, 1080).galleryOnly()
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
            ivUplaodImage.setImageURI(uri)
            mNetworkCallUploadFileAPI()
        }
    }


    private fun mNetworkCallUploadFileAPI() {
        pbLoadData.visibility = View.VISIBLE
        val folder: RequestBody = RequestBody.create(
            MediaType.parse("multipart/form-data"), "brand"
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
        var isValid = true
        if (edtBrandName.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter brand name", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        return isValid
    }

    private fun addBrand() {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.addBrand(
            edtBrandName.text.toString().trim(),
            edtBrandName.text.toString().trim().lowercase(),
            file,
            fileUrl
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            Toast.makeText(
                                mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        } else {
                            Toast.makeText(
                                mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        pbLoadData.visibility = View.GONE
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

    private fun updateBrand() {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.updateBrand(
            brandModel.xid,
            edtBrandName.text.toString().trim(),
            edtBrandName.text.toString().trim().lowercase(),
            file,
            fileUrl
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            Toast.makeText(
                                mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        } else {
                            Toast.makeText(
                                mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        pbLoadData.visibility = View.GONE
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

}