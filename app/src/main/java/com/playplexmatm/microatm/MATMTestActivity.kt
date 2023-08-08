package com.playplexmatm.microatm

import `in`.credopay.payment.sdk.CredopayPaymentConstants
import `in`.credopay.payment.sdk.PaymentActivity
import `in`.credopay.payment.sdk.PaymentManager
import `in`.credopay.payment.sdk.Utils
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.playplexmatm.R
import com.playplexmatm.activity.fragments.ReportsFragment
import com.playplexmatm.activity.fragments.SalesFragment
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.util.AppCommonMethods
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.AppPrefs
import com.playplexmatm.util.toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_matmtest.*
import kotlinx.android.synthetic.main.layout_dialog_confirmpin.*
import org.json.JSONObject


class MATMTestActivity : AppCompatActivity(), AppApiCalls.OnAPICallCompleteListener {
    lateinit var dialog: Dialog
    var email = ""
    var password = ""
    var amount = 0
    var transaction_type = 0
    lateinit var userModel : UserModel
    var newaepskyc_status: String = ""
    var aeps_kyc_status: String = ""
    var credopay_merchant_onboarding_status = ""

    var matm_user_status = ""
    var callback_status = ""
    var callback_remark = ""
    private val USER_LOGOUT: String = "USER_LOGOUT"

    var merchant_ref_id = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matmtest)
        Log.e("matmactivity","inside activity")

        val gson = Gson()
        val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
        userModel = gson.fromJson(json, UserModel::class.java)
//        dashboardApi(userModel.cus_mobile)

        AppPrefs.putStringPref("merchant_ref_id","64948aa6c3e6d761aebdf4b9",this)

        try {
            Picasso.setSingletonInstance(Picasso.Builder(this).build())
        } catch (e: IllegalStateException) {
            //TODO
        }



        email = AppPrefs.getStringPref("email",this).toString()
        password = AppPrefs.getStringPref("password",this).toString()


        openFragment(com.playplexmatm.activity.fragments.HomeFragment())

        bottom_navigation.setOnNavigationItemSelectedListener {
            var fragment: Fragment? = null

            when (it.itemId) {
                R.id.navigation_home -> {
                    fragment = com.playplexmatm.activity.fragments.HomeFragment()
                    openFragment(fragment)
                }
                R.id.navigation_sales -> {
                    fragment = SalesFragment()
                    openFragment(fragment)
                }
                R.id.navigation_report -> {
                    fragment = ReportsFragment()
                    openFragment(fragment)
                }
                R.id.navigation_account -> {
                    fragment = com.playplexmatm.activity.fragments.AccountFragment()
                    openFragment(fragment)
                }
                else -> null
            } != null
        }


        ivLogout.setOnClickListener {
            showLogout()
        }


        /*
        ll_Matm_CW.setOnClickListener {

            confirmAmountDialog(CredopayPaymentConstants.MICROATM)
        }

        ll_Matm_BE.setOnClickListener {
            amount = 0
            gotoSdk(email,password,amount,CredopayPaymentConstants.BALANCE_ENQUIRY)
        }

        ll_POS_PURCHASE.setOnClickListener {
            confirmAmountDialog(CredopayPaymentConstants.PURCHASE)
        }

        ll_POS_VOID.setOnClickListener {
            confirmAmountDialog(CredopayPaymentConstants.VOID)
        }

        ll_POS_CASH.setOnClickListener {
            confirmAmountDialog(CredopayPaymentConstants.CASH_AT_POS)
        }

        ll_AEPS_BE.setOnClickListener {
            amount = 0
            gotoSdk(email,password,amount,CredopayPaymentConstants.AEPS_BALANCE_ENQUIRY)
        }

        ll_AEPS_CW.setOnClickListener {
            confirmAmountDialog(CredopayPaymentConstants.AEPS_CASH_WITHDRAWAL)

        }

         */


    }



    fun confirmAmountDialog(transaction_type : Int) {
        dialog = Dialog(this, com.google.android.material.R.style.Theme_MaterialComponents_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_confirmpin)

        dialog.etPin.requestFocus()
        dialog.tvDialogCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.getWindow()!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        dialog.tvConfirmPin.setOnClickListener {



            val amount = dialog.etPin.text.toString().toInt() * 100

            gotoSdk(email,password,amount, transaction_type)
            dialog.cancel()


        }

        dialog.show()
    }

    private fun gotoSdk(login: String, password: String, amount: Int,transaction_type : Int) {
        Log.e("email",email)
        Log.e("password",password)
        Log.e("amount",amount.toString())
        Log.e("transaction",transaction_type.toString())

        val intent = Intent (this, PaymentActivity::class.java )
        intent.putExtra("TRANSACTION_TYPE", transaction_type)
        intent.putExtra("LOGIN_ID",login)
        intent.putExtra("LOGIN_PASSWORD",password)
        intent.putExtra("DEBUG_MODE",true)
        intent.putExtra("PRODUCTION",true)
//        if(amount!=0)
//        {
            intent.putExtra("AMOUNT",amount)
//        }
        intent.putExtra("LOGO", Utils.getVariableImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.splashlogo)))
        startActivity(intent)
    }

