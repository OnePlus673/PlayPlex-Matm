package com.playplelx.activity

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonObject
import com.playplelx.R
import com.playplelx.activity.itemlist.AddEditProductActivity
import com.playplelx.activity.onlineorder.OnlineOrderActivity
import com.playplelx.activity.pos.PosSettingActivity
import com.playplelx.activity.pos.saleList.SaleListActivity
import com.playplelx.adapter.DashBoardAdapter
import com.playplelx.adapter.DrawerAdapter
import com.playplelx.adapter.PartyListPagerAdapter
import com.playplelx.model.drawer.DrawerModel
import com.playplelx.network.ApiInterface
import com.playplelx.network.Apiclient
import com.playplelx.util.Constants
import com.playplelx.util.InternetConnection
import com.playplelx.util.PrefManager
import com.playplelx.util.Util
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.DecimalFormat
import java.util.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener, DrawerAdapter.onItemClick {

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
    lateinit var rrSaleList: RelativeLayout
    lateinit var rrMoneyIn: RelativeLayout
    lateinit var rrReports: RelativeLayout
    lateinit var rrReceivableReports: RelativeLayout
    lateinit var llShellList: LinearLayout
    lateinit var llProduct: LinearLayout
    lateinit var llSettings: LinearLayout
    lateinit var llOrder: LinearLayout
    lateinit var llStock: LinearLayout
    lateinit var llContact: LinearLayout
    lateinit var llWallet: LinearLayout
    lateinit var llWalletReport: LinearLayout
    lateinit var llBillInvoice: LinearLayout
    lateinit var tvSales: TextView
    lateinit var tvPaymentReceived: TextView

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        drawerLayout = findViewById(R.id.drawerlayout)
        navigationView = findViewById(R.id.navigationview)
        ivMenu = findViewById(R.id.ivMenu)
        pbLoadData = findViewById(R.id.pbLoadData)
        rrSaleList = findViewById(R.id.rrSaleList)
        rrMoneyIn = findViewById(R.id.rrMoneyIn)
        rrReports = findViewById(R.id.rrReports)
        rrReceivableReports = findViewById(R.id.rrReceivableReports)
        llShellList = findViewById(R.id.llShellList)
        llProduct = findViewById(R.id.llProduct)
        llSettings = findViewById(R.id.llSettings)
        llOrder = findViewById(R.id.llOnlineOrder)
        llStock = findViewById(R.id.llStock)
        llContact = findViewById(R.id.llContact)
        llWallet = findViewById(R.id.llWallet)
        llWalletReport = findViewById(R.id.llWalletReport)
        llBillInvoice = findViewById(R.id.llBillInvoice)
        tvSales = findViewById(R.id.tvSales)
        tvPaymentReceived = findViewById(R.id.tvPaymentReceived)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        setTabLayOut()
        setupViewPager()

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


        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallDashboardApi()
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun addListner() {
        ivMenu.setOnClickListener(this)
        rrSaleList.setOnClickListener(this)
        rrMoneyIn.setOnClickListener(this)
        rrReports.setOnClickListener(this)
        rrReceivableReports.setOnClickListener(this)
        llShellList.setOnClickListener(this)
        llProduct.setOnClickListener(this)
        llOrder.setOnClickListener(this)
        llStock.setOnClickListener(this)
        llContact.setOnClickListener(this)
        llWallet.setOnClickListener(this)
        llWalletReport.setOnClickListener(this)
        llSettings.setOnClickListener(this)
        llBillInvoice.setOnClickListener(this)
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


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivMenu -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            R.id.rrSaleList -> {
                startActivity(Intent(mContext, SaleListActivity::class.java))
            }
            R.id.rrMoneyIn -> {
                startActivity(Intent(mContext, PaymentInActivity::class.java))

            }
            R.id.rrReports -> {
                startActivity(Intent(mContext, ReportActivity::class.java))
            }
            R.id.rrReceivableReports -> {
                startActivity(Intent(mContext, ReportActivity::class.java))
            }
            R.id.llShellList -> {
                startActivity(Intent(mContext, PosSettingActivity::class.java))

            }
            R.id.llProduct -> {
                startActivity(
                    Intent(mContext, AddEditProductActivity::class.java)
                        .putExtra(Constants.mFrom, Constants.isAdd)
                )

            }
            R.id.llOnlineOrder -> {
                startActivity(Intent(mContext, OnlineOrderActivity::class.java))
            }
            R.id.llStock -> {
                startActivity(Intent(mContext, ReportActivity::class.java))

            }
            R.id.llContact -> {
                startActivity(Intent(mContext, PartyListActivity::class.java))

            }
            R.id.llWallet -> {
                Toast.makeText(mContext, "comming soon", Toast.LENGTH_SHORT).show()
            }
            R.id.llWalletReport -> {
                Toast.makeText(mContext, "comming soon", Toast.LENGTH_SHORT).show()

            }
            R.id.llSettings -> {
                startActivity(Intent(mContext, SettingsActivity::class.java))

            }
            R.id.llBillInvoice -> {
                startActivity(Intent(mContext, PosSettingActivity::class.java))

            }
        }
    }

    private fun setupViewPager() {
        viewPager.apply {
            adapter = DashBoardAdapter(supportFragmentManager, tabLayout.tabCount)
            addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        }
    }

    private fun setTabLayOut() {
        tabLayout.apply {
            addTab(this.newTab().setText(mContext.resources.getString(R.string.str_sell)))
            addTab(this.newTab().setText(mContext.resources.getString(R.string.str_purchase)))
            addTab(this.newTab().setText(mContext.resources.getString(R.string.str_sellreturn)))
            addTab(this.newTab().setText(mContext.resources.getString(R.string.str_purchasereturn)))

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.position?.let {
                        viewPager.currentItem = it
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })


        }
    }


    private fun setDrawerData() {

        drawerModelList.clear()

        drawerModelList.add(
            DrawerModel(
                R.drawable.home,
                mContext.resources.getString(R.string.str_dashboard)
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
                mContext.resources.getString(R.string.str_sale_list)
            )
        )
        drawerModelList.add(
            DrawerModel(
                R.drawable.list,
                mContext.resources.getString(R.string.str_purchase_list)
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
                mContext.resources.getString(R.string.str_expense_list)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.rupee,
                mContext.resources.getString(R.string.str_money_in_list)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.rupee,
                mContext.resources.getString(R.string.str_money_out_list)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.itemlist,
                mContext.resources.getString(R.string.str_item_list)
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
                R.drawable.setting,
                mContext.resources.getString(R.string.str_settings)
            )
        )

        drawerModelList.add(
            DrawerModel(
                R.drawable.logout,
                mContext.resources.getString(R.string.str_logout)
            )
        )
    }

    override fun onClick(position: Int, drawerModel: DrawerModel) {
        when (position) {
            0 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            1 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            2 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, SaleListActivity::class.java))
            }
            3 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, PurchaseListActivity::class.java))
            }
            4 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, PosSettingActivity::class.java))
            }
            5 -> {
                drawerLayout.closeDrawer(GravityCompat.START)

            }
            6 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, PaymentInActivity::class.java))
            }
            7 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, PaymentOutActivity::class.java))
            }
            8 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, ItemListActivity::class.java))
            }
            9 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, PartyListActivity::class.java))
            }
            10 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(mContext, SettingsActivity::class.java))
            }
            11 -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                doLogout()
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
                            val sales =
                                (DecimalFormat("##.#").format(stateData.optDouble("totalSales"))).toString()
                            tvSales.text = "₹" + sales.toString()
                            tvPaymentReceived.text =
                                "₹" + stateData.optInt("paymentReceived").toString()

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

}