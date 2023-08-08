package com.playplexmatm.activity.itemlist

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.playplexmatm.R
import kotlinx.android.synthetic.main.fragment_stock.view.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StockFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StockFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var root  : View
    lateinit var selectedDate : String


    interface onSomeEventListener {
        fun stockEvent(currentStock: String?,openingStock : String?, openingStockDate : String? )
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        someEventListener = try {
            activity as onSomeEventListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement onSomeEventListener")
        }
    }

    var someEventListener: onSomeEventListener? = null

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
        root = inflater.inflate(R.layout.fragment_stock, container, false)

        root.edtOpeningStockDate.setOnClickListener{ showDatePicker(root.edtOpeningStockDate)}


        root.edtOpeningStockDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                someEventListener!!.stockEvent(root.edtOpeningStock.text.toString(),
                    root.edtOpeningStock.text.toString(),s.toString()
                )
            }

            override fun afterTextChanged(s: Editable?) {


            }

        })

        root.edtOpeningStock.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                someEventListener!!.stockEvent(root.edtOpeningStock.text.toString(),
                    s.toString(),root.edtOpeningStockDate.text.toString()
                );

            }

            override fun afterTextChanged(s: Editable?) {


            }

        })


//        root.edtCurrentStock.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                someEventListener!!.stockEvent(s.toString(),
//                    root.edtOpeningStock.text.toString(),root.edtOpeningStockDate.text.toString()
//                );
//
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//
//
//            }
//
//        })



        return root
    }

    private fun showDatePicker(edtDate: EditText) {
        var mYear = 0
        var mMonth = 0
        var mDay = 0
        // Get Current Date
        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { view, year, monthOfYear, dayOfMonth ->
                selectedDate =
                    year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString()
                root.edtOpeningStockDate.setText(year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString())
            },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StockFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StockFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}