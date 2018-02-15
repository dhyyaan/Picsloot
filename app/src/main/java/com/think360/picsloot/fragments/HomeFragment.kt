package com.think360.picsloot.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast

import com.rafakob.drawme.DrawMeButton

import com.squareup.picasso.Picasso
import com.think360.picsloot.R
import com.think360.picsloot.R.drawable.images
import com.think360.picsloot.R.id.*
import com.think360.picsloot.activities.PicsLootActivity
import com.think360.picsloot.api.AppController
import com.think360.picsloot.api.EventToRefresh
import com.think360.picsloot.api.data.Responce
import com.think360.picsloot.api.interfaces.ApiService
import com.think360.picsloot.databinding.ConfirmOrderImagesItemBinding
import com.think360.picsloot.databinding.HomeFragmentBinding
import com.think360.picsloot.imagepicker.SImagePicker
import com.think360.picsloot.imagepickermodified.adapter.PickAdapter
import com.think360.picsloot.recyclerbindingadapter.DataBoundAdapter
import com.think360.picsloot.recyclerbindingadapter.DataBoundViewHolder
import com.think360.picsloot.util.ConnectivityReceiver

import gun0912.tedbottompicker.TedBottomPicker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import net.yazeed44.imagepicker.model.ImageEntry
import net.yazeed44.imagepicker.util.Picker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import javax.inject.Inject

/**
 * Created by think360 on 23/10/17.
 */
