package com.playplexmatm.activity.fragments.bills

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.playplexmatm.R
import com.playplexmatm.adapter.bills.CustomerAdapter
import com.playplexmatm.databinding.BsAddNotesBinding
import com.playplexmatm.databinding.BsRenameBillNoBinding
import com.playplexmatm.databinding.BsShowPartiesBinding
import com.playplexmatm.extentions.ADD_NEW_PARTY
import com.playplexmatm.model.bills.Customer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

abstract class BaseActivity : AppCompatActivity() {
    private var setRenameBillNoCallBack: (String) -> Unit = {}
    private var setShowPartiesCallBack: (String) -> Unit = {}
    private var setDateCallBack: (String) -> Unit = {}
    private var setNotesCallBack: (String) -> Unit = {}

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun bsShowPartiesList(adapter: CustomerAdapter, setShowPartiesCallBack: (String) -> Unit) {
        this.setShowPartiesCallBack = setShowPartiesCallBack
        val setRingtoneBottomSheet: BottomSheetDialog?
        setRingtoneBottomSheet = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val binding = BsShowPartiesBinding.inflate(LayoutInflater.from(this))
        setRingtoneBottomSheet.setContentView(binding.root)
        binding.partiesRv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.partiesRv.adapter = adapter

        binding.addNewParty.setOnClickListener {
            setShowPartiesCallBack(ADD_NEW_PARTY)
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        binding.cross.setOnClickListener {
            setRingtoneBottomSheet.dismiss()
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
    fun bsAddNotes(setNotesCallBack: (String) -> Unit) {
        this.setNotesCallBack = setNotesCallBack
        val setRingtoneBottomSheet: BottomSheetDialog?
        setRingtoneBottomSheet = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val binding = BsAddNotesBinding.inflate(LayoutInflater.from(this))
        setRingtoneBottomSheet.setContentView(binding.root)
        binding.cancelButton.setOnClickListener {
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        binding.saveButton.setOnClickListener {
            setNotesCallBack(binding.note.text.toString())
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

    fun fetchCustomerData(
        onDataLoaded: (List<Customer>) -> Unit,
        onError: (DatabaseError) -> Unit
    ) {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val user = auth.currentUser
        val userUid = user?.uid
        if (userUid != null) {
            val userRef = database.reference.child("customers").child(userUid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val customerList = mutableListOf<Customer>()
                    for (customerSnapshot in snapshot.children) {
                        val customer = customerSnapshot.getValue(Customer::class.java)
                        if (customer != null) {
                            customerList.add(customer)
                        }
                    }
                    onDataLoaded(customerList)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error)
                }
            })
        }
    }


}