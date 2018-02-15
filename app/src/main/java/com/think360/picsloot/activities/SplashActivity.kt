package com.think360.picsloot.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.think360.picsloot.R
import com.think360.picsloot.api.AppController



class SplashActivity : AppCompatActivity() {
    private val handle = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

    }

    override fun onStart() {
        super.onStart()
        val t = object : Thread() {
            override fun run() {
                try {
                    sleep(3000)
                    if(AppController.getSharedPref().getBoolean("is_login", false)){
                        startActivity(Intent(applicationContext, PicsLootActivity::class.java))
                           finish()
                       }  else{
                        startActivity(Intent(applicationContext, LoginActivity::class.java))
                           finish()
                       }
                } catch (e : InterruptedException ) {
                    e.printStackTrace()
                }
            }
        }
        t.start()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        handle.removeCallbacksAndMessages(null)
    }
    override fun onDestroy() {
        super.onDestroy()
        handle.removeCallbacksAndMessages(null)
    }

}