class HomeFragment : Fragment() {
    @Inject
    internal lateinit var apiService: ApiService
    private val compositeDisposable = CompositeDisposable()
    private lateinit var homeFragmentBinding: HomeFragmentBinding
    val REQUEST_CODE_IMAGE = 101
    companion object {

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeFragmentBinding = DataBindingUtil.inflate<HomeFragmentBinding>(inflater, R.layout.home_fragment, container, false)

        getLatestOrder()

        compositeDisposable.add((activity!!.application as AppController).bus().toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { o ->
            if (o is EventToRefresh && o.body ==1 ) {

               if(ConnectivityReceiver.isConnected()){
                   getLatestOrder()
               }else{
                   val toast = Toast.makeText(PicsLootActivity.picsLootActivity!!,"No internet connection!", Toast.LENGTH_SHORT)
                   toast.setGravity(Gravity.CENTER, 0, 0)
                   toast.show()
               }

            }

        })
        homeFragmentBinding.swiperefresh.setRefreshing(true)
        homeFragmentBinding.swiperefresh.setOnRefreshListener{

            if(ConnectivityReceiver.isConnected()){
                getLatestOrder()
            }else{
                val toast = Toast.makeText(PicsLootActivity.picsLootActivity!!,"No internet connection!", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }

        homeFragmentBinding.btnComposeOrder.setOnClickListener {

            if(ConnectivityReceiver.isConnected()){
                getQuestion()
            }else{
                val toast =  Toast.makeText(activity,"No internet connection!",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }

        }

        return homeFragmentBinding.root
    }
    internal inner class LatestOrderAdapter : DataBoundAdapter<ConfirmOrderImagesItemBinding> {
        var mProfileList : MutableList<String>
        constructor(mProfileList: MutableList<String>) : super(R.layout.confirm_order_images_item) {
            this. mProfileList = mProfileList
        }
        override fun bindItem(holder: DataBoundViewHolder<ConfirmOrderImagesItemBinding>?, position: Int, payloads: MutableList<Any>?) {
            Picasso.with(activity)
                    .load(mProfileList.get(position))
                    .resize(150, 150)
                    .centerCrop()
                    .into(holder?.binding?.imageView2)

        }
        override fun getItemCount(): Int {
            return mProfileList.size
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        ( context.applicationContext as AppController).getComponent().inject(this@HomeFragment)
    }
    fun getLatestOrder(){
        homeFragmentBinding.swiperefresh.setRefreshing(true)
         val user_id = AppController.getSharedPref().getString("user_id","null")
        apiService.latestOrder(user_id).enqueue(object : Callback<Responce.LatestOrderResponce> {
            override fun onResponse(call: Call<Responce.LatestOrderResponce>, response: Response<Responce.LatestOrderResponce>) {
                if (response.body().getStatus()) {
                    homeFragmentBinding.swiperefresh.setRefreshing(false)

                    homeFragmentBinding.svComposeOrder.visibility  = View.VISIBLE
                    homeFragmentBinding.tvNoData.visibility  = View.GONE

                    val adapter = LatestOrderAdapter(response.body().getLatest_order().getImage())
                    homeFragmentBinding. rv.adapter = adapter
                    adapter.notifyDataSetChanged()

                    homeFragmentBinding.tvDateTime.setText(response.body().getLatest_order().getDate_sent())
                    homeFragmentBinding.tvNoOfImages.setText(response.body().getLatest_order().getSent_message())


                  if(!TextUtils.isEmpty(response.body().getLatest_order().getDate_recieved())){
                         homeFragmentBinding.flImages.visibility = View.VISIBLE
                         homeFragmentBinding.tvName.visibility = View.VISIBLE
                         homeFragmentBinding.tvDateTimeImages2.setText(response.body().getLatest_order().getDate_recieved())
                         homeFragmentBinding.tvName.setText(response.body().getLatest_order().getRecieved_message())
                       }else{
                        homeFragmentBinding.flImages.visibility = View.GONE
                        homeFragmentBinding.tvName.visibility = View.GONE
                        }


                   if(!TextUtils.isEmpty(response.body().getLatest_order().getDate_shipped())){
                       homeFragmentBinding.flImages2.visibility = View.VISIBLE
                       homeFragmentBinding.  tvName2.visibility = View.VISIBLE
                        homeFragmentBinding.tvDateTimeImages3.setText(response.body().getLatest_order().getDate_shipped())
                        homeFragmentBinding.tvName2.setText(response.body().getLatest_order().getShipped_message())
                       }else{
                       homeFragmentBinding.flImages2.visibility = View.GONE
                       homeFragmentBinding.  tvName2.visibility = View.GONE
                          }



                   if(!TextUtils.isEmpty(response.body().getLatest_order().getDate_complete())){
                      homeFragmentBinding.flCompletet.visibility = View.VISIBLE
                       homeFragmentBinding.tvStatus.visibility = View.VISIBLE
                        homeFragmentBinding.tvDateTimeImages4.setText(response.body().getLatest_order().getDate_complete())
                        homeFragmentBinding.tvStatus.setText(response.body().getLatest_order().getComplete_message())

                     }else{
                         homeFragmentBinding.flCompletet.visibility = View.GONE
                       homeFragmentBinding.tvStatus.visibility = View.GONE

                          }



                }else{
                    homeFragmentBinding.swiperefresh.setRefreshing(false)
                    homeFragmentBinding.svComposeOrder.visibility  = View.GONE
                    homeFragmentBinding.tvNoData.visibility  = View.VISIBLE
                }

            }
            override fun onFailure(call: Call<Responce.LatestOrderResponce>, t: Throwable) {
                homeFragmentBinding.swiperefresh.setRefreshing(false)
                homeFragmentBinding.svComposeOrder.visibility  = View.GONE
                homeFragmentBinding.tvNoData.visibility  = View.VISIBLE

                t.printStackTrace()

            }
        })
    }
    fun getQuestion(){

        homeFragmentBinding.progressBar.isIndeterminate = true
        homeFragmentBinding.progressBar.visibility = View.VISIBLE
        homeFragmentBinding.progressBar.setClickable(false)

        val user_id = AppController.getSharedPref().getString("user_id","null")
        apiService.question(user_id).enqueue(object : Callback<Responce.QuestionResponce> {
            override fun onResponse(call: Call<Responce.QuestionResponce>, response: Response<Responce.QuestionResponce>) {
              if(response.isSuccessful){

                  homeFragmentBinding.progressBar.visibility = View.GONE
                  AppController.getSharedPref().edit().putString("eligible_images",response.body().getEligible_images()).apply()
                  AppController.getSharedPref().edit().putString("order_status",response.body().getOrder_status()).apply()
                  PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.badge.setNumber(response.body().getCount().toInt())

                         if (response.body().getStatus()) {

                             if (response.body().getOrder_status().equals("0")) {

                                     dialogOption(response.body().getData())


                             } else if (response.body().getOrder_status().equals("1")) {
                                 val msg =    "You already sended an order in this month. Please wait for next month to send the order."
                                 dialogNotEligible(msg)

                             } else if (response.body().getOrder_status().equals("2")) {
                                 val msg =    "You are not eligible. Please wait for next month to send the order."
                                 dialogNotEligible(msg)
                             }else{
                                 homeFragmentBinding.progressBar.visibility = View.GONE
                             }
                         } else {
                             if (response.body().getOrder_status().equals("0")) {
                                 dialogEligible()

                             }
                             else if (response.body().getOrder_status().equals("1")) {
                                 val msg =    "You already sended an order in this month. Please wait for next month to send the order."
                                 dialogNotEligible(msg)

                             } else if (response.body().getOrder_status().equals("2")) {
                                 val msg =    "You are not eligible. Please wait for next month to send the order."
                                 dialogNotEligible(msg)
                             }


                             else {
                                 homeFragmentBinding.progressBar.visibility = View.GONE
                             }


                         }


            }else {
                  homeFragmentBinding.progressBar.visibility = View.GONE
              }

            }
            override fun onFailure(call: Call<Responce.QuestionResponce>, t: Throwable) {

                homeFragmentBinding.progressBar.visibility = View.GONE


                t.printStackTrace()
             /*   val toast = Toast.makeText(PicsLootActivity.picsLootActivity,""+t,Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()*/
            }
        })
    }
    fun dialogEligible(){
      val dialog =  Dialog(PicsLootActivity.picsLootActivity)
       dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
       dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
       dialog.setCancelable(false)
       dialog.setContentView(R.layout.dialog_access_photos)
       val btnDoNotAllow = dialog.findViewById<DrawMeButton>(R.id.btnDoNotAllow) as DrawMeButton
       btnDoNotAllow.setOnClickListener { dialog.dismiss() }

       val btnOK = dialog.findViewById<DrawMeButton>(R.id.btnOK)
       btnOK.setOnClickListener {
           dialog.dismiss()

         val eligible_images =   AppController.getSharedPref().getString("eligible_images","-1").toInt()
           if (AppController.retriveImageList().size > 0) {
               SImagePicker
                       .from(PicsLootActivity.picsLootActivity)

                       .maxCount(eligible_images)
                       .rowCount(3)
                       .setSelected(AppController.retriveImageList())
                       // .showCamera(showCamera.isChecked())
                       .showCamera(false)
                       .pickMode(SImagePicker.MODE_IMAGE)
                       .forResult(REQUEST_CODE_IMAGE)
           } else {
               SImagePicker
                       .from(PicsLootActivity.picsLootActivity)

                       .maxCount(eligible_images)
                       .rowCount(3)
                       .showCamera(false)
                       .pickMode(SImagePicker.MODE_IMAGE)
                       .forResult(REQUEST_CODE_IMAGE)

           }




       }
        dialog.show()


    }
    fun dialogNotEligible(msg :String){
        val dialog =  Dialog(PicsLootActivity.picsLootActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_access_photos)
        val tvMsg1 = dialog.findViewById<TextView>(R.id.tvMsg1)
        tvMsg1.setText(msg)
        val btnDoNotAllow = dialog.findViewById<DrawMeButton>(R.id.btnDoNotAllow)
        btnDoNotAllow.visibility = View.GONE

        val btnOK = dialog.findViewById<DrawMeButton>(R.id.btnOK)
        btnOK.setOnClickListener {
            dialog.dismiss()


        }
        dialog.show()


    }

    fun dialogOption(data: Responce.QuestionResponce.Data) {
        val dialog =  Dialog(PicsLootActivity.picsLootActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_option)

        val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
        tvTitle.setText(data.getQuestion())

        val rbOne = dialog.findViewById<RadioButton>(R.id.rbOne) as RadioButton
        rbOne.setText(data.getAnswer().get(0))

        val rbTwo = dialog.findViewById<RadioButton>(R.id.rbTwo)
        rbTwo.setText(data.getAnswer().get(1))

        val rbThree = dialog.findViewById<RadioButton>(R.id.rbThree)
        rbThree.setText(data.getAnswer().get(2))

        val rbFour = dialog.findViewById<RadioButton>(R.id.rbFour)
        rbFour.setText(data.getAnswer().get(3))

        val btnOK = dialog.findViewById<DrawMeButton>(R.id.btnOK)
        btnOK.setOnClickListener {
            dialog.dismiss()

            if(ConnectivityReceiver.isConnected()){

                if(rbOne.isChecked()){
                    answer(data.getAnswer().get(0))

                }else if(rbTwo.isChecked()){
                    answer(data.getAnswer().get(1))
                }else if(rbThree.isChecked()){
                    answer(data.getAnswer().get(2))
                }else if(rbFour.isChecked()){
                    answer(data.getAnswer().get(3))
                }else{

                    val toast = Toast.makeText(activity,"Please select atleast one option",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }else{
                val toast =  Toast.makeText(activity,"No internet connection!",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }


        }
        dialog.show()
    }
    fun answer(ans : String){
        homeFragmentBinding.progressBar.isIndeterminate = true
        homeFragmentBinding.progressBar.visibility = View.VISIBLE
        homeFragmentBinding.progressBar.setClickable(false)

        val user_id = AppController.getSharedPref().getString("user_id","null")
        apiService.ans(user_id,ans).enqueue(object : Callback<Responce.AnswerResponce> {
            override fun onResponse(call: Call<Responce.AnswerResponce>, response: Response<Responce.AnswerResponce>) {
                if (response.body().getStatus()) {
                    homeFragmentBinding.progressBar.visibility = View.GONE
                    dialogEligible()
                }else{
                    homeFragmentBinding.progressBar.visibility = View.GONE
                    val toast = Toast.makeText(activity,response.body().getMessage(),Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.AnswerResponce>, t: Throwable) {

                homeFragmentBinding.progressBar.visibility = View.GONE


                t.printStackTrace()
              /*  val toast = Toast.makeText(activity,""+t,Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()*/
            }
        })

    }
}