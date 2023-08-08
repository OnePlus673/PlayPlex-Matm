package com.playplexmatm.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.playplexmatm.R
import com.playplexmatm.adapter.PaymentListAdapter
import com.playplexmatm.aeps.activities_aeps.AepsTransactionActivity
import com.playplexmatm.aeps.activities_aeps.PaysprintsOnboardingActivity
import com.playplexmatm.aeps.authentication.AepsLoginActivity
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.model.categoryofproducts.Items
import com.playplexmatm.model.paymentmode.PaymentModeModel
import com.playplexmatm.model.pos.PaymentModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.*
import org.json.JSONObject

class PurchasePaymentModeActivity : AppCompatActivity(), AppApiCalls.OnAPICallCompleteListener {

    lateinit var mContext: PurchasePaymentModeActivity
    lateinit var ivBack: ImageView
    lateinit var pbLoadData: ProgressBar
    lateinit var acPaymentMode: AutoCompleteTextView
    private var paymentModeList: ArrayList<PaymentModeModel> = arrayListOf()
    private var paymentModeStringList: ArrayList<String> = arrayListOf()
    lateinit var apiInterface: ApiInterface
    private var paymentModeName: String = ""
    private var paymentModeId: String = ""
    lateinit var tvBasicAmount: TextView
    lateinit var tvPaybleAmountValue: TextView
    lateinit var tvPayNow: TextView
    lateinit var tvSave: TextView
    lateinit var edtNotes: EditText
    lateinit var edtAmount: EditText
    lateinit var rvPaymentMode: RecyclerView
    private var paymentModeAmount: Double = 0.0
    private var grandAmount: Double = 0.0
    lateinit var paymentListAdapter: PaymentListAdapter
    lateinit var tvAdd: TextView
    private var paymentFilterList: ArrayList<PaymentModel> = arrayListOf()
    private var posProductModelArrayList: java.util.ArrayList<Items> = arrayListOf()
    private var userId: String = ""
    private var dueAmountValue = 0.0
    private var dueAmountEditValue = 0.0
    private var payingAmountvalue = 0.0
    private var payingeditAmountvalue = 0.0
    private var paybleAmountvalue = 0.0
    private var payingtextvalue = 0.0
    lateinit var databaseHelper: DatabaseHelper


    lateinit var userModel: UserModel
    private var discount: String = ""
    private var shipping: String = ""
    var subtotal: Double = 0.0

    lateinit var basicAmount: String
    lateinit var totalAmount: String

    lateinit var from: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_payment_mode)


        initUI()
        addListener()

        val bundle = intent.extras
        if(bundle != null) {
            basicAmount = bundle.getString("basicAmount").toString()
            totalAmount = bundle.getString("totalAmount").toString()
        }

        tvBasicAmount.setText(resources.getString(R.string.Rupee)+" "+basicAmount)
        tvPaybleAmountValue.setText(resources.getString(R.string.Rupee)+" "+totalAmount)

    }

    fun initUI() {
        //supportActionBar!!.hide()
        mContext = this
        databaseHelper = DatabaseHelper(mContext)
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        pbLoadData = findViewById(R.id.pbLoadData)
        tvBasicAmount = findViewById(R.id.tvBasicAmount)
        tvPaybleAmountValue = findViewById(R.id.tvPaybleAmountValue)
        tvPayNow = findViewById(R.id.tvPayNow)
        edtAmount = findViewById(R.id.edtAmount)
        tvSave = findViewById(R.id.tvSave)
    }

    fun addListener() {
        tvSave.setOnClickListener {
            val islogin: Boolean =
                AppPrefs.getBooleanPref(AppConstants.IS_LOGIN, mContext)
            if (islogin) {
                val gson = Gson()
                val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
                userModel = gson.fromJson(json, UserModel::class.java)

                dashboardApi(userModel.cus_mobile)

            } else {
                val intent = Intent(this, AepsLoginActivity::class.java)
                startActivity(intent)
            }
        }

        edtAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.isNotEmpty()) {
                    tvPayNow.setText(resources.getString(R.string.Rupee)+" "+s.toString())
                } else {
                    tvPayNow.setText(resources.getString(R.string.Rupee)+" "+"0")
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }


    private fun dashboardApi(
        cus_id: String
    ) {
        pbLoadData.visibility = View.VISIBLE
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

    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(AppConstants.DASHBOARD_API)) {
            pbLoadData.visibility = View.GONE
            Log.e(AppConstants.DASHBOARD_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)

            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {

                try {
                    val cusData = jsonObject.getJSONArray("cusData")
                    for (i in 0 until cusData.length()) {
                        val notifyObjJson = cusData.getJSONObject(i)
                        if (notifyObjJson.getString("newaepskyc_status").equals("done")) {
                            val intent = Intent(this, AepsTransactionActivity::class.java)
                            val bundle = Bundle()
                            bundle.putString("amount",edtAmount.text.toString())
                            intent.putExtras(bundle)
                            startActivityForResult(intent,108)
                        } else {
                            toast("Please Complete KYC")
                            val intent = Intent(this, PaysprintsOnboardingActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } catch(e: Exception) {
                    toast("Please Login Again")
                    val intent = Intent(this,AepsLoginActivity::class.java)
                    startActivity(intent)
                }

            } else {
                if (messageCode.equals(getString(R.string.error_expired_token))) {
                    AppCommonMethods.logoutOnExpiredDialog(this)
                } else {
                    toast(messageCode.trim())
                }
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            108 -> {
                if (resultCode == Activity.RESULT_OK) {
                    val extra: Bundle = data!!.getBundleExtra("data")!!

                    Log.e("data", extra.getString("transactionType")!!)
                    val bundle = Bundle()
                    bundle.putString("transactionType", extra.getString("transactionType")!!)
                    val resultIntent = Intent()
                    resultIntent.putExtra("data", bundle)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
        }

    }
}