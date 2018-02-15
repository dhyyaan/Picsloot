package com.think360.picsloot.fragments

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.adapters.SeekBarBindingAdapter.setProgress
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.think360.picsloot.R
import com.think360.picsloot.R.drawable.images
import com.think360.picsloot.activities.PicsLootActivity
import com.think360.picsloot.api.AppController
import com.think360.picsloot.api.AppController.makeEmptyImageList
import com.think360.picsloot.api.data.Responce
import com.think360.picsloot.api.interfaces.ApiService

import com.think360.picsloot.databinding.FinishDeliveryOrderFragmentBinding

import com.think360.picsloot.util.ProgressRequestBody
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
 * Created by think360 on 25/10/17.
 */
class FinishDeliveryOrderFragment : Fragment(), ProgressRequestBody.UploadCallbacks {

    @Inject
    internal lateinit var apiService: ApiService
    private lateinit var finishDeliveryOrderFragmentBinding: FinishDeliveryOrderFragmentBinding


  //  lateinit var  deliveryAdd : ArrayList<String>

    lateinit var image1Part : MultipartBody.Part
    lateinit var image2Part : MultipartBody.Part
    lateinit var image3Part : MultipartBody.Part
    lateinit var image4Part : MultipartBody.Part
    lateinit var image5Part : MultipartBody.Part
    private var image6Part : MultipartBody.Part? = null
    private var  allImageSize : Long = 0
    companion object {
        fun newInstance(): FinishDeliveryOrderFragment {

            val frament = FinishDeliveryOrderFragment()
           // val bundle  = Bundle()

           // bundle.putStringArrayList("deliveryAdd",deliveryAdd)
          //  frament.setArguments(bundle)

            return frament
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

          if(AppController.retriveImageList().size==5){

               allImageSize = File(AppController.retriveImageList().get(0)).length()+File(AppController.
                        retriveImageList().get(1)).length()+File(AppController.retriveImageList().get(2)).
                        length()+File(AppController.retriveImageList().get(3)).length()+File(AppController.retriveImageList().get(4)).length()

                image1Part =   prepareFilePart("image1",File(AppController.retriveImageList().get(0)))
                image2Part = prepareFilePart("image2",File(AppController.retriveImageList().get(1)))
                image3Part= prepareFilePart("image3",File(AppController.retriveImageList().get(2)))
                image4Part = prepareFilePart("image4",File(AppController.retriveImageList().get(3)))
                image5Part = prepareFilePart("image5",File(AppController.retriveImageList().get(4)))


            }else{
               allImageSize =
                        File(AppController.retriveImageList().get(0)).length()+File(AppController.retriveImageList().get(1)).length()+
                        File(AppController.retriveImageList().get(2)).length()+ File(AppController.retriveImageList().get(3)).length()+
                        File(AppController.retriveImageList().get(4)).length()+ File(AppController.retriveImageList().get(5)).length()

                image1Part =   prepareFilePart("image1",File(AppController.retriveImageList().get(0)))
                image2Part = prepareFilePart("image2",File(AppController.retriveImageList().get(1)))
                image3Part= prepareFilePart("image3",File(AppController.retriveImageList().get(2)))
                image4Part = prepareFilePart("image4",File(AppController.retriveImageList().get(3)))
                image5Part = prepareFilePart("image5",File(AppController.retriveImageList().get(4)))
                image6Part = prepareFilePart("image6",File(AppController.retriveImageList().get(5)))

          }

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        finishDeliveryOrderFragmentBinding = DataBindingUtil.inflate<FinishDeliveryOrderFragmentBinding>(inflater, R.layout.finish_delivery_order_fragment, container, false)

        AppController.getSharedPref().edit().putBoolean("onBack",false).apply()

        PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.navigation.visibility = View.GONE
        PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.badge.visibility=View.GONE
        PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.ivNotification.visibility=View.GONE

        getSendOrder()


        return finishDeliveryOrderFragmentBinding.root
    }
    override fun onProgressUpdate(percentage: Int) {
        finishDeliveryOrderFragmentBinding.circularProgress.setProgress(percentage)
    }

    override fun onError() {

    }

    override fun onFinish() {

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        ( context.applicationContext as AppController).getComponent().inject(this@FinishDeliveryOrderFragment)
    }
    fun getSendOrder(){
        finishDeliveryOrderFragmentBinding.circularProgress.setProgress(0)


        val user_id = RequestBody.create(MediaType.parse("text/plain"),  AppController.getSharedPref().getString("user_id","null"))

        val add = RequestBody.create(MediaType.parse("text/plain"),   AppController.getSharedPref().getString("address",""))
        val state_id = RequestBody.create(MediaType.parse("text/plain"),   AppController.getSharedPref().getString("state_id",""))
        val city_id = RequestBody.create(MediaType.parse("text/plain"),   AppController.getSharedPref().getString("city_id",""))
        val pin_code = RequestBody.create(MediaType.parse("text/plain"),   AppController.getSharedPref().getString("pincode",""))
        val comment = RequestBody.create(MediaType.parse("text/plain"),   AppController.getSharedPref().getString("comment",""))

        AppController.getSharedPref().edit().putLong("allImageSize",allImageSize).apply()

        apiService.confirmOrder(user_id,image1Part,image2Part,image3Part,image4Part,image5Part,image6Part,add,state_id,city_id,pin_code,comment
                ).enqueue(object : Callback<Responce.EditDeliveryAddResponce> {
            override fun onResponse(call: Call<Responce.EditDeliveryAddResponce>, response: Response<Responce.EditDeliveryAddResponce>) {

                if (response.body().getStatus()) {
                    AppController.makeEmptyImageList()
                    finishDeliveryOrderFragmentBinding.circularProgress.setProgress(100)
                    PicsLootActivity.uploaded = 0
                    AppController.getSharedPref().edit().putString("address","").apply()
                    AppController.getSharedPref().edit().putString("state","").apply()
                    AppController.getSharedPref().edit().putString("state_id","").apply()
                    AppController.getSharedPref().edit().putString("city_id","").apply()
                    AppController.getSharedPref().edit().putString("city","").apply()
                    AppController.getSharedPref().edit().putString("pincode","").apply()
                    AppController.getSharedPref().edit().putString("comment","").apply()

                    PicsLootActivity.picsLootActivity!!.replaceFragment(OrderStatusFragment.newInstance())
                }else{

                    AppController.makeEmptyImageList()
                    PicsLootActivity.uploaded = 0
                    val toast = Toast.makeText(PicsLootActivity.picsLootActivity!!,""+response.body().getMessage(), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }
            override fun onFailure(call: Call<Responce.EditDeliveryAddResponce>, t: Throwable) {
                t.printStackTrace()
                PicsLootActivity.uploaded = 0

            }
        })
    }
    private fun prepareFilePart(param : String,file: File): MultipartBody.Part{

        return MultipartBody.Part.createFormData(param, file.getName(), ProgressRequestBody(file, this))
    }


}






/*        val c3 = findViewById(R.id.circularprogressbar3) as CircularProgressBar
        c3.title = "June"
        c3.setSubTitle("2013")
        c3.progress = 42

        val c4 = findViewById(R.id.circularprogressbar4) as CircularProgressBar
        c4.progress = 99*/


/*  finishDeliveryOrderFragmentBinding.btnCompleteOrder.setOnClickListener {  }*/