//    private fun gotoSdk2(login: String, password: String, transaction : Int) {
//        Log.e("email",email)
//        Log.e("password",password)
//        Log.e("transaction",transaction.toString())
//
//
//        val intent = Intent (this, PaymentActivity::class.java )
//        intent.putExtra("TRANSACTION_TYPE",transaction)
//        intent.putExtra("LOGIN_ID",login)
//        intent.putExtra("LOGIN_PASSWORD",password)
//        intent.putExtra("DEBUG_MODE",true)
//        intent.putExtra("PRODUCTION",true)
//        intent.putExtra("LOGO", Utils.getVariableImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.splashlogo)))
//        startActivity(intent)
//    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {

            Log.e("Credopay SDK", "Inside request code")
            Log.e("Result Code ",resultCode.toString())

            when (resultCode) {
                CredopayPaymentConstants.TRANSACTION_COMPLETED ->
                {
                    Log.e("Transaction","TRANSACTION_COMPLETED "+data)
                    PaymentManager.getInstance().logout()
                }


                CredopayPaymentConstants.VOID_CANCELLED ->
                {
                    Log.e("Transaction","TRANSACTION_CANCELLED")
                }


                CredopayPaymentConstants.LOGIN_FAILED ->
                    Log.e("Transaction","LOGIN_FAILED MATM4")

                CredopayPaymentConstants.CHANGE_PASSWORD_FAILED ->
                    Log.e("Transaction","CHANGE_PASSWORD_FAILED")
//
                CredopayPaymentConstants.CHANGE_PASSWORD_SUCCESS ->
                    Log.e("Transaction","CHANGE_PASSWORD_SUCCESS")

                CredopayPaymentConstants.BLUETOOTH_CONNECTIVITY_FAILED ->
                    Log.e("Transaction","BLUETOOTH_CONNECTIVITY_FAILED")

                CredopayPaymentConstants.CHANGE_PASSWORD -> {
                    Log.e("Transaction", "CHANGE_PASSWORD")
                    gotoSdk(email,"Payplex@123",amount,transaction_type)
                }
                CredopayPaymentConstants.CHANGE_PASSWORD_FAILED -> {
                    Log.e("Transaction", "CHANGE_PASSWORD_FAILED")
                }
                CredopayPaymentConstants.CHANGE_PASSWORD_SUCCESS -> {
                    Log.e("Transaction", "CHANGE_PASSWORD_SUCCESS")

                }
            }


            return
        }
    }

    private fun dashboardApi(
        cus_id: String
    ) {
        progress_bar.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                AppConstants.DASHBOARD_API,
                this
            )
            mAPIcall.dashboard(cus_id)
        } else {
            toast(getString(R.string.error_internet))
        }
    }

    private fun getOnboardingStatusApi(
        cus_id: String
    ) {
        progress_bar.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                AppConstants.MATM_ONBOARDING_STATUS,
                this
            )
            mAPIcall.getMatmOnboardingStatusAPi(cus_id)
        } else {
            toast(getString(R.string.error_internet))
        }
    }

    private fun showLogout() {
        val builder1 =
            AlertDialog.Builder(this)
        builder1.setTitle("Attention!")
        builder1.setMessage("Do you want to Log Out?")
        builder1.setCancelable(true)
        builder1.setPositiveButton(
            "OK"
        ) { dialog, id ->
            val gson = Gson()
            val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
            userModel = gson.fromJson(json, UserModel::class.java)

            userLogout(
                userModel.cus_id, AppCommonMethods.getDeviceId(this), AppCommonMethods.getDeviceName(),
                userModel.cus_pin, userModel.cus_pass,
                userModel.cus_mobile, userModel.cus_type
            )


            dialog.cancel()
        }
        builder1.setNegativeButton(
            "CANCEL"
        ) { dialog, id -> dialog.cancel() }
        val alert11 = builder1.create()
        alert11.show()
    }


    private fun userLogout(
        cusid: String,
        deviceId: String,
        deviceNameDet: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {
        progress_bar.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(this, USER_LOGOUT, this)
            mAPIcall.userLogout(
                cusid, deviceId, deviceNameDet, pin,
                pass, cus_mobile, cus_type
            )
        } else {
            Toast.makeText(this, "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFragment(fragment: Fragment?): Boolean {
        //switching fragment
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            return true
        }
        return false
    }



    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(AppConstants.DASHBOARD_API)) {
            progress_bar.visibility = View.GONE
            Log.e(AppConstants.DASHBOARD_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)

            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {



                try
                {
                    val cusData = jsonObject.getJSONArray("cusData")
                    for (i in 0 until cusData.length()) {
                        val notifyObjJson = cusData.getJSONObject(i)
                        newaepskyc_status = notifyObjJson.getString("newaepskyc_status")
                        aeps_kyc_status = notifyObjJson.getString("aeps_kyc_status")

                        credopay_merchant_onboarding_status = notifyObjJson.getString("credopay_merchant_onboarding_status")


                        btnOnboardMerchant.setOnClickListener {

                            if (credopay_merchant_onboarding_status.equals("PENDING"))
                            {

                                val intent = Intent(this,MatmOnboardingActivity::class.java)
//                                val intent = Intent(this,MatmDocumentUploadActivity::class.java)
                                startActivity(intent)
                            }
                            else if(credopay_merchant_onboarding_status.equals("PROCESSING"))
                            {

                                getOnboardingStatusApi(userModel.cus_id)

                            }
                            else if(credopay_merchant_onboarding_status.equals("APPROVED"))
                            {
                                toast("Onboarding Completed")
                            }


                        }

                        Log.e("credopay",credopay_merchant_onboarding_status)

                        rl_microatm.setOnClickListener {

                            if(credopay_merchant_onboarding_status.equals("APPROVED"))
                            {
                                llMicroAtm.visibility = View.VISIBLE
                                llAEPS.visibility = View.GONE
                                llPOS.visibility = View.GONE

                                rl_pos.setBackgroundResource(R.drawable.bg_image_white)
                                rl_microatm.setBackgroundResource(R.drawable.bg_image)
                                rl_aeps.setBackgroundResource(R.drawable.bg_image_white)
                            }
                            else
                            {
                                showDialog("Onboarding required for using services")
                            }


                        }

                        rl_aeps.setOnClickListener {

                            if(credopay_merchant_onboarding_status.equals("APPROVED"))
                            {
                                llMicroAtm.visibility = View.GONE
                                llAEPS.visibility = View.VISIBLE
                                llPOS.visibility = View.GONE

                                rl_pos.setBackgroundResource(R.drawable.bg_image_white)
                                rl_microatm.setBackgroundResource(R.drawable.bg_image_white)
                                rl_aeps.setBackgroundResource(R.drawable.bg_image)
                            }
                            else
                            {
                                showDialog("Onboarding required for using services")
                            }

                        }

                        rl_pos.setOnClickListener {

                            if(credopay_merchant_onboarding_status.equals("APPROVED"))
                            {
                                llMicroAtm.visibility = View.GONE
                                llAEPS.visibility = View.GONE
                                llPOS.visibility = View.VISIBLE

                                rl_pos.setBackgroundResource(R.drawable.bg_image)
                                rl_microatm.setBackgroundResource(R.drawable.bg_image_white)
                                rl_aeps.setBackgroundResource(R.drawable.bg_image_white)
                            }
                            else
                            {
                                showDialog("Onboarding required for using services")
                            }


                        }

                        val login_status = notifyObjJson.getString("login_status")

                        if(login_status.equals("loggedout"))
                        {
//                            showLogoutNew("You are Logging Out! Contact Admin")
                        }
                    }

                }  //end of try
                catch (e:Exception )
                {
//                    showLogoutNew("Your Id is InActive ! Contact Admin")
                }





            } else {
                if (messageCode.equals(getString(R.string.error_expired_token))) {
                    AppCommonMethods.logoutOnExpiredDialog(this)
                } else {
                    toast(messageCode.trim())
                }
            }
        }

        if (flag.equals(AppConstants.MATM_ONBOARDING_STATUS)) {
            progress_bar.visibility = View.GONE
            Log.e(AppConstants.MATM_ONBOARDING_STATUS, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)

            //   val token = jsonObject.getString(AppConstants.TOKEN)
            Log.e(AppConstants.STATUS, status)
            if (status.contains(AppConstants.TRUE)) {
                progress_bar.visibility = View.GONE

                val cast = jsonObject.getJSONArray("result")

                for(i in 0 until cast.length() )
                {
                    matm_user_status = cast.getJSONObject(i).getString("status")
                    callback_status = cast.getJSONObject(i).getString("callback_status")
                    callback_remark = cast.getJSONObject(i).getString("callback_remark")
                    merchant_ref_id = cast.getJSONObject(i).getString("merchant_ref_id")
                }

                AppPrefs.putStringPref("merchant_ref_id",merchant_ref_id,this)


                if(matm_user_status.equals("APPROVED"))
                {
                    toast("Merchant Approved Successfully")
                }
                else
                {
                    AppPrefs.putStringPref("matm_user_status",matm_user_status,this)

                    if(matm_user_status.equals("PROCESSING"))
                    {
                        val msg = "CallBack Satus : ${callback_status} \nCallBack Remark : ${callback_remark}"

                        toast("Application Under Process")
                        showMessage(msg)
                    }
                    else
                    {
                        toast("Onboard User for using MATM services")

                        val intent = Intent(this,MatmOnboardingActivity::class.java)
                        startActivity(intent)
                    }


                }



            } else {
                progress_bar.visibility = View.GONE
                toast("OnBoard User First")
                AppPrefs.putStringPref("matm_user_status","PENDING",this)
                val intent = Intent(this,MatmOnboardingActivity::class.java)
                startActivity(intent)
            }
        }

        if (flag.equals(USER_LOGOUT)) {
            Log.e("USER_LOGOUT", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status)
            if (status.contains("true")) {
                progress_bar.visibility = View.INVISIBLE
                AppPrefs.putStringPref("userModel", "", this)
                AppPrefs.putStringPref("cus_id", "", this)
                AppPrefs.putStringPref("user_id", "", this)
                AppPrefs.putBooleanPref(AppConstants.IS_LOGIN, false, this)

                PaymentManager.getInstance().logout()
                val intentLogin = Intent(this, com.playplexmatm.activity.LoginActivity::class.java)
                startActivity(intentLogin)
                finish()

            } else {
                progress_bar.visibility = View.INVISIBLE
                val response = jsonObject.getString("message")

                toast(response)

            }
        }



    }

    private fun showMessage(msg : String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Attention !")
        builder.setMessage((Html.fromHtml(msg + "<font color='#ff0000'> <b> <br><br>Note: Please wait for callback response if updated recently</b></font>")   ))
        builder.setPositiveButton("UPDATE") { dialog, which ->

            val intent = Intent(this,MatmOnboardingActivity::class.java)
            startActivity(intent)
            dialog.cancel()



        }
        builder.setNegativeButton("CANCEL") { dialog, which ->
            dialog.cancel()

        }

        val alert = builder.create()
        alert.show()
    }


    fun showDialog(message: String?) {

        // Create the object of AlertDialog Builder class
        val builder = android.app.AlertDialog.Builder(this)

        // Set the message show for the Alert time
        builder.setMessage(message)

        // Set Alert Title
        builder.setTitle("Message")

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false)

        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setNegativeButton("OK",
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                // If user click no then dialog box is canceled.
                if (credopay_merchant_onboarding_status.equals("PENDING"))
                {

                    val intent = Intent(this,MatmOnboardingActivity::class.java)
//                                val intent = Intent(this,MatmDocumentUploadActivity::class.java)
                    startActivity(intent)
                }
                else if(credopay_merchant_onboarding_status.equals("PROCESSING"))
                {

                    getOnboardingStatusApi(userModel.cus_id)

                }
            })

        // Create the Alert dialog
        val alertDialog = builder.create()
        // Show the Alert Dialog box
        alertDialog.show()
    }






}