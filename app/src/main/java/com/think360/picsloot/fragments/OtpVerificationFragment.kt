package com.think360.pro.healthguru.registration.fragments

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.think360.picsloot.R
import com.think360.picsloot.databinding.OtpVerificationFragmentBinding
import android.databinding.adapters.TextViewBindingAdapter.setText
import android.os.CountDownTimer
import android.view.Gravity
import com.think360.picsloot.activities.LoginActivity
import com.think360.picsloot.activities.PicsLootActivity
import com.think360.picsloot.api.AppController
import com.think360.picsloot.api.data.Responce
import com.think360.picsloot.api.interfaces.ApiService
import com.think360.picsloot.fragments.ConfirmYourOrderFragment
import com.think360.picsloot.fragments.EnterMobileNumberFragment
import com.think360.picsloot.fragments.ProfileSaveUploadFragment
import com.think360.picsloot.fragments.UploadImagesForPrintFragment
import com.think360.picsloot.util.ConnectivityReceiver
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


/**
 * Created by think360 on 24/07/17.
 */


class OtpVerificationFragment : Fragment() {

    @Inject
    internal lateinit var apiService: ApiService

    private lateinit var   otpVerificationFragmentBinding : OtpVerificationFragmentBinding

    companion object {

        fun newInstance(): OtpVerificationFragment {
            return OtpVerificationFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        otpVerificationFragmentBinding   = DataBindingUtil.inflate<OtpVerificationFragmentBinding>(inflater, R.layout.otp_verification_fragment, container, false)

        countDownTimer()

        val generateOtp = SpannableString("Generate OTP again")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {

                if(ConnectivityReceiver.isConnected()){
                    generateOtpAgain()
                }else{
                    val toast =  Toast.makeText(PicsLootActivity.picsLootActivity,"No internet connection!",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        generateOtp.setSpan(clickableSpan, 0, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val fcs = ForegroundColorSpan(Color.rgb(193, 20, 43))
        generateOtp.setSpan(fcs, 0, 18, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        //For UnderLine
        generateOtp.setSpan(UnderlineSpan(), 0, 18, 0)
        otpVerificationFragmentBinding.tvGenerateOtp.setText(generateOtp)
        otpVerificationFragmentBinding.tvGenerateOtp.setMovementMethod(LinkMovementMethod.getInstance())
        otpVerificationFragmentBinding.tvGenerateOtp.setHighlightColor(Color.TRANSPARENT)

        otpVerificationFragmentBinding.btnNext.setOnClickListener {

            if(otpVerificationFragmentBinding.otpView.hasValidOTP()){

                if(ConnectivityReceiver.isConnected()){
                    verifyOTP()
                }else{
                    val toast =  Toast.makeText(PicsLootActivity.picsLootActivity,"No internet connection!",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }else{
                val toast =  Toast.makeText(PicsLootActivity.picsLootActivity,"OTP is incorrect!",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }

        }

        return otpVerificationFragmentBinding.root
    }

fun verifyOTP(){


    otpVerificationFragmentBinding.progressBar.isIndeterminate = true
    otpVerificationFragmentBinding.progressBar.visibility = View.VISIBLE
    otpVerificationFragmentBinding.progressBar.setClickable(false)

    val user_id =  AppController.getSharedPref().getString("user_id","null")
    val otp = otpVerificationFragmentBinding.otpView.getOTP()


        apiService.verifyOtp(user_id,otp).enqueue(object : Callback<Responce.VerifyOtpResponce> {
            override fun onResponse(call: Call<Responce.VerifyOtpResponce>, response: Response<Responce.VerifyOtpResponce>) {
                if (response.body().getStatus()) {

                    AppController.getSharedPref().edit().putString("mobile_verify","1").apply()
                    otpVerificationFragmentBinding.otpView.setOTP("")
                    otpVerificationFragmentBinding.progressBar.visibility = View.GONE
                    otpVerificationFragmentBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(activity,response.body().getMessage(),Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()

                    PicsLootActivity.picsLootActivity!!.hideSoftKeyboard()
                    PicsLootActivity.picsLootActivity!!.replaceFragment(ConfirmYourOrderFragment.newInstance())



                }else{
                    otpVerificationFragmentBinding.progressBar.visibility = View.GONE
                    otpVerificationFragmentBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(activity,response.body().getMessage(),Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.VerifyOtpResponce>, t: Throwable) {
                otpVerificationFragmentBinding.progressBar.visibility = View.GONE
                otpVerificationFragmentBinding.progressBar.isIndeterminate = false
                t.printStackTrace()
             /*   val toast = Toast.makeText(activity,""+t,Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()*/
            }
        })

}

    fun countDownTimer(){

    object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            otpVerificationFragmentBinding.llValidFor.visibility = View.VISIBLE
            otpVerificationFragmentBinding.tvGenerateOtp.visibility = View.GONE

            otpVerificationFragmentBinding.tvSecons.setText(""+millisUntilFinished / 1000)
        }
        override fun onFinish() {
            otpVerificationFragmentBinding.tvGenerateOtp.visibility = View.VISIBLE
            otpVerificationFragmentBinding.tvSecons.setText("0")
            otpVerificationFragmentBinding.llValidFor.visibility = View.GONE

        }
    }.start()
}
    override fun onAttach(context: Context?) {
        (activity!!.application as AppController).getComponent().inject(this@OtpVerificationFragment)
        super.onAttach(context)
    }

    fun generateOtpAgain(){

        otpVerificationFragmentBinding.progressBar.isIndeterminate = true
        otpVerificationFragmentBinding.progressBar.visibility = View.VISIBLE
        otpVerificationFragmentBinding.progressBar.setClickable(false)

        val user_id = AppController.getSharedPref().getString("user_id","null")

        apiService.resendOtp(user_id).enqueue(object : Callback<Responce.ResendOtpResponce> {
            override fun onResponse(call: Call<Responce.ResendOtpResponce>, response: Response<Responce.ResendOtpResponce>) {
                if (response.body().getStatus()) {

                    otpVerificationFragmentBinding.progressBar.visibility = View.GONE
                    otpVerificationFragmentBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(activity,response.body().getMessage(),Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    countDownTimer()


                }else{
                    otpVerificationFragmentBinding.progressBar.visibility = View.GONE
                    otpVerificationFragmentBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(activity,response.body().getMessage(),Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.ResendOtpResponce>, t: Throwable) {
                otpVerificationFragmentBinding.progressBar.visibility = View.GONE
                otpVerificationFragmentBinding.progressBar.isIndeterminate = false
                t.printStackTrace()
    /*            val toast = Toast.makeText(activity,""+t,Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()*/
            }
        })

    }



}
