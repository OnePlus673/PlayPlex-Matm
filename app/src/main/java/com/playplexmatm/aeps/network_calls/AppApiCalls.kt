package com.playplexmatm.aeps.network_calls

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.playplexmatm.R
import com.playplexmatm.aeps.main_controller.VolleySingleton
import com.playplexmatm.aeps.network_calls.AppApiUrl.AEPSCOMMISSION_HISTORY
import com.playplexmatm.aeps.network_calls.AppApiUrl.AEPSPAYOUT_HISTORY
import com.playplexmatm.aeps.network_calls.AppApiUrl.AEPS_BANK_LIST
import com.playplexmatm.aeps.network_calls.AppApiUrl.AEPS_COMMISIONSLAB_URL
import com.playplexmatm.aeps.network_calls.AppApiUrl.AEPS_HISTORY
import com.playplexmatm.aeps.network_calls.AppApiUrl.AEPS_PAYOUT
import com.playplexmatm.aeps.network_calls.AppApiUrl.AEPS_TRANSACTION
import com.playplexmatm.aeps.network_calls.AppApiUrl.BROWSE_PLANS
import com.playplexmatm.aeps.network_calls.AppApiUrl.BROWSE_PLANS_DTH
import com.playplexmatm.aeps.network_calls.AppApiUrl.CHANGE_PASWORD
import com.playplexmatm.aeps.network_calls.AppApiUrl.CHANGE_PIN
import com.playplexmatm.aeps.network_calls.AppApiUrl.CHECKSAME_FUNDTRANSFER
import com.playplexmatm.aeps.network_calls.AppApiUrl.CHECK_IF_SAME_RECHARGE
import com.playplexmatm.aeps.network_calls.AppApiUrl.CHECK_STATUS
import com.playplexmatm.aeps.network_calls.AppApiUrl.COMMISION_REPORT_URL
import com.playplexmatm.aeps.network_calls.AppApiUrl.DELETE_RECIPIENT
import com.playplexmatm.aeps.network_calls.AppApiUrl.DISSPUTE_HISTORY
import com.playplexmatm.aeps.network_calls.AppApiUrl.DMT_ADD_BENFICIARY
import com.playplexmatm.aeps.network_calls.AppApiUrl.DMT_BANK_LIST
import com.playplexmatm.aeps.network_calls.AppApiUrl.DMT_COMMISIONSLAB_URL
import com.playplexmatm.aeps.network_calls.AppApiUrl.DMT_HISTORY
import com.playplexmatm.aeps.network_calls.AppApiUrl.DMT_LOGIN
import com.playplexmatm.aeps.network_calls.AppApiUrl.DMT_REGISTER
import com.playplexmatm.aeps.network_calls.AppApiUrl.DMT_SENDOTP
import com.playplexmatm.aeps.network_calls.AppApiUrl.DMT_TRANSACTION
import com.playplexmatm.aeps.network_calls.AppApiUrl.DMT_VIEW_BENIFICIARY
import com.playplexmatm.aeps.network_calls.AppApiUrl.DUMMY_PID
import com.playplexmatm.aeps.network_calls.AppApiUrl.FORGETPIN
import com.playplexmatm.aeps.network_calls.AppApiUrl.FORGOT_PASS
import com.playplexmatm.aeps.network_calls.AppApiUrl.FUND_CREDIT
import com.playplexmatm.aeps.network_calls.AppApiUrl.FUND_DEBIT
import com.playplexmatm.aeps.network_calls.AppApiUrl.FUND_MYREQUEST
import com.playplexmatm.aeps.network_calls.AppApiUrl.FUND_REQUEST_URL
import com.playplexmatm.aeps.network_calls.AppApiUrl.FUND_TRANSFER
import com.playplexmatm.aeps.network_calls.AppApiUrl.GETBILLDETAILS
import com.playplexmatm.aeps.network_calls.AppApiUrl.GETFORGOTOTP
import com.playplexmatm.aeps.network_calls.AppApiUrl.GETPINOTP
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_AEPS_BALANCE
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_AEPS_CHARGE
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_BALANCE
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_CHARGE
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_DASHBOARD
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_OPERATORS
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_PAYOUT_DETAILS
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_PROFILE
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_SUPPORT
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_UPIDETAILS
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_USER_ID
import com.playplexmatm.aeps.network_calls.AppApiUrl.LEDGER_REPORT
import com.playplexmatm.aeps.network_calls.AppApiUrl.LOGIN_BY_PASSWORD
import com.playplexmatm.aeps.network_calls.AppApiUrl.LOGOUT_USER
import com.playplexmatm.aeps.network_calls.AppApiUrl.MICRO_ATM_LOGIN
import com.playplexmatm.aeps.network_calls.AppApiUrl.MICRO_ATM_TRANSACTION
import com.playplexmatm.aeps.network_calls.AppApiUrl.NEWUSER_URL
import com.playplexmatm.aeps.network_calls.AppApiUrl.NEW_DISTRIBUTOR_URL
import com.playplexmatm.aeps.network_calls.AppApiUrl.RAISE_DISPUTE
import com.playplexmatm.aeps.network_calls.AppApiUrl.RECHARGE
import com.playplexmatm.aeps.network_calls.AppApiUrl.RECHARGE_HISTORY
import com.playplexmatm.aeps.network_calls.AppApiUrl.RECHARGE_HISTORY_BY_DATE
import com.playplexmatm.aeps.network_calls.AppApiUrl.RECHARGE_HISTORY_BY_MOBILE
import com.playplexmatm.aeps.network_calls.AppApiUrl.REGISTER_USER
import com.playplexmatm.aeps.network_calls.AppApiUrl.UPDATE_WALLET
import com.playplexmatm.aeps.network_calls.AppApiUrl.USER_DAYBOOK
import com.playplexmatm.aeps.network_calls.AppApiUrl.USER_LIST
import com.playplexmatm.aeps.network_calls.AppApiUrl.AEPS_LEDGER_HISTORY
import com.playplexmatm.aeps.network_calls.AppApiUrl.BILLPAY
import com.playplexmatm.aeps.network_calls.AppApiUrl.CIRCLE_API
import com.playplexmatm.aeps.network_calls.AppApiUrl.EKYC
import com.playplexmatm.aeps.network_calls.AppApiUrl.FETCH_ONBOARDING_DETAILS
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_ALL_IFSC
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_DYNAMIC_QR_CODE
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_IFSC
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_LOGIN_DETAILS
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_MERCHANT_CATEGORY
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_OPERATORS_API
import com.playplexmatm.aeps.network_calls.AppApiUrl.GET_PINCODE
import com.playplexmatm.aeps.network_calls.AppApiUrl.MICROATM_COMMISIONSLAB_URL
import com.playplexmatm.aeps.network_calls.AppApiUrl.MICROATM_HISTORY
import com.playplexmatm.aeps.network_calls.AppApiUrl.MICRO_ATM_ONBOARDING_STATUS
import com.playplexmatm.aeps.network_calls.AppApiUrl.POS_COMMISIONSLAB_URL
import com.playplexmatm.aeps.network_calls.AppApiUrl.POS_HISTORY
import com.playplexmatm.aeps.network_calls.AppApiUrl.RESEND_EKYC_OTP
import com.playplexmatm.aeps.network_calls.AppApiUrl.SEND_REGISTER_OTP
import com.playplexmatm.aeps.network_calls.AppApiUrl.STATE_LIST
import com.playplexmatm.aeps.network_calls.AppApiUrl.UPDATE_AEPS_BANK
import com.playplexmatm.aeps.network_calls.AppApiUrl.UPDATE_KYC_MATM
import com.playplexmatm.aeps.network_calls.AppApiUrl.UPDATE_LOGIN_DETAILS
import com.playplexmatm.aeps.network_calls.AppApiUrl.USER_SEARCH
import com.playplexmatm.aeps.network_calls.AppApiUrl.VALIDATE_EKYC_OTP
import com.playplexmatm.aeps.network_calls.AppApiUrl.VERIFY_BANK
import com.playplexmatm.aeps.network_calls.AppApiUrl.VERIFY_KYC
import com.playplexmatm.aeps.network_calls.AppApiUrl.VERIFY_KYC_MATM
import com.playplexmatm.aeps.network_calls.AppApiUrl.VERIFY_MOBILE
import com.playplexmatm.aeps.network_calls.AppApiUrl.VERIFY_PIN
import com.playplexmatm.util.AppCommonMethods
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.AppConstants.Companion.AMOUNT
import com.playplexmatm.util.AppConstants.Companion.API_KEY
import com.playplexmatm.util.AppConstants.Companion.BASIC
import com.playplexmatm.util.AppConstants.Companion.BASIC_TOKEN
import com.playplexmatm.util.AppConstants.Companion.CUS_ID
import com.playplexmatm.util.AppConstants.Companion.CUS_MOBILE_API
import com.playplexmatm.util.AppConstants.Companion.CUS_TYPE
import com.playplexmatm.util.AppConstants.Companion.DATE
import com.playplexmatm.util.AppConstants.Companion.DEVICE_ID
import com.playplexmatm.util.AppConstants.Companion.DEVICE_NAME
import com.playplexmatm.util.AppConstants.Companion.FORM_URL_ENCODED
import com.playplexmatm.util.AppConstants.Companion.FROM_DATE
import com.playplexmatm.util.AppConstants.Companion.MOBILE
import com.playplexmatm.util.AppConstants.Companion.MOBILE_RECHARGE
import com.playplexmatm.util.AppConstants.Companion.OPERATOR
import com.playplexmatm.util.AppConstants.Companion.OPERATOR_TYPE
import com.playplexmatm.util.AppConstants.Companion.PASSWORD
import com.playplexmatm.util.AppConstants.Companion.PIN
import com.playplexmatm.util.AppConstants.Companion.REC_MOBILE
import com.playplexmatm.util.AppConstants.Companion.TOKEN
import com.playplexmatm.util.AppConstants.Companion.TO_DATE
import com.playplexmatm.util.AppPrefs
import org.json.JSONException
import java.util.*
import kotlin.jvm.Throws


