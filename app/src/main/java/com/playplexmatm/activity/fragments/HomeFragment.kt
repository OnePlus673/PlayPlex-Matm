package com.playplexmatm.activity.fragments

import `in`.credopay.payment.sdk.CredopayPaymentConstants
import `in`.credopay.payment.sdk.PaymentActivity
import `in`.credopay.payment.sdk.PaymentManager
import `in`.credopay.payment.sdk.Utils
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.playplexmatm.R
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.microatm.MATMTestActivity
import com.playplexmatm.microatm.MatmOnboardingActivity
import com.playplexmatm.util.AppCommonMethods
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.AppPrefs
import com.playplexmatm.util.toast
import kotlinx.android.synthetic.main.activity_matmtest.*
import kotlinx.android.synthetic.main.activity_matmtest.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_home.view.llAEPS
import kotlinx.android.synthetic.main.fragment_home.view.llMicroAtm
import kotlinx.android.synthetic.main.fragment_home.view.llPOS
import kotlinx.android.synthetic.main.fragment_home.view.ll_AEPS_BE
import kotlinx.android.synthetic.main.fragment_home.view.ll_AEPS_CW
import kotlinx.android.synthetic.main.fragment_home.view.ll_Matm_BE
import kotlinx.android.synthetic.main.fragment_home.view.ll_Matm_CW
import kotlinx.android.synthetic.main.fragment_home.view.ll_POS_CASH
import kotlinx.android.synthetic.main.fragment_home.view.ll_POS_PURCHASE
import kotlinx.android.synthetic.main.fragment_home.view.ll_POS_VOID
import kotlinx.android.synthetic.main.fragment_home.view.progress_bar
import kotlinx.android.synthetic.main.fragment_home.view.rl_aeps
import kotlinx.android.synthetic.main.fragment_home.view.rl_microatm
import kotlinx.android.synthetic.main.fragment_home.view.rl_pos
import kotlinx.android.synthetic.main.fragment_home.view.tvTodaySale
import kotlinx.android.synthetic.main.layout_dialog_confirmamount.*
import kotlinx.android.synthetic.main.layout_dialog_confirmamount.view.*
import kotlinx.android.synthetic.main.layout_dialog_confirmpin.*
import kotlinx.android.synthetic.main.layout_dialog_confirmpin.etPin
import kotlinx.android.synthetic.main.layout_dialog_confirmpin.tvConfirmPin
import kotlinx.android.synthetic.main.layout_dialog_confirmpin.tvDialogCancel
import org.json.JSONObject
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), AppApiCalls.OnAPICallCompleteListener, SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename and change types of parameters

    lateinit var root : View
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
    val REQUEST_ENABLE_BLUETOOTH = 2
    var merchant_ref_id = ""
    var terminal_status = ""
    var terminal_remark = ""
    var curnValue = ""
    var credopay_terminal_status = ""
    var newPassword = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_home, container, false)

        val gson = Gson()
        val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, requireContext())
        userModel = gson.fromJson(json, UserModel::class.java)
