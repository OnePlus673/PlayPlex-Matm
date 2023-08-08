package com.playplexmatm.microatm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.playplexmatm.R
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.model.BankMatmModel
import com.playplexmatm.util.AppCommonMethods
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.AppPrefs
import com.playplexmatm.util.toast
import kotlinx.android.synthetic.main.activity_matm_bank_details.*
import kotlinx.android.synthetic.main.activity_matm_bank_details.btnNextUserDetails
import kotlinx.android.synthetic.main.activity_matm_bank_details.custToolbar
import kotlinx.android.synthetic.main.activity_matm_bank_details.pbLoadData
import kotlinx.android.synthetic.main.activity_matm_bank_details.view.*
import kotlinx.android.synthetic.main.activity_matm_bank_details.view.ivBackBtn
import kotlinx.android.synthetic.main.activity_matm_onboarding.*
import org.json.JSONObject

class MatmBankDetailsActivity : AppCompatActivity(), AppApiCalls.OnAPICallCompleteListener {

//    lateinit var userModel: UserModel
    lateinit var bankMatmModel: BankMatmModel
    var selectedIfscId = ""
    var bankMatmModelArrayList = ArrayList<BankMatmModel>()

    var Ifsc = ""
    var bankIfscModelArrayList = ArrayList<BankMatmModel>()
    var matm_user_status = ""
    lateinit var userModel : UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matm_bank_details)

        val gson = Gson()
        val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
        userModel = gson.fromJson(json, UserModel::class.java)

        custToolbar.ivBackBtn.setOnClickListener { onBackPressed() }

        matm_user_status = AppPrefs.getStringPref("matm_user_status",this).toString()
        if(matm_user_status.equals("PROCESSING"))
            fetchOnboardingDetailsApi(
                AppPrefs.getStringPref("merchant_ref_id",this).toString(),
                userModel.cus_id

            )

//        getALlIfsc()

        etBeneficiaryBank.setOnClickListener {
            toast("Input Bank IFSC")
        }

//        val gson = Gson()
//        val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
//        userModel = gson.fromJson(json, UserModel::class.java)

        etBenifIFSCNo.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if(p0.toString().length == 11)
                {
                    getIfsc(p0.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        } )


        etConfirmAccount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (etBenifAccnNo.text.toString().equals(etConfirmAccount.text.toString())
                ) {
                    ivAccNumberConfirm.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_check_circle_24))
                } else {
                    ivAccNumberConfirm.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_cancel))
                }
            }
        })



        btnNextUserDetails.setOnClickListener {


            if(etBeneficiaryBank.text.toString().isNullOrEmpty())
            {
                etBenifIFSCNo.requestFocus()
                etBenifIFSCNo.error = "Invalid IFSC"
                toast("Input correct Bank IFSC Code")
            }
            else if(etBenifAccnNo.text.toString().isNullOrEmpty())
            {
                etBeneficiaryBank.requestFocus()
                etBeneficiaryBank.error = "Invalid account"
            }
            else if(etConfirmAccount.text.toString().isNullOrEmpty())
            {
                etConfirmAccount.requestFocus()
                etConfirmAccount.error = "Invalid account"
            }
            else if(!etBenifAccnNo.text.toString().equals(etConfirmAccount.text.toString()))
            {
                toast("Account do not match")
            }
            else if(etBenifIFSCNo.text.toString().isNullOrEmpty())
            {
                etBenifIFSCNo.requestFocus()
                etBenifIFSCNo.error = "Invalid IFSC"
            }
            else

            {
                AppPrefs.putStringPref("merchant_bank",etBeneficiaryBank.text.toString(),this)
                AppPrefs.putStringPref("merchant_account",etBenifAccnNo.text.toString(),this)
                AppPrefs.putStringPref("merchant_ifsc",selectedIfscId,this)
                AppPrefs.putStringPref("ifsc_code",etBenifIFSCNo.text.toString(),this)
                val intent = Intent(this,MatmDocumentUploadActivity::class.java)
                startActivity(intent)

            }
            
        }
        
    } // end of onCreate


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
            mAPIcall.fetchOnboardingDetails(merchant_ref_id,cus_email
            )
        } else {
            toast(getString(R.string.error_internet))
        }
    }

    private fun getIfsc (
        ifsc: String
    ) {
        pbLoadData.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                AppConstants.IFSC_API,
                this
            )
            mAPIcall.getIfsc(ifsc)
        } else {
            toast(getString(R.string.error_internet))
        }
    }

    private fun getALlIfsc (
    ) {
        pbLoadData.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                AppConstants.IFSC_ALL_API,
                this
            )
            mAPIcall.getAllIfsc()
        } else {
            toast(getString(R.string.error_internet))
        }
    }

    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(AppConstants.IFSC_API)) {
            pbLoadData.visibility = View.GONE
            Log.e(AppConstants.IFSC_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
//            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)

            Log.e(AppConstants.STATUS, status)
//            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {

               val cast = jsonObject.getJSONArray("result")

                for(i in 0 until cast.length())
                {
                   val notifyObjJson = cast.getJSONObject(i)

                   bankMatmModel = Gson().fromJson(notifyObjJson.toString(),BankMatmModel::class.java)

                   bankMatmModelArrayList.add(bankMatmModel)

                }


                etBeneficiaryBank.setText(bankMatmModel.bank)
                selectedIfscId = bankMatmModel._id


            }
            else {
                    toast(jsonObject.getString("result"))
                    etBeneficiaryBank.text = ""
            }
        }

        if (flag.equals(AppConstants.IFSC_ALL_API)) {
            pbLoadData.visibility = View.GONE
            Log.e(AppConstants.IFSC_ALL_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
//            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)

            Log.e(AppConstants.STATUS, status)
//            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {

                val cast = jsonObject.getJSONArray("result")

                for(i in 0 until cast.length())
                {
                    val notifyObjJson = cast.getJSONObject(i)

                    bankMatmModel = Gson().fromJson(notifyObjJson.toString(),BankMatmModel::class.java)

                    bankIfscModelArrayList.add(bankMatmModel)

                }


//                etBeneficiaryBank.setText(bankMatmModel.bank)
//                selectedIfscId = bankMatmModel._id

                if(matm_user_status.equals("PROCESSING"))
                    fetchOnboardingDetailsApi(
                        AppPrefs.getStringPref("merchant_ref_id",this).toString(),
                        userModel.cus_id

                    )


            }
            else {
                toast(jsonObject.getString("result"))
                etBeneficiaryBank.text = ""
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

                val aadhar_data = jsonObject.getJSONArray("aadhar_data")

                val adharObj = aadhar_data.getJSONObject(0)

                val ifsc = adharObj.getString("ifsc_code")


                val cast2 = jsonObject.getJSONObject("result").getJSONObject("bank_information")

                val acc_number = cast2.getString("account_number")

                etBenifAccnNo.setText(acc_number)
                etConfirmAccount.setText(acc_number)

//                val position = getPositionIFSC_id(ifsc)

//                Log.e("position",position.toString())

                etBenifIFSCNo.setText(ifsc)


            }
            else {
                toast(messageCode.trim())
            }
        }



    }


   private fun getPositionIFSC_id(id : String) : Int
   {
       for ( i in 0 until bankIfscModelArrayList.size)
       {
           if(bankIfscModelArrayList[i]._id.equals(id))
               return i
       }

       return 0
   }


}