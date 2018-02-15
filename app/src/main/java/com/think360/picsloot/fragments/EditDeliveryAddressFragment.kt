package com.think360.picsloot.fragments

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.think360.picsloot.R
import com.think360.picsloot.activities.PicsLootActivity
import com.think360.picsloot.api.AppController
import com.think360.picsloot.api.EventToRefresh
import com.think360.picsloot.api.data.Responce
import com.think360.picsloot.api.interfaces.ApiService
import com.think360.picsloot.databinding.EditDeliveryAddressBinding
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
class EditDeliveryAddressFragment : Fragment() {
    @Inject
    internal lateinit var apiService: ApiService

    private lateinit var editDeliveryAddressBinding: EditDeliveryAddressBinding
    private val compositeDisposable = CompositeDisposable()

    private var city_name = ""
    private var state_name = ""

    lateinit var  listspStateId : ArrayList<String>
    lateinit var  listspCityId : ArrayList<String>

    companion object {
        fun newInstance(): EditDeliveryAddressFragment {
            return EditDeliveryAddressFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        editDeliveryAddressBinding = DataBindingUtil.inflate<EditDeliveryAddressBinding>(inflater, R.layout.edit_delivery_address, container, false)
       PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.navigation.visibility = View.GONE
        compositeDisposable.add((activity!!.application as AppController).bus().toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { o ->
            if (o is EventToRefresh && o.body ==1 ) {
                showProfile()
            }
        })


        PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.flBack.visibility = View.VISIBLE
        PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.ivBack.setOnClickListener {
            PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.flBack.visibility = View.GONE
            PicsLootActivity.picsLootActivity!!.onBackPressed() }

        editDeliveryAddressBinding.btnSaveDeliveryAddress.setOnClickListener {
            if(!listspStateId.get(editDeliveryAddressBinding.spState.selectedItemPosition).equals("-1")){

                if(!listspCityId.get(editDeliveryAddressBinding.spCity.selectedItemPosition).equals("-1")){
                   if(ConnectivityReceiver.isConnected()){
                       editDeliveryAddress()
                   }else{
                       val toast = Toast.makeText(activity,"No internet connection!", Toast.LENGTH_SHORT)
                       toast.setGravity(Gravity.CENTER, 0, 0)
                       toast.show()
                   }


                }else{
                    val toast = Toast.makeText(activity,"Please select city", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
            }else{
                val toast = Toast.makeText(activity,"Please select state", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }

          }

        showProfile()

        return editDeliveryAddressBinding.root
    }
    val sonItemSelectedListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            if(listspStateId.size>0) {
                getCities(listspStateId.get(position), false)
            }

        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
    fun getStates(status : Boolean){

        apiService.allStates().enqueue(object : Callback<Responce.StateResponce> {
            override fun onResponse(call: Call<Responce.StateResponce>, response: Response<Responce.StateResponce>) {
                if (response.body().getStatus()==1) {
                    editDeliveryAddressBinding.progressBar.visibility = View.GONE
                    editDeliveryAddressBinding.progressBar.isIndeterminate = false

                    listspStateId = ArrayList<String>()
                    listspStateId.add("-1")

                    val  listSchName = ArrayList<String>()
                    listSchName.add("Select State")
                    for( i in response.body().getData()){
                        listspStateId.add(i.getState_id())
                        listSchName.add(i.getState_name())
                    }

                    val   yOJAdapter = ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listSchName)
                    editDeliveryAddressBinding.spState.adapter = yOJAdapter

                    if(!state_name.equals("") && status){
                    editDeliveryAddressBinding.spState.setSelection(listSchName.indexOf(state_name))
                      }
                    editDeliveryAddressBinding.spState.onItemSelectedListener  =  sonItemSelectedListener
                }else{
                    listspStateId = ArrayList<String>()
                    listspStateId.add("-1")

                    val  listSchName = ArrayList<String>()
                    listSchName.add("No state")

                    val   yOJAdapter = ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listSchName)
                    editDeliveryAddressBinding.spState.adapter = yOJAdapter

                    editDeliveryAddressBinding.progressBar.visibility = View.GONE
                    editDeliveryAddressBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.StateResponce>, t: Throwable) {

                editDeliveryAddressBinding.progressBar.visibility = View.GONE
                editDeliveryAddressBinding.progressBar.isIndeterminate = false
                t.printStackTrace()
             /*   val toast = Toast.makeText(activity,""+t, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()*/
            }
        })
    }
    fun getCities(state_id : String,status : Boolean){

            if(!status) {
                      editDeliveryAddressBinding.progressBar.isIndeterminate = true
                      editDeliveryAddressBinding.progressBar.visibility = View.VISIBLE
                      editDeliveryAddressBinding.progressBar.setClickable(false)
                        }
           apiService.allCities(state_id).enqueue(object : Callback<Responce.CityResponce> {
            override fun onResponse(call: Call<Responce.CityResponce>, response: Response<Responce.CityResponce>) {
                if (response.body().getStatus()==1) {

                   if(!status) {
                      editDeliveryAddressBinding.progressBar.visibility = View.GONE
                      editDeliveryAddressBinding.progressBar.isIndeterminate = false
                       }

                    listspCityId = ArrayList<String>()
                    listspCityId.add("-1")

                    val  listCityName = ArrayList<String>()
                    listCityName.add("Select City")
                    for( i in response.body().getData()){
                        listspCityId.add(i.getCity_id())
                        listCityName.add(i.getCity_name())
                    }

                    val   yOJAdapter = ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listCityName)
                    editDeliveryAddressBinding.spCity.adapter = yOJAdapter



                    if(!city_name.equals("") && status){
                        editDeliveryAddressBinding.spCity.setSelection(listCityName.indexOf(city_name))
                    }else if(!city_name.equals("")) {
                        editDeliveryAddressBinding.spCity.setSelection(listCityName.indexOf(city_name))
                    }



                }else{

                    listspCityId = ArrayList<String>()
                    listspCityId.add("-1")

                    val  listCityName = ArrayList<String>()
                    listCityName.add("No city")



                    val   yOJAdapter = ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listCityName)
                    editDeliveryAddressBinding.spCity.adapter = yOJAdapter

                    editDeliveryAddressBinding.progressBar.visibility = View.GONE
                    editDeliveryAddressBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.CityResponce>, t: Throwable) {

                editDeliveryAddressBinding.progressBar.visibility = View.GONE
                editDeliveryAddressBinding.progressBar.isIndeterminate = false
                t.printStackTrace()
               // val toast = Toast.makeText(activity,""+t, Toast.LENGTH_SHORT)
               // toast.setGravity(Gravity.CENTER, 0, 0)
               // toast.show()
            }
        })
    }
    fun showProfile(){

               editDeliveryAddressBinding.progressBar.isIndeterminate = true
               editDeliveryAddressBinding.progressBar.visibility = View.VISIBLE
               editDeliveryAddressBinding.progressBar.setClickable(false)
               val user_id =  AppController.getSharedPref().getString("user_id","null")

        apiService.showProfile(user_id).enqueue(object : Callback<Responce.ShowProfileResponce> {
              override fun onResponse(call: Call<Responce.ShowProfileResponce>, response: Response<Responce.ShowProfileResponce>) {
                if (response.body().getStatus()) {
                    editDeliveryAddressBinding.progressBar.visibility = View.GONE
                    editDeliveryAddressBinding.progressBar.isIndeterminate = false

                    editDeliveryAddressBinding.etAddress.setText(response.body().getData().getAddress().getAddress())
                    editDeliveryAddressBinding.etPinCode.setText(response.body().getData().getAddress().getPin_code())
                    editDeliveryAddressBinding.etComment.setText(response.body().getData().getAddress().getComment())



                    city_name = response.body().getData().getAddress().getCity_name()
                    state_name = response.body().getData().getAddress().getState_name()

                    getStates(true)

                   if(!response.body().getData().getAddress().getState_id().equals("")) {
                      getCities(response.body().getData().getAddress().getState_id(), true)

                          }

                }else{

                    editDeliveryAddressBinding.progressBar.visibility = View.GONE
                    editDeliveryAddressBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.ShowProfileResponce>, t: Throwable) {

                editDeliveryAddressBinding.progressBar.visibility = View.GONE
                editDeliveryAddressBinding.progressBar.isIndeterminate = false
                t.printStackTrace()
      /*          val toast = Toast.makeText(activity,""+t, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()*/
            }
        })
    }
    fun editDeliveryAddress(){
        editDeliveryAddressBinding.progressBar.isIndeterminate = true
        editDeliveryAddressBinding.progressBar.visibility = View.VISIBLE
        editDeliveryAddressBinding.progressBar.setClickable(false)

        val add = editDeliveryAddressBinding.etAddress.text.toString()

        val state_id = listspStateId.get(editDeliveryAddressBinding.spState.selectedItemPosition)
        val city_id = listspCityId.get(editDeliveryAddressBinding.spCity.selectedItemPosition)

        val pic_code =  editDeliveryAddressBinding.etPinCode.text.toString()
        val comment = editDeliveryAddressBinding.etComment.text.toString()
        val user_id =  AppController.getSharedPref().getString("user_id","null")
        apiService.editDelivaryAddress(user_id,
                add,state_id,city_id,pic_code,comment).enqueue(object : Callback<Responce.EditDeliveryAddResponce> {
            override fun onResponse(call: Call<Responce.EditDeliveryAddResponce>, response: Response<Responce.EditDeliveryAddResponce>) {
                if (response.body().getStatus()) {

                    editDeliveryAddressBinding.progressBar.visibility = View.GONE
                    editDeliveryAddressBinding.progressBar.isIndeterminate = false




                    val toast =  Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()

                    PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.flBack.visibility = View.GONE
                    PicsLootActivity.picsLootActivity!!.onBackPressed()



                }else{

                    editDeliveryAddressBinding.progressBar.visibility = View.GONE
                    editDeliveryAddressBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.EditDeliveryAddResponce>, t: Throwable) {

                editDeliveryAddressBinding.progressBar.visibility = View.GONE
                editDeliveryAddressBinding.progressBar.isIndeterminate = false
                t.printStackTrace()
             /*   val toast = Toast.makeText(activity,""+t, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()*/
            }
        })
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        ( context.applicationContext as AppController).getComponent().inject(this@EditDeliveryAddressFragment)
    }
}