package com.playplexmatm.activity.itemlist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.activity.settings.AddEditTaxActivity
import com.playplexmatm.model.taxes.TaxesModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.fragment_pricing.view.*
import kotlinx.android.synthetic.main.layout_list_category.view.*
import kotlinx.android.synthetic.main.layout_listview_bottomsheet.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PricingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PricingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var root : View
    private var taxList: ArrayList<TaxesModel> = arrayListOf()
    private var taxStringList: ArrayList<String> = arrayListOf()
    private var taxIdList: ArrayList<String> = arrayListOf()
    private var taxInfoList: ArrayList<String> = arrayListOf("With Tax","Without Tax")
    var bottomSheetDialogCategory: BottomSheetDialog? = null
    lateinit var apiInterface: ApiInterface

    lateinit var mContext : Context

    var taxId = ""


    // This interface can be implemented by the Activity, parent Fragment,
    // o

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    interface onSomeEventListener {
        fun someEvent(purchasePrice: String?,salePrice : String?, mrpP: String?, taxP: String? )
    }

    var someEventListener: onSomeEventListener? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        someEventListener = try {
            activity as onSomeEventListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement onSomeEventListener")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        root = inflater.inflate(R.layout.fragment_pricing, container, false)


        mContext = requireContext()

        apiInterface = Apiclient(requireContext()).getClient()!!.create(ApiInterface::class.java)

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallTaxAPI()
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }


        root.ivAddTax.setOnClickListener {

            val intent = Intent(requireContext(),AddEditTaxActivity::class.java)
            startActivity(intent)

        }

        root.tvTaxInfoPurchase.setOnClickListener { showTaxInfoPurchaseBottomSheet() }

        root.tvTaxInfoSale.setOnClickListener { showTaxInfoSaleBottomSheet() }

        root.acTax.setOnClickListener {
            showTaxBottomSheet()
        }

        root.edtPurchasePrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                someEventListener!!.someEvent(s.toString(),
                    root.edtSalePrice.text.toString(),
                    root.edtMRP.text.toString(),
                    taxId
                );

            }

            override fun afterTextChanged(s: Editable?) {


            }

        })

        root.acTax.setOnClickListener {
            showTaxBottomSheet()
        }


        root.edtSalePrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                someEventListener!!.someEvent(root.edtPurchasePrice.text.toString(),
                    s.toString(), root.edtMRP.text.toString(),
                    taxId
                );

            }

            override fun afterTextChanged(s: Editable?) {


            }

        })


        root.edtMRP.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                someEventListener!!.someEvent(root.edtPurchasePrice.text.toString(),
                    s.toString(), root.edtMRP.text.toString(),
                    taxId
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
         * @return A new instance of fragment PricingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PricingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun showTaxInfoPurchaseBottomSheet()
    {
        val view: View = layoutInflater.inflate(R.layout.layout_listview_bottomsheet, null)


        val  listAdapter = ArrayAdapter<String>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, taxInfoList)

        view.lvCategory.adapter = listAdapter

        bottomSheetDialogCategory = BottomSheetDialog(requireContext())
        bottomSheetDialogCategory!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        view.lvCategory.setOnItemClickListener { parent, view, position, id ->
            val element = listAdapter.getItem(position) // The item that was clicked

//            Toast.makeText(this,element,Toast.LENGTH_SHORT).show()

            root.tvTaxInfoPurchase.setText(element)

            bottomSheetDialogCategory!!.dismiss()


        }




        bottomSheetDialogCategory!!.show()

    }



    private fun setTaxAdapter(taxList: ArrayList<TaxesModel>) {
        for (i in 0 until taxList.size) {
            taxStringList.add(taxList[i].name)
            taxIdList.add(taxList[i].xid)
        }

//        val adapter: ArrayAdapter<String> =
//            ArrayAdapter<String>(this, R.layout.dropdown, taxStringList)
//
//        acTax.threshold = 0 //will start working from first character
//
//        acTax.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView
//
//        acTax.setOnItemClickListener { parent, view, position, id ->
//            taxId = taxList[position].xid
//        }
    }




    private fun showTaxInfoSaleBottomSheet()
    {
        val view: View = layoutInflater.inflate(R.layout.layout_listview_bottomsheet, null)

        val  listAdapter = ArrayAdapter<String>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, taxInfoList)
        val  listIdAdapter = ArrayAdapter<String>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, taxIdList)

        view.lvCategory.adapter = listAdapter

        bottomSheetDialogCategory = BottomSheetDialog(requireContext())
        bottomSheetDialogCategory!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        view.lvCategory.setOnItemClickListener { parent, view, position, id ->
            val element = listAdapter.getItem(position) // The item that was clicked

//            Toast.makeText(this,element,Toast.LENGTH_SHORT).show()

            root.tvTaxInfoSale.setText(element)
            taxId = listIdAdapter.getItem(position).toString()
            Log.e("TAX ID",taxId)

            bottomSheetDialogCategory!!.dismiss()

        }
        bottomSheetDialogCategory!!.show()
    }


    private fun showTaxBottomSheet()
    {
        val view: View = layoutInflater.inflate(R.layout.layout_listview_bottomsheet, null)


        val  listAdapter = ArrayAdapter<String>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, taxStringList)

        view.lvCategory.adapter = listAdapter

        bottomSheetDialogCategory = BottomSheetDialog(requireContext())
        bottomSheetDialogCategory!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        view.lvCategory.setOnItemClickListener { parent, view, position, id ->
            val element = listAdapter.getItem(position) // The item that was clicked

//            Toast.makeText(this,element,Toast.LENGTH_SHORT).show()

            root.acTax.setText(element)
            someEventListener!!.someEvent(root.edtPurchasePrice.text.toString(),
                root.edtSalePrice.text.toString(), root.edtMRP.text.toString(),
                taxId
            );
            bottomSheetDialogCategory!!.dismiss()


        }


        bottomSheetDialogCategory!!.show()

    }


    private fun mNetworkCallTaxAPI() {
        taxList.clear()
        taxStringList.clear()
        root.pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getTaxesDropDown()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    root.pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONArray("data")
                            taxList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<TaxesModel?>?>() {}.type
                                )
                            )
                            setTaxAdapter(taxList)
                        } else {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (response.code() == 401) {
                    try {
                        root.pbLoadData.visibility = View.GONE
                        val JsonObject = JSONObject(response.errorBody()!!.string())
                        Util(requireActivity()).logOutAlertDialog(mContext, JsonObject.optString("message"))
                    } catch (e: Exception) {

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                root.pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }



}