package com.think360.picsloot.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.rafakob.drawme.DrawMeButton
import com.squareup.picasso.Picasso
import com.think360.picsloot.R
import com.think360.picsloot.R.attr.otp
import com.think360.picsloot.R.id.*
import com.think360.picsloot.activities.LoginActivity
import com.think360.picsloot.activities.PicsLootActivity
import com.think360.picsloot.api.AppController
import com.think360.picsloot.api.EventToRefresh
import com.think360.picsloot.api.data.Responce
import com.think360.picsloot.api.interfaces.ApiService
import com.think360.picsloot.databinding.ProfileFragmentBinding
import com.think360.picsloot.imagepicker.SImagePicker
import com.think360.picsloot.util.ConnectivityReceiver
import com.think360.pro.healthguru.registration.fragments.OtpVerificationFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import me.shaohui.advancedluban.Luban
import me.shaohui.advancedluban.OnMultiCompressListener
import net.yazeed44.imagepicker.model.ImageEntry
import net.yazeed44.imagepicker.ui.PickerActivity
import net.yazeed44.imagepicker.util.Picker
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.ArrayList
import javax.inject.Inject


/**
 * Created by think360 on 23/10/17.
 */
class ProfileFragment : Fragment() {
    @Inject
    internal lateinit var apiService: ApiService
    private var image_url: String= ""

    private lateinit var profileFragmentBinding: ProfileFragmentBinding
    private val compositeDisposable = CompositeDisposable()
    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       profileFragmentBinding = DataBindingUtil.inflate<ProfileFragmentBinding>(inflater, R.layout.profile_fragment, container, false)
       showProfile()
        compositeDisposable.add((activity!!.application as AppController).bus().toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { o ->
            if (o is EventToRefresh && o.body ==navigation_profile ) {
                showProfile()

            }
        })