//        dashboardApi(userModel.cus_mobile)
        getAepsBalanceApi(userModel.cus_mobile)

        (activity as MATMTestActivity).custToolbar.tvTitle.setText("Home")

        AppPrefs.putStringPref("merchant_ref_id","64948aa6c3e6d761aebdf4b9",requireContext())

        root.mSwipeRefresh.setOnRefreshListener(this);

        root.mSwipeRefresh.post(Runnable {
            if (root.mSwipeRefresh != null) {
                root.mSwipeRefresh.setRefreshing(true)
            }
            dashboardApi(userModel.cus_mobile)
            root.mSwipeRefresh.setRefreshing(false)

        })


        root.ll_Matm_CW.setOnClickListener {

            confirmAmountDialog(CredopayPaymentConstants.MICROATM)
        }

        root.ll_Matm_BE.setOnClickListener {
            amount = 0
            gotoSdk(email,password,amount, CredopayPaymentConstants.BALANCE_ENQUIRY,getCurn())
        }

        root.ll_POS_PURCHASE.setOnClickListener {
            confirmAmountDialog(CredopayPaymentConstants.PURCHASE)
        }

        root.ll_POS_VOID.setOnClickListener {
            confirmAmountDialog(CredopayPaymentConstants.VOID)
        }

        root.ll_POS_CASH.setOnClickListener {
            confirmAmountDialog(CredopayPaymentConstants.CASH_AT_POS)
        }

        root.ll_AEPS_BE.setOnClickListener {
            amount = 0
            gotoSdk(email,password,amount, CredopayPaymentConstants.AEPS_BALANCE_ENQUIRY,getCurn())
        }

        root.ll_AEPS_CW.setOnClickListener {
            confirmAmountDialog(CredopayPaymentConstants.AEPS_CASH_WITHDRAWAL)

        }


        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun confirmAmountDialog(transaction_type : Int) {
        dialog = Dialog(requireContext(), R.style.Widget_MaterialComponents_MaterialCalendar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_confirmamount)
        dialog.custToolbarDialog.ivClosePinDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.etPin.requestFocus()
        dialog.tvDialogCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.getWindow()!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        dialog.tvConfirmPin.setOnClickListener {



            val amount = dialog.etPin.text.toString().toInt() * 100

            gotoSdk(email,password,amount, transaction_type,getCurn())
            dialog.cancel()


        }

        dialog.show()
    }

    private fun gotoSdk(login: String, password: String, amount: Int,transaction_type : Int, CRN_U : String) {
        Log.e("email",email)
        Log.e("password",password)
        Log.e("amount",amount.toString())
        Log.e("transaction",transaction_type.toString())

        val intent = Intent (requireContext(), PaymentActivity::class.java )
        intent.putExtra("TRANSACTION_TYPE", transaction_type)
        intent.putExtra("LOGIN_ID",login)
        intent.putExtra("LOGIN_PASSWORD",password)
        intent.putExtra("DEBUG_MODE",true)
        intent.putExtra("PRODUCTION",true)
        intent.putExtra("CRN_U",CRN_U)
        intent.putExtra("AMOUNT",amount)
        intent.putExtra("LOGO", Utils.getVariableImage(ContextCompat.getDrawable( requireActivity().getApplicationContext(), R.drawable.logo_tp)))
        startActivityForResult(intent,1)
    }

    private fun gotoSdk_password_change(login: String, password: String,
                                        password_new : String,
                                        amount: Int,transaction_type : Int, CRN_U : String) {
        Log.e("email",email)
        Log.e("password",password)
        Log.e("amount",amount.toString())
        Log.e("transaction",transaction_type.toString())

        val intent = Intent (requireContext(), PaymentActivity::class.java )
        intent.putExtra("TRANSACTION_TYPE", transaction_type)
        intent.putExtra("LOGIN_ID",login)
        intent.putExtra("LOGIN_PASSWORD",password)
        intent.putExtra("DEBUG_MODE",true)
        intent.putExtra("PRODUCTION",true)
        intent.putExtra("CRN_U",CRN_U)
        intent.putExtra("AMOUNT",amount)
        intent.putExtra("LOGIN_PASSWORD", password_new)
        intent.putExtra("LOGO", Utils.getVariableImage(ContextCompat.getDrawable( requireActivity().getApplicationContext(), R.drawable.splashlogo)))
        startActivityForResult(intent,1)
    }

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
                    updateMicroAtmLoginDetails(userModel.cus_id,newPassword)
                    gotoSdk_password_change(email,password,newPassword,amount,transaction_type,getCurn())
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

        else if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(ContentValues.TAG, "Bluetooth enabled")
                Toast.makeText(requireContext(), "Bluetooth Enabled", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(ContentValues.TAG, "Bluetooth enable request cancelled")
                Toast.makeText(
                    requireContext(),
                    "Bluetooth enable request cancelled",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun dashboardApi(
        cus_id: String
    ) {
        root.progress_bar.visibility = View.VISIBLE
        if (AppCommonMethods(requireContext()).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                requireContext(),
                AppConstants.DASHBOARD_API,
                this
            )
            mAPIcall.dashboard(cus_id)
        } else {
           requireActivity().toast(getString(R.string.error_internet))
        }
    }


    private fun getMicroAtmLoginDetails(
        cus_id: String
    ) {
        root.progress_bar.visibility = View.VISIBLE
        if (AppCommonMethods(requireContext()).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                requireContext(),
                AppConstants.GET_LOGIN_DETAILS_API,
                this
            )
            mAPIcall.getMicroAtmLoginDetails(cus_id)
        } else {
            requireActivity().toast(getString(R.string.error_internet))
        }
    }


    private fun updateMicroAtmLoginDetails(
        cus_id: String,
        newPassword : String
    ) {
        root.progress_bar.visibility = View.VISIBLE
        if (AppCommonMethods(requireContext()).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                requireContext(),
                AppConstants.UPDATE_LOGIN_DETAILS_API,
                this
            )
            mAPIcall.updateMicroAtmLoginDetails(cus_id,newPassword)
        } else {
            requireActivity().toast(getString(R.string.error_internet))
        }
    }

    private fun getAepsBalanceApi(
        cus_id: String
    ) {
        root.progress_bar.visibility = View.VISIBLE
        if (AppCommonMethods(requireContext()).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                requireContext(),
                AppConstants.BALANCE_API,
                this
            )
            mAPIcall.getAepsBalance(cus_id)
        } else {
            requireActivity().toast(getString(R.string.error_internet))
        }
    }



    @SuppressLint("LongLogTag")
    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(AppConstants.DASHBOARD_API)) {
            root.progress_bar.visibility = View.GONE
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
                        credopay_terminal_status = notifyObjJson.getString("credopay_terminal_status")

                        val login_status = notifyObjJson.getString("login_status")

                        if(login_status.equals("loggedout"))
                        {
//                            showLogoutNew("You are Logging Out! Contact Admin")
                        }
                    }

                    if(credopay_merchant_onboarding_status.equals("APPROVED"))
                    {

                        if(credopay_terminal_status.equals("ACTIVATED"))
                        {
                            getMicroAtmLoginDetails(userModel.cus_id)
                        }
                        else if(credopay_terminal_status.equals("PROCESSING"))
                        {
                            root.rl_microatm.setOnClickListener{
                                showDialog("TERMINAL ONBOARDING IS UNDER PROCESSING")
                            }

                            root.rl_aeps.setOnClickListener {
                                showDialog("TERMINAL ONBOARDING IS UNDER PROCESSING")
                            }

                            root.rl_pos.setOnClickListener {
                                showDialog("TERMINAL ONBOARDING IS UNDER PROCESSING")
                            }
                        }
                        else if(credopay_terminal_status.equals("PENDING"))
                        {
                            root.rl_microatm.setOnClickListener{
                                showDialog("TERMINAL ONBOARDING IS PENDING")
                            }

                            root.rl_aeps.setOnClickListener {
                                showDialog("TERMINAL ONBOARDING IS PENDING")
                            }

                            root.rl_pos.setOnClickListener {
                                showDialog("TERMINAL ONBOARDING IS PENDING")
                            }
                        }
                        else if(credopay_terminal_status.equals("APPROVED"))
                        {
                            root.rl_microatm.setOnClickListener{
                                showDialog("TERMINAL IS APPROVED SUCCESSFULLY WAITING FOR LOGIN DETAILS")
                            }

                            root.rl_aeps.setOnClickListener {
                                showDialog("TERMINAL IS APPROVED SUCCESSFULLY WAITING FOR LOGIN DETAILS")
                            }

                            root.rl_pos.setOnClickListener {
                                showDialog("TERMINAL IS APPROVED SUCCESSFULLY WAITING FOR LOGIN DETAILS")
                            }
                        }
                        else if(credopay_terminal_status.equals("DEACTIVATED"))
                        {
                            root.rl_microatm.setOnClickListener{
                                showDialog("TERMINAL IN DEACTIVATED ")
                            }

                            root.rl_aeps.setOnClickListener {
                                showDialog("TERMINAL IN DEACTIVATED ")
                            }

                            root.rl_pos.setOnClickListener {
                                showDialog("TERMINAL IN DEACTIVATED ")
                            }
                        }

                    }
                    else if(credopay_merchant_onboarding_status.equals("PROCESSING"))
                    {
                        root.rl_microatm.setOnClickListener{
                            showDialog("Onboarding Application Under Process")
                        }

                        root.rl_aeps.setOnClickListener {
                            showDialog("Onboarding Application Under Process")
                        }

                        root.rl_pos.setOnClickListener {
                            showDialog("Onboarding Application Under Process")
                        }

                    }
                    else
                    {
                        root.rl_microatm.setOnClickListener{
                            showDialog("Please Complete onboarding from Web Panel")
                        }

                        root.rl_aeps.setOnClickListener {
                            showDialog("Please Complete onboarding from Web Panel")
                        }

                        root.rl_pos.setOnClickListener {
                            showDialog("Please Complete onboarding from Web Panel")
                        }
                    }


                    Log.e("credopay",credopay_merchant_onboarding_status)


                }  //end of try
                catch (e:Exception )
                {
//                    showLogoutNew("Your Id is InActive ! Contact Admin")
                }





            } else {
                if (messageCode.equals(getString(R.string.error_expired_token))) {
                    AppCommonMethods.logoutOnExpiredDialog(requireContext())
                } else {
                    requireActivity().toast(messageCode.trim())
                }
            }
        }

        if (flag.equals(AppConstants.BALANCE_API)) {
            root.progress_bar.visibility = View.GONE
            Log.e(AppConstants.BALANCE_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)
            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {
                root.progress_bar.visibility = View.GONE
                root.tvWalletBalance.text =
                    "${getString(R.string.Rupee)} ${jsonObject.getString("AEPSBalance")}"
                root.tvTodaySale.text =
                    "${getString(R.string.Rupee)} ${jsonObject.getString("todaySales")}"
                /* tvAepsBalance.text =
                     "${getString(R.string.Rupee)} ${jsonObject.getString(AEPSBALANCE)}"*/

            } else {
                root.progress_bar.visibility = View.GONE
                if (messageCode.equals(getString(R.string.error_expired_token))) {
                    AppCommonMethods.logoutOnExpiredDialog(requireContext())
                } else {
                    requireActivity().toast(messageCode.trim())
                }
            }
        }

        if (flag.equals(USER_LOGOUT)) {
            Log.e("USER_LOGOUT", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status)
            if (status.contains("true")) {
                root.progress_bar.visibility = View.INVISIBLE
                AppPrefs.putStringPref("userModel", "", requireContext())
                AppPrefs.putStringPref("cus_id", "", requireContext())
                AppPrefs.putStringPref("user_id", "", requireContext())
                AppPrefs.putBooleanPref(AppConstants.IS_LOGIN, false, requireContext())

                val intentLogin = Intent(requireContext(), com.playplexmatm.activity.LoginActivity::class.java)
                startActivity(intentLogin)
                requireActivity().finish()

            } else {
                root.progress_bar.visibility = View.INVISIBLE
                val response = jsonObject.getString("message")

                requireActivity().toast(response)

            }
        }

        if (flag.equals(AppConstants.GET_LOGIN_DETAILS_API)) {
            root.progress_bar.visibility = View.GONE
            Log.e(AppConstants.GET_LOGIN_DETAILS_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)

            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {


                val cast = jsonObject.getJSONArray("result")
                newPassword = jsonObject.getString("newPassword")

//                updateMicroAtmLoginDetails(userModel.cus_id,newPassword)

                for(i in 0 until cast.length())
                {
                    val notifyObjJson = cast.getJSONObject(i)
                    email = notifyObjJson.getString("credopayLoginID")
                    password = notifyObjJson.getString("credopayLoginPassword")

                }

//               AppPrefs.putStringPref("email",email,requireContext())
//               AppPrefs.putStringPref("password",password,requireContext())


                root.rl_microatm.setOnClickListener {



                    root.llMicroAtm.visibility = View.VISIBLE
                    root.llAEPS.visibility = View.GONE
                    root.llPOS.visibility = View.GONE

                    root.rl_pos.setBackgroundResource(R.drawable.bg_image_white)
                    root.rl_microatm.setBackgroundResource(R.drawable.bg_image)
                    root.rl_aeps.setBackgroundResource(R.drawable.bg_image_white)

                }

                root.rl_aeps.setOnClickListener {
                    root.llMicroAtm.visibility = View.GONE
                    root.llAEPS.visibility = View.VISIBLE
                    root.llPOS.visibility = View.GONE

                    root.rl_pos.setBackgroundResource(R.drawable.bg_image_white)
                    root.rl_microatm.setBackgroundResource(R.drawable.bg_image_white)
                    root.rl_aeps.setBackgroundResource(R.drawable.bg_image)
                    }

                root.rl_pos.setOnClickListener {

                    root.llMicroAtm.visibility = View.GONE
                    root.llAEPS.visibility = View.GONE
                    root.llPOS.visibility = View.VISIBLE

                    root.rl_pos.setBackgroundResource(R.drawable.bg_image)
                    root.rl_microatm.setBackgroundResource(R.drawable.bg_image_white)
                    root.rl_aeps.setBackgroundResource(R.drawable.bg_image_white)
                }



            } else {
                if (messageCode.equals(getString(R.string.error_expired_token))) {
                    AppCommonMethods.logoutOnExpiredDialog(requireContext())
                } else {
                    requireActivity().toast(messageCode.trim())
                }
            }
        }

        if (flag.equals(AppConstants.UPDATE_LOGIN_DETAILS_API)) {
            root.progress_bar.visibility = View.GONE
            Log.e(AppConstants.UPDATE_LOGIN_DETAILS_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)

            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {

               requireActivity().toast("Password Updated Successfully !!!")


            } else {
                if (messageCode.equals(getString(R.string.error_expired_token))) {
                    AppCommonMethods.logoutOnExpiredDialog(requireContext())
                } else {
                    requireActivity().toast(messageCode.trim())
                }
            }
        }

    }



    private fun showMessage(msg : String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Attention !")
        builder.setMessage((Html.fromHtml(msg + "<font color='#ff0000'> <b> <br><br>Note: Please wait for callback response if updated recently</b></font>")   ))
        builder.setPositiveButton("UPDATE") { dialog, which ->

            val intent = Intent(requireContext(), MatmOnboardingActivity::class.java)
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
        val builder = android.app.AlertDialog.Builder(requireContext())

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

                dialog.dismiss()
            })

        // Create the Alert dialog
        val alertDialog = builder.create()
        // Show the Alert Dialog box
        alertDialog.show()
    }


    fun getCurn() : String
    {
        val r = Random(System.currentTimeMillis())
        curnValue = "PP" + (10000 + r.nextInt(20000)).toString()
        return  curnValue

    }

    override fun onResume() {
        super.onResume()
        dashboardApi(userModel.cus_mobile)

    }

    override fun onRefresh() {
        dashboardApi(userModel.cus_mobile)
        root.mSwipeRefresh.setRefreshing(false)

    }

}