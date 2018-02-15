package com.think360.picsloot.fragments
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.think360.picsloot.R
import com.think360.picsloot.activities.LoginActivity
import com.think360.picsloot.activities.PicsLootActivity
import com.think360.picsloot.api.AppController
import com.think360.picsloot.api.data.Responce
import com.think360.picsloot.api.interfaces.ApiService
import com.think360.picsloot.databinding.ProfileSaveAndUploadFragmentBinding
import com.think360.picsloot.util.ConnectivityReceiver
import me.shaohui.advancedluban.Luban
import me.shaohui.advancedluban.OnMultiCompressListener
import net.yazeed44.imagepicker.model.ImageEntry
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
 * Created by think360 on 17/10/17.
 */
class ProfileSaveUploadFragment : Fragment() {

    @Inject
    internal lateinit var apiService: ApiService

    private var image_url: String= "null"
    private var social_id = "null"
    private lateinit var   profileSaveAndUploadFragmentBinding : ProfileSaveAndUploadFragmentBinding

    companion object {
        fun newInstance(): ProfileSaveUploadFragment {
            return ProfileSaveUploadFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        profileSaveAndUploadFragmentBinding   = DataBindingUtil.inflate<ProfileSaveAndUploadFragmentBinding>(inflater, R.layout.profile_save_and_upload_fragment, container, false)

        if(!AppController.getSharedPref().getString("social_id","null").equals("null")){
            social_id = AppController.getSharedPref().getString("social_id","null")
            profileSaveAndUploadFragmentBinding.etName.setText(AppController.getSharedPref().getString("name","null"))
            profileSaveAndUploadFragmentBinding.etEmail.setText(AppController.getSharedPref().getString("email","null"))
            image_url = AppController.getSharedPref().getString("image_url","null")
            if(!image_url.equals("null")){
                Picasso.with(LoginActivity.loginActivity)
                        .load(AppController.getSharedPref().getString("image_url","null"))
                        .resize(150, 150)
                        .centerCrop()
                        .into(profileSaveAndUploadFragmentBinding.cIvProfilePic)
            }else{
                Picasso.with(LoginActivity.loginActivity)
                        .load("android.resource://com.think360.picsloot/drawable/user")
                        .resize(150, 150)
                        .centerCrop()
                        .into(profileSaveAndUploadFragmentBinding.cIvProfilePic)
            }

          }else{
            social_id="null"
        }

        val s_text_login = SpannableString("I have understood the terms & conditions")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                if(!profileSaveAndUploadFragmentBinding.chTc.isChecked)
                showTermsConditions()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        s_text_login.setSpan(clickableSpan, 22, 40, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val fcs = ForegroundColorSpan(Color.rgb(193, 20, 43))
        s_text_login.setSpan(fcs, 22, 40, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        //For UnderLine
        s_text_login.setSpan(UnderlineSpan(), 22, 40, 0)
        profileSaveAndUploadFragmentBinding.chTc.setText(s_text_login)
        profileSaveAndUploadFragmentBinding.chTc.setMovementMethod(LinkMovementMethod.getInstance())
        profileSaveAndUploadFragmentBinding.chTc.setHighlightColor(Color.TRANSPARENT)
        profileSaveAndUploadFragmentBinding.ivProfileImageEdit.setOnClickListener {

            if(ConnectivityReceiver.isConnected()){

                profileSaveAndUploadFragmentBinding.  progressBar2.isIndeterminate = true
                profileSaveAndUploadFragmentBinding. progressBar2.visibility = View.VISIBLE
                profileSaveAndUploadFragmentBinding. progressBar2.setClickable(false)

                Picker.Builder(LoginActivity.loginActivity!!, object : Picker.PickListener {
                    override fun onCancel() {
                        profileSaveAndUploadFragmentBinding.progressBar2.setVisibility(View.GONE)
                    }
                    override fun onPickedSuccessfully(images: ArrayList<ImageEntry>?) {
                        Luban.compress(LoginActivity.loginActivity!!, File(images!!.get(0).path))
                                .putGear(Luban.THIRD_GEAR)
                                .launch(object : OnMultiCompressListener {
                                    override fun onStart() {}
                                    override fun onSuccess(fileList: List<File>) {
                                        uploadimage(prepareFilePart("profile_pic",fileList.get(0)))
                                    }
                                    override fun onError(e: Throwable) { e.printStackTrace() }
                                })
                    } }
                        , R.style.MIP_theme)
                        .setPickMode(Picker.PickMode.SINGLE_IMAGE)
                        .setLimit(1)
                        .build()
                        .startActivity()
            }else{
                val toast =  Toast.makeText(activity,"No internet connection!",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }

        }
        profileSaveAndUploadFragmentBinding.btnSaveUpload.setOnClickListener {
            if(!TextUtils.isEmpty(profileSaveAndUploadFragmentBinding.etName.text)  ){
                if(!TextUtils.isEmpty(profileSaveAndUploadFragmentBinding.etEmail.text)){

                    if( !image_url .equals("null")){
                        if( profileSaveAndUploadFragmentBinding.chTc.isChecked){

                          if(ConnectivityReceiver.isConnected()){
                              registrationComple()
                          }else{
                              val toast =  Toast.makeText(activity,"No internet connection!",Toast.LENGTH_SHORT)
                              toast.setGravity(Gravity.CENTER, 0, 0)
                              toast.show()
                          }

                        }else{
                            val toast =  Toast.makeText(activity,"Please check terms & conditions!",Toast.LENGTH_SHORT)
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                        }


                    }else{
                        val toast =  Toast.makeText(activity,"Please select profile pic!",Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }
                }else{
                    val toast =  Toast.makeText(activity,"Please enter email!",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }else{
                val toast =  Toast.makeText(activity,"Please enter name!",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }


        }
        return profileSaveAndUploadFragmentBinding.root
    }

 fun registrationComple(){

     profileSaveAndUploadFragmentBinding.progressBar.isIndeterminate = true
     profileSaveAndUploadFragmentBinding.progressBar.visibility = View.VISIBLE
     profileSaveAndUploadFragmentBinding.progressBar.setClickable(false)
   //  val mobile_no = AppController.getSharedPref().getString("mobile_no","null")

     val name =  profileSaveAndUploadFragmentBinding.etName.text.toString()
     val email =  profileSaveAndUploadFragmentBinding.etEmail.text.toString()

     val device_type = "android"
     val device_id = AppController.getSharedPref().getString("firebase_reg_token","null")

     apiService.registration(name,email,AppController.getSharedPref().getString("form","null"),social_id,device_type,
             device_id,image_url).enqueue(object : Callback<Responce.RegCompleteResponce> {
         override fun onResponse(call: Call<Responce.RegCompleteResponce>, response: Response<Responce.RegCompleteResponce>) {
             if (response.body().getStatus()) {

                 profileSaveAndUploadFragmentBinding.progressBar.visibility = View.GONE
                 profileSaveAndUploadFragmentBinding.progressBar.isIndeterminate = false


                 AppController.getSharedPref().edit().putBoolean("is_login",true).apply()
                 AppController.getSharedPref().edit().putString("user_id",response.body().getData().getUser_id()).apply()

                 AppController.getSharedPref().edit().putString("mobile_no",response.body().getData().getMobile_no()).apply()

                 if(!TextUtils.isEmpty(response.body().getData().getMobile_verify())){
                     AppController.getSharedPref().edit().putString("mobile_verify",response.body().getData().getMobile_verify()).apply()
                 }else{
                     AppController.getSharedPref().edit().putString("mobile_verify","").apply()
                 }

                 val intent = Intent(activity, PicsLootActivity::class.java)
                 //only for welcome dialog
                 intent.putExtra("dialog","dialogWelcome")
                 startActivity(intent)
                 activity!!.finish()

             }else{

                 AppController.getSharedPref().edit().putBoolean("is_login",false).apply()
                 AppController.getSharedPref().edit().putString("user_id","").apply()

                 AppController.getSharedPref().edit().putString("mobile_no","").apply()
                 AppController.getSharedPref().edit().putString("mobile_verify","").apply()

                 profileSaveAndUploadFragmentBinding.progressBar.visibility = View.GONE
                 profileSaveAndUploadFragmentBinding.progressBar.isIndeterminate = false

                 val toast =  Toast.makeText(activity,response.body().getMessage(),Toast.LENGTH_SHORT)
                 toast.setGravity(Gravity.CENTER, 0, 0)
                 toast.show()
             }

         }
         override fun onFailure(call: Call<Responce.RegCompleteResponce>, t: Throwable) {
             AppController.getSharedPref().edit().putBoolean("is_login",false).apply()
             AppController.getSharedPref().edit().putString("user_id","").apply()

             AppController.getSharedPref().edit().putString("mobile_no","").apply()
             AppController.getSharedPref().edit().putString("mobile_verify","").apply()

             profileSaveAndUploadFragmentBinding.progressBar.visibility = View.GONE
             profileSaveAndUploadFragmentBinding.progressBar.isIndeterminate = false
             t.printStackTrace()
          /*   val toast = Toast.makeText(activity,""+t,Toast.LENGTH_SHORT)
             toast.setGravity(Gravity.CENTER, 0, 0)
             toast.show()*/
         }
     })
 }
    fun uploadimage(prepareFilePart: MultipartBody.Part) {

        profileSaveAndUploadFragmentBinding.progressBar.isIndeterminate = true
        profileSaveAndUploadFragmentBinding.progressBar.visibility = View.VISIBLE
        profileSaveAndUploadFragmentBinding.progressBar.setClickable(false)

      //  val mobile_no = AppController.getSharedPref().getString("mobile_no","null")

     //   val mobile_no_part = RequestBody.create(MediaType.parse("text/plain"), mobile_no)




        apiService.profilePicUpload(prepareFilePart).enqueue(object : Callback<Responce.UploadProfilePicResponce> {
            override fun onResponse(call: Call<Responce.UploadProfilePicResponce>, response: Response<Responce.UploadProfilePicResponce>) {
                if (response.body().getStatus()) {

                    if(!TextUtils.isEmpty(response.body().getImage_url())){
                        image_url = response.body().getImage_url()
                        Picasso.with(LoginActivity.loginActivity)
                                .load(response.body().getImage_url())
                                .resize(150, 150)
                                .centerCrop()
                                .into(profileSaveAndUploadFragmentBinding.cIvProfilePic)
                    }else{
                        image_url = "null"
                        Picasso.with(LoginActivity.loginActivity)
                                .load("android.resource://com.think360.picsloot/drawable/user")
                                .resize(150, 150)
                                .centerCrop()
                                .into(profileSaveAndUploadFragmentBinding.cIvProfilePic)
                    }


                    profileSaveAndUploadFragmentBinding.progressBar.visibility = View.GONE
                    profileSaveAndUploadFragmentBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(LoginActivity.loginActivity,response.body().getMessage(),Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()


                }else{
                    image_url = "null"
                    profileSaveAndUploadFragmentBinding.progressBar.visibility = View.GONE
                    profileSaveAndUploadFragmentBinding.progressBar.isIndeterminate = false

                    val toast =  Toast.makeText(LoginActivity.loginActivity,response.body().getMessage(),Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.UploadProfilePicResponce>, t: Throwable) {
                image_url = "null"
                profileSaveAndUploadFragmentBinding.progressBar.visibility = View.GONE
                profileSaveAndUploadFragmentBinding.progressBar.isIndeterminate = false
                t.printStackTrace()
          /*      val toast = Toast.makeText(activity,""+t,Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()*/
            }
        })
    }
    private fun showTermsConditions() {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY


        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_terms_conditions)
        val window = dialog.window
        val wlp = window!!.attributes

        wlp.gravity = Gravity.CENTER
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        window.attributes = wlp
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val close = dialog.findViewById<ImageView>(R.id.ivCancel)
        close.setOnClickListener { dialog.dismiss() }

        dialog.show()

    }
    private fun prepareFilePart(param : String,file: File): MultipartBody.Part{
        return MultipartBody.Part.createFormData(param, file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
    }
    override fun onAttach(context: Context?) {
        (activity!!.application as AppController).getComponent().inject(this@ProfileSaveUploadFragment)
        super.onAttach(context)
    }
}