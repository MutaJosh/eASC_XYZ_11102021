package com.betterise.maladiecorona

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*

/**
 * Created by MJC on 01/07/20.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        logo.postDelayed({
            val locale = Locale("rw")
            val conf = resources.configuration
            conf.setLocale(locale)
            resources.updateConfiguration(conf, resources.displayMetrics)

            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            finish()
        }, 2000)
    }
}