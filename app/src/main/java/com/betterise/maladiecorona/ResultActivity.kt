package com.betterise.maladiecorona

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.betterise.maladiecorona.managers.*
import com.betterise.maladiecorona.model.Answer
import com.betterise.maladiecorona.model.QuestionType
import com.betterise.maladiecorona.model.ResultType
import com.betterise.maladiecorona.managers.QuestionManager
import com.betterise.maladiecorona.model.out.Poll
import com.betterise.maladiecorona.model.out.PollAnswer
import com.betterise.maladiecorona.model.out.PollResult
import kotlinx.android.synthetic.main.activity_agent.*
import kotlinx.android.synthetic.main.activity_patient_details.*
import kotlinx.android.synthetic.main.activity_results.*
import org.rdtoolkit.support.interop.RdtIntentBuilder
import org.rdtoolkit.support.interop.RdtUtils.getRdtSession
import org.rdtoolkit.support.model.session.ProvisionMode
import org.rdtoolkit.support.model.session.TestSession
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


/**
 * Created by MJC on 01/07/20.
 */
class ResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RESULT = "EXTRA_RESULT"

        const val PRIVATE_MODE = 0

        const val PREFS = "PREFS"
        const val PREF_POLLS = "patient_data"
    }

    var qm: QuestionManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        intent.extras?.getSerializable(EXTRA_RESULT)

        val res: ResultType = intent.extras?.getSerializable(EXTRA_RESULT) as ResultType


        result_names.setText(intent.getStringExtra("firstname") + " " + intent.getStringExtra("lastname"))
        result_gender.setText(intent.getStringExtra("gender"))
        result_telephone.setText(intent.getStringExtra("telephone"))
        result_dob.setText(intent.getStringExtra("dob"))
        result_nid.setText(intent.getStringExtra("nid"))
        result_nationality.setText(intent.getStringExtra("nationality"))
        result_address.setText(intent.getStringExtra("province") + "," + intent.getStringExtra("district") + "," + intent.getStringExtra(
            "sector") + "," + intent.getStringExtra("cell") + "," + intent.getStringExtra("village"))

        result_ascov.text=getString(
            when (res) {
                ResultType.CASE1 -> R.string.result1
                ResultType.CASE2 -> R.string.result2
                ResultType.CASE3 -> R.string.result3
                ResultType.CASE3bis -> R.string.result3bis
                ResultType.CASE4 -> R.string.result4
                ResultType.CASE5 -> R.string.result5 })
        result_rdtresult.setText("No RDT result available yet")



        btn_start.setOnClickListener {

            startActivity(Intent(this, IntroActivity::class.java))
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
             finish()
        }
        qm = QuestionManager(
            resources.getStringArray(R.array.questions),
            resources.getStringArray(R.array.choices),
            resources.getStringArray(R.array.question_types)
        )

        var poll = qm!!.createPoll(this)

        PollManager().addPoll(this, poll)


    }


    fun simulateTestRequest(view: View?) {
//        val a=intent.getStringExtra("national_ID").substring(5,9)
//        val b=intent.getStringExtra("patienttelephone").substring(0,5)

        val nid:String=intent.getStringExtra("nid")
        val i = RdtIntentBuilder.forProvisioning()
            .setSessionId(intent.getStringExtra("firstname")+" "+intent.getStringExtra("firstname")+"-"+nid) //.requestTestProfile("debug_mal_pf_pv")
            //.requestTestProfile("sd_bioline_mal_pf_pv")
            .requestProfileCriteria(
                "sars_cov2",
                ProvisionMode.CRITERIA_SET_AND
            ) //.requestProfileCriteria("sd_bioline_mal_pf_pv carestart_mal_pf_pv", ProvisionMode.CRITERIA_SET_OR)
            //.requestProfileCriteria("fake", ProvisionMode.CRITERIA_SET_OR)
            .setFlavorOne(intent.getStringExtra("firstname") + " " + intent.getStringExtra("lastname"))
            .setFlavorTwo(nid) //.setClassifierBehavior(ClassifierMode.CHECK_YOUR_WORK)
            .setInTestQaMode() //.setSecondaryCaptureRequirements("capture_windowed")
            .setSubmitAllImagesToCloudworks(true)
            .setCloudworksBackend("https://vmi651800.contaboserver.net/.../"+intent.getStringExtra("firstname")+intent.getStringExtra("lastname"), nid) // DSN Config

            // .setCallingPackage()
               .setReturnApplication(this)
            .setIndeterminateResultsAllowed(true)
            .build()

       //   RdtIntentBuilder.forCapture().setSessionId(nid) //Populated during provisioning callout, or result

        this.startActivityForResult(i, 1)

        Handler().postDelayed({
            val iz = RdtIntentBuilder.forCapture()
                .setSessionId(intent.getStringExtra("firstname")+" "+intent.getStringExtra("firstname")+"-"+nid) //Populated during provisioning callout, or result
                .build()
            this.startActivityForResult(iz, 2)
        }, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 7 && resultCode == RESULT_OK) {
            val session = getRdtSession(data!!)
            println(
                String.format(
                    "Test will be available to read at %s",
                    session!!.timeResolved.toString()
                )
            )
            Toast.makeText(this, "RDT-Result: " + session.timeResolved.toString(),
                Toast.LENGTH_LONG
            ).show()
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            val sess = getRdtSession(data!!)
            val result: TestSession.TestResult? = sess!!.result
            val red = String.format("result is  %s", sess.result.toString())


            Toast.makeText(this, "RDT-Result: " + sess.sessionId.toString(), Toast.LENGTH_LONG).show()

            val u: List<String> = Pattern.compile(",").split(red).toList()

            val ibisubizoo = u[4].substring(u[4].indexOf("{") + 1, u[4].indexOf("}"))

            if (ibisubizoo == "sars_cov2=sars_cov2_pos") {
                result_rdtresult.setText("POSITIVE").toString()
                AgentManager().saverdt_result(this,"POSITIVE")
            } else if (ibisubizoo == "sars_cov2=sars_cov2_neg") {
                result_rdtresult.setText("NEGATIVE").toString()
                AgentManager().saverdt_result(this,"NEGATIVE")
            } else if (ibisubizoo == "sars_cov2=universal_control_failure") {
                result_rdtresult.setText("Invalid Test").toString()
                AgentManager().saverdt_result(this,"Invalid Test")
            } else {
                result_rdtresult.setText("Error occured").toString()
                AgentManager().saverdt_result(this,"null")
            }

        }
    }






}