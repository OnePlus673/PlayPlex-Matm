package com.playplexmatm.activity.fragments.bills

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.playplexmatm.R
import com.playplexmatm.databinding.ActivityAddNewSaleBinding
import com.playplexmatm.util.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddNewSaleActivity : BaseActivity() {
    private lateinit var binding: ActivityAddNewSaleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_sale)
        setUpViews()
    }

    private fun setUpViews() {
        defaultDate()
        binding.billNumberIcon.setOnClickListener {
            bsRenameBillNumber {
                binding.billNumber.text = it
            }
        }
        binding.date.setOnClickListener {
            showDatePickerDialog {
                binding.date.text = it
            }
        }
        binding.searchParties.setOnClickListener {
            bsShowPartiesList { callback ->
                when (callback) {
                    Constants.ADDNEWPARTY -> {
                        startActivity(Intent(this,AddNewPartyActivity::class.java))
                    }
                }
            }
        }
        binding.addNewParty.setOnClickListener {
            startActivity(Intent(this,AddNewPartyActivity::class.java))
        }
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun defaultDate() {
        val calendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("dd MMM yy", Locale.US)
        val formattedDate = dateFormatter.format(calendar.time)
        binding.date.text = formattedDate
    }
}