package com.betterise.maladiecorona

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_intro.*
import org.json.JSONObject
import java.util.*


class IntroActivity : AppCompatActivity() {


   // private static final int TIME_INTERVAL = 3000
    private var mBackPressed: Long = 0
    var requestQueue: RequestQueue? = null
    private var support_number:String="";
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
            finish()
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

        startActivity(Intent(this, IntroActivity::class.java ))
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
       // finish()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        // you can set menu header with title icon etc
       // menu.setHeaderTitle("Choose Action")
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

            requestQueue = Volley.newRequestQueue(this)
            val url = "https://rbc.gov.rw/community/data-sharing/api/contact.php"
            val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { stringResponse ->
                    //handle the response

                    val jresponse = JSONObject(stringResponse)
                    support_number = jresponse.getString("telephone")
                    Log.e("nimero",support_number);

                },
                Response.ErrorListener { volleyError ->
                    // handle error
                    Toast.makeText(baseContext,getString(R.string.check_internet),Toast.LENGTH_LONG).show()

                }
            )
            requestQueue!!.add(stringRequest)


            val builder_main = AlertDialog.Builder(this)

          //  builder_main.setTitle("Are you sure ?")
            builder_main.setMessage("Ukeneye Ubufasha  bujyanye no gukoresha iyi porogaramu. "+"\n"+"Nyamuneka kanda Hamagara ariko niba udakanda buto yo guhagarika")
            builder_main.setNegativeButton("GUSUBIKA",
                DialogInterface.OnClickListener { dialog, which -> })
            builder_main.setPositiveButton("HAMAGARA",
                DialogInterface.OnClickListener { dialog, which ->
//                    val phone = ""
                    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", support_number, null))
                    startActivity(intent)

                })

            builder_main.show()
        }
        return true
    }

    override fun onBackPressed() {
        if (mBackPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            return
        } else {
            Toast.makeText(this, "Ongera usubire inyuma kugirango ubashe kuva muri sisitemu", Toast.LENGTH_SHORT).show()
        }
        mBackPressed = System.currentTimeMillis()
    }
}