package com.playplexmatm.microatm

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.gson.Gson
import com.playplexmatm.R
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.model.PincodeModel
import com.playplexmatm.util.AppCommonMethods
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.AppPrefs
import com.playplexmatm.util.toast
import kotlinx.android.synthetic.main.activity_matm_onboarding.*

import kotlinx.android.synthetic.main.activity_matm_personal_details.*
import kotlinx.android.synthetic.main.activity_matm_personal_details.btnNextUserDetails
import kotlinx.android.synthetic.main.activity_matm_personal_details.custToolbar
import kotlinx.android.synthetic.main.activity_matm_personal_details.etCity
import kotlinx.android.synthetic.main.activity_matm_personal_details.etPanNumber
import kotlinx.android.synthetic.main.activity_matm_personal_details.etState
import kotlinx.android.synthetic.main.activity_matm_personal_details.pbLoadData
import kotlinx.android.synthetic.main.activity_matm_personal_details.spinnerPincode
import kotlinx.android.synthetic.main.activity_matm_personal_details.view.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class MatmPersonalDetailsActivity : AppCompatActivity(), AppApiCalls.OnAPICallCompleteListener {

    lateinit var pincodeModel: PincodeModel
    var pincodeModelArrayList = ArrayList<PincodeModel>()
    var pincodeStringList = ArrayList<String>()
    lateinit var selectedPincode : String
    lateinit var selectedPincodeId : String
    var titleArray =  arrayOf("Mr.","Mrs.","Miss.")
    var selectedTitle = ""
    var matm_user_status = ""
    lateinit var userModel : UserModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matm_personal_details)

        val gson = Gson()
        val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
        userModel = gson.fromJson(json, UserModel::class.java)

        matm_user_status = AppPrefs.getStringPref("matm_user_status",this).toString()
        custToolbar.ivBackBtnP.setOnClickListener { onBackPressed() }

        spinnerTitle.adapter = ArrayAdapter(this,R.layout.spinner,titleArray)


        spinnerTitle.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    selectedTitle = titleArray[position]

//                    toast(selectedMerchantType)

                }

            }



        etDob.setOnClickListener {
            datePicker()
        }


        pincodeApi()

        btnNextUserDetails.setOnClickListener {


            if (etFirstName.text.toString().isNullOrEmpty()) {
                etFirstName.requestFocus()
                etFirstName.error = "Invalid Name"
            } else if (etLastName.text.toString().isNullOrEmpty()) {
                etLastName.requestFocus()
                etLastName.error = "Invalid Name"
            } else if (!AppCommonMethods.checkForEmail(etEmail)) {
                etEmail.requestFocus()
                etEmail.error = "Invalid Email"
            } else if (!AppCommonMethods.checkForMobile(etMobile)) {
                etMobile.requestFocus()
                etMobile.error = "Invalid Mobile"
            } else if (etDob.text.toString().isNullOrEmpty()) {
                etDob.requestFocus()
                etDob.error = "Invalid DOB"
            } else if (etAddress.text.toString().isNullOrEmpty()) {
                etAddress.requestFocus()
                etAddress.error = "Invalid Address"
            }
            else if (etNationality.text.toString().isNullOrEmpty()) {
                etNationality.requestFocus()
                etNationality.error = "Invalid Nationality"
            } else if (etAadhaarNumber.text.toString()
                    .isNullOrEmpty() || etAadhaarNumber.text.toString().length != 12
            ) {
                etAadhaarNumber.requestFocus()
                etAadhaarNumber.error = "Invalid Aaadhaar"
            } else if (!AppCommonMethods.checkForPan(etPanNumber)) {
                etPanNumber.requestFocus()
                etPanNumber.error = "Invalid Pan"
            } else {

                AppPrefs.putStringPref("title_personal", spinnerTitle.selectedItem.toString(),this )
                AppPrefs.putStringPref("first_name_personal",etFirstName.text.toString(),this )
                AppPrefs.putStringPref("last_name_personal",etLastName.text.toString(),this )
                AppPrefs.putStringPref("email_personal",etEmail.text.toString(),this )
                AppPrefs.putStringPref("mobile_personal",etMobile.text.toString(),this )
                AppPrefs.putStringPref("dob_personal",etDob.text.toString(),this )
                AppPrefs.putStringPref("address_personal",etAddress.text.toString(),this )
                AppPrefs.putStringPref("pin_personal",selectedPincodeId,this )
                AppPrefs.putStringPref("city_personal",etCity.text.toString() ,this )
                AppPrefs.putStringPref("state_personal",etState.text.toString(),this )
                AppPrefs.putStringPref("nationality_personal",etNationality.text.toString(),this )
                AppPrefs.putStringPref("aadhaar_personal",etAadhaarNumber.text.toString(),this )
                AppPrefs.putStringPref("pan_personal",etPanNumber.text.toString(),this )



                val intent = Intent(this, MatmBankDetailsActivity::class.java)
                startActivity(intent)

            }

        }




    }   // end of onCreate


    fun datePicker() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var dpd =
            DatePickerDialog(this, { view, mYear, mMonth, mDay ->
                val mmMonth = mMonth + 1
                val date = "$mYear/$mmMonth/$mDay"

                etDob.setText(
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
        if (flag.equals(AppConstants.PINCODE_API)) {
            pbLoadData.visibility = View.GONE
            Log.e(AppConstants.DASHBOARD_API, result)
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
                     userModel.cus_id)


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

                val cast2 = jsonObject.getJSONObject("result").getJSONArray("personal_information")
                val cast = cast2.getJSONObject(0)
                val title = cast.getString("title")
                val first_name  =   cast.getString("first_name")
                val last_name    =   cast.getString("last_name")
                val  address   =   cast.getString("address")
                val  pincode   =   cast.getString("pincode")
                val   mobile  =   cast.getString("mobile")
                val   email  =   cast.getString("email")
                val   nationality  =   cast.getString("nationality")
                val   dob  =   cast.getString("dob")
                val   pan  =   cast.getString("pan")
                val   aadhar_number  =   cast.getString("aadhar_number")

               spinnerTitle.setSelection(getTitlePosition(title))
                etFirstName.setText(first_name)
                etLastName.setText(last_name)
                etEmail.setText(email)
                etMobile.setText(mobile)
                etDob.setText(dob)
                etAddress.setText(address)

                spinnerPincode.setSelection(getPositionByPin_Id(pincode))
                etNationality.setText(nationality)
                etAadhaarNumber.setText(aadhar_number)
                etPanNumber.setText(pan)



            }
            else {
                toast(messageCode.trim())
            }
        }


    } // end of on App Api Complete Listener

    private fun getIdBycode(pincode : String) : String
    {
        for (i in 0 until pincodeModelArrayList.size )
        {
            if( pincode.contains(pincodeModelArrayList[i].pincode))
                return pincodeModelArrayList[i]._id
        }

        return "0"

    }

    private fun getPositionByPin_Id(pincode_id : String) : Int
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


    private fun getTitlePosition(title : String) : Int
    {
        for(i in titleArray.indices)
        {
            if(titleArray[i] == title)
                return i
        }

        return 0
    }



}