        profileFragmentBinding.ivProfileImageEdit.setOnClickListener {

            if(ConnectivityReceiver.isConnected()){
                getGallaryCamraImage()
            }else{
                val toast =  Toast.makeText(activity,"No internet connection!",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }

        }
        profileFragmentBinding.btnEdit.setOnClickListener {

               if(ConnectivityReceiver.isConnected()){
                   PicsLootActivity.picsLootActivity!!.replaceFragment(EditDeliveryAddressFragment.newInstance())
               }else{
                   val toast =  Toast.makeText(activity,"No internet connection!",Toast.LENGTH_SHORT)
                   toast.setGravity(Gravity.CENTER, 0, 0)
                   toast.show()
               }


        }

        profileFragmentBinding.btnLogout.setOnClickListener {

            AppController.getSharedPref().edit().putBoolean("is_login",false).apply()
            AppController.getSharedPref().edit().putString("user_id","null").apply()

            AppController.getSharedPref().edit().putString("form","null").apply()
            AppController.getSharedPref().edit().putString("social_id","null").apply()
            AppController.getSharedPref().edit().putString("name","null").apply()
            AppController.getSharedPref().edit().putString("email","null").apply()
            AppController.getSharedPref().edit().putString("image_url","null").apply()



            startActivity(Intent(activity!!.applicationContext, LoginActivity::class.java))
            activity!!. finish()
        }
        profileFragmentBinding.btnSaveProfile.setOnClickListener {

            if(ConnectivityReceiver.isConnected()){
                saveEditProfile()
            }else{
                val toast =  Toast.makeText(activity,"No internet connection!",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }

        }
        profileFragmentBinding.btnDelete.setOnClickListener {
            deleteDeliveryAddAlertDialog()
        }
        profileFragmentBinding.etMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

                if(p0!!.length==10){
                    profileFragmentBinding.btnVerify.visibility = View.VISIBLE

                }else{
                    profileFragmentBinding.btnVerify.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        }


        )
        profileFragmentBinding.btnVerify.setOnClickListener {
            if(ConnectivityReceiver.isConnected()){
                sendOTP()
            }else{
                val toast =  Toast.makeText(activity,"No internet connection!",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }

        }
        return profileFragmentBinding.root
    }
fun getGallaryCamraImage(){

    profileFragmentBinding.progressBar2.isIndeterminate = true
    profileFragmentBinding.progressBar2.visibility = View.GONE
    profileFragmentBinding.progressBar2.setClickable(false)

    Picker.Builder(PicsLootActivity.picsLootActivity!!, object : Picker.PickListener {
        override fun onCancel() {
            profileFragmentBinding.progressBar2.setVisibility(View.GONE)
        }

        override fun onPickedSuccessfully(images: ArrayList<ImageEntry>?) {
            Luban.compress(activity!!.applicationContext, File(images!!.get(0).path))
                    .putGear(Luban.THIRD_GEAR)
                    .launch(object : OnMultiCompressListener {
                        override fun onStart() {}
                        override fun onSuccess(fileList: List<File>) {
                            uploadimage(prepareFilePart("profile_pic",fileList.get(0)))

                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                        }
                    })
        }
    }, R.style.MIP_theme)
            .setPickMode(Picker.PickMode.SINGLE_IMAGE)
            .setLimit(1)
            .build().startActivity()

}
   fun uploadimage(prepareFilePart: MultipartBody.Part) {



       apiService.profilePicUpload(prepareFilePart).enqueue(object : Callback<Responce.UploadProfilePicResponce> {
            override fun onResponse(call: Call<Responce.UploadProfilePicResponce>, response: Response<Responce.UploadProfilePicResponce>) {
                if (response.body().getStatus()) {
                    Picasso.with(activity)
                            .load(response.body().getImage_url())
                            .resize(150, 150)
                            .centerCrop()
                            .into(profileFragmentBinding.cIvProfilePic)
                    image_url = response.body().getImage_url()
                    profileFragmentBinding.progressBar.visibility = View.GONE
                    profileFragmentBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()


                }else{
                    image_url = response.body().getImage_url()
                    profileFragmentBinding.progressBar.visibility = View.GONE
                    profileFragmentBinding.progressBar.isIndeterminate = false

                  val toast =  Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.UploadProfilePicResponce>, t: Throwable) {

                profileFragmentBinding.progressBar.visibility = View.GONE
                profileFragmentBinding.progressBar.isIndeterminate = false
                t.printStackTrace()
           /*     val toast = Toast.makeText(activity,""+t, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()*/
            }
        })
    }

    private fun prepareFilePart(param : String,file: File): MultipartBody.Part{
        return MultipartBody.Part.createFormData(param, file.getName(), RequestBody.create(MediaType.parse("image*//*"), file))
    }
    fun showProfile(){

        profileFragmentBinding.progressBar.isIndeterminate = true
        profileFragmentBinding.progressBar.visibility = View.VISIBLE
        profileFragmentBinding.progressBar.setClickable(false)

        val user_id =  AppController.getSharedPref().getString("user_id","null")


        apiService.showProfile(user_id).enqueue(object : Callback<Responce.ShowProfileResponce> {
            override fun onResponse(call: Call<Responce.ShowProfileResponce>, response: Response<Responce.ShowProfileResponce>) {
                if (response.body().getStatus()) {
                    profileFragmentBinding.progressBar.visibility = View.GONE
                    profileFragmentBinding.progressBar.isIndeterminate = false
                    if(response.body().getData().getProfile_image().equals("")){

                        Glide.with(AppController.getAppContext()).load(Uri.parse("android.resource://com.think360.picsloot/drawable/no_img"))
                                .error(R.drawable.no_img).dontAnimate().into(profileFragmentBinding.cIvProfilePic)

                    }else{
                      val image =  response.body().getData().getProfile_image() as String
                     Glide.with(AppController.getAppContext()).load(Uri.parse(image))
                                .error(R.drawable.no_img).dontAnimate().into(profileFragmentBinding.cIvProfilePic)


                    }
                    profileFragmentBinding. etName.setText(response.body().getData().getName())
                    profileFragmentBinding. etEmail.setText(response.body().getData().getEmail())
                    profileFragmentBinding. etMobileNumber.setText(response.body().getData().getMobile_no())

                    AppController.getSharedPref().edit().putString("mobile_no",response.body().getData().getMobile_no()).apply()


                    if(!TextUtils.isEmpty(response.body().getData().getMobile_verify())){
                        AppController.getSharedPref().edit().putString("mobile_verify",response.body().getData().getMobile_verify()).apply()
                    }else{
                        AppController.getSharedPref().edit().putString("mobile_verify","").apply()
                    }

                   if(!TextUtils.isEmpty(response.body().getData().getMobile_verify())){

                       if(TextUtils.isEmpty(response.body().getData().getMobile_verify()) || response.body().getData().getMobile_verify().equals("0")){
                           profileFragmentBinding.btnVerify.visibility = View.VISIBLE
                       }else{
                           profileFragmentBinding.btnVerify.visibility = View.GONE
                       }
                   }






                }else{

                    profileFragmentBinding.progressBar.visibility = View.GONE
                    profileFragmentBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.ShowProfileResponce>, t: Throwable) {

                profileFragmentBinding.progressBar.visibility = View.GONE
                profileFragmentBinding.progressBar.isIndeterminate = false
                t.printStackTrace()
             /*   val toast = Toast.makeText(activity,""+t, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()*/
            }
        })
    }
    fun saveEditProfile(){

        val user_id =  AppController.getSharedPref().getString("user_id","null")


        profileFragmentBinding.progressBar.isIndeterminate = true
        profileFragmentBinding.progressBar.visibility = View.VISIBLE
        profileFragmentBinding.progressBar.setClickable(false)

        apiService.saveProfile(user_id,profileFragmentBinding. etName.text.toString(),profileFragmentBinding. etEmail.text.toString(),profileFragmentBinding. etMobileNumber.text.toString(),image_url).enqueue(object : Callback<Responce.SaveProfileResponce> {
            override fun onResponse(call: Call<Responce.SaveProfileResponce>, response: Response<Responce.SaveProfileResponce>) {
                if (response.body().getStatus()) {
                    profileFragmentBinding.progressBar.visibility = View.GONE
                    profileFragmentBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()




                }else{

                    profileFragmentBinding.progressBar.visibility = View.GONE
                    profileFragmentBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.SaveProfileResponce>, t: Throwable) {

                profileFragmentBinding.progressBar.visibility = View.GONE
                profileFragmentBinding.progressBar.isIndeterminate = false
                t.printStackTrace()
             /*   val toast = Toast.makeText(activity,""+t, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()*/
            }
        })
    }
    fun deleteDeliveryAddress(){
        profileFragmentBinding.progressBar.isIndeterminate = true
        profileFragmentBinding.progressBar.visibility = View.VISIBLE
        profileFragmentBinding.progressBar.setClickable(false)

        val user_id =  AppController.getSharedPref().getString("user_id","null")


        apiService.adeleteDeliveryAddress(user_id).enqueue(object : Callback<Responce.EditDeliveryAddResponce> {
            override fun onResponse(call: Call<Responce.EditDeliveryAddResponce>, response: Response<Responce.EditDeliveryAddResponce>) {
                if (response.body().getStatus()) {

                    profileFragmentBinding.progressBar.visibility = View.GONE
                    profileFragmentBinding.progressBar.isIndeterminate = false




                    val toast =  Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()

                    PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.flBack.visibility = View.GONE
                    PicsLootActivity.picsLootActivity!!.onBackPressed()



                }else{

                    profileFragmentBinding.progressBar.visibility = View.GONE
                    profileFragmentBinding.progressBar.isIndeterminate = false

                 val toast =  Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_SHORT)
                   toast.setGravity(Gravity.CENTER, 0, 0)
                   toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.EditDeliveryAddResponce>, t: Throwable) {

                profileFragmentBinding.progressBar.visibility = View.GONE
                profileFragmentBinding.progressBar.isIndeterminate = false
                t.printStackTrace()
              //  val toast = Toast.makeText(activity,""+t, Toast.LENGTH_SHORT)
               // toast.setGravity(Gravity.CENTER, 0, 0)
              //  toast.show()
            }
        })
    }
    private fun deleteDeliveryAddAlertDialog() {
        val dialog =  Dialog(PicsLootActivity.picsLootActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_alert)
        //val tvMsg = dialog.findViewById<TextView>(R.id.tvMsg)
      //  tvMsg.setText("Are you sure to Push Threat Alert?")
        val btnNo = dialog.findViewById<DrawMeButton>(R.id.btnNo)

         btnNo.setOnClickListener { dialog.dismiss() }

        val btnYes = dialog.findViewById<DrawMeButton>(R.id.btnYes)
        btnYes.setOnClickListener {

            if(ConnectivityReceiver.isConnected()){
                dialog.dismiss()
                deleteDeliveryAddress()
            }else{
                val toast =  Toast.makeText(activity,"No internet connection!",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }

        }
        dialog.show()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        ( context.applicationContext as AppController).getComponent().inject(this@ProfileFragment)
    }
    fun verifyCodeDialog(){
        val dialog =  Dialog(PicsLootActivity.picsLootActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_verify_mobile_no)
        val etCode = dialog.findViewById<View>(R.id.etCode) as EditText

        val btnCancel = dialog.findViewById<DrawMeButton>(R.id.btnCancel) as DrawMeButton
        btnCancel.setOnClickListener { dialog.dismiss() }

        val btnDone = dialog.findViewById<DrawMeButton>(R.id.btnDone)
        btnDone.setOnClickListener {
            // Done API
          if(ConnectivityReceiver.isConnected()){

           val code =  etCode.text.toString()
            if(!TextUtils.isEmpty(code)){
              verifyOTP(dialog,code)
              }else{
                 val toast =  Toast.makeText(activity,"Please enter verification code!",Toast.LENGTH_SHORT)
                 toast.setGravity(Gravity.CENTER, 0, 0)
                  toast.show()
                  }
           }else{
              val toast =  Toast.makeText(activity,"No internet connection!",Toast.LENGTH_SHORT)
              toast.setGravity(Gravity.CENTER, 0, 0)
              toast.show()
            }

        }
        val btnResend = dialog.findViewById<DrawMeButton>(R.id.btnResend)
        btnResend.setOnClickListener {
            //Hit Resend API
            if(ConnectivityReceiver.isConnected()){
                generateOtpAgain()
            }else{
                val toast =  Toast.makeText(activity,"No internet connection!",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }

        }
        dialog.show()
    }
    fun verifyOTP(dialog :Dialog,otp : String){

        profileFragmentBinding.progressBar.visibility = View.VISIBLE
        profileFragmentBinding.progressBar.setClickable(false)

        val user_id =  AppController.getSharedPref().getString("user_id","null")

        apiService.verifyOtp(user_id,otp).enqueue(object : Callback<Responce.VerifyOtpResponce> {
            override fun onResponse(call: Call<Responce.VerifyOtpResponce>, response: Response<Responce.VerifyOtpResponce>) {
                if (response.body().getStatus()) {

                    dialog.dismiss()
                    profileFragmentBinding.btnVerify.visibility = View.GONE
                    profileFragmentBinding.progressBar.visibility = View.GONE

                    val toast =  Toast.makeText(activity,response.body().getMessage(),Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()

                }else {
                    profileFragmentBinding.progressBar.visibility = View.GONE

                    val toast =  Toast.makeText(activity,response.body().getMessage(),Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()


                }
            }
            override fun onFailure(call: Call<Responce.VerifyOtpResponce>, t: Throwable) {
                profileFragmentBinding.progressBar.visibility = View.GONE

                t.printStackTrace()
                /*   val toast = Toast.makeText(activity,""+t,Toast.LENGTH_SHORT)
                   toast.setGravity(Gravity.CENTER, 0, 0)
                   toast.show()*/
            }
        })

    }
    fun generateOtpAgain(){

        profileFragmentBinding.progressBar.visibility = View.VISIBLE

       val user_id = AppController.getSharedPref().getString("user_id","null")

        apiService.resendOtp(user_id).enqueue(object : Callback<Responce.ResendOtpResponce> {
            override fun onResponse(call: Call<Responce.ResendOtpResponce>, response: Response<Responce.ResendOtpResponce>) {
                if (response.body().getStatus()) {

                    profileFragmentBinding.progressBar.visibility = View.GONE


                    val toast =  Toast.makeText(activity,response.body().getMessage(),Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()



                }else{
                    profileFragmentBinding.progressBar.visibility = View.GONE


                    val toast =  Toast.makeText(activity,response.body().getMessage(),Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.ResendOtpResponce>, t: Throwable) {
                profileFragmentBinding.progressBar.visibility = View.GONE

                t.printStackTrace()
                /*            val toast = Toast.makeText(activity,""+t,Toast.LENGTH_SHORT)
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()*/
            }
        })

    }
    fun sendOTP(){

        profileFragmentBinding.progressBar.isIndeterminate = true
        profileFragmentBinding.progressBar.visibility = View.VISIBLE
        profileFragmentBinding.progressBar.setClickable(false)

        val mobile = profileFragmentBinding.etMobileNumber.text.toString()
        val user_id = AppController.getSharedPref().getString("user_id","null")

        apiService.sendOTP(mobile,user_id).enqueue(object : Callback<Responce.SendOtpResponce> {
            override fun onResponse(call: Call<Responce.SendOtpResponce>, response: Response<Responce.SendOtpResponce>) {
                if (response.body().getStatus()) {

                    AppController.getSharedPref().edit().putString("mobile_no",mobile).apply()

                   // profileFragmentBinding.etMobileNumber.setText("")
                    profileFragmentBinding.progressBar.visibility = View.GONE
                    profileFragmentBinding.progressBar.isIndeterminate = false
                    val toast =  Toast.makeText(activity,response.body().getMessage(),Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    PicsLootActivity.picsLootActivity!!.hideSoftKeyboard()
                    verifyCodeDialog()

                }else{

                    profileFragmentBinding.progressBar.visibility = View.GONE
                    profileFragmentBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(activity,response.body().getMessage(),Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()

                }
            }
            override fun onFailure(call: Call<Responce.SendOtpResponce>, t: Throwable) {
                profileFragmentBinding.progressBar.visibility = View.GONE
                profileFragmentBinding.progressBar.isIndeterminate = false
                t.printStackTrace()
                /*  val toast = Toast.makeText(activity,""+t,Toast.LENGTH_SHORT)
                  toast.setGravity(Gravity.CENTER, 0, 0)
                  toast.show()*/
            }
        })

    }
}