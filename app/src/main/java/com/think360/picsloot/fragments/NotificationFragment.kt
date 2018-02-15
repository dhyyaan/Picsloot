package com.think360.picsloot.fragments

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.think360.picsloot.R
import com.think360.picsloot.activities.PicsLootActivity
import com.think360.picsloot.api.AppController
import com.think360.picsloot.api.EventToRefresh
import com.think360.picsloot.api.data.Responce
import com.think360.picsloot.api.interfaces.ApiService
import com.think360.picsloot.databinding.NotificationFragmentBinding
import com.think360.picsloot.databinding.NotificationRvItemBinding
import com.think360.picsloot.recyclerbindingadapter.DataBoundAdapter
import com.think360.picsloot.recyclerbindingadapter.DataBoundViewHolder

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


/**
 * Created by think360 on 11/12/17.
 */
class NotificationFragment : Fragment() {
    @Inject
    internal lateinit var apiService: ApiService
    private lateinit var notificationFragment: NotificationFragmentBinding
    private val compositeDisposable = CompositeDisposable()
    companion object {
        fun newInstance(): NotificationFragment {
            return NotificationFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       notificationFragment = DataBindingUtil.inflate<NotificationFragmentBinding>(inflater, R.layout.notification_fragment, container, false)
        PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.badge.visibility = View.GONE

        showNotifications()
        compositeDisposable.add((activity!!.application as AppController).bus().toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { o ->
            if (o is EventToRefresh && o.body ==1 ) {
                showNotifications()

            }
        })
        return notificationFragment.root
    }
    fun showNotifications(){
        notificationFragment.swiperefresh.setRefreshing(true)

        val user_id =     AppController.getSharedPref().getString("user_id","null")
        val firebase_id =  AppController.getSharedPref().getString("firebase_reg_token","null")


        apiService. getNotification(user_id,firebase_id).enqueue(object : Callback<Responce.NotificationResponce> {
            override fun onResponse(call: Call<Responce.NotificationResponce>, response: Response<Responce.NotificationResponce>) {
                if (response.body().getStatus()==1) {

                    val adapter = ProfileBindingAdapter(response.body().getData())
                    notificationFragment.rvImages.adapter = adapter
                    adapter.notifyDataSetChanged()
                    notificationFragment.swiperefresh.setRefreshing(false)

                }else{

                    notificationFragment.swiperefresh.setRefreshing(true)

                    val toast =  Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.NotificationResponce>, t: Throwable) {
                notificationFragment.swiperefresh.setRefreshing(true)
                t.printStackTrace()
  /*              val toast = Toast.makeText(activity,""+t, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()*/
            }
        })
    }
    internal inner class ProfileBindingAdapter : DataBoundAdapter<NotificationRvItemBinding> {
        var mProfileList : MutableList<Responce.NotificationResponce.Data>
        constructor(mProfileList: MutableList<Responce.NotificationResponce.Data>) : super(R.layout.notification_rv_item) {
            this. mProfileList = mProfileList
        }
        override fun bindItem(holder: DataBoundViewHolder<NotificationRvItemBinding>?, position: Int, payloads: MutableList<Any>?) {
           val item = mProfileList.get(position)
            holder?.binding?.tvDateTime!!.setText(item.getNotification_date())
            holder?.binding?.tvNoOfImages!!.setText(item.getMessage())
        }
        override fun getItemCount(): Int {
            return mProfileList.size
        }
    }
    override fun onAttach(context: Context?) {
        (activity!!.application as AppController).getComponent().inject(this@NotificationFragment)
        super.onAttach(context)
    }
}