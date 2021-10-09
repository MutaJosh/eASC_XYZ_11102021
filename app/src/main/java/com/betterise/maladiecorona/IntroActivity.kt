package com.betterise.maladiecorona

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import kotlinx.android.synthetic.main.activity_intro.*
import java.util.*

class IntroActivity : AppCompatActivity() {


    /**
     * Created by MJC on 01/08/21.
     */

    private var btn_export: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)



        btn_export = findViewById<ImageView>(R.id.btn_export)
        registerForContextMenu(btn_export)


        btn_start.setOnClickListener{
            startActivity(Intent(this, ActivityChooseCategory::class.java))
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)


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

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        // you can set menu header with title icon etc
        menu.setHeaderTitle("Choose Action")
        // add menu items
        menu.add(0, v.id, 0, getString(R.string.screening_test_sessions))
        menu.add(0, v.id, 0, getString(R.string.sync_data))
        menu.add(0, v.id, 0, getString(R.string.contact_support))
    }
    // menu item select listener
    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.getTitle() === getString(R.string.screening_test_sessions)) {
            startActivity(Intent(this, TestSessionsActivity::class.java))
        } else if (item.getTitle() === getString(R.string.sync_data)) {
            startActivity(Intent(this, ExportActivity::class.java))
        } else if (item.getTitle() === getString(R.string.contact_support)) {
            val builder_main = AlertDialog.Builder(this)
          //  builder_main.setTitle("Are you sure ?")
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
        return true
    }

}