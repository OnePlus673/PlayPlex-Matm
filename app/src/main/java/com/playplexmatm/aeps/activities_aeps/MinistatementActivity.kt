package com.playplexmatm.aeps.activities_aeps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.payplex.aeps.activities_aeps.MiniStatementModel
import com.playplexmatm.R
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.util.AppPrefs
import kotlinx.android.synthetic.main.activity_ministatement.*
import kotlinx.android.synthetic.main.activity_ministatement.view.*

class MinistatementActivity : AppCompatActivity() {
    lateinit var ministatementAdapter: MinistatementAdapter
    var miniStatementModelArrayList = ArrayList<MiniStatementModel>()
    lateinit var userModel: UserModel
     var url =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ministatement)


        //Toolbar
        custToolbar.ivBackBtn.setOnClickListener { onBackPressed() }

        val gson = Gson()
        val json = AppPrefs.getStringPref("userModel", this)
        userModel = gson.fromJson(json, UserModel::class.java)


        val bundle = intent.extras
        if (bundle != null) {
            miniStatementModelArrayList = bundle.getParcelableArrayList<MiniStatementModel>("miniStatementModelArrayList") as ArrayList<MiniStatementModel>
            url =bundle.getString("url").toString()
        }

        rvMiniStatement.apply {

            layoutManager = LinearLayoutManager(this@MinistatementActivity)
            ministatementAdapter = MinistatementAdapter(
                context, miniStatementModelArrayList
            )
            rvMiniStatement.adapter = ministatementAdapter
        }

        custToolbar.tvPrintReceipt.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("url", url)
            val intent = Intent(this, InvoiceViewActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            finish() }


    }
}