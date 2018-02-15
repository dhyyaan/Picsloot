package com.think360.picsloot.fragments
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.think360.picsloot.R
import com.think360.picsloot.activities.PicsLootActivity

import com.think360.picsloot.api.AppController
import com.think360.picsloot.api.EventToRefresh
import com.think360.picsloot.api.data.Responce
import com.think360.picsloot.api.interfaces.ApiService
import com.think360.picsloot.databinding.ConfirmOrderImagesItemBinding

import com.think360.picsloot.databinding.OrderFragmentBinding
import com.think360.picsloot.databinding.OrderRvItemBinding
import com.think360.picsloot.recyclerbindingadapter.DataBoundAdapter
import com.think360.picsloot.recyclerbindingadapter.DataBoundViewHolder
import com.think360.picsloot.util.ConnectivityReceiver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


/**
 * Created by think360 on 23/10/17.
 */
class OrderFragment : Fragment() {
    @Inject
    internal lateinit var apiService: ApiService
    private lateinit var orderFragmentBinding: OrderFragmentBinding

    private val compositeDisposable = CompositeDisposable()

    companion object {
        fun newInstance(): OrderFragment {
            return OrderFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        orderFragmentBinding = DataBindingUtil.inflate<OrderFragmentBinding>(inflater, R.layout.order_fragment, container, false)

        getLatestOrOrderHistory()
        compositeDisposable.add((activity!!.application as AppController).bus().toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { o ->
            if (o is EventToRefresh && o.body ==1 ) {

                if(ConnectivityReceiver.isConnected()){
                    getLatestOrOrderHistory()
                }else{
                    val toast = Toast.makeText(PicsLootActivity.picsLootActivity!!,"No internet connection!", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
            }
        })

        orderFragmentBinding.swiperefresh.setRefreshing(true)
        orderFragmentBinding.swiperefresh.setOnRefreshListener{

            if(ConnectivityReceiver.isConnected()){
                getLatestOrOrderHistory()
            }else{
                val toast = Toast.makeText(PicsLootActivity.picsLootActivity!!,"No internet connection!", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }


        return orderFragmentBinding.root
    }



    internal inner class OrderHistoryBindingAdapter(var mProfileList: MutableList<Responce.ViewOrderResponce.OrderHistory>) : DataBoundAdapter<OrderRvItemBinding>(R.layout.order_rv_item) {

        override fun bindItem(holder: DataBoundViewHolder<OrderRvItemBinding>?, position: Int, payloads: MutableList<Any>?) {
           holder!!.binding.tvDateTime!!.setText(mProfileList.get(position).getDate_sent())
            holder.binding.tvNoOfImages.setText(mProfileList.get(position).getSent_message())

            val adapter1 = ConfirmOrderImagesAdapter(mProfileList.get(position).getImage())
            holder.binding?.rv!!.adapter = adapter1
            adapter1.notifyDataSetChanged()

            if(!TextUtils.isEmpty(mProfileList.get(position).getDate_recieved())){
                holder.binding.flImages.visibility = View.VISIBLE
                holder.binding.tvName.visibility = View.VISIBLE
                holder.binding.tvDateTimeImages2.setText(mProfileList.get(position).getDate_recieved())
                holder.binding.tvName.setText(mProfileList.get(position).getRecieved_message())
            }else{
                holder.binding.flImages.visibility = View.GONE
                holder.binding.tvName.visibility = View.GONE
            }


            if(!TextUtils.isEmpty(mProfileList.get(position).getDate_shipped())){
                holder.binding.flImages2.visibility = View.VISIBLE
                holder.binding.tvName2.visibility = View.VISIBLE
                holder.binding.tvDateTimeImages3.setText(mProfileList.get(position).getDate_shipped())
                holder.binding.tvName2.setText(mProfileList.get(position).getShipped_message())
            }else{
                holder.binding.flImages2.visibility = View.GONE
                holder.binding.tvName2.visibility = View.GONE
            }


            if(!TextUtils.isEmpty(mProfileList.get(position).getDate_complete())){
                holder.binding.flCompletet.visibility = View.VISIBLE
                holder.binding.tvStatus.visibility = View.VISIBLE
                holder.binding.tvDateTimeImages4.setText(mProfileList.get(position).getDate_complete())
                holder.binding.tvStatus.setText(mProfileList.get(position).getComplete_message())
            }else{
                holder.binding.flCompletet.visibility = View.GONE
                holder.binding.tvStatus.visibility = View.GONE
            }

        }
        override fun getItemCount(): Int {
            return mProfileList.size
        }
    }
    internal inner class ConfirmOrderImagesAdapter(var mProfileList: MutableList<String>) : DataBoundAdapter<ConfirmOrderImagesItemBinding>(R.layout.confirm_order_images_item) {
        override fun bindItem(holder: DataBoundViewHolder<ConfirmOrderImagesItemBinding>?, position: Int, payloads: MutableList<Any>?) {
            Picasso.with(activity)
                    .load(mProfileList.get(position))
                    .resize(50, 50)
                    .centerCrop()
                    .into(holder?.binding?.imageView2)
        }
        override fun getItemCount(): Int {
            return mProfileList.size
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        ( context.applicationContext as AppController).getComponent().inject(this@OrderFragment)
    }
    fun getLatestOrOrderHistory(){
        orderFragmentBinding.swiperefresh.setRefreshing(true)
        val user_id =  AppController.getSharedPref().getString("user_id","null")

        apiService.viewOrder(user_id).enqueue(object : Callback<Responce.ViewOrderResponce> {
            override fun onResponse(call: Call<Responce.ViewOrderResponce>, response: Response<Responce.ViewOrderResponce>) {
                if (response.body().getStatus()) {


                    orderFragmentBinding. tvDateTime.setText(response.body().getLatest_order().getDate_sent())
                    orderFragmentBinding. tvNoOfImages.setText(response.body().getLatest_order().getSent_message())


                    val adapter1 = ConfirmOrderImagesAdapter(response.body().getLatest_order().getImage())
                    orderFragmentBinding.rv1!!.adapter = adapter1
                    adapter1.notifyDataSetChanged()

                    if(!TextUtils.isEmpty(response.body().getLatest_order().getDate_recieved())){
                        orderFragmentBinding.flImages.visibility = View.VISIBLE
                        orderFragmentBinding.tvName.visibility = View.VISIBLE
                        orderFragmentBinding. tvDateTimeImages2.setText(response.body().getLatest_order().getDate_recieved())
                        orderFragmentBinding. tvName.setText(response.body().getLatest_order().getRecieved_message())

                    }else{
                        orderFragmentBinding.flImages.visibility = View.GONE
                        orderFragmentBinding.tvName.visibility = View.GONE
                    }


                    if(!TextUtils.isEmpty(response.body().getLatest_order().getDate_shipped())){
                        orderFragmentBinding.flImages2.visibility = View.VISIBLE
                        orderFragmentBinding.tvName2.visibility = View.VISIBLE

                        orderFragmentBinding. tvDateTimeImages3.setText(response.body().getLatest_order().getDate_shipped())
                        orderFragmentBinding. tvName2.setText(response.body().getLatest_order().getShipped_message())

                    }else{
                        orderFragmentBinding.flImages2.visibility = View.GONE
                        orderFragmentBinding.tvName2.visibility = View.GONE
                    }


                    if(!TextUtils.isEmpty(response.body().getLatest_order().getDate_complete())){
                        orderFragmentBinding.flCompletet.visibility = View.VISIBLE
                        orderFragmentBinding.tvStatus.visibility = View.VISIBLE

                        orderFragmentBinding. tvDateTimeImages4.setText(response.body().getLatest_order().getDate_complete())
                        orderFragmentBinding. tvStatus.setText(response.body().getLatest_order().getComplete_message())


                    }else{
                        orderFragmentBinding.flCompletet.visibility = View.GONE
                        orderFragmentBinding.tvStatus.visibility = View.GONE

                    }


                  if(response.body().getOrder_history() !=null){
                      val adapter = OrderHistoryBindingAdapter(response.body().getOrder_history())
                      orderFragmentBinding.recyclerview.adapter = adapter
                      adapter.notifyDataSetChanged()
                  }


                    orderFragmentBinding.swiperefresh.setRefreshing(false)


                }else{
                    orderFragmentBinding.swiperefresh.setRefreshing(false)
                    orderFragmentBinding.nestScroll.visibility  = View.GONE
                    orderFragmentBinding.tvNoData.visibility  = View.VISIBLE
                }

            }
            override fun onFailure(call: Call<Responce.ViewOrderResponce>, t: Throwable) {
                orderFragmentBinding.swiperefresh.setRefreshing(false)
                orderFragmentBinding.nestScroll.visibility  = View.GONE
                orderFragmentBinding.tvNoData.visibility  = View.VISIBLE
                t.printStackTrace()

            }
        })
    }

}