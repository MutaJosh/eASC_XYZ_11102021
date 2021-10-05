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


/**
 * Created by MJC on 29/09/21.
 */
class ExportActivity : AppCompatActivity(), View.OnClickListener{

    companion object{
        const val PRIVATE_MODE = 0
        const val prefs = "PREFS"
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
        btn_start.setOnClickListener(this)



        networkManager = NetworkManager(this, getString(R.string.api_url))
        polls = pollManager.getPolls(this)
        export_text.text = getString(R.string.export_text, polls.size)


        var lastExport = getLastExportFlag()

        if (!lastExport.isNullOrBlank())
            last_export.text = getLastExportFlag()

    }

    /***
     * Sending poll list
     */
    @SuppressLint("CheckResult", "WrongConstant")
    override fun onClick(v: View?) {


        val progressDialog = ProgressDialog(this@ExportActivity)
        progressDialog.setTitle("Exporting the data")
        progressDialog.setMessage("Busy syncing the data, please wait")
       // progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        val sharedPreferences: SharedPreferences =
            baseContext.getSharedPreferences(prefs, MODE_PRIVATE)
        val allEntries = sharedPreferences.all
        for ((key, value1) in allEntries) {
            val value = value1!!
            Log.d("Data from Storage", "$key: $value")

        //    Toast.makeText(this, "$key: $value",Toast.LENGTH_LONG).show()

            testpref.text="data from SP : "+"$key: $value"
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
                        progressDialog.dismiss()
                        export_text.text = getString(R.string.export_error)

                    }  else {

                        // On success, flagging date and erasing current poll list
                        flagLastExport()
                        last_export.text = getLastExportFlag()
                        pollManager.clearPolls(this)
                        polls = pollManager.getPolls(this)

                        export_text.text = getString(R.string.export_text, polls.size)

                        progressDialog.dismiss()
                        Toast.makeText(this,"Result "+res.toString(),Toast.LENGTH_LONG).show()

                    }

                    isSending = false

                }, { error ->

                    // Error handling
                    Log.e(this.localClassName, error.message)
                    export_text.text = getString(R.string.export_error)
                    isSending = false
                    progressDialog.dismiss()
                })

        }
    }


    /***
     * Saving the date and time of the last working export
     */
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
    private fun getLastExportFlag() : String {
        var timeStamp = getSharedPreferences(prefs, PRIVATE_MODE)
            .getLong(prefLastExport, 0)

        if (timeStamp == 0L)
            return ""

        return getString(R.string.export_last,
            SimpleDateFormat(getString(R.string.date_format)).format(timeStamp),
            SimpleDateFormat(getString(R.string.time_format)).format(timeStamp))
    }


}