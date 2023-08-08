package com.playplexmatm.aeps

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.google.gson.Gson
import com.playplexmatm.R
import com.playplexmatm.aeps.activities_aeps.AepsTransactionActivity
import com.playplexmatm.aeps.activities_aeps.PaysprintsOnboardingActivity
import com.playplexmatm.aeps.adapter.SliderAdapter
import com.playplexmatm.aeps.authentication.AepsLoginActivity
import com.playplexmatm.aeps.model.BannerModel
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.util.AppCommonMethods
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.AppPrefs
import com.playplexmatm.util.toast
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_dashboard.view.*
import kotlinx.android.synthetic.main.navigation_drawer.view.*
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class DashboardActivity : AppCompatActivity(), AppApiCalls.OnAPICallCompleteListener {

    val MICRO_ATMLOGIN = "MICRO_ATMLOGIN"
    val GET_DYNAMIC_QR_CODE = "GET_DYNAMIC_QR_CODE"

    lateinit var packagesModel_id: String

    private val REQUEST_CODE = 1

    var newaepskyc_status: String = ""
    var aeps_kyc_status: String = ""
    var bank_status: String = ""
    var mobile: String = ""

    //Navigation Variables
    var position = 0
    lateinit var userModel: UserModel
    lateinit var bannerModel: BannerModel
    var bannerModelArrayList: ArrayList<BannerModel>? = null
    var Hash_file_maps: HashMap<String, String> = HashMap()
    private val USER_LOGOUT: String = "USER_LOGOUT"
    private val AEPS_BALANCE: String = "AEPS_BALANCE"
    var deviceId: String = ""
    var deviceNameDet: String? = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
//        supportActionBar!!.hide()

        val gson = Gson()
        val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
        userModel = gson.fromJson(json, UserModel::class.java)
        dashboardApi(userModel.cus_mobile)

        getBalanceApi(userModel.cus_mobile)
        aepsBalance(userModel.cus_id)

        mobile = userModel.cus_mobile
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), REQUEST_CODE
        )
        //**************NAVIGATION START*********************//
        ivHamburgerBtn.setOnClickListener { openDrawer() }

        navigation_view.rl_support.setOnClickListener {
//            val intent = Intent(this, SupportActivity::class.java)
//            startActivity(intent)
        }

        navigation_view.rl_daybook.setOnClickListener {
//            val intent = Intent(this,DayBookActivity::class.java)
//            startActivity(intent)
        }

        navigation_view.rl_settings.setOnClickListener {
//            val intent = Intent(this, ProfileActivity::class.java)
//            startActivity(intent)
        }

        navigation_view.rl_logOut.setOnClickListener {
            showLogout()
        }



        //**************NAVIGATION END*********************//
        initView()
        //INTENT CLICK LISTENERS
        activityIntents()
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
                userModel.cus_id, deviceId, deviceNameDet.toString(),
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

    private fun initView() {
        setUpDrawerLayout()
        // navigation_view..setText(userModel.cust_name)

        navigation_view.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()
            true
        }
    }

    fun openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START)
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

    private fun getBalanceApi(
        cus_id: String
    ) {
        progress_bar.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                AppConstants.BALANCE_API,
                this
            )
            mAPIcall.getBalance(cus_id)
        } else {
            toast(getString(R.string.error_internet))
        }
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

    private fun aepsBalance(
        cusid: String
    ) {
        progress_bar.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(this, AEPS_BALANCE, this)
            mAPIcall.getAepsBalance(
                cusid
            )
        } else {
            Toast.makeText(this, "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpDrawerLayout() {
        val toggle = ActionBarDrawerToggle(
            this, mDrawerLayout, null,
            R.string.drawerOpen,
            R.string.drawerClose
        )
        mDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    fun activityIntents() {

        rl_dmt.setOnClickListener { toast("coming Soon") }

//        rl_manageusers.setOnClickListener {
//            val intent = Intent(this, MyUsersActivity::class.java)
//            startActivity(intent)
//        }

        rl_aeps.setOnClickListener {
            if(newaepskyc_status.equals("done")) {
                val intent = Intent(this, AepsTransactionActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, PaysprintsOnboardingActivity::class.java)
                startActivity(intent)
            }
        }

        rl_dmt.setOnClickListener {
//            val intent = Intent(this, DmtLoginActivity::class.java)
//            startActivity(intent)
            toast("Coming Soon")
        }

//        rl_microatm.setOnClickListener {
//            val intent = Intent(this, MicroAtmActivity::class.java)
//            startActivity(intent)
//        }

//        rl_requestmoney.setOnClickListener {
//            val intent = Intent(this, RequestFundsActivity::class.java)
//            startActivity(intent)
//        }

//        rl_addmoneytowallet.setOnClickListener {
//            val intent = Intent(this, AddMoneyActivity::class.java)
//            startActivity(intent)
//        }

//        rl_transferMoney.setOnClickListener {
//            if (userModel.cus_type == "retailer") {
//                toast("This Service is Not Available...!")
//            } else {
//                val intent = Intent(this, TransferFundsActivity::class.java)
//                startActivity(intent)
//            }
//        }

//        cvViewReport.setOnClickListener {
//            val intent = Intent(this, SelectReportsActivity::class.java)
//            startActivity(intent)
//        }

//        custToolbar.ivProfileBtn.setOnClickListener {
//            val intent = Intent(this, ProfileActivity::class.java)
//            startActivity(intent)
//        }

    }


    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(AppConstants.DASHBOARD_API)) {
            bannerModelArrayList = ArrayList()
            progress_bar.visibility = View.GONE
            Log.e(AppConstants.DASHBOARD_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)

            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {

                /* tvWalletBalance.text =
                     "${getString(R.string.Rupee)} ${jsonObject.getString(WALLETBALANCE)}"
                 tvAepsBalance.text =
                     "${getString(R.string.Rupee)} ${jsonObject.getString(AEPSBALANCE)}"*/

                tvNews.text = jsonObject.getString("news")

                setTickerAnimation(tvNews)


                val banners = jsonObject.getJSONArray(AppConstants.BANNER)
                for (i in 0 until banners.length()) {
                    val notifyObjJson = banners.getJSONObject(i)
                    bannerModel = Gson()
                        .fromJson(notifyObjJson.toString(), BannerModel::class.java)
                    bannerModelArrayList!!.add(bannerModel)


                }
                for (product in bannerModelArrayList!!) {
                    Hash_file_maps.put(product.bid, product.image)
                }

                try
                {
                    val cusData = jsonObject.getJSONArray("cusData")
                    for (i in 0 until cusData.length()) {
                        val notifyObjJson = cusData.getJSONObject(i)
                        newaepskyc_status = notifyObjJson.getString("newaepskyc_status")
                        aeps_kyc_status = notifyObjJson.getString("aeps_kyc_status")

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

                try {
                    val adapter = SliderAdapter(bannerModelArrayList!!, this)
                    sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_RTL)
                    sliderView.setSliderAdapter(adapter)
                    sliderView.setScrollTimeInSec(3)

                    sliderView.setAutoCycle(true)
                    sliderView.startAutoCycle()
                } catch (e: Exception) {
                    e.printStackTrace()
                }



                //  val posters = resources.obtainTypedArray(bannerModelArrayList)
                //viewPager.adapter = BannerAdapter(bannerModelArrayList!!, this)

/*                val user = jsonObject.getJSONArray(AppConstants.CUS_DATA)
                for (i in 0 until user.length()) {
                    val notifyObjJson = user.getJSONObject(i)
                    dmt_service = notifyObjJson.getString("dmt_service")
                    aeps_service = notifyObjJson.getString("aeps_service")
                    userModel = Gson()
                            .fromJson(notifyObjJson.toString(), UserModel::class.java)
                }*/
                navigation_view.tvCustName.setText(userModel.cus_name)
                navigation_view.tvCustMobile.setText(userModel.cus_email)

                if (userModel.cus_type.equals("retailer", ignoreCase = true)) {
                    rl_manageusers.visibility = View.GONE

                } else {

                    rl_manageusers.visibility = View.VISIBLE

                }

            } else {
                if (messageCode.equals(getString(R.string.error_expired_token))) {
                    AppCommonMethods.logoutOnExpiredDialog(this)
                } else {
                    toast(messageCode.trim())
                }
            }
        }
        if (flag.equals(AppConstants.BALANCE_API)) {
            progress_bar.visibility = View.GONE
            Log.e(AppConstants.BALANCE_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)
            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {
                progress_bar.visibility = View.GONE
                tvWalletBalance.text =
                    "${getString(R.string.Rupee)} ${jsonObject.getString(AppConstants.WALLETBALANCE)}"
                /* tvAepsBalance.text =
                     "${getString(R.string.Rupee)} ${jsonObject.getString(AEPSBALANCE)}"*/

            } else {
                progress_bar.visibility = View.GONE
                if (messageCode.equals(getString(R.string.error_expired_token))) {
                    AppCommonMethods.logoutOnExpiredDialog(this)
                } else {
                    toast(messageCode.trim())
                }
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

                val intentLogin = Intent(this, AepsLoginActivity::class.java)
                startActivity(intentLogin)
                finish()

            } else {
                progress_bar.visibility = View.INVISIBLE
                val response = jsonObject.getString("message")

                toast(response)

            }
        }
    }

    fun setTickerAnimation(view: View) {
        val animation: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, +1f,
            Animation.RELATIVE_TO_SELF, -1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.RESTART
        animation.interpolator = LinearInterpolator()
        animation.duration = 8000
        view.startAnimation(animation)
    }

}