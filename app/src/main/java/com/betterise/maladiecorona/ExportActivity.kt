package com.betterise.maladiecorona

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.betterise.maladiecorona.managers.PollManager
import com.betterise.maladiecorona.model.out.Poll
import com.betterise.maladiecorona.model.out.PollItems
import com.betterise.maladiecorona.networking.ApiService
import com.betterise.maladiecorona.networking.NetworkManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_export.*
import java.text.SimpleDateFormat
import android.app.ProgressDialog
import java.util.*
import android.preference.PreferenceManager

import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


/**
 *  MJC on 29/09/21.
 */
class ExportActivity : AppCompatActivity(), View.OnClickListener{

    companion object{
        const val PRIVATE_MODE = 0
        const val prefs = "PREFS"
        const val PREF_POLLS="PREF_POLLS"
        const val prefLastExport = "prefLastExport"

    }

    private var pollManager = PollManager()
    private lateinit var networkManager : NetworkManager
    private lateinit var polls : MutableList<Poll>

    @Volatile
    private var isSending = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export)

        btn_back.setOnClickListener{finish()}
        btn_BackfromExport.setOnClickListener{finish()}
        btn_starti.setOnClickListener(this)

        networkManager = NetworkManager(this, getString(R.string.api_url))
        polls = pollManager.getPolls(this)
        export_text.text = getString(R.string.export_text, polls.size)

        if (java.lang.String.valueOf(polls.size).equals("0") ){
            btn_starti.setVisibility(View.GONE);
            btn_BackfromExport.setVisibility(View.VISIBLE);
        }else{
            btn_starti.setVisibility(View.VISIBLE)
            btn_BackfromExport.setVisibility(View.GONE)
        }
        var lastExport = getLastExportFlag()

        if (!lastExport.isNullOrBlank())
            last_export.text = getLastExportFlag()

    }


    /***
     * Sending poll list (when sync button is clicked)
     */
    @SuppressLint("CheckResult", "WrongConstant")
    override fun onClick(v: View?) {

        if (checkForInternet(this)) {
           // Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()

//  val sharedPreferences: SharedPreferences = baseContext.getSharedPreferences(prefs, MODE_PRIVATE)
        val sharedPreferences2: SharedPreferences = baseContext.getSharedPreferences(prefs, MODE_PRIVATE)
        val allEntries = sharedPreferences2.all

        for ((key, value1) in allEntries) {
            val value = value1!!
           // Log.d("Data from Storage", "$key: $value")

               // Toast.makeText(this, "$key: $value",Toast.LENGTH_LONG).show()

          //  testpref.text="data from SP : "+"$key: $value"

           // Log.e("Data", getSharedPreferences(prefs, MODE_PRIVATE).all.toString())

        }


        if (polls.size > 0 && !isSending) {
            isSending = true


            val items = PollItems(polls)

            networkManager.apiService.postPolls(items)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ res ->

                    Log.e("Result", res.toString())


                    if (res[ApiService.API_STATUS].asString != ApiService.API_SUCCESS) {
                        //progressDialog.dismiss()
                    // export_text.text = getString(R.string.export_error)

                    }  else {

                        // On success, flagging date and erasing current poll list
                        flagLastExport()
                        last_export.text = getLastExportFlag()
                        pollManager.clearPolls(this)
                        polls = pollManager.getPolls(this)

                        export_text.text = getString(R.string.export_text, polls.size)


                       // Toast.makeText(this,"Result "+res.toString()+"\n"+ "Byakunze,wohereje Amakuru muri sisitemu ",Toast.LENGTH_LONG).show()

                        Toast.makeText(this,"Kohereza amakuru muri sisitemu byagenze neza",Toast.LENGTH_LONG).show()



                    }

                    isSending = false

                }, { error ->

                    // Error handling
                    Log.e("Uknown error 2", error.message +error.localizedMessage.toString())

                    export_text.text = getString(R.string.export_error)
                    isSending = false
                })

        }

    } else {
       // Toast.makeText(this, getString(R.string.disconnected_internet), Toast.LENGTH_LONG).show()
    }

    }


    /***
     * Saving the date and time of the last working export
     */
    @SuppressLint("WrongConstant")
    private fun flagLastExport(){
        var dateFormat = getString(R.string.datetime_format)

        getSharedPreferences(prefs, PRIVATE_MODE)
            .edit()
            .putLong(prefLastExport, Calendar.getInstance().timeInMillis)
            .apply()
    }

    /***
     * Retrieving the date and time of the last working export
     */
    @SuppressLint("WrongConstant")
    private fun getLastExportFlag() : String {
        var timeStamp = getSharedPreferences(prefs, PRIVATE_MODE)
            .getLong(prefLastExport, 0)

        if (timeStamp == 0L)
            return ""

        return getString(R.string.export_last,
            SimpleDateFormat(getString(R.string.date_format)).format(timeStamp),
            SimpleDateFormat(getString(R.string.time_format)).format(timeStamp))
    }


    @SuppressLint("MissingPermission")
    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}