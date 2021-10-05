package com.betterise.maladiecorona

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_intro.*
import java.util.*

class IntroActivity : AppCompatActivity() {


    /**
     * Created by MJC on 01/08/21.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


        btn_start.setOnClickListener{
            startActivity(Intent(this, ActivityChooseCategory::class.java))
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }
        btn_export.setOnClickListener{ startActivity(Intent(this, ExportActivity::class.java))}
        btn_agent.setOnClickListener{


            val builder_main = AlertDialog.Builder(this)
            builder_main.setTitle("Are you sure ?")
            builder_main.setMessage("Do you need to call for more support about using this app."+"\n"+ "if yes, please press Call but if not press cancel button")
            builder_main.setNegativeButton("CANCEL",
                DialogInterface.OnClickListener { dialog, which -> })
            builder_main.setPositiveButton("CALL",
                DialogInterface.OnClickListener { dialog, which ->
                    val phone = "+250786055919"
                    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                    startActivity(intent)

                })

            builder_main.show()

        }

        when (resources.configuration.locale.language){

            "fr" -> {
                lang_fr.setBackgroundResource(R.drawable.shape_button)
                lang_en.setBackgroundResource(R.drawable.shape_button_white)
                lang_rw.setBackgroundResource(R.drawable.shape_button_white)

                lang_fr.setTextColor(Color.WHITE)
                lang_en.setTextColor(Color.BLACK)
                lang_rw.setTextColor(Color.BLACK)
            }

            "en" -> {
                lang_fr.setBackgroundResource(R.drawable.shape_button_white)
                lang_en.setBackgroundResource(R.drawable.shape_button)
                lang_rw.setBackgroundResource(R.drawable.shape_button_white)

                lang_fr.setTextColor(Color.BLACK)
                lang_en.setTextColor(Color.WHITE)
                lang_rw.setTextColor(Color.BLACK)
            }

            "rw" -> {
                lang_fr.setBackgroundResource(R.drawable.shape_button_white)
                lang_en.setBackgroundResource(R.drawable.shape_button_white)
                lang_rw.setBackgroundResource(R.drawable.shape_button)

                lang_fr.setTextColor(Color.BLACK)
                lang_en.setTextColor(Color.BLACK)
                lang_rw.setTextColor(Color.WHITE)
            }
        }


        lang_fr.setOnClickListener {changeLocale("fr")}

        lang_en.setOnClickListener {changeLocale("en")}

        lang_rw.setOnClickListener {changeLocale("rw")}
    }

    private fun changeLocale(lang : String){
        val locale = Locale(lang)
        val conf = resources.configuration
        conf.setLocale(locale)
        resources.updateConfiguration(conf, resources.displayMetrics)
        finish()
        startActivity(Intent(this, IntroActivity::class.java ))
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
    }


}