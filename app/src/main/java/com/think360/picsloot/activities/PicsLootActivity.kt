package com.think360.picsloot.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.think360.picsloot.R
import com.think360.picsloot.api.AppController
import com.think360.picsloot.api.EventToRefresh
import com.think360.picsloot.api.RxBus
import com.think360.picsloot.api.data.Responce
import com.think360.picsloot.api.interfaces.ApiService
import com.think360.picsloot.databinding.PicsLootActivityBinding
import com.think360.picsloot.fragments.*
import com.think360.picsloot.imagepicker.PickerAction
import com.think360.picsloot.imagepicker.activity.PhotoPickerActivity
import com.think360.picsloot.util.ConnectivityReceiver
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import javax.inject.Inject

class PicsLootActivity : RuntimePermissionsActivity() {
    @Inject
    internal lateinit var apiService: ApiService
    lateinit var picsLootActivityBinding: PicsLootActivityBinding
    val REQUEST_CODE_IMAGE = 101
    lateinit var rx : RxBus
   private var  pickerAction : PickerAction?= null

    companion object {
        var uploaded:Long = 0
        val PICK_CONTACT_REQUEST = 1  // The request code
        var picsLootActivity : PicsLootActivity? = null
        fun uploaded(read : Long ) {
            uploaded = uploaded+read

        }
    }
    private var not = true
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                picsLootActivityBinding.viewPager!!.setCurrentItem(0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_order -> {
                picsLootActivityBinding.viewPager!!.setCurrentItem(1)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_share -> {
                if(ConnectivityReceiver.isConnected()){

                    share()
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "App Link")
                    sharingIntent.putExtra("exit_on_sent", true)
                    sharingIntent.type = "text/plain"
                    startActivityForResult(Intent.createChooser(sharingIntent, "Share using"),PICK_CONTACT_REQUEST)
                }else{

                    val toast = Toast.makeText(PicsLootActivity.picsLootActivity!!,"No internet connection!", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }


                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {

                if(ConnectivityReceiver.isConnected()){
                    rx.send(EventToRefresh(R.id.navigation_profile))
                    picsLootActivityBinding.viewPager!!.setCurrentItem(2)
                }else{

                    val toast = Toast.makeText(PicsLootActivity.picsLootActivity!!,"No internet connection!", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        picsLootActivityBinding =  DataBindingUtil. setContentView<PicsLootActivityBinding>(this,R.layout.pics_loot_activity)
        (application as AppController).getComponent().inject(this@PicsLootActivity)
        rx =  (application as AppController).bus()
        picsLootActivity = this
        AppController.getSharedPref().edit().putBoolean("onBack",true).apply()
        if (Build.VERSION.SDK_INT >= 23)
            permissions()

        picsLootActivityBinding.viewPager!!.setAdapter(PagerAdapter(supportFragmentManager, getFragmentArrrayList()))
        picsLootActivityBinding.viewPager!!.setOffscreenPageLimit(3)

        if(intent !=null){
           if(intent.extras.getString("dialog","null").equals("dialogWelcome")){
               val alert =     AlertDialog.Builder(this@PicsLootActivity).create()

               alert .setMessage("Welcome to Picsloot")
               alert.show()
               // Hide after some seconds
               val handler  =  Handler()
               val runnable = Runnable {

                   if (alert.isShowing()) {
                       alert.dismiss()
                   }
               }

               alert.setOnDismissListener( DialogInterface.OnDismissListener {
                   handler.removeCallbacks(runnable)

               })

               handler.postDelayed(runnable, 2500)
           }else if(intent.extras.getInt("TO_OPEN",-2)==R.id.navigation_order){
               picsLootActivityBinding.navigation.selectedItemId =  R.id.navigation_order
               picsLootActivityBinding.viewPager!!.setCurrentItem(1)
               intent.putExtra("TO_OPEN",-2)
           }
              }else{
                 picsLootActivityBinding.viewPager!!.setCurrentItem(0)
        }



        picsLootActivityBinding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        disableShiftMode(picsLootActivityBinding.navigation)
        picsLootActivityBinding.ivNotification.setOnClickListener {

        if(ConnectivityReceiver.isConnected()){
            if(not){
                replaceFragment(NotificationFragment.newInstance())
                picsLootActivityBinding.navigation.visibility = View.GONE
                not = false
            }
        }else{
            val toast = Toast.makeText(PicsLootActivity.picsLootActivity!!,"No internet connection!", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }

        }


    }
    @SuppressLint("RestrictedApi")
    fun disableShiftMode(view: BottomNavigationView) {
        val menuView = view.getChildAt(0) as BottomNavigationMenuView
        try {
            val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
            shiftingMode.isAccessible = true
            shiftingMode.setBoolean(menuView, false)
            shiftingMode.isAccessible = false
            for (i in 0..menuView.childCount - 1) {
                val item = menuView.getChildAt(i) as BottomNavigationItemView

                item.setShiftingMode(false)
                // set once again checked value, so view will be updated

                item.setChecked(item.itemData.isChecked)
            }
        } catch (e: NoSuchFieldException) {
            Log.e("BNVHelper", "Unable to get shift mode field", e)
        } catch (e: IllegalAccessException) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e)
        }
    }
    private fun getFragmentArrrayList(): ArrayList<Fragment> {
        val fragmentSparseArray = ArrayList<Fragment>()
        fragmentSparseArray.add(HomeFragment.newInstance())
        fragmentSparseArray.add(OrderFragment.newInstance())
        fragmentSparseArray.add(ProfileFragment.newInstance())

        return fragmentSparseArray

    }
    private inner class PagerAdapter(fm: FragmentManager, fragmentSparseArray: ArrayList<Fragment>) : FragmentPagerAdapter(fm) {

        private var fragmentSparseArray = ArrayList<Fragment>()

        init {
            this.fragmentSparseArray = fragmentSparseArray
        }

        override fun getItem(position: Int): Fragment {
            return fragmentSparseArray[position]
        }

        override fun getCount(): Int {
            return fragmentSparseArray.size
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
        } else {
            if(AppController.getSharedPref().getBoolean("onBack",true)){
                not = true
                picsLootActivityBinding.badge.visibility = View.VISIBLE
                PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.ivNotification.visibility=View.VISIBLE
                picsLootActivityBinding.navigation.visibility = View.VISIBLE
                updateNotiCount(0)
                super.onBackPressed()

            }else{
                val toast = Toast.makeText(PicsLootActivity.picsLootActivity!!,"You can not go back from here", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()

            }

        }
    }

    fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(RuntimePermissionsActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }
    override  fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("","")
        when (intent.getIntExtra("TO_OPEN", 0)) {
            R.id.ivNotification -> {
                replaceFragment(NotificationFragment.newInstance())
                picsLootActivityBinding.navigation.visibility = View.GONE
                picsLootActivityBinding.badge.setNumber(0)
            }

        }

    }
    fun share(){

        picsLootActivityBinding.progressBar.isIndeterminate = true
        picsLootActivityBinding.progressBar.visibility = View.VISIBLE
        picsLootActivityBinding.progressBar.setClickable(false)

        val user_id = AppController.getSharedPref().getString("user_id","null")
        apiService.share(user_id).enqueue(object : Callback<Responce.ShareResponce> {
            override fun onResponse(call: Call<Responce.ShareResponce>, response: Response<Responce.ShareResponce>) {
                if (response.body().getStatus()) {
                    AppController.getSharedPref().edit().putString("eligible_images",response.body().getEligible_images()).apply()

                    picsLootActivityBinding.progressBar.visibility = View.GONE


                }else{
                    picsLootActivityBinding.progressBar.visibility = View.GONE

                }

            }
            override fun onFailure(call: Call<Responce.ShareResponce>, t: Throwable) {

                picsLootActivityBinding.progressBar.visibility = View.GONE
                t.printStackTrace()
                val toast = Toast.makeText(applicationContext,""+t, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        })
    }
    private fun permissions() {
        val REQUEST_PERMISSIONS = 20
        super@PicsLootActivity.requestAppPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), R.string.app_name, REQUEST_PERMISSIONS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to


        if (resultCode == Activity.RESULT_OK) {

       if(requestCode==REQUEST_CODE_IMAGE){
                val pathList = data!!.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT_SELECTION)
                AppController.storeImageList(pathList)
                replaceFragment( UploadImagesForPrintFragment.newInstance())

            }else if(requestCode==102){
                val pathList = data!!.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT_SELECTION)
                val original = data.getBooleanExtra(PhotoPickerActivity.EXTRA_RESULT_ORIGINAL, false)
                pickerAction!!.proceedResultAndFinish(pathList, original, resultCode)
                AppController.storeImageList(pathList)
            }

        }
    }
   fun setAdapterCallback(pickerAction : PickerAction){
        this.pickerAction = pickerAction
    }
fun updateNotiCount(count : Int){
    runOnUiThread {
        //stuff that updates ui
        picsLootActivityBinding.badge.setNumber(count)
    }

}
}
