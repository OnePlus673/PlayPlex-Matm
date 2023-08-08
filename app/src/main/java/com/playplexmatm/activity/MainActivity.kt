package com.playplexmatm.activity

import `in`.credopay.payment.sdk.CredopayPaymentConstants
import `in`.credopay.payment.sdk.PaymentActivity
import `in`.credopay.payment.sdk.Utils
import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.playplexmatm.R
import com.playplexmatm.activity.onlineorder.OnlineOrderActivity
import com.playplexmatm.activity.pos.PosSettingActivity
import com.playplexmatm.activity.pos.saleList.SaleListActivity
import com.playplexmatm.activity.reports.SelectReportActivity
import com.playplexmatm.adapter.DashBoardAdapter
import com.playplexmatm.adapter.DrawerAdapter
import com.playplexmatm.aeps.activities_aeps.aepshistory.AepsHistoryActivity
import com.playplexmatm.aeps.activities_aeps.aepshistory.AepsLedgerHistoryActivity
import com.playplexmatm.aeps.authentication.AepsLoginActivity
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.fragment.dashboard.CustomerUserReportFragment
import com.playplexmatm.fragment.dashboard.PurchaseFragment
import com.playplexmatm.fragment.dashboard.SaleFragment
import com.playplexmatm.fragment.dashboard.SupplierUserReportFragment
import com.playplexmatm.microatm.MatmOnboardingActivity
import com.playplexmatm.model.drawer.DrawerModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.layout_list_service_bottomsheet.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), View.OnClickListener, DrawerAdapter.onItemClick,
    AppApiCalls.OnAPICallCompleteListener {

    lateinit var mContext: MainActivity
    lateinit var ivMenu: ImageView
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var cvProfile: CircleImageView
    lateinit var tvName: TextView
    lateinit var tvMobile: TextView
    lateinit var pbLoadData: ProgressBar
    lateinit var rvDrawer: RecyclerView
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private var drawerModelList: ArrayList<DrawerModel> = arrayListOf()
    lateinit var drawerAdapter: DrawerAdapter
    lateinit var apiInterface: ApiInterface

    var bottomSheetDialogService: BottomSheetDialog? = null

    lateinit var llOrder: LinearLayout
    lateinit var llStock: LinearLayout

    lateinit var llSell: LinearLayout
    lateinit var llPurchase: LinearLayout


    lateinit var tvOrderUrl: TextView
    lateinit var ivShare: ImageView

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    private var wareHouse_url: String = ""

    lateinit var tvTotalSales: TextView

    lateinit var tvTotalSaleReturnItems: TextView
    lateinit var tvTotalPurchaseItems: TextView
    lateinit var tvTotalPurchaseReturnItems: TextView

    private val PERMISSION_REQUEST_CODE = 200

    lateinit var userModel: UserModel

    private val AEPS_BALANCE: String = "AEPS_BALANCE"

    lateinit var cus_id: String

    var matm_user_status = ""
    var callback_status = ""
    var callback_remark = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppPrefs.putStringPref("merchant_ref_id","64948aa6c3e6d761aebdf4b9",this)
        mContext = this
        initUI()
        addListner()
        
        cus_id = AppPrefs.getStringPref("cus_id",this).toString()
        

        if (checkPermission()) {
        } else {
            requestPermission()
        }
    }

    private fun initUI() {
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        drawerLayout = findViewById(R.id.drawerlayout)
        navigationView = findViewById(R.id.navigationview)
        ivMenu = findViewById(R.id.ivMenu)
        pbLoadData = findViewById(R.id.pbLoadData)
        llSell = findViewById(R.id.llAddSell)
        llPurchase = findViewById(R.id.llPurchase)
        llOrder = findViewById(R.id.llUrl)
        llStock = findViewById(R.id.llStock)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        tvOrderUrl = findViewById(R.id.tvOrderUrl)
        ivShare = findViewById(R.id.ivShare)

        tvTotalSales = findViewById(R.id.tvTotalSales)

        tvTotalSaleReturnItems = findViewById(R.id.tvTotalSaleReturnItems)
        tvTotalPurchaseItems = findViewById(R.id.tvTotalPurchaseItems)
        tvTotalPurchaseReturnItems = findViewById(R.id.tvTotalPurchaseReturnItems)

        setupViewPager()
        setTabLayOut()

        llPlus.setOnClickListener {
            showBottomSheetService()
        }

        navigationView.bringToFront()

        actionBarDrawerToggle = object : ActionBarDrawerToggle(
            mContext,
            drawerLayout,
            null,
            R.string.str_drawer_open,
            R.string.str_drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                drawerLayout.openDrawer(GravityCompat.START)
                setHeaderData()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        drawerLayout.setDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()


        val islogin: Boolean =
            AppPrefs.getBooleanPref(AppConstants.IS_LOGIN, mContext)
        if (islogin) {
            val gson = Gson()
            val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
            userModel = gson.fromJson(json, UserModel::class.java)

            aepsBalance(userModel.cus_id)
        }


        tvAepsBalance.setOnClickListener {
            if (islogin) {
                val gson = Gson()
                val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
                userModel = gson.fromJson(json, UserModel::class.java)

                aepsBalance(userModel.cus_id)
            } else {
                val intent = Intent(this,AepsLoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun addListner() {
        ivMenu.setOnClickListener(this)

        llOrder.setOnClickListener(this)
        llStock.setOnClickListener(this)
        tvOrderUrl.setOnClickListener(this)
        ivShare.setOnClickListener(this)
        llSell.setOnClickListener(this)
        llPurchase.setOnClickListener(this)
    }

    private fun setHeaderData() {
        val hView = navigationView.getHeaderView(0)
        cvProfile = hView.findViewById(R.id.cvProfile)
        tvName = hView.findViewById(R.id.tvName)
        tvMobile = hView.findViewById(R.id.tvMobileNumber)
        rvDrawer = hView.findViewById(R.id.rvDrawer)



        setDrawerData()

        rvDrawer.layoutManager = LinearLayoutManager(mContext)
        rvDrawer.setHasFixedSize(true)
        drawerAdapter = DrawerAdapter(mContext, drawerModelList, this)
        rvDrawer.adapter = drawerAdapter
        drawerAdapter.notifyDataSetChanged()

        tvName.text = PrefManager(mContext).getValue(Constants.name)
        tvMobile.text = PrefManager(mContext).getValue(Constants.phone)

    }

    private fun showBottomSheetService()
    {
        val view: View = layoutInflater.inflate(R.layout.layout_list_service_bottomsheet, null)

        view.ivRemove.setOnClickListener {
            bottomSheetDialogService!!.dismiss()
        }

        view.rlSaleInvoice.setOnClickListener {
            val intent = Intent(this,SaleListActivity::class.java)
            startActivity(intent)
        }

        view.rlPaymentIn.setOnClickListener {
            val intent = Intent(this,PaymentInActivity::class.java)
            startActivity(intent)
        }

        view.rlPostPaid.setOnClickListener {
            val intent = Intent(this,OnlineOrderActivity::class.java)
            startActivity(intent)
        }

        view.rlPos.setOnClickListener {
            val intent = Intent(this,PosSettingActivity::class.java)
            startActivity(intent)
        }

        view.rlPurchase.setOnClickListener {
            val intent = Intent(this,PurchaseListActivity::class.java)
            startActivity(intent)
        }

        view.rlReports.setOnClickListener {
            val intent = Intent(this,SelectReportActivity::class.java)
            startActivity(intent)
        }

        view.rlPaymentOut.setOnClickListener {
            val intent = Intent(this,PaymentOutActivity::class.java)
            startActivity(intent)
        }


        view.rlSaleOrder.setOnClickListener {

        }


        bottomSheetDialogService = BottomSheetDialog(this)
        bottomSheetDialogService!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(bottomSheetDialogService!!.getWindow()!!.getAttributes())

        val displayMetrics = DisplayMetrics()
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)

        lp.width = (displayMetrics.widthPixels * 0.9).toInt()
        bottomSheetDialogService!!.getWindow()!!.setAttributes(lp)

        bottomSheetDialogService!!.show()

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivMenu -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            R.id.llUrl -> {
                startActivity(Intent(mContext, OnlineOrderActivity::class.java))
            }
            R.id.llStock -> {
                startActivity(Intent(mContext, ReportActivity::class.java))
            }
            R.id.llWallet -> {
                Toast.makeText(mContext, "coming soon", Toast.LENGTH_SHORT).show()
            }
            R.id.llAddSell -> {
                gotoPosDialog()
//                startActivity(Intent(mContext, PosSettingActivity::class.java))
            }
            R.id.llPurchase -> {
                startActivity(Intent(mContext, AddPurchaseListActivity::class.java))
            }
            R.id.tvOrderUrl -> {
                setLinkData()
            }
            R.id.ivShare -> {
                shareData()
            }
        }
    }

    private fun setLinkData() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(wareHouse_url))
        startActivity(browserIntent)
    }

    private fun shareData() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody = "Please purchase order in below url"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody + "\n" + wareHouse_url)
        startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }

    private fun setupViewPager() {
//        viewPager.apply {
//            adapter = DashBoardAdapter(supportFragmentManager, tabLayout.tabCount)
//            addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
//        }

        val adapter = DashBoardAdapter(supportFragmentManager)
        val salesFragment = SaleFragment()
        val purchaseFragment = PurchaseFragment()
        val customerUserReportFragment = CustomerUserReportFragment()
        val supplierUserReportFragment = SupplierUserReportFragment()

        adapter.addFragment(salesFragment, "Sales")
        adapter.addFragment(purchaseFragment, "Purchase")
        adapter.addFragment(customerUserReportFragment, "Customer Report")
        adapter.addFragment(supplierUserReportFragment, "Supplier Report")

        viewPager.offscreenPageLimit = (2 - 1)
        viewPager.adapter = adapter

        tabLayout.setupWithViewPager(viewPager)

    }

    private fun setTabLayOut() {
//        tabLayout.apply {
////            addTab(this.newTab().setText(mContext.resources.getString(R.string.str_product)))
//            addTab(this.newTab().setText(mContext.resources.getString(R.string.str_sell)))
//            addTab(this.newTab().setText(mContext.resources.getString(R.string.str_purchase)))
//            addTab(this.newTab().setText(mContext.resources.getString(R.string.str_customer_report)))
//            addTab(this.newTab().setText(mContext.resources.getString(R.string.str_supplier_report)))
//
//            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabSelected(tab: TabLayout.Tab?) {
//                    tab?.position?.let {
//                        viewPager.currentItem = it
//                    }
//                }
//
//                override fun onTabUnselected(tab: TabLayout.Tab?) {
//                }
//
//                override fun onTabReselected(tab: TabLayout.Tab?) {
//                }
//            })
//
//
//        }
    }

    override fun onResume() {
        super.onResume()
        mNetworkCallDashboardApi()
    }

    private fun setDrawerData() {

        drawerModelList.clear()

        drawerModelList.add(
            DrawerModel(
                R.drawable.itemlist,
                mContext.resources.getString(R.string.str_items)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.home,
                mContext.resources.getString(R.string.str_business_dashboard)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.report,
                mContext.resources.getString(R.string.str_reports)
            )
        )
        drawerModelList.add(
            DrawerModel(
                R.drawable.list,
                mContext.resources.getString(R.string.str_sale)
            )
        )
        drawerModelList.add(
            DrawerModel(
                R.drawable.list,
                mContext.resources.getString(R.string.str_purchase)
            )
        )
        drawerModelList.add(
            DrawerModel(
                R.drawable.estimate,
                mContext.resources.getString(R.string.str_pos)
            )
        )
        drawerModelList.add(
            DrawerModel(
                R.drawable.list,
                mContext.resources.getString(R.string.str_expense)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.ic_baseline_account_balance_24,
                mContext.resources.getString(R.string.str_cash_and_bank)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.purchase,
                mContext.resources.getString(R.string.str_my_online_store)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.partylist,
                mContext.resources.getString(R.string.str_party_list)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.product,
                mContext.resources.getString(R.string.str_products)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.purchase,
                mContext.resources.getString(R.string.str_stock_transfer)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.paymentout,
                mContext.resources.getString(R.string.str_stock_adjustment)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.setting,
                mContext.resources.getString(R.string.str_settings)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.estimate,
                mContext.resources.getString(R.string.str_subscription)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.report,
                mContext.resources.getString(R.string.aeps_report)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.report,
                mContext.resources.getString(R.string.wallet_ledger)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.logout,
                mContext.resources.getString(R.string.str_logout)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.ic_atm_machine,
                mContext.resources.getString(R.string.str_microatm)
            )
        )
    }

    override fun onClick(position: Int, drawerModel: DrawerModel) {
        when (position) {
            0 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, ItemListActivity::class.java))
