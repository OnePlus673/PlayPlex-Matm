package com.playplexmatm.microatm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.playplexmatm.R
import com.playplexmatm.activity.MainActivity
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.util.*
import kotlinx.android.synthetic.main.activity_matm_bank_details.*
import kotlinx.android.synthetic.main.activity_matm_document_upload.*
import kotlinx.android.synthetic.main.activity_matm_document_upload.custToolbar
import kotlinx.android.synthetic.main.activity_matm_document_upload.pbLoadData
import kotlinx.android.synthetic.main.activity_matm_document_upload.view.*
import kotlinx.android.synthetic.main.activity_matm_document_upload.view.ivBackBtn
import kotlinx.android.synthetic.main.activity_matm_onboarding.*
import org.json.JSONObject
import java.io.IOException

class MatmDocumentUploadActivity : AppCompatActivity() , AppApiCalls.OnAPICallCompleteListener {

    var fromImageView: String = ""
    private val PERMISSION_REQUEST_CODE = 200
    private val GALLERY = 1
    private var CAMERA = 2
    var encoded_adhaar_front_img: String = ""
    var encoded_adhaar_back_img: String = ""
    var imagePan: String = ""
    var imageCheque: String = ""

    var encoded_adhaar_front_img_filename: String = ""
    var encoded_adhaar_back_img_filename: String = ""
    var imagePan_filename: String = ""
    var imageCheque_filename: String = ""


    lateinit var userModel : UserModel
    var merchant_type_id = ""
    var application_date = ""
     var aggregator_application_number = ""
    var matm_user_status = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matm_document_upload)
        matm_user_status = AppPrefs.getStringPref("matm_user_status",this).toString()

        val gson = Gson()
        val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
        userModel = gson.fromJson(json, UserModel::class.java)

