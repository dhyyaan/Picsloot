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
import com.think360.picsloot.R
import com.think360.picsloot.activities.LoginActivity
import com.think360.picsloot.activities.LoginActivity.Companion.loginActivity
import com.think360.picsloot.activities.PicsLootActivity
import com.think360.picsloot.api.AppController
import com.think360.picsloot.api.data.Responce
import com.think360.picsloot.api.interfaces.ApiService
import com.think360.picsloot.databinding.EnterMobileNumberFragmentBinding
import com.think360.picsloot.util.ConnectivityReceiver
import com.think360.pro.healthguru.registration.fragments.OtpVerificationFragment
import javax.inject.Inject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.util.regex.Pattern

/**
 * Created by think360 on 17/10/17.
 */
class EnterMobileNumberFragment : Fragment() {
    @Inject
    internal lateinit var apiService: ApiService

    private lateinit var  enterMobileNumberFragmentBinding : EnterMobileNumberFragmentBinding
    companion object {

        fun newInstance(): EnterMobileNumberFragment {
            return EnterMobileNumberFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       enterMobileNumberFragmentBinding  = DataBindingUtil.inflate<EnterMobileNumberFragmentBinding>(inflater, R.layout.enter_mobile_number_fragment, container, false)

        enterMobileNumberFragmentBinding.btnNext.setOnClickListener {

            if(!TextUtils.isEmpty(enterMobileNumberFragmentBinding.etMobileNumber.text)){

                if(checkMobileNo(enterMobileNumberFragmentBinding.etMobileNumber.text.toString())){

                    if(ConnectivityReceiver.isConnected()){
                        sendOTP()
                    }else{
                        val toast =  Toast.makeText(activity,"No internet connection!",Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }

                }else{

                    val toast =  Toast.makeText(activity,"Incorrect mobile number!",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()

                }

            }else{
                val toast = Toast.makeText(activity,"Please enter mobile number!",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }


        }
        return enterMobileNumberFragmentBinding.root
    }

    fun sendOTP(){

        enterMobileNumberFragmentBinding.progressBar.isIndeterminate = true
        enterMobileNumberFragmentBinding.progressBar.visibility = View.VISIBLE
        enterMobileNumberFragmentBinding.progressBar.setClickable(false)

        val mobile = enterMobileNumberFragmentBinding.etMobileNumber.text.toString()

        val user_id = AppController.getSharedPref().getString("user_id","null")

        apiService.sendOTP(mobile,user_id).enqueue(object : Callback<Responce.SendOtpResponce> {
            override fun onResponse(call: Call<Responce.SendOtpResponce>, response: Response<Responce.SendOtpResponce>) {
                if (response.body().getStatus()) {

                    AppController.getSharedPref().edit().putString("mobile_no",mobile).apply()

                    enterMobileNumberFragmentBinding.etMobileNumber.setText("")
                    enterMobileNumberFragmentBinding.progressBar.visibility = View.GONE
                    enterMobileNumberFragmentBinding.progressBar.isIndeterminate = false
                    val toast =  Toast.makeText(activity,response.body().getMessage(),Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    PicsLootActivity.picsLootActivity!!.hideSoftKeyboard()
                    PicsLootActivity.picsLootActivity!!.replaceFragment(OtpVerificationFragment.newInstance())

                }else{

                    enterMobileNumberFragmentBinding.progressBar.visibility = View.GONE
                    enterMobileNumberFragmentBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(activity,response.body().getMessage(),Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()

                }
            }
            override fun onFailure(call: Call<Responce.SendOtpResponce>, t: Throwable) {
                enterMobileNumberFragmentBinding.progressBar.visibility = View.GONE
                enterMobileNumberFragmentBinding.progressBar.isIndeterminate = false
                t.printStackTrace()
              /*  val toast = Toast.makeText(activity,""+t,Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()*/
            }
        })

}
    fun checkMobileNo(mobile: String): Boolean {
        return Pattern.matches("^[7-9][0-9]{9}$", mobile)
    }
    override fun onAttach(context: Context?) {
        (activity!!.application as AppController).getComponent().inject(this@EnterMobileNumberFragment)
        super.onAttach(context)
    }

}