class AppApiCalls(
    private val mContext: Context,
    flag: String,
    listener: OnAPICallCompleteListener)
    :
    AppConstants {
    private val TAG = "Demo_APICalls"
    private val mApiCallCompleteListener: OnAPICallCompleteListener
    private var mFlag = ""
    var mRetryPolicy: RetryPolicy = DefaultRetryPolicy(
        0,
        -1,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
    )

    //Api functions
    //Login
    fun login(mobile: String, password: String, deviceId: String, deviceName: String, otp: String) {

        val url: String = LOGIN_BY_PASSWORD
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    ).show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[MOBILE] = mobile
                    jsonObject[PASSWORD] = password
                    jsonObject[DEVICE_ID] = deviceId
                    jsonObject[DEVICE_NAME] = deviceName
                    jsonObject["otp"] = otp
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    //Register
    fun register(
        username: String,
        mobile: String,
        email: String,
        password: String,
        pin: String,
        deviceId: String,
        deviceName: String
    ) {
        val url: String = REGISTER_USER
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    ).show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["user_name"] = username
                    jsonObject["user_mobile"] = mobile
                    jsonObject["email"] = email
                    jsonObject["password"] = password
                    jsonObject["pin"] = pin
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun sendRegisterOtp(
        otp: String,
        mobile_number: String
    ) {
        val url: String = SEND_REGISTER_OTP
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    ).show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()

                    jsonObject["otp"] = otp
                    jsonObject["mobile_number"] = mobile_number
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun verifyMobile(mobile: String, otp: String) {
        Log.e("MOBILE", mobile)

        val url: String = VERIFY_MOBILE
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = mobile
                    jsonObject["otp"] = otp
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    //Dashboard
    fun dashboard(
        cus_id: String
    ) {
        val url: String = GET_DASHBOARD
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[CUS_MOBILE_API] = cus_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun getMicroAtmLoginDetails(
        cus_id: String
    ) {
        val url: String = GET_LOGIN_DETAILS
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun updateMicroAtmLoginDetails(
        cus_id: String,
        newPassword : String
    ) {
        val url: String = UPDATE_LOGIN_DETAILS
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["newPassword"] = newPassword
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun getIfsc(
        ifsc : String
    ) {
        val url: String = GET_IFSC
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["ifsc"] = ifsc
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun getAllIfsc(
    ) {
        val url: String = GET_ALL_IFSC
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun getPinCode(
    ) {
        val url: String = GET_PINCODE
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    //Get Balance
    fun getBalance(
        cus_id: String
    ) {

        val url: String = GET_BALANCE
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[CUS_MOBILE_API] = cus_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    //Get AEPS Balance
    fun getAepsBalance(
        cus_id: String
    ) {

        val url: String = GET_AEPS_BALANCE
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[CUS_ID] = cus_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    //Get Profile
    fun getProfile(
        cus_id: String
    ) {

        val url: String = GET_PROFILE
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[CUS_MOBILE_API] = cus_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    //getCircle
    fun getcircle() {
        val url: String = CIRCLE_API
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)
                    }
                    // response
                    Log.d("Response", response)
                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    //Get Operators
    fun getOperators(
        operator_type: String
    ) {

        val url: String = GET_OPERATORS
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[OPERATOR_TYPE] = operator_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun billPay(
        cus_id: String,
        rec_mobile: String,
        amount: String,
        operator: String,
        cus_type: String,
        optional1: String,
        mobile_number: String,
        optional2: String,
        ref_id: String
    ) {
        val url: String = BILLPAY
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)
                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[CUS_ID] = cus_id
                    jsonObject[MOBILE_RECHARGE] = rec_mobile
                    jsonObject[AMOUNT] = amount
                    jsonObject[OPERATOR] = operator
                    jsonObject[CUS_TYPE] = cus_type
                    jsonObject["optinal1"] = optional1
                    jsonObject["mobile_number"] = mobile_number
                    jsonObject["optional2"] = optional2
                    jsonObject["ref_id"] = ref_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    //Check if Same recharge
    fun checkIfSameRecharge(
        cus_id: String,
        rec_mobile: String,
        amount: String,
        operator: String
    ) {

        val url: String = CHECK_IF_SAME_RECHARGE
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[CUS_MOBILE_API] = cus_id
                    jsonObject[OPERATOR] = operator
                    jsonObject[REC_MOBILE] = rec_mobile
                    jsonObject[AMOUNT] = amount
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    //Verify Pin
    fun verifyPin(
        cus_mobile: String,
        pin: String
    ) {

        val url: String = VERIFY_PIN
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[CUS_MOBILE_API] = cus_mobile
                    jsonObject[PIN] = pin
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    //Recharge Api
    fun rechargeApi(
        cus_id: String,
        rec_mobile: String,
        amount: String,
        operator: String,
        cus_type: String,
    ) {

        val url: String = RECHARGE
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[CUS_ID] = cus_id
                    jsonObject[MOBILE_RECHARGE] = rec_mobile
                    jsonObject[AMOUNT] = amount
                    jsonObject[OPERATOR] = operator
                    jsonObject[CUS_TYPE] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    //Recharge History
    fun rechargeHistoryFromTo(
        cus_mobile: String,
        fromDate: String,
        toDate: String,
    ) {

        val url: String = RECHARGE_HISTORY
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    ).show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[CUS_MOBILE_API] = cus_mobile
                    jsonObject[FROM_DATE] = fromDate
                    jsonObject[TO_DATE] = toDate
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()


                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    //Recharge History by date
    fun rechargeHistoryByDate(
        cus_mobile: String,
        date: String,
    ) {

        val url: String = RECHARGE_HISTORY
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    ).show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[CUS_MOBILE_API] = cus_mobile
                    jsonObject[DATE] = date
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()


                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    //****************************Do not copy from here***************************//


    fun contactUsApi(
        cus_id: String,
        deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = GET_SUPPORT
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()



                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun changePassword(
        cus_id: String,
        current_password: String,
        new_password: String,
        deviceId: String,
        deviceName: String,
        pin: String,
        pass: String,
        cus_mobile: String,
        cus_type: String
    ) {

        val url: String = CHANGE_PASWORD
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["password"] = current_password
                    jsonObject["newpassword"] = new_password
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun changePin(
        cus_id: String,
        current_pin: String,
        new_pin: String,
        deviceId: String,
        deviceName: String,
        pin: String,
        pass: String,
        cus_mobile: String,
        cus_type: String
    ) {

        val url: String = CHANGE_PIN
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["id"] = cus_id
                    jsonObject["curr_pin"] = current_pin
                    jsonObject["new_pin"] = new_pin
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun forgotPassword(mobile: String, deviceId: String, deviceName: String) {
        Log.e("MOBILE", mobile)

        val url: String = FORGOT_PASS
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = mobile
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun getUserId(
        cus_id: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = GET_USER_ID
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = cus_id
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun getUserList(
        dis_cus_id: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {
        Log.e("DISCUS", dis_cus_id)
        val url: String = USER_LIST
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["dis_cus_id"] = dis_cus_id
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun createUserApi(
        newMob: String, dis_id: String, newName: String,
        newPass: String, newEmail: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = NEWUSER_URL
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = newMob
                    jsonObject["dis_cus_id"] = dis_id
                    jsonObject["name"] = newName
                    jsonObject["password"] = newPass
                    jsonObject["email"] = newEmail
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun createDistributorApi(
        newMob: String, dis_id: String, newName: String,
        newPass: String, newEmail: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = NEW_DISTRIBUTOR_URL
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = newMob
                    jsonObject["mst_cus_id"] = dis_id
                    jsonObject["name"] = newName
                    jsonObject["password"] = newPass
                    jsonObject["email"] = newEmail
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun dthInfo(
        mobile: String, operator: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = BROWSE_PLANS_DTH
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = mobile
                    jsonObject["operator"] = operator
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun mobileOffers(
        mobile: String, operator: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = BROWSE_PLANS
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = mobile
                    jsonObject["operator"] = operator
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    Log.e("offer",operator)
             //       Log.e("offer",mrobotic_code)

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()

                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun getBillDetails(
        mobile: String, operator: String , number : String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String, bill_unit: String
    ) {

        val url: String = GETBILLDETAILS
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = mobile
                    jsonObject["operator"] = operator
                    jsonObject["number"] = number
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject["bill_unit"] = bill_unit
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }
    fun commisionSlab(
        cus_id: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = COMMISION_REPORT_URL
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun disputeHistory(
        cus_id: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = DISSPUTE_HISTORY
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun searcUser(
        dis_cus_id: String, mobileorname: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = USER_SEARCH
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["dis_cus_id"] = dis_cus_id
                    jsonObject["mobileorname"] = mobileorname
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun fundRecieveHistory(
        cus_id: String,
        fromdate: String,
        todate: String,
        deviceId: String,
        deviceName: String,
        pin: String,
        pass: String,
        cus_mobile: String,
        cus_type: String
    ) {

        val url: String = FUND_CREDIT
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["fromdate"] = fromdate
                    jsonObject["todate"] = todate
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()

                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun fundTransferHistory(
        cus_id: String,
        fromdate: String,
        todate: String,
        deviceId: String,
        deviceName: String,
        pin: String,
        pass: String,
        cus_mobile: String,
        cus_type: String
    ) {

        val url: String = FUND_DEBIT
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["fromdate"] = fromdate
                    jsonObject["todate"] = todate
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"


                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun ledgerReportApi(
        cus_id: String,
        fromdate: String,
        todate: String,
        deviceId: String,
        deviceName: String,
        pin: String,
        pass: String,
        cus_mobile: String,
        cus_type: String
    ) {

        val url: String = LEDGER_REPORT
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["fromdate"] = fromdate
                    jsonObject["todate"] = todate
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun userDayBook(
        cus_id: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = USER_DAYBOOK
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun checkIfSameFundTransfer(
        cus_id: String,
        to_id: String,
        amount: String,
        deviceId: String,
        deviceName: String,
        pin: String,
        pass: String,
        cus_mobile: String,
        cus_type: String
    ) {

        val url: String = CHECKSAME_FUNDTRANSFER
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()

                    jsonObject["cus_id"] = cus_id
                    jsonObject["to_id"] = to_id
                    jsonObject["amount"] = amount
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()


                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun getpinotp(
        mobile: String, otp: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        Log.e("mobile", mobile)
        val url: String = GETPINOTP
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = mobile
                    jsonObject["otp"] = otp
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun getForgetPassOtp(
        mobile: String, otp: String
    ) {

        Log.e("mobile", mobile)
        val url: String = GETFORGOTOTP
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = mobile
                    jsonObject["otp"] = otp
                    // jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun forgetpin(
        cus_id: String,
        deviceId: String,
        deviceName: String, cus_type: String
    ) {

        val url: String = FORGETPIN
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["cus_type"] = cus_type
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun getDynamicQrCode(mobile: String,
                         cusName:String,
                         cus_id:String
    ) {
        val url: String = GET_DYNAMIC_QR_CODE
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = mobile
                    jsonObject["cus_id"] = cus_id
                    jsonObject["name"] = cusName
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun fundRequestHistory(
        cus_id: String,
        fromdate: String,
        todate: String,
        deviceId: String,
        deviceName: String,
        pin: String,
        pass: String,
        cus_mobile: String,
        cus_type: String
    ) {

        val url: String = FUND_MYREQUEST
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["fromdate"] = fromdate
                    jsonObject["todate"] = todate
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun fundRequestApi(
        cus_id: String,
        req_to: String,
        amount: String,
        bank: String,
        refrenceNumber: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = FUND_REQUEST_URL
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["req_to"] = req_to
                    jsonObject["amount"] = amount
                    jsonObject["bank"] = bank
                    jsonObject["ref_no"] = refrenceNumber
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun fundTransferApi(
        dis_id: String,
        cus_id: String,
        amount_string: String,
        deviceId: String,
        deviceName: String,
        pin: String,
        pass: String,
        cus_mobile: String,
        cus_type: String
    ) {

        val url: String = FUND_TRANSFER
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["dis_id"] = dis_id
                    jsonObject["c_id"] = cus_id
                    jsonObject["amount"] = amount_string
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun raiseDisputeApi(
        cus_id: String,
        recid: String,
        issue: String,
        subject: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = RAISE_DISPUTE
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["recid"] = recid
                    jsonObject["issue"] = issue
                    jsonObject["subject"] = subject
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()


                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun getUpi(
        cus_id: String,
        deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = GET_UPIDETAILS
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> = HashMap()

                    jsonObject["cus_id"] = cus_id
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun addFundsApi(
        cus_id: String,
        amount: String,
        bank: String,
        transactionId: String,
        txnRef: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = UPDATE_WALLET
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> = HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["amount"] = amount
                    jsonObject["bank"] = bank
                    jsonObject["transaction_id"] = transactionId
                    jsonObject["transaction_ref"] = txnRef
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun userLogout(
        cus_id: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = LOGOUT_USER
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun rechargeHistory(
        cus_id: String,
        fromdate: String,
        todate: String,
        deviceId: String,
        deviceName: String,
        pin: String,
        pass: String,
        cus_mobile: String,
        cus_type: String
    ) {

        val url: String = RECHARGE_HISTORY
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["fromDate"] = fromdate
                    jsonObject["toDate"] = todate
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()


                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun rechargeHistoryByMobile(
        cus_id: String, mobile: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = RECHARGE_HISTORY_BY_MOBILE
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["mobile"] = mobile
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()

                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun rechargeHistoryByDate(
        cus_id: String, date: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = RECHARGE_HISTORY_BY_DATE
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["date"] = date
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()

                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    //DMT APIS
    fun loginDmt(mobile: String, deviceId: String, deviceName: String) {
        Log.e("MOBILE", mobile)

        val url: String = DMT_LOGIN
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = mobile
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun addBeneficiaryAccount(
        senderMobile: String,
        name: String,
        bank_acct: String,
        ifsc: String,
        bankname: String,
        cus_id: String
    ) {
        Log.e("MOBILE", senderMobile)

        val url: String = DMT_ADD_BENFICIARY
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = senderMobile
                    jsonObject["name"] = name
                    jsonObject["bank_acct"] = bank_acct
                    jsonObject["ifsc"] = ifsc
                    jsonObject["bankname"] = bankname
                    jsonObject["cus_id"] = cus_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun verifBenefAccount(
        adhar_number: String,
        pan_number: String,
        mobile_number: String,
        account_number: String,
        bank_code: String,
    ) {
        Log.e("MOBILE", adhar_number)

        val url: String = VERIFY_BANK
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["adhar_number"] = adhar_number
                    jsonObject["pan_number"] = pan_number
                    jsonObject["mobile_number"] = mobile_number
                    jsonObject["account_number"] = account_number
                    jsonObject["bank_code"] = bank_code
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun viewBenificiary(
        dmt_user_id: String
    ) {
        Log.e("MOBILE", dmt_user_id)

        val url: String = DMT_VIEW_BENIFICIARY
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = dmt_user_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun dummyPid(
        PidData: String, pidOptions : String
    ) {
        Log.e("MOBILE", PidData)

        val url: String = DUMMY_PID
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["PidData"] = PidData
                    jsonObject["PidOption"] = pidOptions
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun bankListDmt(
    ) {

        val url: String = DMT_BANK_LIST
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun registerDmt(
        name: String,
        mobile: String,
        deviceId: String,
        deviceName: String,
        cus_id: String
    ) {
        Log.e("MOBILE", mobile)

        val url: String = DMT_REGISTER
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["name"] = name
                    jsonObject["mobile"] = mobile
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["cus_id"] = cus_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun verifySenderOtp(
        mobile: String,
        verifyReferenceNo: String,
        otp: String,
        deviceId: String,
        deviceName: String,

        ) {
        Log.e("MOBILE", mobile)

        val url: String = DMT_SENDOTP
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = mobile
                    jsonObject["VerifyReferenceNo"] = verifyReferenceNo
                    jsonObject["otp"] = otp
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun dmtHistory(cus_id: String) {

        val url: String = DMT_HISTORY
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun deleteRecipient(dmt_user_id: String, bene_id: String) {

        val url: String = DELETE_RECIPIENT
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(
                            true,
                            mFlag,
                            response
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = dmt_user_id
                    jsonObject["bene_code"] = bene_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun checkStatus(unique_id: String) {

        val url: String = CHECK_STATUS
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(
                            true,
                            mFlag,
                            response
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["unique_id"] = unique_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun dmtTransactionApi(
        mobile: String,
        bene_code: String,
        aadhar_no: String,
        pan_no: String,
        amount: String,
        deviceId: String,
        deviceName: String,
        cus_id: String
    ) {

        val url: String = DMT_TRANSACTION
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(
                            true,
                            mFlag,
                            response
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = mobile
                    jsonObject["bene_code"] = bene_code
                    jsonObject["aadhar_no"] = aadhar_no
                    jsonObject["pan_number"] = pan_no
                    jsonObject["deviceName"] = deviceName
                    jsonObject["deviceId"] = deviceId
                    jsonObject["amount"] = amount
                    jsonObject["cus_id"] = cus_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun getCharge(amount: String) {

        val url: String = GET_CHARGE
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(
                            true,
                            mFlag,
                            response
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["amount"] = amount
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun dmtcommisionSlab(
        cus_id: String
    ) {

        val url: String = DMT_COMMISIONSLAB_URL
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id

                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    //MicroAtm
    fun fetchOnboardingDetails(
        merchant_ref_id : String,
        cus_email : String
    ) {

        val url: String = FETCH_ONBOARDING_DETAILS
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(
                            true,
                            mFlag,
                            response
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["merchant_ref_id"] = merchant_ref_id
                    jsonObject["cus_id"] = cus_email
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun getMerchantCategory() {

        val url: String = GET_MERCHANT_CATEGORY
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(
                            true,
                            mFlag,
                            response
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun microatmHistory(
        cus_id: String,
        fromDate: String,
        toDate: String,
        deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = MICROATM_HISTORY
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["fromDate"] = fromDate
                    jsonObject["toDate"] = toDate
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun posHistory(
        cus_id: String,
        fromDate: String,
        toDate: String,
        deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = POS_HISTORY
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["fromDate"] = fromDate
                    jsonObject["toDate"] = toDate
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun microatmcommisionSlab(
        cus_id: String
    ) {

        val url: String = MICROATM_COMMISIONSLAB_URL
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id

                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun poscommisionSlab(
        cus_id: String
    ) {

        val url: String = POS_COMMISIONSLAB_URL
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id

                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    //Micro Atm
    fun getMatmOnboardingStatusAPi(
        cus_id: String
    ) {

        val url: String = MICRO_ATM_ONBOARDING_STATUS
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d(mContext.getString(R.string.response), response)

                    }
                    // response
                    Log.d(mContext.getString(R.string.response), response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e(mContext.getString(R.string.error_api), error.toString())
                    }
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.error_server_error),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun onboardMicroAtm(
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
        merchant_account : String,
        merchant_ifsc : String,
        ifsc_code : String,
        img_aadhaar_front : String,
        img_aadhaar_back : String,
        img_pan : String,
        img_cc : String,
        cus_id : String,
        user_email : String
    ) {

        val url: String = VERIFY_KYC_MATM
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(
                            true,
                            mFlag,
                            response
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_mobile"] = cus_mobile
                    Log.e("Matm_cusmobile",cus_mobile)
                    jsonObject["merchant_type"] = merchant_type
                    Log.e("Matm_merchant_type",merchant_type)

                    jsonObject["merchant_id"] = merchant_id
                    Log.e("Matm_merchant_id",merchant_id)

                    jsonObject["contact_name"] = contact_name
                    Log.e("Matm_contact_name",contact_name)

                    jsonObject["legal_name"] = legal_name
                    Log.e("Matm_legal_name",legal_name)

                    jsonObject["contact_mobile"] = contact_mobile
                    Log.e("Matm_contact_mobile",contact_mobile)

                    jsonObject["contact_alternate_mobile"] =  contact_alternate_mobile
                    Log.e("Matm_contact_alternate_mobile",contact_alternate_mobile)
                    jsonObject["contact_email"] = contact_email
                    Log.e("Matm_contact_email",contact_email)
                    jsonObject["brand_name"] = brand_name
                    Log.e("Matm_brand_name",brand_name)
                    jsonObject["business_nature"] =  business_nature
                    Log.e("Matm_business_nature",business_nature)
                    jsonObject["business_type"] = business_type
                    Log.e("Matm_business_type",business_type)
                    jsonObject["established_year"] = established_year
                    Log.e("Matm_established_year",established_year)
                    jsonObject["pan_number"] = pan_number
                    Log.e("Matm_pan_number",pan_number)
                    jsonObject["registered_mobile"] = registered_mobile
                    Log.e("Matm_registered_mobile",registered_mobile)
                    jsonObject["registered_pin"] = registered_pin
                    Log.e("Matm_registered_pin",registered_pin)
                    jsonObject["registered_address"] = registered_address
                    Log.e("Matm_registered_address",registered_address)

                    jsonObject["agreement_date"] = agreement_date
                    Log.e("Matm_agreement_date",agreement_date)

                    jsonObject["title_personal"] = title_personal
                    Log.e("Matm_title_personal",title_personal)
                    jsonObject["first_name_personal"] = first_name_personal
                    Log.e("Matm_first_name_personal",first_name_personal)
                    jsonObject["last_name_personal"] = last_name_personal
                    Log.e("Matm_last_name_personal",last_name_personal)
                    jsonObject["mobile_personal"] = mobile_personal
                    Log.e("Matm_mobile_personal",mobile_personal)
                    jsonObject["email_personal"] = email_personal
                    Log.e("Matm_email_personal",email_personal)
                    jsonObject["dob_personal"] = dob_personal
                    Log.e("Matm_dob_personal",dob_personal)
                    jsonObject["address_personal"] = address_personal
                    Log.e("Matm_address_personal",address_personal)
                    jsonObject["pin_personal"] = pin_personal
                    Log.e("Matm_pin_personal",pin_personal)
                    jsonObject["city_personal"] = city_personal
                    Log.e("Matm_city_personal",city_personal)
                    jsonObject["state_personal"] = state_personal
                    Log.e("Matm_state_personal",state_personal)
                    jsonObject["nationality_personal"] = nationality_personal
                    Log.e("Matm_nationality_personal",nationality_personal)
                    jsonObject["aadhaar_personal"] =  aadhaar_personal
                    Log.e("Matm_aadhaar_personal",aadhaar_personal)
                    jsonObject["pan_personal"] =  pan_personal
                    Log.e("Matm_pan_personal",pan_personal)
                    jsonObject["merchant_bank"] =  merchant_bank
                    Log.e("Matm_merchant_bank",merchant_bank)
                    jsonObject["merchant_account"] = merchant_account
                    Log.e("Matm_merchant_account",merchant_account)
                    jsonObject["merchant_ifsc"] = merchant_ifsc
                    Log.e("Matm_merchant_ifsc",merchant_ifsc)
                    jsonObject["ifsc_code"] = ifsc_code


                    jsonObject["img_aadhaar_front"] = img_aadhaar_front
                    Log.e("Matm_",img_aadhaar_front.substringBefore("a") )
                    jsonObject["img_aadhaar_back"] = img_aadhaar_back
                    Log.e("Matm_",img_aadhaar_back.substringBefore("a") )
                    jsonObject["img_pan"] = img_pan
                    Log.e("Matm_img_pan",img_pan.substringBefore("a"))
                    jsonObject["img_cc"] = img_cc
                    Log.e("Matm_img_cc",img_cc.substringBefore("a"))
                    jsonObject["cus_id"] = cus_id
                    Log.e("cus_id",cus_id)
                    jsonObject["user_email"] = user_email
                    Log.e("user_email",user_email)


                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun update_onboardMicroAtm(
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
        merchant_account : String,
        merchant_ifsc : String,
        ifsc_code : String,
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

        val url: String = UPDATE_KYC_MATM
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(
                            true,
                            mFlag,
                            response
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["cus_mobile"] = cus_mobile
                    Log.e("Matm_cusmobile",cus_mobile)
                    jsonObject["merchant_type"] = merchant_type
                    Log.e("Matm_merchant_type",merchant_type)

                    jsonObject["merchant_id"] = merchant_id
                    Log.e("Matm_merchant_id",merchant_id)

                    jsonObject["contact_name"] = contact_name
                    Log.e("Matm_contact_name",contact_name)

                    jsonObject["legal_name"] = legal_name
                    Log.e("Matm_legal_name",legal_name)

                    jsonObject["contact_mobile"] = contact_mobile
                    Log.e("Matm_contact_mobile",contact_mobile)

                    jsonObject["contact_alternate_mobile"] =  contact_alternate_mobile
                    Log.e("Matm_contact_alternate_mobile",contact_alternate_mobile)
                    jsonObject["contact_email"] = contact_email
                    Log.e("Matm_contact_email",contact_email)
                    jsonObject["brand_name"] = brand_name
                    Log.e("Matm_brand_name",brand_name)
                    jsonObject["business_nature"] =  business_nature
                    Log.e("Matm_business_nature",business_nature)
                    jsonObject["business_type"] = business_type
                    Log.e("Matm_business_type",business_type)
                    jsonObject["established_year"] = established_year
                    Log.e("Matm_established_year",established_year)
                    jsonObject["pan_number"] = pan_number
                    Log.e("Matm_pan_number",pan_number)
                    jsonObject["registered_mobile"] = registered_mobile
                    Log.e("Matm_registered_mobile",registered_mobile)
                    jsonObject["registered_pin"] = registered_pin
                    Log.e("Matm_registered_pin",registered_pin)
                    jsonObject["registered_address"] = registered_address
                    Log.e("Matm_registered_address",registered_address)

                    jsonObject["agreement_date"] = agreement_date
                    Log.e("Matm_agreement_date",agreement_date)

                    jsonObject["title_personal"] = title_personal
                    Log.e("Matm_title_personal",title_personal)
                    jsonObject["first_name_personal"] = first_name_personal
                    Log.e("Matm_first_name_personal",first_name_personal)
                    jsonObject["last_name_personal"] = last_name_personal
                    Log.e("Matm_last_name_personal",last_name_personal)
                    jsonObject["mobile_personal"] = mobile_personal
                    Log.e("Matm_mobile_personal",mobile_personal)
                    jsonObject["email_personal"] = email_personal
                    Log.e("Matm_email_personal",email_personal)
                    jsonObject["dob_personal"] = dob_personal
                    Log.e("Matm_dob_personal",dob_personal)
                    jsonObject["address_personal"] = address_personal
                    Log.e("Matm_address_personal",address_personal)
                    jsonObject["pin_personal"] = pin_personal
                    Log.e("Matm_pin_personal",pin_personal)
                    jsonObject["city_personal"] = city_personal
                    Log.e("Matm_city_personal",city_personal)
                    jsonObject["state_personal"] = state_personal
                    Log.e("Matm_state_personal",state_personal)
                    jsonObject["nationality_personal"] = nationality_personal
                    Log.e("Matm_nationality_personal",nationality_personal)
                    jsonObject["aadhaar_personal"] =  aadhaar_personal
                    Log.e("Matm_aadhaar_personal",aadhaar_personal)
                    jsonObject["pan_personal"] =  pan_personal
                    Log.e("Matm_pan_personal",pan_personal)
                    jsonObject["merchant_bank"] =  merchant_bank
                    Log.e("Matm_merchant_bank",merchant_bank)
                    jsonObject["merchant_account"] = merchant_account
                    Log.e("Matm_merchant_account",merchant_account)

                    jsonObject["merchant_ifsc"] = merchant_ifsc
                    Log.e("Matm_merchant_ifsc",merchant_ifsc)
                    jsonObject["ifsc_code"] = ifsc_code

                    jsonObject["img_aadhaar_front"] = img_aadhaar_front
                    Log.e("Matm_",img_aadhaar_front.substringBefore("a") )
                    jsonObject["img_aadhaar_back"] = img_aadhaar_back
                    Log.e("Matm_",img_aadhaar_back.substringBefore("a") )
                    jsonObject["img_pan"] = img_pan
                    Log.e("Matm_img_pan",img_pan.substringBefore("a"))
                    jsonObject["img_cc"] = img_cc
                    Log.e("Matm_img_cc",img_cc.substringBefore("a"))

                    jsonObject["merchant_type_id"] = merchant_type_id
                    Log.e("merchant_type_id",merchant_type_id)

                    jsonObject["application_date"] =application_date
                        Log.e("application_date",application_date)

                    jsonObject["aggregator_application_number"] =aggregator_application_number
                        Log.e("aggregator_application_number",aggregator_application_number)

                    jsonObject["merchant_ref_id"] =merchant_ref_id
                        Log.e("merchant_ref_id",merchant_ref_id)

                    jsonObject["aadhar_image_name_front"] =aadhar_image_name_front
                    Log.e("aadhar_image_name_front",aadhar_image_name_front)

                    jsonObject["aadhar_image_name_back"] =aadhar_image_name_back
                        Log.e("aadhar_image_name_back",aadhar_image_name_back)

                    jsonObject["pan_image_name"] =pan_image_name
                        Log.e("pan_image_name",pan_image_name)

                    jsonObject["cc_image_name"] =cc_image_name
                        Log.e("cc_image_name",cc_image_name)



                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }





    //AEPS
    fun getAepsCharge(cus_id: String,amount: String) {

        val url: String = GET_AEPS_CHARGE
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(
                            true,
                            mFlag,
                            response
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["amount"] = amount
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }



    fun aepsCommissionHistory(
        cus_id: String, date: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = AEPSCOMMISSION_HISTORY
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["date"] = date
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun aepsHistory(
        cus_id: String, date: String, deviceId: String, deviceName: String, pin: String,
        pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = AEPS_HISTORY
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["date"] = date
                    jsonObject["deviceId"] = deviceId
                    jsonObject["deviceName"] = deviceName
                    jsonObject["pin"] = pin
                    jsonObject["pass"] = pass
                    jsonObject["cus_mobile"] = cus_mobile
                    jsonObject["cus_type"] = cus_type
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun aepsLedgerHistory(
            cus_id: String, fromDate: String, toDate: String, deviceId: String, deviceName: String, pin: String,
            pass: String, cus_mobile: String, cus_type: String
    ) {

        val url: String = AEPS_LEDGER_HISTORY
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
                object : StringRequest(
                        Method.POST,
                        url,
                        Response.Listener { response ->
                            if (!(mContext as Activity).isFinishing) {
                                Log.d("Response", response)

                            }
                            // response
                            Log.d("Response", response)

                            try {
                                mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        },
                        Response.ErrorListener { error ->
                            if (!(mContext as Activity).isFinishing) {
                                // gotoNoInternet();
                                Log.e("Error of API", error.toString())
                            }
                            Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                                    .show()
                            onErrorResponse(error)
                        }
                ) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val jsonObject: MutableMap<String, String> =
                                HashMap()
                        jsonObject["cus_id"] = cus_id
                        jsonObject["fromDate"] = fromDate
                        jsonObject["toDate"] = toDate
                        jsonObject["deviceId"] = deviceId
                        jsonObject["deviceName"] = deviceName
                        jsonObject["pin"] = pin
                        jsonObject["pass"] = pass
                        jsonObject["cus_mobile"] = cus_mobile
                        jsonObject["cus_type"] = cus_type
                        jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                        return jsonObject
                    }


                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String>? {
                        val headers =
                                HashMap<String, String>()
                        headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                        headers[mContext.getString(R.string.x_api_key)] = API_KEY
                        headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"

                        return headers
                    }
                }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun aepscommisionSlab(
        cus_id: String
    ) {

        val url: String = AEPS_COMMISIONSLAB_URL
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id

                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun aepsPayoutHistory(cus_id: String) {

        val url: String = AEPSPAYOUT_HISTORY
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.retryPolicy = mRetryPolicy
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun aepsPayout(
        cus_id: String, bank_name: String, account_number: String,
        ifsc_code: String, account_holder_name: String, amount: String,
        charge: String, type : String
    ) {

        val url: String = AEPS_PAYOUT
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["bank_name"] = bank_name
                    jsonObject["account_number"] = account_number
                    jsonObject["ifsc_code"] = ifsc_code
                    jsonObject["account_holder_name"] = account_holder_name
                    jsonObject["amount"] = amount
                    jsonObject["charge"] = charge
                    jsonObject["type"] = type

                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun stateList() {
        val url: String = STATE_LIST
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)
                    }
                    // response
                    Log.d("Response", response)
                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun onboarding(
        cus_id: String,
        latitude: String,
        longitude: String,
        merchantName: String,
        merchantPhoneNumber: String,
        companyLegalName: String,
        companyMarketingName: String,
        emailId: String,
        merchantPinCode: String,
        merchantCityName: String,
        merchantDistrictName: String,
        merchantState: String,
        merchantAddress: String,
        userPan: String,
        aadhaarNumber: String,
        gstInNumber: String,
        companyOrShopPan: String,
        companyBankAccountNumber: String,
        bankIfscCode: String,
        companyBankName: String,
        bankBranchName: String,
        bankAccountName: String,
        cancellationCheckImages: String,
        shopAndPanImage: String,
        ekycDocuments: String
    ) {

        val url: String = VERIFY_KYC
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)
                    }
                    // response
                    Log.d("Response", response)
                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["latitude"] = latitude
                    jsonObject["longitude"] = longitude
                    jsonObject["merchantName"] = merchantName
                    jsonObject["merchantPhoneNumber"] = merchantPhoneNumber
                    jsonObject["companyLegalName"] = companyLegalName
                    jsonObject["companyMarketingName"] = companyMarketingName
                    jsonObject["emailId"] = emailId
                    jsonObject["merchantPinCode"] = merchantPinCode
                    jsonObject["merchantCityName"] = merchantCityName
                    jsonObject["merchantDistrictName"] = merchantDistrictName
                    jsonObject["merchantState"] = merchantState
                    jsonObject["merchantAddress"] = merchantAddress
                    jsonObject["userPan"] = userPan
                    jsonObject["aadhaarNumber"] = aadhaarNumber
                    jsonObject["gstInNumber"] = gstInNumber
                    jsonObject["companyOrShopPan"] = companyOrShopPan
                    jsonObject["companyBankAccountNumber"] = companyBankAccountNumber
                    jsonObject["bankIfscCode"] = bankIfscCode
                    jsonObject["companyBankName"] = companyBankName
                    jsonObject["bankBranchName"] = bankBranchName
                    jsonObject["bankAccountName"] = bankAccountName
                    jsonObject["cancellationCheckImages"] = cancellationCheckImages
                    jsonObject["shopAndPanImage"] = shopAndPanImage
                    jsonObject["ekycDocuments"] = ekycDocuments
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)

    }

    fun eKyc(
        requestRemarks: String,
        userPan: String,
        aadhaarNumber: String,
        txtPidData: String,
        PidOptions: String,
        cus_id: String
    ) {

        val url: String = EKYC
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["requestRemarks"] = requestRemarks
                    jsonObject["userPan"] = userPan
                    jsonObject["aadhaarNumber"] = aadhaarNumber
                    jsonObject["txtPidData"] = txtPidData
                    jsonObject["PidOptions"] = PidOptions
                    jsonObject["cus_id"] = cus_id

                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun validateekycotp(
        otp: String,
        cus_id: String
    ) {

        val url: String = VALIDATE_EKYC_OTP
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["otp"] = otp
                    jsonObject["cus_id"] = cus_id

                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun resendekycotp(
        cus_id: String
    ) {

        val url: String = RESEND_EKYC_OTP
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()

                    jsonObject["cus_id"] = cus_id

                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun aepsPayountAccountDetails(
        cus_id: String
    ) {

        val url: String = GET_PAYOUT_DETAILS
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()

                    jsonObject["cus_id"] = cus_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun updateAepsBank(
        bankName: String,
        bankIfscCode: String,
        bankAccountNumber: String,
        bankAccountHolderName: String,
        cus_id: String
    ) {

        val url: String = UPDATE_AEPS_BANK
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()

                    jsonObject["BankName"] = bankName
                    jsonObject["bankIfscCode"] = bankIfscCode
                    jsonObject["BankAccountNumber"] = bankAccountNumber
                    jsonObject["BankAccountHolderName"] = bankAccountHolderName
                    jsonObject["cus_id"] = cus_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    fun aepsTransaction(
        cus_id: String,
        txtPidData: String,
        adhaarNumber: String,
        nationalBankIdenticationNumber: String,
        mobileNumber: String,
        type: String,
        transactionAmount: String
    ) {
        Log.e("NAC", nationalBankIdenticationNumber)

        val url: String = AEPS_TRANSACTION
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject["txtPidData"] = txtPidData
                    jsonObject["adhaarNumber"] = adhaarNumber
                    jsonObject["nationalBankIdenticationNumber"] = nationalBankIdenticationNumber
                    jsonObject["mobileNumber"] = mobileNumber
                    jsonObject["type"] = type
                    jsonObject["transactionAmount"] = transactionAmount
                   // jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()
                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun bankListAeps(
    ) {

        val url: String = AEPS_BANK_LIST
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }


    //MICRO ATM

    fun microAtmTransaction(
        cus_id: String,
        status: String,
        response: String,
        transAmount: String,
        balAmount: String,
        bankRrn: String,
        transType: String,
        type: String,
        cardNum: String,
        bankName: String,
        cardType: String,
        terminalId: String,
        fpId: String,
        transId: String,
    ) {

        val url: String = MICRO_ATM_TRANSACTION
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["status"] = status
                    jsonObject["response"] = response
                    jsonObject["transAmount"] = transAmount
                    jsonObject["balAmount"] = balAmount
                    jsonObject["bankRrn"] = bankRrn
                    jsonObject["transType"] = transType
                    jsonObject["type"] = type
                    jsonObject["cardNum"] = cardNum
                    jsonObject["bankName"] = bankName
                    jsonObject["cardType"] = cardType
                    jsonObject["terminalId"] = terminalId
                    jsonObject["fpId"] = fpId
                    jsonObject["transId"] = transId
                    jsonObject["cus_id"] = cus_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }
    fun microAtmLogin(
        cus_id: String

    ) {

        val url: String = MICRO_ATM_LOGIN
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)

                    }
                    // response
                    Log.d("Response", response)

                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["cus_id"] = cus_id
                    jsonObject[TOKEN] = AppPrefs.getStringPref(TOKEN, mContext).toString()

                    return jsonObject
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    fun getOperator(mobile: String) {
        val url: String = GET_OPERATORS_API
        AppCommonMethods(mContext).LOG(0, TAG, url)
        val getRequest: StringRequest =
            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    if (!(mContext as Activity).isFinishing) {
                        Log.d("Response", response)
                    }
                    // response
                    Log.d("Response", response)
                    try {
                        mApiCallCompleteListener.onAPICallCompleteListner(true, mFlag, response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    if (!(mContext as Activity).isFinishing) {
                        // gotoNoInternet();
                        Log.e("Error of API", error.toString())
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    Toast.makeText(mContext, "Oops! Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    onErrorResponse(error)
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val jsonObject: MutableMap<String, String> =
                        HashMap()
                    jsonObject["mobile"] = mobile
                    return jsonObject
                }
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val headers =
                        HashMap<String, String>()
                    headers[mContext.getString(R.string.content_type)] = FORM_URL_ENCODED
                    headers[mContext.getString(R.string.x_api_key)] = API_KEY
                    headers[mContext.getString(R.string.authorization)] = "$BASIC $BASIC_TOKEN"
                    return headers
                }
            }
        getRequest.setShouldCache(false)
        getRequest.setRetryPolicy(mRetryPolicy);
        VolleySingleton.instance?.addToRequestQueue(getRequest)
    }

    /**
     * This function is used to handle error in response
     *
     * @param error
     */
    private fun onErrorResponse(error: VolleyError) {
//        Crashlytics.logException(error);
        VolleyLog.d(TAG, "Error: " + error.message)
        val response = error.networkResponse
        if (response != null) {
            try {
                if (response.statusCode == 401) {
                    Log.d(TAG, "Error: " + String(response.data))
                    // new AppCommonMethods(mContext).showToast(mContext,"" + (new JSONObject(new String(response.data))).getString("message"));
                    //                   Log.d(TAG, "Error: " + error.getMessage() + " status code: " + error.networkResponse.statusCode+" "+(new JSONObject(new String(response.data))).getString("message"));
                } else {
                    Log.d(TAG, "Error: " + String(response.data))
                    // new AppCommonMethods(mContext).showToast(mContext, "" + (new JSONObject(new String(response.data))).getString("message"));
//                    Log.d(TAG, "Error: " + error.getMessage() + " status code: " + error.networkResponse.statusCode+" "+(new JSONObject(new String(response.data))).getString("message"));
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            VolleyLog.d(
                TAG,
                "Error: " + error.message + " status code: " + error.networkResponse.statusCode + " " + response.data
            )
        }
    }



    interface OnAPICallCompleteListener {
        @Throws(JSONException::class)
        fun onAPICallCompleteListner(
            item: Any?,
            flag: String?,
            result: String
        )
    }

    interface OnAPICallError {
        @Throws(JSONException::class)
        fun Error(item: Any?, flag: String?, error: VolleyError?)
    }

    init {
        mFlag = flag
        mApiCallCompleteListener = listener
        //            mAccess = NCLDatabaseAccess.getInstance(mContext);
    }
}