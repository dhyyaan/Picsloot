package com.think360.picsloot.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.think360.picsloot.R
import com.think360.picsloot.api.AppController
import com.think360.picsloot.api.data.Responce
import com.think360.picsloot.api.interfaces.ApiService
import com.think360.picsloot.databinding.LoginActivityBinding
import com.think360.picsloot.fragments.ProfileSaveUploadFragment
import com.think360.picsloot.util.ConnectivityReceiver
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.inject.Inject


class LoginActivity : RuntimePermissionsActivity(), GoogleApiClient.OnConnectionFailedListener {

    @Inject
    internal lateinit var apiService: ApiService
    private lateinit var loginActivityBinding: LoginActivityBinding
    private lateinit var callbackManager : CallbackManager
    private lateinit var mGoogleApiClient : GoogleApiClient
    private val RC_SIGN_IN = 200


    companion object {
        var loginActivity : LoginActivity? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginActivityBinding = DataBindingUtil.setContentView<LoginActivityBinding>(this, R.layout.login_activity)
        (application as AppController).getComponent().inject(this@LoginActivity)
        loginActivity = this

        if (Build.VERSION.SDK_INT >= 23)
            permissions()

        printHashKey()

        callbackManager = CallbackManager.Factory.create()
        loginActivityBinding.  loginButton.setOnClickListener {

           if(ConnectivityReceiver.isConnected()){
               LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("public_profile", "email"))
            //   loginActivityBinding.  loginButton.setReadPermissions(Arrays.asList("public_profile", "email"))
               LoginManager.getInstance().registerCallback(callbackManager,  object : FacebookCallback<LoginResult> {

                   override fun onSuccess( loginResult : LoginResult) {
                       val request = GraphRequest.newMeRequest(loginResult.getAccessToken(), { `object`, response ->
                           // Application code

                           val social_id = `object`.getString("id")
                           val name = `object`.getString("name")
                           val email = `object`.getString("email")
                           val image_url = `object`.getJSONObject("picture").getJSONObject("data").getString("url")

                           AppController.getSharedPref().edit().putString("form","facebook").apply()
                           AppController.getSharedPref().edit().putString("social_id",social_id).apply()
                           AppController.getSharedPref().edit().putString("name",name).apply()
                           AppController.getSharedPref().edit().putString("email",email).apply()
                           AppController.getSharedPref().edit().putString("image_url",image_url).apply()


                               socialLogin(social_id,"facebook",email)



                           LoginManager.getInstance().logOut()

                       })
                       val parameters = Bundle()
                       parameters.putString("fields", "name,email,picture")
                       request.setParameters(parameters)
                       request.executeAsync()
                   }

                   override fun onCancel() { }
                   override fun onError( exception : FacebookException) {
                       Log.d("",""+exception)
                   }
               })
           }else{
               val toast = Toast.makeText(AppController.getAppContext(),"No internet connection!",Toast.LENGTH_SHORT)
               toast.setGravity(Gravity.CENTER, 0, 0)
               toast.show()
           }
        }



// Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val   gso : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        mGoogleApiClient =  GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this@LoginActivity /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        loginActivityBinding.signInButton.setOnClickListener {

