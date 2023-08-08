package com.playplexmatm.microatm

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.playplexmatm.R
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.model.MerchantTypeModel
import com.playplexmatm.model.PincodeModel
import com.playplexmatm.util.AppCommonMethods
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.AppPrefs
import com.playplexmatm.util.toast
import kotlinx.android.synthetic.main.activity_matm_onboarding.*
import kotlinx.android.synthetic.main.activity_matm_onboarding.btnNextUserDetails
import kotlinx.android.synthetic.main.activity_matm_onboarding.custToolbar
import kotlinx.android.synthetic.main.activity_matm_onboarding.etCity
import kotlinx.android.synthetic.main.activity_matm_onboarding.etPanNumber
import kotlinx.android.synthetic.main.activity_matm_onboarding.etState
import kotlinx.android.synthetic.main.activity_matm_onboarding.pbLoadData
import kotlinx.android.synthetic.main.activity_matm_onboarding.spinnerPincode
import kotlinx.android.synthetic.main.activity_matm_onboarding.view.*
import kotlinx.android.synthetic.main.activity_matm_personal_details.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class MatmOnboardingActivity : AppCompatActivity(), AppApiCalls.OnAPICallCompleteListener {

   lateinit var merchantCategoryModel : MerchantTypeModel
   var merchantTypeModelArrayList = ArrayList<MerchantTypeModel>()
   var merchantTypeStringList = ArrayList<String>()
   var selectedMerchantTypeId = ""
   var selectedMerchantType = ""

    lateinit var pincodeModel: PincodeModel
    var pincodeModelArrayList = ArrayList<PincodeModel>()
    var pincodeStringList = ArrayList<String>()
    lateinit var selectedPincode : String
    lateinit var selectedPincodeId : String
    var onBoardStatus = ""
    var selectedTitle = ""

    var matm_user_status = ""
    lateinit var userModel : UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matm_onboarding)

        val gson = Gson()
        val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
        userModel = gson.fromJson(json, UserModel::class.java)

