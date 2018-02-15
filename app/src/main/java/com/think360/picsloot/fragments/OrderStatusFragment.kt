package com.think360.picsloot.fragments

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.think360.picsloot.R
import com.think360.picsloot.activities.PicsLootActivity
import com.think360.picsloot.api.AppController
import com.think360.picsloot.databinding.FinishDeliveryOrderFragmentBinding
import com.think360.picsloot.databinding.OrderStatusFragmentBinding

/**
 * Created by think360 on 25/10/17.
 */
class OrderStatusFragment : Fragment() {

    private lateinit var orderStatusFragmentBinding: OrderStatusFragmentBinding

    companion object {
        fun newInstance(): OrderStatusFragment {
            return OrderStatusFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        orderStatusFragmentBinding = DataBindingUtil.inflate<OrderStatusFragmentBinding>(inflater, R.layout.order_status_fragment, container, false)

        AppController.getSharedPref().edit().putBoolean("onBack",false).apply()

        PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.navigation.visibility = View.GONE
        PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.ivNotification.visibility=View.GONE
        PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.badge.visibility=View.GONE

        orderStatusFragmentBinding.btnViewOrder.setOnClickListener {
            AppController.getSharedPref().edit().putBoolean("onBack",true).apply()
            PicsLootActivity.picsLootActivity!!.finish()
            val intent = Intent(activity, PicsLootActivity::class.java)
            intent.putExtra("TO_OPEN",R.id.navigation_order)
           // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)



     }

        return orderStatusFragmentBinding.root
    }
}