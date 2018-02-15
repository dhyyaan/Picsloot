package com.think360.picsloot.fragments

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.think360.picsloot.R
import com.think360.picsloot.databinding.UploadImagesForPrintFragmentBinding
import com.think360.picsloot.activities.PicsLootActivity
import com.think360.picsloot.api.AppController
import com.think360.picsloot.imagepicker.PickerAction
import com.think360.picsloot.imagepicker.SImagePicker
import com.think360.picsloot.util.ConnectivityReceiver
import com.think360.picsloot.util.RecyclerItemClickListener
import com.think360.picsloot.util.Utility
import java.util.ArrayList

/**
 * Created by think360 on 24/10/17.
 */

class UploadImagesForPrintFragment : Fragment(), PickerAction {
    val REQUEST_CODE_IMAGE = 102
    private  var adapter: UploadImagesAdapter? = null
    lateinit var uploadImagesForPrintFragmentBinding: UploadImagesForPrintFragmentBinding
    companion object {

        fun newInstance(): UploadImagesForPrintFragment {
            return UploadImagesForPrintFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uploadImagesForPrintFragmentBinding = DataBindingUtil.inflate<UploadImagesForPrintFragmentBinding>(inflater, R.layout.upload_images_for_print_fragment, container, false)
        PicsLootActivity.picsLootActivity!!.picsLootActivityBinding.navigation.visibility = View.GONE
        PicsLootActivity.picsLootActivity!!.setAdapterCallback(this)
        uploadImagesForPrintFragmentBinding.tvImageUploaded.setText(""+AppController.retriveImageList().size+" images selected")


        adapter = UploadImagesAdapter(PicsLootActivity.picsLootActivity!!.applicationContext)

        uploadImagesForPrintFragmentBinding.rvImages.setLayoutManager(GridLayoutManager(PicsLootActivity.picsLootActivity, Utility.calculateNoOfColumns(AppController.getAppContext())))
        uploadImagesForPrintFragmentBinding.rvImages.setAdapter(adapter)

        uploadImagesForPrintFragmentBinding.btnUploadImages.setOnClickListener {

             if(ConnectivityReceiver.isConnected()){
                 PicsLootActivity.picsLootActivity!!.replaceFragment(CompleteDeliveryOrderAddFragment.newInstance())
             }else{
                 val toast = Toast.makeText(AppController.getAppContext(),"No internet connection!", Toast.LENGTH_SHORT)
                 toast.setGravity(Gravity.CENTER, 0, 0)
                 toast.show()
             }
        }

        uploadImagesForPrintFragmentBinding.rvImages.addOnItemTouchListener(RecyclerItemClickListener(
                context,
                RecyclerItemClickListener.OnItemClickListener({ view: View, i: Int ->
                    if (AppController.retriveImageList().size > 0) {
                        val eligible_images =   AppController.getSharedPref().getString("eligible_images","-1").toInt()
                        SImagePicker
                                .from(PicsLootActivity.picsLootActivity)

                                .maxCount(eligible_images)
                                .rowCount(3)
                                .setSelected(AppController.retriveImageList())
                                // .showCamera(showCamera.isChecked())
                                .showCamera(false)
                                .pickMode(SImagePicker.MODE_IMAGE)
                                .forResult(REQUEST_CODE_IMAGE)
                    } else {
                        val eligible_images =   AppController.getSharedPref().getString("eligible_images","-1").toInt()
                        SImagePicker
                                .from(PicsLootActivity.picsLootActivity)

                                .maxCount(eligible_images)
                                .rowCount(3)
                                .showCamera(false)
                                .pickMode(SImagePicker.MODE_IMAGE)
                                .forResult(REQUEST_CODE_IMAGE)

                    }
                }

       )
        ))

                return uploadImagesForPrintFragmentBinding.root
    }

   internal class UploadImagesAdapter( mContext: Context) : RecyclerView.Adapter<UploadImagesAdapter.ViewHolder>() {

         private  var ctx :Context

       init {
           this.ctx = mContext
       }
       override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.test_row_item1, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(ctx)
                    .load(AppController.retriveImageList().get(position))
                    .placeholder(R.drawable.no_img)
             //       .error(R.drawable.imagenotfound)
                    .into(holder.iv)
        }

        override fun getItemCount(): Int {
            return AppController.retriveImageList().size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
             val iv: ImageView

            init {
                val params = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 0, 0, 0)

                iv = itemView.findViewById(R.id.iv)

            }
        }

    }
    override fun proceedResultAndFinish(selected: ArrayList<String>?, original: Boolean, resultCode: Int) {
        adapter!!.notifyDataSetChanged()
        val selected = ""+ selected!!.size +" images selected"
        uploadImagesForPrintFragmentBinding.tvImageUploaded.setText(selected)
    }
}