//        onBoardStatus = intent.getStringExtra("OnBoardStatus").toString()
        matm_user_status = AppPrefs.getStringPref("matm_user_status",this).toString()



        getMerchantCategory()



        custToolbar.ivBackBtn.setOnClickListener { onBackPressed() }


        etEstablishedYear.setOnClickListener {
            datePicker()
        }

        etAgreementDate.setOnClickListener {
            datePicker2()
        }

        btnNextUserDetails.setOnClickListener {

            val intent = Intent(this, MatmPersonalDetailsActivity::class.java)
            startActivity(intent)

            if(etContactName.text.toString().isNullOrEmpty())
            {
                etContactName.requestFocus()
                etContactName.error = "Invalid Name"
            }
            else if(etLegalName.text.toString().isNullOrEmpty())
            {
                etLegalName.requestFocus()
                etLegalName.error = "Invalid Name"
            }
           else if(!AppCommonMethods.checkForMobile(etContactMobile))
            {
                etContactMobile.requestFocus()
                etContactMobile.error = "Invalid Mobile"
            }
            else if(!AppCommonMethods.checkForMobile(etContactAlternateMobile))
            {
                etContactAlternateMobile.requestFocus()
                etContactAlternateMobile.error = "Invalid Mobile"
            }

            else if(!AppCommonMethods.checkForEmail(etContactEmail))
            {
                etContactEmail.requestFocus()
                etContactEmail.error = "Invalid Email"
            }
            else if(etBrandName.text.toString().isNullOrEmpty())
            {
                etBrandName.requestFocus()
                etBrandName.error = "Invalid Name"
            }
            else if(etBusinessNature.text.toString().isNullOrEmpty())
            {
                etBusinessNature.requestFocus()
                etBusinessNature.error = "Invalid nature"
            }
           else if(etEstablishedYear.text.toString().isNullOrEmpty())
            {
                etEstablishedYear.requestFocus()
                etEstablishedYear.error = "Invalid year"
            }
            else if(!AppCommonMethods.checkForPan(etPanNumber))
            {
                etPanNumber.requestFocus()
                etPanNumber.error = "Invalid Pan"
            }
            else if(!AppCommonMethods.checkForMobile(etRegisterMobile))
            {
                etRegisterMobile.requestFocus()
                etRegisterMobile.error = "Invalid Mobile"
            }
            else if(etAgreementDate.text.toString().isNullOrEmpty())
            {
                etAgreementDate.requestFocus()
                etAgreementDate.error = "Invalid Date"
            }
            else
            {


                AppPrefs.putStringPref("merchant_type",spinnerMerchantType.selectedItem.toString(),this)
                AppPrefs.putStringPref("merchant_id",selectedMerchantTypeId,this)
                AppPrefs.putStringPref("contact_name",etContactName.text.toString(),this)
                AppPrefs.putStringPref("legal_name",etLegalName.text.toString(),this)
                AppPrefs.putStringPref("contact_mobile",etContactMobile.text.toString(),this)
                AppPrefs.putStringPref("contact_alternate_mobile",etContactAlternateMobile.text.toString(),this)
                AppPrefs.putStringPref("contact_email",etContactEmail.text.toString(),this)
                AppPrefs.putStringPref("brand_name",etBrandName.text.toString(),this)
                AppPrefs.putStringPref("buisness_nature",etBusinessNature.text.toString(),this)
                AppPrefs.putStringPref("buisness_type",spinnerBusinessType.selectedItem.toString(),this)
                AppPrefs.putStringPref("established_year",etEstablishedYear.text.toString(),this)
                AppPrefs.putStringPref("pan_number",etPanNumber.text.toString(),this)
                AppPrefs.putStringPref("registered_mobile",etRegisterMobile.text.toString(),this)
                AppPrefs.putStringPref("registered_pin",selectedPincodeId,this)
                AppPrefs.putStringPref("registered_address",etRegisterAddress.text.toString(),this)
                AppPrefs.putStringPref("agreement_date",etAgreementDate.text.toString(),this)


                val intent = Intent(this, MatmPersonalDetailsActivity::class.java)
                startActivity(intent)
            }



        }
    }

    fun datePicker() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var dpd =
            DatePickerDialog(this, { view, mYear, mMonth, mDay ->
                val mmMonth = mMonth + 1
                val date = "$mYear/$mmMonth/$mDay"

                etEstablishedYear.setText(
                    AppCommonMethods.convertDateFormat(
                        "yyyy/MM/dd",
                        "yyyy-MM-dd", date
                    ).toString()
                )

//                dob = AppCommonMethods.convertDateFormat(
//                    "yyyy/MM/dd",
//                    "yyyy-MM-dd", date
//                ).toString()

            }, year, month, day)
        dpd.show()
    }


    fun datePicker2() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var dpd =
            DatePickerDialog(this, { view, mYear, mMonth, mDay ->
                val mmMonth = mMonth + 1
                val date = "$mYear/$mmMonth/$mDay"

                etAgreementDate.setText(
                    AppCommonMethods.convertDateFormat(
                        "yyyy/MM/dd",
                        "yyyy-MM-dd", date
                    ).toString()
                )

//                dob = AppCommonMethods.convertDateFormat(
//                    "yyyy/MM/dd",
//                    "yyyy-MM-dd", date
//                ).toString()

            }, year, month, day)
        dpd.show()
    }



    private fun pincodeApi(
    ) {
        pbLoadData.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                AppConstants.PINCODE_API,
                this
            )
            mAPIcall.getPinCode()
        } else {
            toast(getString(R.string.error_internet))
        }
    }

    private fun getMerchantCategory(
    ) {
        pbLoadData.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                AppConstants.GET_MERCHANT_CAT_API,
                this
            )
            mAPIcall.getMerchantCategory()
        } else {
            toast(getString(R.string.error_internet))
        }
    }


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



    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(AppConstants.GET_MERCHANT_CAT_API)) {
            pbLoadData.visibility = View.GONE
            Log.e(AppConstants.GET_MERCHANT_CAT_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)


            //   val token = jsonObject.getString(AppConstants.TOKEN)

            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {

                val cast = jsonObject.getJSONArray("result")

                for(i in 0  until cast.length() )
                {

                    val notifyObjJson = cast.getJSONObject(i)

                    merchantCategoryModel = Gson().fromJson(notifyObjJson.toString(),MerchantTypeModel::class.java)
                    merchantTypeModelArrayList.add(merchantCategoryModel)
                    merchantTypeStringList.add(merchantCategoryModel.description)

                }

                spinnerMerchantType.adapter = ArrayAdapter(this,R.layout.spinner,merchantTypeStringList)


                spinnerMerchantType.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {

                            selectedMerchantType = merchantTypeStringList[position]
                            selectedMerchantTypeId = getIdByDesc(selectedMerchantType)

//                    toast(selectedMerchantType)

                        }

                    }

                pincodeApi()


            } else {

                    toast(messageCode.trim())
            }
        }

        if (flag.equals(AppConstants.PINCODE_API)) {
            pbLoadData.visibility = View.GONE
            Log.e(AppConstants.PINCODE_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)

            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {

                val cast = jsonObject.getJSONArray("result")

                for(i in 0 until cast.length())
                {
                    val notifyObjJson = cast.getJSONObject(i)
                    pincodeModel = Gson().fromJson(notifyObjJson.toString(), PincodeModel::class.java )
                    pincodeModelArrayList.add(pincodeModel)
                    pincodeStringList.add(pincodeModel.pincode + "  (" +  pincodeModel.state + " )" )
                }

                spinnerPincode.adapter = ArrayAdapter(this, R.layout.spinner,pincodeStringList)

                spinnerPincode.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {

                            selectedPincode = pincodeStringList[position]
                            selectedPincodeId = getIdBycode(selectedPincode)

                            Log.e("PincodeId",selectedPincodeId)

                            etState.setText( pincodeModelArrayList[position].state )

                            etCity.setText(pincodeModelArrayList[position].city)

//                    toast(selectedMerchantType)

                        }

                    }

                if(matm_user_status.equals("PROCESSING"))
                    fetchOnboardingDetailsApi(AppPrefs.getStringPref("merchant_ref_id",this).toString(),
                        userModel.cus_id
                        )


            }
            else {
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

            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {

               val cast = jsonObject.getJSONObject("result").getJSONObject("company_information")
               val cast2 = jsonObject.getJSONObject("result").getJSONObject("sales_information")
               val legal_name = cast.getString("legal_name")
               val brand_name = cast.getString("brand_name")
                val registered_address = cast.getString("registered_address")
                val registered_pincode_id    =  cast.getString("registered_pincode")
                val  pan   =  cast.getString("pan")
                val  business_type   =  cast.getString("business_type")
                val  established_year   =  cast.getString("established_year")
                val  registered_number   =  cast.getString("registered_number")
                val  business_nature   =  cast.getString("business_nature")
                val  merchant_category_code_id   =  cast.getString("merchant_category_code_id")
                val  merchant_type_id   =  cast.getString("merchant_type_id")
                val   contact_name  =  cast.getString("contact_name")
                val   contact_mobile  =  cast.getString("contact_mobile")
                val   contact_alternate_mobile  =  cast.getString("contact_alternate_mobile")
                val   contact_email  =  cast.getString("contact_email")

                etContactName.setText(contact_name)
                etLegalName.setText(legal_name)
                etContactMobile.setText(contact_mobile)
                etContactMobile.setText(contact_mobile)
                etContactAlternateMobile.setText(contact_alternate_mobile)
                etContactEmail.setText(contact_email)
                etBrandName.setText(brand_name)
                etBusinessNature.setText(business_nature)
                etPanNumber.setText(pan)
                etRegisterMobile.setText(registered_number)
                etRegisterAddress.setText(registered_address)
                etEstablishedYear.setText(established_year)
                etAgreementDate.setText(cast2.getString("aggreement_date"))
                spinnerPincode.setSelection(getPositionById(registered_pincode_id))
                spinnerMerchantType.setSelection( getCatIdPosition(merchant_category_code_id) )


            }
            else {
                toast(messageCode.trim())
            }
        }


    }


   private fun getIdByDesc(desc : String) : String
   {

       for ( i in 0 until merchantTypeModelArrayList.size )
       {
           if(merchantTypeModelArrayList[i].description.equals(desc) )
               return merchantTypeModelArrayList[i]._id
       }

       return "0"
   }

    private fun getCatIdPosition(cat_id : String) : Int
    {

        for ( i in 0 until merchantTypeModelArrayList.size )
        {
            if(   merchantTypeModelArrayList[i]._id.equals(cat_id) )
                return i
        }

        return 0
    }


    private fun getIdBycode(pincode : String) : String
    {
        for (i in 0 until pincodeModelArrayList.size )
        {
            if( pincode.contains(pincodeModelArrayList[i].pincode))
                return pincodeModelArrayList[i]._id
        }

        return "0"

    }

    private fun getPositionById(pincode_id : String) : Int
    {
//        Log.e("searchPinCode",pincodeModelArrayList[i].pincode_)
        for (i in 0 until pincodeModelArrayList.size )
        {
            if( pincodeModelArrayList[i]._id.equals(pincode_id))

            {
                Log.e("findCity",pincodeModelArrayList[i].city)
                Log.e("findPincode",pincodeModelArrayList[i].pincode)
                return i
            }
        }

        return -1

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