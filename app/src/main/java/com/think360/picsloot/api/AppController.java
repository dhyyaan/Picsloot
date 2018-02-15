package com.think360.picsloot.api;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.util.Log;
import com.think360.picsloot.R;
import com.think360.picsloot.api.interfaces.AppComponent;
import com.think360.picsloot.api.interfaces.DaggerAppComponent;
import com.think360.picsloot.imagepicker.PickerConfig;
import com.think360.picsloot.imagepicker.SImagePicker;
import com.think360.picsloot.imagepickermodified.imageloader.FrescoImageLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class AppController extends Application  {
    private static SharedPreferences sharedPreferencesPicsloot;
    private AppComponent component;
    @SuppressLint("StaticFieldLeak")
    private static Application instance;
  private RxBus bus;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        bus = new RxBus();

   component = DaggerAppComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .httpModule(new HttpModule())
                .build();

        sharedPreferencesPicsloot = getSharedPreferences("ShotaKnightSharedPref", MODE_PRIVATE);

        SImagePicker.init(new PickerConfig.Builder().setAppContext(this)
                .setImageLoader(new FrescoImageLoader())
                .setToolbaseColor(getResources().getColor(R.color.colorPrimary)).build());



    }
    public static Context getAppContext() {
        return instance;
    }
  public RxBus bus() {
       return bus;
   }



 public AppComponent getComponent() {
        return component;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static SharedPreferences getSharedPref() {
        return sharedPreferencesPicsloot;
    }

    public static  void storeImageList(ArrayList<String> imageList) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        Set<String> set = new HashSet<>();
        set.addAll(imageList);
        editor.putStringSet("array_list", set);
        editor.apply();
        Log.d("storesharedPreferences",""+set);
    }


    public static ArrayList<String> retriveImageList() {
        ArrayList<String> list =  new ArrayList<>();
        Set<String> set = getSharedPref().getStringSet("array_list", null);
        if(set !=null){
            list.addAll(set);
        }
        Log.d("array list ",""+list);
        return list;
    }
    public static void makeEmptyImageList() {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putStringSet("array_list", null);
        editor.apply();


    }
}