//                throw RuntimeException("Test Crash")
            }
            1 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, MainActivity::class.java))
            }
            2 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, SelectReportActivity::class.java))
            }
            3 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, SaleListActivity::class.java))
            }
            4 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, PurchaseListActivity::class.java))
            }
            5 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                gotoPosDialog()
//                startActivity(Intent(mContext, PosSettingActivity::class.java))
            }
            6 -> {
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            7 -> {
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            8 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, OnlineOrderActivity::class.java))
            }

            9 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, PartyListActivity::class.java))
            }
            10 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, ItemListActivity::class.java))
            }
            11 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, SettingsActivity::class.java))
            }
            12 -> {
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
            }
            13 -> {
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
            }
            14 -> {
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
            }
            15 -> {
                val islogin: Boolean =
                    AppPrefs.getBooleanPref(AppConstants.IS_LOGIN, mContext)
                if (islogin) {
                    val gson = Gson()
                    val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
                    userModel = gson.fromJson(json, UserModel::class.java)

                    val intent = Intent(this,AepsHistoryActivity::class.java)
                    startActivity(intent)

                } else {
                    val intent = Intent(this, AepsLoginActivity::class.java)
                    startActivity(intent)
                }
            }
            16 -> {
                val islogin: Boolean =
                    AppPrefs.getBooleanPref(AppConstants.IS_LOGIN, mContext)
                if (islogin) {
                    val gson = Gson()
                    val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
                    userModel = gson.fromJson(json, UserModel::class.java)

                    val intent = Intent(this,AepsLedgerHistoryActivity::class.java)
                    startActivity(intent)

                } else {
                    val intent = Intent(this, AepsLoginActivity::class.java)
                    startActivity(intent)
                }
            }
            17 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                doLogout()
            }
            18 -> {

//                val intent = Intent(this,MatmDocumentUploadActivity::class.java)
//                val intent = Intent(this,MatmPersonalDetailsActivity::class.java)
                drawerLayout.closeDrawer(GravityCompat.START)
                getOnboardingStatusApi(AppPrefs.getStringPref("cus_email",this).toString())



//                gotoSdk("2000045513","Payplex@123",1000)
            }
        }
    }

    private fun doLogout() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("YES") { dialog, which ->
            if (InternetConnection.checkConnection(mContext)) {
                mNetworkCallLogoutAPI(dialog)
            } else {
                Toast.makeText(
                    mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        builder.setNegativeButton(
            "NO"
        ) { dialog, which -> // Do nothing
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }


    private fun mNetworkCallLogoutAPI(dialog: DialogInterface) {
        pbLoadData.visibility = View.VISIBLE
        val call: Call<JsonObject> = apiInterface.doLogout()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            dialog.dismiss()
                            PrefManager(mContext).clear()
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(mContext, LoginActivity::class.java))
                            finishAffinity()
                        } else {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    pbLoadData.visibility = View.GONE
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun mNetworkCallDashboardApi() {
        pbLoadData.visibility = View.VISIBLE
        val call: Call<JsonObject> = apiInterface.getDashBoard()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONObject("data")
                            val stateData = data.optJSONObject("stateData")
                            val stockHistoryStatsData = data.optJSONObject("stockHistoryStatsData")
                            val sales =
                                (DecimalFormat("##.#").format(stateData.optDouble("totalSales"))).toString()

                            wareHouse_url = data.optString("warehouse_url")
                            tvOrderUrl.text = wareHouse_url

                            tvTotalSales.setText(resources.getString(R.string.Rupee)+" "+(DecimalFormat("##.##").format(stateData.getString("totalSales").toDouble())))

                            tvTotalPurchaseItems.setText(stockHistoryStatsData.getString("totalPurchases"))
                            tvTotalSaleReturnItems.setText(stockHistoryStatsData.getString("totalSalesReturn"))
                            tvTotalPurchaseReturnItems.setText(stockHistoryStatsData.getString("totalPurchaseReturn"))
                        } else {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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

    private fun checkPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Not Granted", Toast.LENGTH_SHORT).show()
            // Permission is not granted
            return false
        }
        return true
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                // main logic
            } else {
                val PERMISSIONS = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) !=
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        showMessageOKCancel(
                            "You need to allow access permissions"
                        ) { dialog, which ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermission()
                            }
                        }
                    }
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) !=
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        showMessageOKCancel(
                            "You need to allow access permissions"
                        ) { dialog, which ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermission()
                            }
                        }
                    }
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        showMessageOKCancel(
                            "You need to allow access permissions"
                        ) { dialog, which ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermission()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun gotoPosDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf(
            "Direct",
            "Product"
        )
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 ->  startActivity(Intent(this,PosSettingActivity::class.java).putExtra("from","direct"))
                1 ->  startActivity(Intent(this,PosSettingActivity::class.java).putExtra("from","product"))
            }
        }
        pictureDialog.show()
    }

    private fun aepsBalance(
        cusid: String
    ) {
        pbLoadData.visibility = View.VISIBLE
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

    private fun getBalanceApi(
        cus_id: String
    ) {
        pbLoadData.visibility = View.VISIBLE
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

    private fun getOnboardingStatusApi(
        cus_id: String
    ) {
        pbLoadData.visibility = View.VISIBLE
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



    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(AEPS_BALANCE)) {
            pbLoadData.visibility = View.GONE
            Log.e(AppConstants.AEPS_BALANCE_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)
            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {
                tvAepsBalance.text =
                    "${getString(R.string.Rupee)} ${jsonObject.getString("AEPSBalance")}"
            } else {
                if (messageCode.equals(getString(R.string.error_expired_token))) {
                    AppCommonMethods.logoutOnExpiredDialog(this)
                } else {
                    toast(messageCode.trim())
                }
            }
        }
        if (flag.equals(AppConstants.BALANCE_API)) {
            pbLoadData.visibility = View.GONE
            Log.e(AppConstants.BALANCE_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)
            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {
                pbLoadData.visibility = View.GONE
                tvAepsBalance.text =
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

        if (flag.equals(AppConstants.MATM_ONBOARDING_STATUS)) {
            pbLoadData.visibility = View.GONE
            Log.e(AppConstants.MATM_ONBOARDING_STATUS, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)

            //   val token = jsonObject.getString(AppConstants.TOKEN)
            Log.e(AppConstants.STATUS, status)
            if (status.contains(AppConstants.TRUE)) {
                pbLoadData.visibility = View.GONE

                val cast = jsonObject.getJSONArray("result")

                for(i in 0 until cast.length() )
                {
                    matm_user_status = cast.getJSONObject(i).getString("status")
                    callback_status = cast.getJSONObject(i).getString("callback_status")
                    callback_remark = cast.getJSONObject(i).getString("callback_remark")
                }

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
                pbLoadData.visibility = View.GONE
                toast("OnBoard User First")
                AppPrefs.putStringPref("matm_user_status","PENDING",this)
                val intent = Intent(this,MatmOnboardingActivity::class.java)
                startActivity(intent)
            }
        }


    }

    fun gotoSdk(login: String,password: String, amount: Int) {
        val intent = Intent (this, PaymentActivity::class.java)
        intent.putExtra("TRANSACTION_TYPE", CredopayPaymentConstants.MICROATM)
        intent.putExtra("LOGIN_ID",login)
        intent.putExtra("LOGIN_PASSWORD",password)
        intent.putExtra("DEBUG_MODE",true)
        intent.putExtra("PRODUCTION",true)
        intent.putExtra("AMOUNT",amount)
        intent.putExtra("LOGO", Utils.getVariableImage(ContextCompat.getDrawable(getApplicationContext(), R.drawable.splashlogo)))
        startActivityForResult(intent,1)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {

            when (resultCode) {
                CredopayPaymentConstants.TRANSACTION_COMPLETED ->
                    Log.e("Transaction","TRANSACTION_COMPLETED "+data)

                CredopayPaymentConstants.TRANSACTION_CANCELLED ->
                    Log.e("Transaction","TRANSACTION_CANCELLED")

                CredopayPaymentConstants.VOID_CANCELLED ->
                    Log.e("Transaction","TRANSACTION_CANCELLED")

                CredopayPaymentConstants.LOGIN_FAILED ->
                    Log.e("Transaction","LOGIN_FAILED")

//                    CredopayPaymentConstants.CHANGE_PASSWORD_FAILED ->
//                        Log.e("Transaction","CHANGE_PASSWORD_FAILED")
//
//                    CredopayPaymentConstants.CHANGE_PASSWORD_SUCCESS ->
//                        Log.e("Transaction","CHANGE_PASSWORD_SUCCESS")

                CredopayPaymentConstants.BLUETOOTH_CONNECTIVITY_FAILED ->
                    Log.e("Transaction","BLUETOOTH_CONNECTIVITY_FAILED")

                CredopayPaymentConstants.CHANGE_PASSWORD -> {
                    Log.e("Transaction", "CHANGE_PASSWORD")
                    gotoSdk("2000045513","Payplex@123",1000)

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

    private fun showMessage(msg : String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Attention !")
        builder.setMessage(msg)
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

}