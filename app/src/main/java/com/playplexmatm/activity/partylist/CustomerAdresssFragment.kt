package com.playplexmatm.activity.partylist

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.playplexmatm.R
import kotlinx.android.synthetic.main.fragment_customer_adresss.view.*
import kotlinx.android.synthetic.main.fragment_pricing.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CustomerAdresssFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CustomerAdresssFragment(var address: String,var shipping_address: String) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var root : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    interface OnTextChangeListener {
        fun address(billingAddress: String?,address : String? )
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_customer_adresss, container, false)


        root.edtAddressFrag.setText(address)
        root.edtBillingAddress.setText(shipping_address)

        root.edtBillingAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textChangeListener!!.address(s.toString(),
                    root.edtAddressFrag.text.toString()
                );

            }

            override fun afterTextChanged(s: Editable?) {


            }

        })

        root.edtAddressFrag.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textChangeListener!!.address( root.edtBillingAddress.text.toString() ,
                    s.toString()
                );

            }

            override fun afterTextChanged(s: Editable?) {


            }

        })


        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CustomerAdresssFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CustomerAdresssFragment(param1, param2).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}