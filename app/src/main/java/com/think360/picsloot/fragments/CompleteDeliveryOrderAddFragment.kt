package com.think360.picsloot.fragments

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Selection.setSelection
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Toast
import com.think360.picsloot.R
import com.think360.picsloot.R.id.comment
import com.think360.picsloot.activities.PicsLootActivity
import com.think360.picsloot.api.AppController
import com.think360.picsloot.api.EventToRefresh
import com.think360.picsloot.api.data.Responce
import com.think360.picsloot.api.interfaces.ApiService
import com.think360.picsloot.databinding.CompleteDeliveryOrderAddFragmentBinding
import com.think360.pro.healthguru.registration.fragments.OtpVerificationFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import javax.inject.Inject


/**
 * Created by think360 on 25/10/17.
 */
class CompleteDeliveryOrderAddFragment : Fragment() {
   @Inject
    internal lateinit var apiService: ApiService

    private lateinit var completeDeliveryOrderFragmentBinding: CompleteDeliveryOrderAddFragmentBinding

    private val compositeDisposable = CompositeDisposable()

    lateinit var  listspStateId : ArrayList<String>
    lateinit var  listspCityId : ArrayList<String>

    companion object {
        fun newInstance(): CompleteDeliveryOrderAddFragment {
            val frament = CompleteDeliveryOrderAddFragment()

            return frament

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        completeDeliveryOrderFragmentBinding = DataBindingUtil.inflate<CompleteDeliveryOrderAddFragmentBinding>(inflater, R.layout.complete_delivery_order_add_fragment, container, false)
        PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.navigation.visibility = View.GONE
        getStates()
        compositeDisposable.add((activity!!.application as AppController).bus().toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { o ->
            if (o is EventToRefresh && o.body ==1 ) {
                getStates()

            }
        })

        completeDeliveryOrderFragmentBinding.etAddress.setText(AppController.getSharedPref().getString("address",""))
        completeDeliveryOrderFragmentBinding.etPinCode.setText(AppController.getSharedPref().getString("pincode",""))
        completeDeliveryOrderFragmentBinding.etComment.setText(AppController.getSharedPref().getString("comment",""))


        completeDeliveryOrderFragmentBinding.btnCompleteOrder.setOnClickListener {
if(!TextUtils.isEmpty(completeDeliveryOrderFragmentBinding.etAddress.text)){
    if(!listspStateId.get(completeDeliveryOrderFragmentBinding.spState.selectedItemPosition).equals("-1")){

        if(listspCityId.size >0 && !listspCityId.get(completeDeliveryOrderFragmentBinding.spCity.selectedItemPosition).equals("-1")){

            if(!TextUtils.isEmpty(completeDeliveryOrderFragmentBinding.etPinCode.text)){

                if(!TextUtils.isEmpty(completeDeliveryOrderFragmentBinding.etComment.text)){

                    AppController.getSharedPref().edit().putString("address",completeDeliveryOrderFragmentBinding.etAddress.text.toString()).apply()
                    AppController.getSharedPref().edit().putString("state",completeDeliveryOrderFragmentBinding.spState.selectedItem.toString()).apply()
                    AppController.getSharedPref().edit().putString("state_id",listspStateId.get(completeDeliveryOrderFragmentBinding.spState.selectedItemPosition)).apply()
                    AppController.getSharedPref().edit().putString("city_id",listspCityId.get(completeDeliveryOrderFragmentBinding.spCity.selectedItemPosition)).apply()
                    AppController.getSharedPref().edit().putString("city",completeDeliveryOrderFragmentBinding.spCity.selectedItem.toString()).apply()
                    AppController.getSharedPref().edit().putString("pincode",completeDeliveryOrderFragmentBinding.etPinCode.text.toString()).apply()
                    AppController.getSharedPref().edit().putString("comment",completeDeliveryOrderFragmentBinding.etComment.text.toString()).apply()

                    if(TextUtils.isEmpty(AppController.getSharedPref().getString("mobile_no",""))){
                        PicsLootActivity.picsLootActivity!!.replaceFragment(EnterMobileNumberFragment.newInstance())
                    }else{
                        if(AppController.getSharedPref().getString("mobile_verify","").equals("1")){
                            PicsLootActivity.picsLootActivity!!.replaceFragment(ConfirmYourOrderFragment.newInstance())
                        }else{
                            PicsLootActivity.picsLootActivity!!.replaceFragment(OtpVerificationFragment.newInstance())
                        }
                    }


                }else{
                    val toast =  Toast.makeText(activity,"Please enter comment!", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }else{
                val toast =  Toast.makeText(activity,"Please enter pincode!", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }

        }else{
            val toast =  Toast.makeText(activity,"Please select city!", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
    }else{
        val toast =  Toast.makeText(activity,"Please select state!", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
}else{
    val toast =  Toast.makeText(activity,"Please enter address!", Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}

             }
          completeDeliveryOrderFragmentBinding.spState.onItemSelectedListener  =  sonItemSelectedListener
        return completeDeliveryOrderFragmentBinding.root
    }
    val sonItemSelectedListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            if(listspStateId.size>0) {
                getCities(listspStateId.get(position))
            }

        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
    fun getStates(){

        completeDeliveryOrderFragmentBinding.progressBar.isIndeterminate = true
        completeDeliveryOrderFragmentBinding.progressBar.visibility = View.VISIBLE
        completeDeliveryOrderFragmentBinding.progressBar.setClickable(false)

        apiService.allStates().enqueue(object : Callback<Responce.StateResponce> {
            override fun onResponse(call: Call<Responce.StateResponce>, response: Response<Responce.StateResponce>) {
                if (response.body().getStatus()==1) {
                    completeDeliveryOrderFragmentBinding.progressBar.visibility = View.GONE
                    completeDeliveryOrderFragmentBinding.progressBar.isIndeterminate = false

                    listspStateId = ArrayList<String>()
                    listspStateId.add("-1")

                    val  listSchName = ArrayList<String>()
                    listSchName.add("Select State")
                    for( i in response.body().getData()){
                        listspStateId.add(i.getState_id())
                        listSchName.add(i.getState_name())
                    }

                    val   yOJAdapter = ArrayAdapter<String>(PicsLootActivity.picsLootActivity, android.R.layout.simple_list_item_1, listSchName)
                    completeDeliveryOrderFragmentBinding.spState.adapter = yOJAdapter

                  if(!TextUtils.isEmpty(AppController.getSharedPref().getString("state",""))){
                        completeDeliveryOrderFragmentBinding.spState.setSelection(listSchName.indexOf(AppController.getSharedPref().getString("state","")))
                        if(!TextUtils.isEmpty(AppController.getSharedPref().getString("state_id",""))){
                            getCities(AppController.getSharedPref().getString("state_id",""))
                        }
                    }




                }else{
                    listspStateId = ArrayList<String>()
                    listspStateId.add("-1")

                    val  listSchName = ArrayList<String>()
                    listSchName.add("No state")

                    val   yOJAdapter = ArrayAdapter<String>(PicsLootActivity.picsLootActivity, android.R.layout.simple_list_item_1, listSchName)
                    completeDeliveryOrderFragmentBinding.spState.adapter = yOJAdapter

                    completeDeliveryOrderFragmentBinding.progressBar.visibility = View.GONE
                    completeDeliveryOrderFragmentBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.StateResponce>, t: Throwable) {

                completeDeliveryOrderFragmentBinding.progressBar.visibility = View.GONE
                completeDeliveryOrderFragmentBinding.progressBar.isIndeterminate = false
                t.printStackTrace()

            }
        })
    }
    fun getCities(state_id : String){


        completeDeliveryOrderFragmentBinding.progressBar.isIndeterminate = true
        completeDeliveryOrderFragmentBinding.progressBar.visibility = View.VISIBLE
        completeDeliveryOrderFragmentBinding.progressBar.setClickable(false)

        apiService.allCities(state_id).enqueue(object : Callback<Responce.CityResponce> {
            override fun onResponse(call: Call<Responce.CityResponce>, response: Response<Responce.CityResponce>) {
                if (response.body().getStatus()==1) {


                    completeDeliveryOrderFragmentBinding.progressBar.visibility = View.GONE
                    completeDeliveryOrderFragmentBinding.progressBar.isIndeterminate = false


                    listspCityId = ArrayList<String>()
                    listspCityId.add("-1")

                    val  listCityName = ArrayList<String>()
                    listCityName.add("Select City")
                    for( i in response.body().getData()){
                        listspCityId.add(i.getCity_id())
                        listCityName.add(i.getCity_name())
                    }

                    val   yOJAdapter = ArrayAdapter<String>(PicsLootActivity.picsLootActivity, android.R.layout.simple_list_item_1, listCityName)
                    completeDeliveryOrderFragmentBinding.spCity.adapter = yOJAdapter

                  if(!TextUtils.isEmpty(AppController.getSharedPref().getString("city",""))){
                        completeDeliveryOrderFragmentBinding.spCity.setSelection(listCityName.indexOf(AppController.getSharedPref().getString("city","")))

                    }



                }else{
                    listspCityId = ArrayList<String>()
                    listspCityId.add("-1")

                    val  listCityName = ArrayList<String>()
                    listCityName.add("No city")



                    val   yOJAdapter = ArrayAdapter<String>(PicsLootActivity.picsLootActivity, android.R.layout.simple_list_item_1, listCityName)
                    completeDeliveryOrderFragmentBinding.spCity.adapter = yOJAdapter

                    completeDeliveryOrderFragmentBinding.progressBar.visibility = View.GONE
                    completeDeliveryOrderFragmentBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(PicsLootActivity.picsLootActivity,response.body().getMessage(), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.CityResponce>, t: Throwable) {

                completeDeliveryOrderFragmentBinding.progressBar.visibility = View.GONE
                completeDeliveryOrderFragmentBinding.progressBar.isIndeterminate = false
                t.printStackTrace()

            }
        })
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        ( context.applicationContext as AppController).getComponent().inject(this@CompleteDeliveryOrderAddFragment)
    }
}