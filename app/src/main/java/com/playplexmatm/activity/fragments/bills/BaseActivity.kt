package com.playplexmatm.activity.fragments.bills

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.playplexmatm.R
import com.playplexmatm.databinding.BsRenameBillNoBinding
import com.playplexmatm.databinding.BsShowPartiesBinding
import com.playplexmatm.util.Constants.Companion.ADDNEWPARTY
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

abstract class BaseActivity : AppCompatActivity() {
    private var setRenameBillNoCallBack: (String) -> Unit = {}
    private var setShowPartiesCallBack: (String) -> Unit = {}
    private var setDateCallBack: (String) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun bsShowPartiesList(setShowPartiesCallBack: (String) -> Unit) {
        this.setShowPartiesCallBack = setShowPartiesCallBack
        val setRingtoneBottomSheet: BottomSheetDialog?
        setRingtoneBottomSheet = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val binding = BsShowPartiesBinding.inflate(LayoutInflater.from(this))
        setRingtoneBottomSheet.setContentView(binding.root)
        binding.addNewParty.setOnClickListener {
            setShowPartiesCallBack(ADDNEWPARTY)
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        setRingtoneBottomSheet.show()
    }

    fun bsRenameBillNumber(setRenameBillNoCallBack: (String) -> Unit) {
        this.setRenameBillNoCallBack = setRenameBillNoCallBack
        val setRingtoneBottomSheet: BottomSheetDialog?
        setRingtoneBottomSheet = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val binding = BsRenameBillNoBinding.inflate(LayoutInflater.from(this))
        setRingtoneBottomSheet.setContentView(binding.root)
        binding.cancelButton.setOnClickListener {
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        binding.saveButton.setOnClickListener {
            setRenameBillNoCallBack(binding.etName.text.toString())
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        setRingtoneBottomSheet.show()
    }

    fun showDatePickerDialog(setDateCallBack: (String) -> Unit) {
        this.setDateCallBack = setDateCallBack
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)

                val dateFormatter = SimpleDateFormat("dd MMM yy", Locale.US)
                val formattedDate = dateFormatter.format(selectedDate.time)
                setDateCallBack(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }
}