package com.playplexmatm.activity.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.playplexmatm.R
import com.playplexmatm.aeps.activities_aeps.aepshistory.AepsHistoryActivity
import com.playplexmatm.aeps.activities_aeps.aepshistory.AepsLedgerHistoryActivity
import com.playplexmatm.activities_aeps.AepsCommissionActivity
import com.playplexmatm.activities_aeps.AepsCommissionSlabActivity
import com.playplexmatm.activity.pos_reports.PosCommissionSlabActivity
import com.playplexmatm.activity.pos_reports.PosHistoryActivity
import com.playplexmatm.microatm.MATMTestActivity
import com.playplexmatm.microatm.MicroAtmCommissionSlabActivity
import com.playplexmatm.microatm.MicroAtmHistoryActivity
import kotlinx.android.synthetic.main.activity_matmtest.*
import kotlinx.android.synthetic.main.activity_matmtest.view.*
import kotlinx.android.synthetic.main.fragment_reports.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReportsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportsFragment : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_reports, container, false)


//        (activity as MATMTestActivity).custToolbar.tvTitle.setText("Reports")

//        root.ll_aeps_ledger.setOnClickListener{
//            val intent = Intent(requireContext(), AepsLedgerHistoryActivity::class.java)
//            startActivity(intent)
//        }

        root.ll_payouthistory.setOnClickListener {
//            val intent = Intent(requireContext(), PayoutHistoryActivity::class.java)
//            startActivity(intent)
        }
        root.ll_aeps_commissionslab.setOnClickListener {
            val intent = Intent(requireContext(), AepsCommissionSlabActivity::class.java)
            startActivity(intent)
        }
        root.ll_aeps_history.setOnClickListener {
            val intent = Intent(requireContext(), AepsHistoryActivity::class.java)
            startActivity(intent)
        }

        root.ll_aeps_comm_history.setOnClickListener {
            val intent = Intent(requireContext(), AepsCommissionActivity::class.java)
            startActivity(intent)
        }

        root.ll_matm_commission_slab.setOnClickListener {

            val intent = Intent(requireContext(), MicroAtmCommissionSlabActivity::class.java)
            startActivity(intent)

        }

        root.ll_matm_history.setOnClickListener {
            val intent = Intent(requireContext(), MicroAtmHistoryActivity::class.java)
            startActivity(intent)

        }

        root.ll_pos_commission_slab.setOnClickListener {

            val intent = Intent(requireContext(), PosCommissionSlabActivity::class.java)
            startActivity(intent)

        }

        root.ll_pos_history.setOnClickListener {
            val intent = Intent(requireContext(), PosHistoryActivity::class.java)
            startActivity(intent)

        }
        root.ll_sales.setOnClickListener {
            val fragmentHome = SalesFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.contentFrame, fragmentHome)
                .commit()
        }

        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReportsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReportsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}