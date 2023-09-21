package com.playplexmatm.activity.fragments

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.playplexmatm.R
import com.playplexmatm.activity.User.SupportActivity
import com.playplexmatm.activity.fragments.bills.BusinessProfileActivity
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.microatm.MATMTestActivity
import com.playplexmatm.util.AppCommonMethods
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.AppPrefs
import com.playplexmatm.util.toast
import com.sg.swapnapay.model.UserModel
import kotlinx.android.synthetic.main.activity_matmtest.*
import kotlinx.android.synthetic.main.activity_matmtest.view.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.fragment_account.view.progress_bar
import kotlinx.android.synthetic.main.layout_dialog_about_us.*
import kotlinx.android.synthetic.main.layout_dialog_about_us.view.*
import kotlinx.android.synthetic.main.layout_dialog_about_us.view.tvTitle
import kotlinx.android.synthetic.main.layout_dialog_change_password.*
import kotlinx.android.synthetic.main.layout_dialog_change_password.custToolbarDialog
import kotlinx.android.synthetic.main.layout_dialog_change_password.view.*
import kotlinx.android.synthetic.main.layout_dialog_change_pin.*
import kotlinx.android.synthetic.main.layout_dialog_change_pin.view.*
import kotlinx.android.synthetic.main.layout_dialog_change_pin.view.ivClosePinDialog
import kotlinx.android.synthetic.main.layout_dialog_confirmotp.*
import org.json.JSONObject
import java.util.*

class AccountFragment : Fragment(), AppApiCalls.OnAPICallCompleteListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var root : View
    lateinit var userModel : UserModel
    var deviceId: String = ""
    var deviceNameDet: String = ""
    private val CHANGEPASSWORD_API: String = "CHANGEPASSWORD_API"
    private val CHANGEPIN_API: String = "CHANGEPIN_API"
    private val SEND_OTP: String = "SEND_OTP"
    private val FORGET_PIN: String = "FORGET_PIN"
    lateinit var otp: String
    lateinit var dialog: Dialog
    val CONTACTUS = "CONTACTUS"
    var about_us = ""
    var privacy_policy = ""
    var M_ID = ""
    var T_ID = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_account, container, false)

        val gson = Gson()
        val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, requireContext())
        userModel = gson.fromJson(json, UserModel::class.java)