//        toast(AppPrefs.getStringPref("date",this).toString())
        if(matm_user_status.equals("PROCESSING"))
         fetchOnboardingDetailsApi(AppPrefs.getStringPref("merchant_ref_id",this).toString(),
             userModel.cus_id)


        custToolbar.ivBackBtn.setOnClickListener {
            onBackPressed()
        }

        val islogin: Boolean =
            AppPrefs.getBooleanPref(AppConstants.IS_LOGIN, this)
        if (islogin) {
            val gson = Gson()
            val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
            userModel = gson.fromJson(json, UserModel::class.java)

        }
        else
        {
//            toast("Login First")
        }

        ivAddFrontAadhar.setOnClickListener {

            if (checkPermission()) {
                fromImageView = "aadhaar_front"
                showPictureDialog()
            } else {
                requestPermission()
            }

        }

        ivAddBackAadhar.setOnClickListener {

            if (checkPermission()) {
                fromImageView = "aadhaar_back"
                showPictureDialog()
            } else {
                requestPermission()
            }

        }


        ivPanCard.setOnClickListener {

            if (checkPermission()) {
                fromImageView = "pan"
                showPictureDialog()
            } else {
                requestPermission()
            }
        }

        ivCancelledCheque.setOnClickListener {

            if (checkPermission()) {
                fromImageView = "cc"
                showPictureDialog()
            } else {
                requestPermission()
            }

        }


        btnSubmit.setOnClickListener {


            if(encoded_adhaar_front_img.isNullOrEmpty())
            {
                toast("Upload Aadhar Image")
            }
            else if(imagePan.isNullOrEmpty())
            {
                toast("Upload Pan Image")
            }
            else if(imageCheque.isNullOrEmpty())
            {
                toast("Upload Cancelled Cheque Image")
            }
            else

            {
                btnSubmit.isEnabled = false

                if(matm_user_status.equals("PROCESSING"))
                {
                    update_onboardMatmApi(
                        userModel.cus_id,
                        userModel.cus_mobile ,
                        AppPrefs.getStringPref("merchant_type",this).toString(),
                        AppPrefs.getStringPref("merchant_id",this).toString(),
                        AppPrefs.getStringPref("contact_name",this).toString(),
                        AppPrefs.getStringPref("legal_name",this).toString(),
                        AppPrefs.getStringPref("contact_mobile",this).toString(),
                        AppPrefs.getStringPref("contact_alternate_mobile",this).toString(),
                        AppPrefs.getStringPref("contact_email",this).toString(),
                        AppPrefs.getStringPref("brand_name",this).toString(),
                        AppPrefs.getStringPref("buisness_nature",this).toString(),
                        AppPrefs.getStringPref("buisness_type",this).toString(),
                        AppPrefs.getStringPref("established_year",this).toString(),
                        AppPrefs.getStringPref("pan_number",this).toString(),
                        AppPrefs.getStringPref("registered_mobile",this).toString(),
                        AppPrefs.getStringPref("registered_pin",this).toString(),
                        AppPrefs.getStringPref("registered_address",this).toString(),
                        AppPrefs.getStringPref("agreement_date",this).toString(),
                        AppPrefs.getStringPref("title_personal", this ).toString(),
                        AppPrefs.getStringPref("first_name_personal",this ).toString(),
                        AppPrefs.getStringPref("last_name_personal",this ).toString(),
                        AppPrefs.getStringPref("email_personal",this ).toString(),
                        AppPrefs.getStringPref("mobile_personal",this ).toString(),
                        AppPrefs.getStringPref("dob_personal",this ).toString(),
                        AppPrefs.getStringPref("address_personal",this ).toString(),
                        AppPrefs.getStringPref("pin_personal",this ).toString(),
                        AppPrefs.getStringPref("city_personal",this ).toString(),
                        AppPrefs.getStringPref("state_personal",this ).toString(),
                        AppPrefs.getStringPref("nationality_personal",this ).toString(),
                        AppPrefs.getStringPref("aadhaar_personal",this ).toString(),
                        AppPrefs.getStringPref("pan_personal",this ).toString(),
                        AppPrefs.getStringPref("merchant_bank",this ).toString(),
                        AppPrefs.getStringPref("merchant_account",this ).toString(),
                        AppPrefs.getStringPref("merchant_ifsc",this ).toString(),
                        AppPrefs.getStringPref("ifsc_code",this).toString(),
                        encoded_adhaar_front_img,
                        encoded_adhaar_back_img,
                        imagePan,
                        imageCheque,
                        merchant_type_id,
                        application_date,
                        aggregator_application_number,
                        AppPrefs.getStringPref("merchant_ref_id",this).toString(),
                        encoded_adhaar_front_img_filename,
                        encoded_adhaar_back_img_filename,
                        imagePan_filename,
                        imageCheque_filename,

                        )
                }
                else
                {
                    onboardMatmApi(
                        userModel.cus_mobile,
                        AppPrefs.getStringPref("merchant_type",this).toString(),
                        AppPrefs.getStringPref("merchant_id",this).toString(),
                        AppPrefs.getStringPref("contact_name",this).toString(),
                        AppPrefs.getStringPref("legal_name",this).toString(),
                        AppPrefs.getStringPref("contact_mobile",this).toString(),
                        AppPrefs.getStringPref("contact_alternate_mobile",this).toString(),
                        AppPrefs.getStringPref("contact_email",this).toString(),
                        AppPrefs.getStringPref("brand_name",this).toString(),
                        AppPrefs.getStringPref("buisness_nature",this).toString(),
                        AppPrefs.getStringPref("buisness_type",this).toString(),
                        AppPrefs.getStringPref("established_year",this).toString(),
                        AppPrefs.getStringPref("pan_number",this).toString(),
                        AppPrefs.getStringPref("registered_mobile",this).toString(),
                        AppPrefs.getStringPref("registered_pin",this).toString(),
                        AppPrefs.getStringPref("registered_address",this).toString(),
                        AppPrefs.getStringPref("agreement_date",this).toString(),
                        AppPrefs.getStringPref("title_personal", this ).toString(),
                        AppPrefs.getStringPref("first_name_personal",this ).toString(),
                        AppPrefs.getStringPref("last_name_personal",this ).toString(),
                        AppPrefs.getStringPref("email_personal",this ).toString(),
                        AppPrefs.getStringPref("mobile_personal",this ).toString(),
                        AppPrefs.getStringPref("dob_personal",this ).toString(),
                        AppPrefs.getStringPref("address_personal",this ).toString(),
                        AppPrefs.getStringPref("pin_personal",this ).toString(),
                        AppPrefs.getStringPref("city_personal",this ).toString(),
                        AppPrefs.getStringPref("state_personal",this ).toString(),
                        AppPrefs.getStringPref("nationality_personal",this ).toString(),
                        AppPrefs.getStringPref("aadhaar_personal",this ).toString(),
                        AppPrefs.getStringPref("pan_personal",this ).toString(),
                        AppPrefs.getStringPref("merchant_bank",this ).toString(),
                        AppPrefs.getStringPref("merchant_account",this ).toString(),
                        AppPrefs.getStringPref("merchant_ifsc",this ).toString(),
                        AppPrefs.getStringPref("ifsc_code",this).toString(),
                        encoded_adhaar_front_img,
                        encoded_adhaar_back_img,
                        imagePan,
                        imageCheque,
                        userModel.cus_id,
                        userModel.cus_email
                    )
                }

            }


        } // en of submit button


    }  // end of onCreate


    private fun fetchOnboardingDetailsApi(
        merchant_ref_id : String,
        cus_email : String
    ) {
        pbLoadData.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                AppConstants.FETCH_ONBOARDING_API,
                this
            )
            mAPIcall.fetchOnboardingDetails(merchant_ref_id,cus_email)
        } else {
            toast(getString(R.string.error_internet))
        }
    }

    private fun onboardMatmApi(
        cus_mobile: String,
        merchant_type : String,
        merchant_id : String,
        contact_name : String,
        legal_name : String,
        contact_mobile : String,
        contact_alternate_mobile : String,
        contact_email : String,
        brand_name : String,
        business_nature : String,
        business_type : String,
        established_year : String,
        pan_number : String,
        registered_mobile : String,
        registered_pin : String,
        registered_address : String,
        agreement_date : String,
        title_personal : String,
        first_name_personal : String,
        last_name_personal : String,
        email_personal : String,
        mobile_personal : String,
        dob_personal : String,
        address_personal : String,
        pin_personal : String,
        city_personal : String,
        state_personal : String,
        nationality_personal : String,
        aadhaar_personal : String,
        pan_personal : String,
        merchant_bank : String,
        marchant_account : String,
        merchant_ifsc : String,
        ifsc_code : String,
        img_aadhaar_front : String,
        img_aadhaar_back : String,
        img_pan : String,
        img_cc : String,
        cus_id : String,
        cus_email : String
    ) {
        pbLoadData.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                AppConstants.ONBOARD_MATM_API,
                this
            )
            mAPIcall.onboardMicroAtm(
                cus_mobile,
                merchant_type ,
                merchant_id ,
                contact_name ,
                legal_name ,
                contact_mobile ,
                contact_alternate_mobile ,
                contact_email ,
                brand_name ,
                business_nature ,
                business_type ,
                established_year ,
                pan_number ,
                registered_mobile ,
                registered_pin ,
                registered_address ,
                agreement_date,
                title_personal ,
                first_name_personal ,
                last_name_personal ,
                email_personal ,
                mobile_personal ,
                dob_personal ,
                address_personal ,
                pin_personal ,
                city_personal ,
                state_personal ,
                nationality_personal ,
                aadhaar_personal ,
                pan_personal ,
                merchant_bank ,
                marchant_account ,
                merchant_ifsc ,
                ifsc_code,
                img_aadhaar_front ,
                img_aadhaar_back ,
                img_pan ,
                img_cc,
                cus_id,
                cus_email
            )
        } else {
            toast(getString(R.string.error_internet))
        }
    }


    private fun update_onboardMatmApi(
        cus_id : String,
        cus_mobile: String,
        merchant_type : String,
        merchant_id : String,
        contact_name : String,
        legal_name : String,
        contact_mobile : String,
        contact_alternate_mobile : String,
        contact_email : String,
        brand_name : String,
        business_nature : String,
        business_type : String,
        established_year : String,
        pan_number : String,
        registered_mobile : String,
        registered_pin : String,
        registered_address : String,
        agreement_date : String,
        title_personal : String,
        first_name_personal : String,
        last_name_personal : String,
        email_personal : String,
        mobile_personal : String,
        dob_personal : String,
        address_personal : String,
        pin_personal : String,
        city_personal : String,
        state_personal : String,
        nationality_personal : String,
        aadhaar_personal : String,
        pan_personal : String,
        merchant_bank : String,
        marchant_account : String,
        merchant_ifsc : String,
        ifsc_code: String,
        img_aadhaar_front : String,
        img_aadhaar_back : String,
        img_pan : String,
        img_cc : String,
        merchant_type_id : String,
        application_date : String,
        aggregator_application_number : String,
        merchant_ref_id : String,
        aadhar_image_name_front : String,
        aadhar_image_name_back : String,
        pan_image_name : String,
        cc_image_name : String



    ) {
        pbLoadData.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                AppConstants.ONBOARD_MATM_API,
                this
            )
            mAPIcall.update_onboardMicroAtm(
                cus_id ,
                cus_mobile,
                merchant_type ,
                merchant_id ,
                contact_name ,
                legal_name ,
                contact_mobile ,
                contact_alternate_mobile ,
                contact_email ,
                brand_name ,
                business_nature ,
                business_type ,
                established_year ,
                pan_number ,
                registered_mobile ,
                registered_pin ,
                registered_address ,
                agreement_date,
                title_personal ,
                first_name_personal ,
                last_name_personal ,
                email_personal ,
                mobile_personal ,
                dob_personal ,
                address_personal ,
                pin_personal ,
                city_personal ,
                state_personal ,
                nationality_personal ,
                aadhaar_personal ,
                pan_personal ,
                merchant_bank ,
                marchant_account ,
                merchant_ifsc ,
                ifsc_code,
                img_aadhaar_front ,
                img_aadhaar_back ,
                img_pan ,
                img_cc,
                merchant_type_id,
                application_date,
                aggregator_application_number,
                merchant_ref_id,
                aadhar_image_name_front,
                aadhar_image_name_back,
                pan_image_name ,
                cc_image_name
            )
        } else {
            toast(getString(R.string.error_internet))
        }
    }


    //Camera function

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf(

            "Capture photo from Camera" , "Upload from Gallery"
        )
        pictureDialog.setItems(
            pictureDialogItems
        ) { _, which ->
            when (which) {

                0 -> takePhotoFromCamera()
                1 -> choosePhotoFromGallary()
            }
        }
        pictureDialog.show()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@MatmDocumentUploadActivity,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun checkPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            showPictureDialog()
            // Permission is not granted
            return false
        }
        return true
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) {
            return
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(getContentResolver(), contentURI)
                    // Toast.makeText(HomeActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();

                    if (fromImageView.equals("cc")) {
                        ivCancelledCheque.setImageBitmap(bitmap)
                        imageCheque = ImageUtil.convert(bitmap)
                    }
                    else  if (fromImageView.equals("pan")) {
                        ivPanCard.setImageBitmap(bitmap)
                        imagePan = ImageUtil.convert(bitmap)

                    } else if (fromImageView.equals("aadhaar_front")) {
                        ivAddFrontAadhar.setImageBitmap(bitmap)
                        encoded_adhaar_front_img = ImageUtil.convert(bitmap)

                    }
                    else if (fromImageView.equals("aadhaar_back")) {
                        ivAddBackAadhar.setImageBitmap(bitmap)
                        encoded_adhaar_back_img = ImageUtil.convert(bitmap)

                    }

                    // encodedBlankCheque = saveImage(bitmap)

                } catch (e: IOException) {
                    e.printStackTrace()
                    //Toast.makeText(HomeActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) {
            val thumbnail = data!!.extras!!["data"] as Bitmap?


            if (fromImageView.equals("cc")) {

                ivCancelledCheque.setImageBitmap(thumbnail)
                imageCheque = ImageUtil.convert(thumbnail!!)


            } else  if (fromImageView.equals("pan")) {
                ivPanCard.setImageBitmap(thumbnail)
                imagePan = ImageUtil.convert(thumbnail!!)

            } else if (fromImageView.equals("aadhaar_front")) {
                ivAddFrontAadhar.setImageBitmap(thumbnail)
                encoded_adhaar_front_img = ImageUtil.convert(thumbnail!!)

            }
            else if (fromImageView.equals("aadhaar_back")) {
                ivAddBackAadhar.setImageBitmap(thumbnail)
                encoded_adhaar_back_img = ImageUtil.convert(thumbnail!!)

            }
        }
    }

    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(AppConstants.ONBOARD_MATM_API)) {
            pbLoadData.visibility = View.GONE
            Log.e(AppConstants.ONBOARD_MATM_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)
            btnSubmit.isEnabled = true

            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {

                val cast = jsonObject.getJSONObject("result")
                val innerstatus = cast.getString("status")

                if(innerstatus.contains("true"))
                {
                    toast(cast.getString("message"))
                    val intent = Intent(this,MATMTestActivity::class.java)
                    startActivity(intent)

                }
                else
                {
//                    toast(cast.getString("message"))
                    showMessage(cast.getString("message"))

                }


            } else {

                    toast(messageCode.trim())
            }
        }

        if (flag.equals(AppConstants.FETCH_ONBOARDING_API)) {
            pbLoadData.visibility = View.GONE
            Log.e(AppConstants.FETCH_ONBOARDING_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)
            btnSubmit.isEnabled = true

            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {


                val aadhar_data = jsonObject.getJSONArray("aadhar_data")

                val adharObj = aadhar_data.getJSONObject(0)

                encoded_adhaar_front_img = adharObj.getString("aadhar_front_image")
                encoded_adhaar_front_img_filename = adharObj.getString("aadhar_image_name_front")
                Glide.with(this).load(encoded_adhaar_front_img).into(ivAddFrontAadhar)
                Log.e("adhaar url front",encoded_adhaar_front_img)

                encoded_adhaar_back_img = adharObj.getString("aadhar_back_image")
                encoded_adhaar_back_img_filename = adharObj.getString("aadhar_image_name_back")
                Glide.with(this).load(encoded_adhaar_back_img).into(ivAddBackAadhar)
                Log.e("adhaar url back",encoded_adhaar_back_img)



                val cast = jsonObject.getJSONObject("result").getJSONArray("documents")

                Log.e("image","inside cast")
                for( i in 0 until cast.length())
                {
                    Log.e("image",cast.getJSONObject(i).getString("name"))

                    if ( cast.getJSONObject(i).getString("name").equals("PAN CARD",true))
                    {
                        Log.e("image","inside pan")

                        val url = cast.getJSONObject(i).getJSONArray("urls").getJSONObject(0).getString("path")
                        Glide.with(this).load(url).into(ivPanCard)
                        Log.e("pan url",url)
                        imagePan = url
                        imagePan_filename = cast.getJSONObject(i).getJSONArray("urls").getJSONObject(0).getString("filename")
                    }



                    if ( cast.getJSONObject(i).getString("name").contains("Cancelled Cheque",true))
                    {
                        Log.e("image","inside cc")

                        val url = cast.getJSONObject(i).getJSONArray("urls").getJSONObject(0).getString("path")
                        Glide.with(this).load(url).into(ivCancelledCheque)
                        Log.e("cc url",url)
                        imageCheque = url
                        imageCheque_filename = cast.getJSONObject(i).getJSONArray("urls").getJSONObject(0).getString("filename")

                    }
                }

               val resultObj = jsonObject.getJSONObject("result")

                aggregator_application_number = resultObj.getJSONObject("sales_information").getString("aggregator_application_number")
                application_date = resultObj.getJSONObject("sales_information").getString("application_date")
                merchant_type_id = resultObj.getJSONObject("company_information").getString("merchant_type_id")




            }
            else {
                toast(messageCode.trim())
            }
        }


    }


    private fun showMessage(msg : String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Attention !")
        builder.setMessage(msg)
        builder.setPositiveButton("OK") { dialog, which ->

            dialog.dismiss()
        }

        val alert = builder.create()
        alert.show()
    }


}