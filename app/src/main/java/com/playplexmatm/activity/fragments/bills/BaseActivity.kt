package com.playplexmatm.activity.fragments.bills

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.playplexmatm.R
import com.playplexmatm.activity.fragments.bills.AddNewSaleActivity.Companion.customerClick
import com.playplexmatm.adapter.bills.CustomerAdapter
import com.playplexmatm.adapter.holder.CustomerClick
import com.playplexmatm.databinding.BsAddNotesBinding
import com.playplexmatm.databinding.BsRenameBillNoBinding
import com.playplexmatm.databinding.BsShowPartiesBinding
import com.playplexmatm.extentions.ADD_NEW_PARTY
import com.playplexmatm.extentions.CANCEL
import com.playplexmatm.extentions.GO_TO_SDK
import com.playplexmatm.extentions.beGone
import com.playplexmatm.extentions.beVisible
import com.playplexmatm.model.bills.Customer
import com.playplexmatm.util.toast
import kotlinx.android.synthetic.main.bs_add_notes.view.bs_title
import kotlinx.android.synthetic.main.bs_rename_bill_no.view.etName
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


abstract class BaseActivity : AppCompatActivity(), CustomerClick {
    private var setRenameBillNoCallBack: (String) -> Unit = {}
    private var setShowPartiesCallBack: (String) -> Unit = {}
    private var setDateCallBack: (String) -> Unit = {}
    private var setNotesCallBack: (String) -> Unit = {}
    private var setGoToSdk: (String) -> Unit = {}
    lateinit var bsShowParties: BottomSheetDialog

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    lateinit var adapter: CustomerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun bsShowPartiesList(context: Context, setShowPartiesCallBack: (String) -> Unit) {
        this.setShowPartiesCallBack = setShowPartiesCallBack
        bsShowParties = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val binding = BsShowPartiesBinding.inflate(LayoutInflater.from(this))
        bsShowParties.setContentView(binding.root)
        binding.loadingBar.beVisible()
        fetchCustomerData(
            onDataLoaded = { customerList ->
                binding.loadingBar.beGone()
                adapter = CustomerAdapter(context, customerList, this@BaseActivity)
                binding.partiesRv.layoutManager =
                    LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                binding.partiesRv.adapter = adapter
                searchParties(binding.searchParties, customerList)
            },
            onError = { error ->
                binding.loadingBar.beGone()
                Log.wtf("Customer fetch error", error.toString())
            }
        )
        binding.addNewParty.setOnClickListener {
            setShowPartiesCallBack(ADD_NEW_PARTY)
            if (bsShowParties.isShowing) {
                bsShowParties.dismiss()
            }
        }
        binding.cross.setOnClickListener {
            bsShowParties.dismiss()
        }
        bsShowParties.show()
    }

    private fun searchParties(searchParties: EditText, customerList: List<Customer>) {
        searchParties.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                filterCustomers(query, customerList)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun filterCustomers(query: String, customerList: List<Customer>) {
        val filteredList: ArrayList<Customer> = ArrayList()
        for (list in customerList) {
            if (list.name.toLowerCase().contains(query)) {
                filteredList.add(list)
            }
        }
        adapter.updateList(filteredList)
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

    fun bsGoToSdk(amount:String,setGoToSdk: (String) -> Unit) {
        this.setGoToSdk = setGoToSdk
        val bsGoToSdk: BottomSheetDialog?
        bsGoToSdk = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val binding = BsAddNotesBinding.inflate(LayoutInflater.from(this))
        bsGoToSdk.setContentView(binding.root)
        binding.bsTitle.text = "Confirm Amount"
        binding.saveButton.text = "Submit"
        binding.note.hint = "Amount in rupees"
        binding.note.setText(amount)
        binding.cancelButton.setOnClickListener {
            setGoToSdk(CANCEL)
            if (bsGoToSdk.isShowing) {
                bsGoToSdk.dismiss()
            }
        }
        binding.saveButton.setOnClickListener {
            setGoToSdk(GO_TO_SDK)
            if (bsGoToSdk.isShowing) {
                bsGoToSdk.dismiss()
            }
        }
        bsGoToSdk.show()
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

    private fun fetchCustomerData(
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

    override fun customerClick(customer: Customer) {
        customerClick?.customerClick(customer)
    }
}