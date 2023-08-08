package com.playplexmatm.activity.partylist

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.playplexmatm.R
import kotlinx.android.synthetic.main.fragment_customer_adresss.view.*
import kotlinx.android.synthetic.main.fragment_customer_gst.*
import kotlinx.android.synthetic.main.fragment_customer_gst.view.*
import kotlinx.android.synthetic.main.layout_listview_bottomsheet.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"

/**
 * A simple [Fragment] subclass.
 * Use the [CustomerGstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CustomerGstFragment(var openingBalance: String,
                          var balanceType: String,
                          var creditPeriod: String,
                          var creditLimit: String) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var root : View
    var bottomSheetDialogCategory: BottomSheetDialog? = null
    private var blanceTypeList: ArrayList<String> = arrayListOf()
    private var blanceType: String = ""


    interface OnTextChangeListener {
        fun tax(taxNumber: String?, openingBalance: String?,
        balanceType: String?, creditPeriod: String?, creditLimit: String?)
    }

    var textChangeListener: OnTextChangeListener? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        textChangeListener = try {
            activity as OnTextChangeListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement onSomeEventListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_customer_gst, container, false)



        root.edtOpeningBalance.setText(openingBalance)
        root.acBalanceType.setText(balanceType)
        root.edtCreditPeriod.setText(creditPeriod)
        root.edtCreditLimit.setText(creditLimit)

        root.edtTaxNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textChangeListener!!.tax(
                    s.toString(),
                    root.edtOpeningBalance.text.toString(),
                    root.acBalanceType.text.toString(),
                    root.edtCreditPeriod.text.toString(),
                    root.edtCreditLimit.text.toString()
                )

            }

            override fun afterTextChanged(s: Editable?) {


            }

        })

        root.acBalanceType.setOnClickListener {
            showBalanceTypeBottomSheet()
        }

        root.edtOpeningBalance.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textChangeListener!!.tax(
                    root.edtTaxNumber.toString(),
                    s.toString(),
                    root.acBalanceType.text.toString(),
                    root.edtCreditPeriod.text.toString(),
                    root.edtCreditLimit.text.toString()
                )

            }

            override fun afterTextChanged(s: Editable?) {


            }

        })

        root.edtCreditLimit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textChangeListener!!.tax(
                    root.edtTaxNumber.toString(),
                    edtOpeningBalance.text.toString(),
                    root.acBalanceType.text.toString(),
                    root.edtCreditPeriod.text.toString(),
                    s.toString()
                )

            }

            override fun afterTextChanged(s: Editable?) {


            }

        })

        root.edtCreditPeriod.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textChangeListener!!.tax(
                    root.edtTaxNumber.toString(),
                    edtOpeningBalance.text.toString(),
                    root.acBalanceType.text.toString(),
                    s.toString(),
                    root.edtCreditPeriod.text.toString()
                )

            }

            override fun afterTextChanged(s: Editable?) {


            }

        })

        return root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String, param3: String, param4: String) =
            CustomerGstFragment(param1, param2, param3, param4).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, param3)
                    putString(ARG_PARAM4, param4)
                }
            }
    }

    private fun showBalanceTypeBottomSheet()
    {
        val view: View = layoutInflater.inflate(R.layout.layout_listview_bottomsheet, null)

        blanceTypeList.add("Receive")
        blanceTypeList.add("Pay")

        val  listAdapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, blanceTypeList)

        view.lvCategory.adapter = listAdapter

        bottomSheetDialogCategory = BottomSheetDialog(requireContext())
        bottomSheetDialogCategory!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        view.lvCategory.setOnItemClickListener { parent, view, position, id ->
            val element = listAdapter.getItem(position) // The item that was clicked

//            Toast.makeText(this,element,Toast.LENGTH_SHORT).show()

            root.acBalanceType.setText(element.toString())
            textChangeListener!!.tax(
                root.edtTaxNumber.toString(),
                root.edtOpeningBalance.text.toString(),
                root.acBalanceType.text.toString(),
                root.edtCreditPeriod.text.toString(),
                root.edtCreditLimit.text.toString()
            )
            bottomSheetDialogCategory!!.dismiss()

        }
        bottomSheetDialogCategory!!.show()

    }

    private fun setBalanceData() {
        blanceTypeList.add("Receive")
        blanceTypeList.add("Pay")

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), R.layout.dropdown, blanceTypeList)

        root.acBalanceType.threshold = 0 //will start working from first character

        root.acBalanceType.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        root.acBalanceType.setOnItemClickListener { parent, view, position, id ->
            blanceType = blanceTypeList[position]
        }
    }


}