//        (activity as MATMTestActivity).custToolbar.tvTitle.setText("Profile")

        getProfileApi(userModel.cus_mobile)

        deviceId = AppCommonMethods.getDeviceId(requireContext())
        deviceNameDet = AppCommonMethods.getDeviceName()


        contactUs(userModel.cus_id, AppPrefs.getStringPref("deviceId", requireContext()).toString(),
            AppPrefs.getStringPref("deviceName",requireContext()).toString(),
            userModel.cus_pin,
            userModel.cus_pass,
            userModel.cus_mobile,userModel.cus_type)


        root.rl_changepassword.setOnClickListener {

            changePasswordDialog()
        }

        root.rl_changepin.setOnClickListener {
            changePinDialog()
        }

        root.rl_get_support.setOnClickListener {
            val intent = Intent(requireContext(), SupportActivity::class.java)
            startActivity(intent)
        }
        root.rl_businessProfile.setOnClickListener {
            startActivity(Intent(requireContext(),BusinessProfileActivity::class.java))
        }

        root.rl_forgotpin.setOnClickListener {
            val r = Random()
            otp = java.lang.String.format("%06d", r.nextInt(999999))
            Log.d("OTP", otp)
            sendSmsforPin(
                userModel.cus_mobile, otp, AppPrefs.getStringPref("deviceId", requireContext()).toString(),
                AppPrefs.getStringPref("deviceName", requireContext()).toString(),
                userModel.cus_pin,
                userModel.cus_pass,
                userModel.cus_mobile, userModel.cus_type
            )

        }



        return root
    }


    fun sendSmsforPin(
        mobile: String, otp: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {
        root.progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(requireContext()).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(requireContext(), SEND_OTP, this)
            mAPIcall.getpinotp(mobile, otp, deviceId, deviceName, pin, pass, cus_mobile, cus_type)
        } else {

           requireActivity().toast("No Internet Connection")

        }
    }

    fun forgetpin(
        cus_id: String,
        deviceId: String,
        deviceName: String,
        cus_type: String

    ) {
        root.progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(requireContext()).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(requireContext(), FORGET_PIN, this)
            mAPIcall.forgetpin(cus_id, deviceId, deviceName, cus_type)
        } else {

            requireActivity().toast("No Internet Connection")
        }
    }

    fun contactUs(
        cus_id : String,
        deviceId: String,
        deviceName: String,
        pin: String,
        pass: String,
        cus_mobile: String,
        cus_type: String
    ) {
        root.progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(requireContext()).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(requireContext(), CONTACTUS, this)
            mAPIcall.contactUsApi(cus_id ,deviceId,deviceName, pin, pass, cus_mobile, cus_type)
        } else {

            Toast.makeText(requireContext(), "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun changePasswordDialog() {
        dialog = Dialog(requireContext(), R.style.Widget_MaterialComponents_MaterialCalendar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_change_password)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.custToolbarDialog.ivClosePasswordDialog.setOnClickListener {
            dialog.dismiss()
        }
        dialog.etConfirmNewPassword.setText("")
        dialog.etCurrentPassword.setText("")
        dialog.etNewPassword.setText("")


        dialog.btnChangePassword.setOnClickListener {
            if (dialog.etCurrentPassword.text.toString().isEmpty()) {

                dialog.etCurrentPassword.requestFocus()
                dialog.etCurrentPassword.error = "Invalid Password"
            } else if (dialog.etNewPassword.text.toString().isEmpty()) {
                dialog.etNewPassword.requestFocus()
                dialog.etNewPassword.error = "Invalid Password"
            } else if (dialog.etConfirmNewPassword.text.toString().isEmpty()) {
                dialog.etConfirmNewPassword.requestFocus()
                dialog.etConfirmNewPassword.error = "Invalid Password"
            } else if (dialog.etNewPassword.text.toString() != dialog.etConfirmNewPassword.text.toString()) {
                requireActivity().toast("New Password doesn't match")
            } else {

                //Code after validation
                changePassword(
                    userModel.cus_id.toString(),
                    dialog.etCurrentPassword.text.toString(),
                    dialog.etConfirmNewPassword.text.toString(),
                    AppPrefs.getStringPref("deviceId", requireContext()).toString(),
                    AppPrefs.getStringPref("deviceName", requireContext()).toString(),
                    userModel.cus_pin, userModel.cus_pass, userModel.cus_mobile,
                    userModel.cus_type

                )

            }
        }



        dialog.show()
    }

    private fun changePinDialog() {
        dialog = Dialog(requireContext(), R.style.Widget_MaterialComponents_MaterialCalendar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_change_pin)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.custToolbarDialog.ivClosePinDialog.setOnClickListener {
            dialog.dismiss()
        }
        dialog.btnChangePin.setOnClickListener {
            val gson = Gson()
            val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, requireContext())
            userModel = gson.fromJson(json, UserModel::class.java)

            if (dialog.etCurrentPin.text.toString().isEmpty()) {

                dialog.etCurrentPin.requestFocus()
                dialog.etCurrentPin.error = "Invalid Pin"
            } else if (dialog.etNewPin.text.toString().isEmpty()) {
                dialog.etNewPin.requestFocus()
                dialog.etNewPin.error = "Invalid Pin"
            } else if (dialog.etConfirmNewPin.text.toString().isEmpty()) {
                dialog.etConfirmNewPin.requestFocus()
                dialog.etConfirmNewPin.error = "Invalid Pin"
            } else if (dialog.etNewPin.text.toString() != dialog.etConfirmNewPin.text.toString()) {
                requireActivity().toast("New Pin doesn't match")
            } else {

                //Code after validation
                changePin(
                    userModel.cus_id,
                    dialog.etCurrentPin.text.toString(),
                    dialog.etConfirmNewPin.text.toString(),
                    AppPrefs.getStringPref("deviceId", requireContext()).toString(),
                    AppPrefs.getStringPref("deviceName", requireContext()).toString(),
                    userModel.cus_pin, userModel.cus_pass, userModel.cus_mobile, userModel.cus_type
                )
            }
        }


        dialog.show()
    }

    //API CALL FUNCTION DEFINITION
    private fun getProfileApi(
        cus_id: String
    ) {
        root.progress_bar.visibility = View.VISIBLE
        if (AppCommonMethods(requireContext()).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                requireContext(),
                AppConstants.PROFILE_API,
                this
            )
            mAPIcall.getProfile(cus_id)

        } else {
            requireActivity().toast(getString(R.string.error_internet))
        }
    }


    //API CALL FUNCTION DEFINITION
    private fun changePassword(
        cus_id: String, current_pass: String, new_pass: String,
        deviceId: String, deviceName: String, pin: String, pass: String,
        cus_mobile: String, cus_type: String
    ) {
        root.progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(requireContext()).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(requireContext(), CHANGEPASSWORD_API, this)
            mAPIcall.changePassword(
                cus_id,
                current_pass,
                new_pass,
                deviceId,
                deviceName,
                pin,
                pass,
                cus_mobile,
                cus_type
            )
        } else {

            Toast.makeText(requireContext(), "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }

    //API CALL FUNCTION DEFINITION
    private fun changePin(
        cus_id: String, current_pin: String, new_pin: String,
        deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {
        root.progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(requireContext()).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(requireContext(), CHANGEPIN_API, this)
            mAPIcall.changePin(
                cus_id, current_pin, new_pin, deviceId, deviceName,
                pin, pass, cus_mobile, cus_type
            )
        } else {

            Toast.makeText(requireContext(), "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(AppConstants.PROFILE_API)) {
            root.progress_bar.visibility = View.GONE
            Log.e(AppConstants.PROFILE_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)
            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {


                val users = jsonObject.getJSONArray(AppConstants.RESULT)
                for (i in 0 until users.length()) {
                    val notifyObjJson = users.getJSONObject(i)

                    userModel = Gson()
                        .fromJson(notifyObjJson.toString(), UserModel::class.java)
                }

                root.tvProfileUserName.text = userModel.cus_name
//                root.tvProfileCustType.text = userModel.cus_type
                root.tvProfileEmail.text = userModel.cus_email
                root.tvProfileMobileNumber.text = jsonObject.getString(AppConstants.CUS_MOBILE)
                root.tvTerminalID.text = "Terminal ID - " +  userModel.T_ID
                root.tvMerchantId.text = "Merchant ID - " + userModel.M_ID
                /*     Glide.with(this)
                         .load(userModel.profile_img)
                         .into(ivProfileImage)
     */

            } else {
                if (messageCode.equals(getString(R.string.error_expired_token))) {
                    AppCommonMethods.logoutOnExpiredDialog(requireContext())

                } else {
                    requireActivity().toast(messageCode.trim())
                }
            }
        }
        if (flag.equals(CHANGEPASSWORD_API)) {
            Log.e("CHANGEPASSWORD_API", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status.toString())
            //Log.e(AppConstants.MESSAGE_CODE, messageCode);
            if (status.contains("true")) {
                root.progress_bar.visibility = View.GONE

                val cast = jsonObject.getJSONArray("result")
                for (i in 0 until cast.length()) {
                    val notifyObjJson = cast.getJSONObject(i)
                    val cust_id = notifyObjJson.getString("cus_id")
                    Log.e("id", cust_id)
                    userModel = Gson()
                        .fromJson(notifyObjJson.toString(), UserModel::class.java)
                }
                val gson = Gson()
                val json = gson.toJson(userModel)
                AppPrefs.putStringPref("userModel", json, requireContext())
                Toast.makeText(requireContext(), "Password Changed Successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else if (status.contains("false")) {

                root.progress_bar.visibility = View.GONE

                //JSONObject locationId = cast.getJSONObject(0);
                Toast.makeText(requireContext(), "Invalid Password", Toast.LENGTH_SHORT).show()

            }
        }
        if (flag.equals(CHANGEPIN_API)) {
            Log.e("CHANGEPIN_API", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status.toString())
            //Log.e(AppConstants.MESSAGE_CODE, messageCode);
            if (status.contains("true")) {
                root.progress_bar.visibility = View.INVISIBLE
                val cast = jsonObject.getJSONArray("result")
                for (i in 0 until cast.length()) {
                    val notifyObjJson = cast.getJSONObject(i)
                    val cust_id = notifyObjJson.getString("cus_id")
                    Log.e("id", cust_id)
                    userModel = Gson()
                        .fromJson(notifyObjJson.toString(), UserModel::class.java)
                }
                val gson = Gson()
                val json = gson.toJson(userModel)
                AppPrefs.putStringPref("userModel", json, requireContext())
                Toast.makeText(requireContext(), "Pin Changed Successfully", Toast.LENGTH_SHORT).show()
                dialog.etCurrentPin.setText("")
                dialog.etNewPin.setText("")
                dialog.etConfirmNewPin.setText("")
                dialog.dismiss()
            } else if (status.contains("false")) {
                root.progress_bar.visibility = View.INVISIBLE
                //JSONObject locationId = cast.getJSONObject(0);
                Toast.makeText(requireContext(), "Invalid Mobile or Pin", Toast.LENGTH_SHORT).show()
                dialog.etCurrentPin.setText("")
                dialog.etNewPin.setText("")
                dialog.etConfirmNewPin.setText("")
            }
        }
        if (flag.equals(SEND_OTP)) {
            Log.e("SEND_OTP", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status)
            //Log.e(AppConstants.MESSAGE_CODE, messageCode);
            if (status.contains("true")) {
                root.progress_bar.visibility = View.INVISIBLE
                confirmOtp(otp)
            } else {

                root.progress_bar.visibility = View.INVISIBLE

            }
        }
        if (flag.equals(FORGET_PIN)) {
            Log.e("FORGET_PIN", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)
            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode);
            if (status.contains("true")) {
                root.progress_bar.visibility = View.INVISIBLE
                showForgotpin(messageCode)
            } else {
                root.progress_bar.visibility = View.INVISIBLE
                Toast.makeText(
                    requireContext(),
                    "Oops! Something Went Wrong Please Try Again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        if (flag.equals(CONTACTUS)) {
            Log.e("CONTACTUS", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status)
            if (status.contains("true")) {
                root.progress_bar.visibility = View.GONE

                val cast = jsonObject.getJSONArray("result")
                for (i in 0 until cast.length()) {
                    val notifyObjJson = cast.getJSONObject(i)
                    privacy_policy = notifyObjJson.getString("privacy_policy")
                    about_us = notifyObjJson.getString("about_us")
//                    tvContactWebsite.setText(notifyObjJson.getString("website"))

                }

                root.rl_privacypolicy.setOnClickListener {

                    aboutUsDialog("Privacy Policy",privacy_policy)
                }

                root.rl_AboutUs.setOnClickListener {

                    aboutUsDialog("About Us",about_us)
                }


                Log.e("privacy_policy", privacy_policy)
                Log.e("about_us", about_us)



            } else {
                root.progress_bar.visibility = View.GONE


            }
        }

    }

    fun confirmOtp(otp: String) {
        dialog = Dialog(requireContext(), R.style.ThemeOverlay_MaterialComponents_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_confirmotp)

        dialog.etOtp.requestFocus()
        dialog.tvDialogCancel.setOnClickListener {
            dialog.dismiss()
        }


        dialog.getWindow()!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        dialog.tvConfirmOtp.setOnClickListener {
            if (!dialog.etOtp.text.toString().equals(otp)) {
                dialog.etOtp.requestFocus()
                dialog.etOtp.setError("Please Enter Valid OTP")
            } else {
                forgetpin(userModel.cus_id, deviceId, deviceNameDet, userModel.cus_type)
                dialog.dismiss()

            }

        }


        dialog.show()
    }

    private fun showForgotpin(msg: String) {
        val builder1 =
            AlertDialog.Builder(requireContext())
        builder1.setTitle("Success!")
        builder1.setMessage(msg)
        builder1.setCancelable(true)
        builder1.setPositiveButton(
            "OK"
        ) { dialog, id ->


            dialog.cancel()
        }

        val alert11 = builder1.create()
        alert11.show()
    }


    private fun aboutUsDialog(title :  String, html : String) {
        dialog = Dialog(requireContext(), R.style.Widget_MaterialComponents_MaterialCalendar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_about_us)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.custToolbarDialog.ivClosePinDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.custToolbarDialog.tvTitle.setText(title)

        dialog.tvContent.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(html)
        }


        dialog.show()
    }


}