            if(ConnectivityReceiver.isConnected()){
                val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }else{
                val toast = Toast.makeText(AppController.getAppContext(),"No internet connection!",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }

        }

    }
    fun replaceFragment(fragment : Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.addToBackStack(fragment.javaClass.simpleName)
        transaction.replace(R.id.fragContainer, fragment).commitAllowingStateLoss()
    }
    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else { super.onBackPressed() }
    }

 /*   fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }*/


   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
       super.onActivityResult(requestCode, resultCode, data)
       callbackManager.onActivityResult(requestCode, resultCode, data)
       // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == RC_SIGN_IN) {
        val  result : GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
        handleSignInResult(result)
    }

   }
   private fun permissions() {
       val REQUEST_PERMISSIONS = 20
       super@LoginActivity.requestAppPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), R.string.app_name, REQUEST_PERMISSIONS)
   }
   @SuppressLint("PackageManagerGetSignatures")
   fun printHashKey() {
        try {
           val info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES)
            for ( signature in info.signatures) {
               val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey =  String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: " + hashKey)
            }
        } catch ( e : NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch ( e : Exception) {
            Log.e("TAG", "printHashKey()", e)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
    //    profileTracker.stopTracking()
    }
    private fun handleSignInResult(result : GoogleSignInResult ) {
        Log.d("TAG", "handleSignInResult:" + result.isSuccess())
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            val acct : GoogleSignInAccount = result.getSignInAccount()!!
            val   social_id =   acct.id
            val   name =   acct.displayName
            val email =  acct.email
            val image_url = acct.photoUrl.toString()
            AppController.getSharedPref().edit().putString("form","google").apply()

            AppController.getSharedPref().edit().putString("social_id",social_id).apply()
            AppController.getSharedPref().edit().putString("name",name).apply()
            AppController.getSharedPref().edit().putString("email",email).apply()
            AppController.getSharedPref().edit().putString("image_url",image_url).apply()

            socialLogin(social_id!!,"google",email!!)
            Auth.GoogleSignInApi.signOut(mGoogleApiClient)

        }
    }
    override fun onConnectionFailed(p0: ConnectionResult) {}

    fun socialLogin(social_id : String,form : String,email : String){

        loginActivityBinding.progressBar.isIndeterminate = true
        loginActivityBinding.progressBar.visibility = View.VISIBLE
        loginActivityBinding.progressBar.setClickable(false)
        val device_id =   AppController.getSharedPref().getString("firebase_reg_token","null")
        val device_type =   "android"

        apiService.socialLogin(social_id,form,device_id,device_type,email).enqueue(object : Callback<Responce.SocialLoginAddResponce> {
            override fun onResponse(call: Call<Responce.SocialLoginAddResponce>, response: Response<Responce.SocialLoginAddResponce>) {
                if (response.body().getStatus()==1) {

                    loginActivityBinding.progressBar.visibility = View.GONE
                    loginActivityBinding.progressBar.isIndeterminate = false

                    AppController.getSharedPref().edit().putBoolean("is_login",true).apply()
                    AppController.getSharedPref().edit().putString("user_id",response.body().getData().getUser_id()).apply()

                    AppController.getSharedPref().edit().putString("mobile_no",response.body().getData().getMobile_no()).apply()

                    if(!TextUtils.isEmpty(response.body().getData().getMobile_verify())){
                        AppController.getSharedPref().edit().putString("mobile_verify",response.body().getData().getMobile_verify()).apply()
                    }else{
                        AppController.getSharedPref().edit().putString("mobile_verify","").apply()
                    }


                    val intent = Intent(applicationContext, PicsLootActivity::class.java)
                    //fort welcome dialog only
                    intent.putExtra("dialog","dialogWelcome")
                    startActivity(intent)
                    finish()


                }else if(response.body().getStatus()==0){

                    AppController.getSharedPref().edit().putBoolean("is_login",false).apply()
                    AppController.getSharedPref().edit().putString("user_id","").apply()

                    AppController.getSharedPref().edit().putString("mobile_no","").apply()
                    AppController.getSharedPref().edit().putString("mobile_verify","").apply()

                    loginActivityBinding.progressBar.visibility = View.GONE
                    loginActivityBinding.progressBar.isIndeterminate = false

                    replaceFragment(ProfileSaveUploadFragment.newInstance())

                }else if(response.body().getStatus()==2){

                    AppController.getSharedPref().edit().putBoolean("is_login",false).apply()
                    AppController.getSharedPref().edit().putString("user_id","").apply()

                    AppController.getSharedPref().edit().putString("mobile_no","").apply()
                    AppController.getSharedPref().edit().putString("mobile_verify","").apply()

                    loginActivityBinding.progressBar.visibility = View.GONE
                    loginActivityBinding.progressBar.isIndeterminate = false

                    val toast = Toast.makeText(LoginActivity.loginActivity,response.body().getMessage(),Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.SocialLoginAddResponce>, t: Throwable) {

                AppController.getSharedPref().edit().putBoolean("is_login",false).apply()
                AppController.getSharedPref().edit().putString("user_id","").apply()

                AppController.getSharedPref().edit().putString("mobile_no","").apply()
                AppController.getSharedPref().edit().putString("mobile_verify","").apply()

                loginActivityBinding.progressBar.visibility = View.GONE
                loginActivityBinding.progressBar.isIndeterminate = false
                t.printStackTrace()
                val toast = Toast.makeText(LoginActivity.loginActivity,""+t,Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        })
    }


}
