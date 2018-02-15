package com.think360.picsloot.fragments

import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.think360.picsloot.R
import com.think360.picsloot.R.drawable.images
import com.think360.picsloot.R.id.comment
import com.think360.picsloot.activities.PicsLootActivity
import com.think360.picsloot.api.AppController

import com.think360.picsloot.databinding.ConfirmOrderImagesItemBinding
import com.think360.picsloot.databinding.ConfirmYourOrderFragmentBinding
import com.think360.picsloot.recyclerbindingadapter.DataBoundAdapter
import com.think360.picsloot.recyclerbindingadapter.DataBoundViewHolder
import com.think360.picsloot.util.ConnectivityReceiver
import java.io.File
import java.util.ArrayList

/**
 * Created by think360 on 25/10/17.
 */
class ConfirmYourOrderFragment : Fragment() {

    private lateinit var confirmYourOrderFragmentBinding: ConfirmYourOrderFragmentBinding
    companion object {
        fun newInstance( ): ConfirmYourOrderFragment {
            val frament = ConfirmYourOrderFragment()
            return frament
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        confirmYourOrderFragmentBinding = DataBindingUtil.inflate<ConfirmYourOrderFragmentBinding>(inflater, R.layout.confirm_your_order_fragment, container, false)
        PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.navigation.visibility = View.GONE


        confirmYourOrderFragmentBinding.tvAddress.setText(AppController.getSharedPref().getString("address","")+", "+
                AppController.getSharedPref().getString("state","")+", "+AppController.getSharedPref().getString("city",""))

        confirmYourOrderFragmentBinding.pincode.setText(AppController.getSharedPref().getString("pincode",""))
        confirmYourOrderFragmentBinding.comment.setText(AppController.getSharedPref().getString("comment",""))
        confirmYourOrderFragmentBinding.tvMobileNo.setText(AppController.getSharedPref().getString("mobile_no",""))

        val adapter = ConfirmOrderImagesAdapter()
        confirmYourOrderFragmentBinding.rv.adapter = adapter
        adapter.notifyDataSetChanged()

        confirmYourOrderFragmentBinding.ivEditDeliveryAdd.setOnClickListener {

            if(ConnectivityReceiver.isConnected()){
                for (i in 0 until fragmentManager!!.getBackStackEntryCount())
                    fragmentManager!!.popBackStack()
                PicsLootActivity.picsLootActivity!!.replaceFragment(UploadImagesForPrintFragment.newInstance())
            }else{
                val toast = Toast.makeText(AppController.getAppContext(),"No internet connection!", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }


        }
        confirmYourOrderFragmentBinding.btnConfirmOrder.setOnClickListener {

            if(ConnectivityReceiver.isConnected()){
                PicsLootActivity.picsLootActivity!!.replaceFragment(FinishDeliveryOrderFragment.newInstance())
            }else{
                val toast = Toast.makeText(AppController.getAppContext(),"No internet connection!", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }

             }

        return confirmYourOrderFragmentBinding.root
    }
    internal inner class ConfirmOrderImagesAdapter : DataBoundAdapter<ConfirmOrderImagesItemBinding> {
        constructor() : super(R.layout.confirm_order_images_item)

        override fun bindItem(holder: DataBoundViewHolder<ConfirmOrderImagesItemBinding>?, position: Int, payloads: MutableList<Any>?) {
            Glide.with(PicsLootActivity.picsLootActivity!!.applicationContext)
                    .load(AppController.retriveImageList().get(position))
                    .placeholder(R.drawable.no_img)
                    //       .error(R.drawable.imagenotfound)
                    .into(holder?.binding?.imageView2)
        }
        override fun getItemCount(): Int {
            return AppController.retriveImageList().size
        }
    }
}