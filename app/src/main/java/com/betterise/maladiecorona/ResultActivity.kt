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

    private val SPLASH_TIME_OUT = 5000
    private val ACTIVITY_PROVISION = 1

    private val answers: MutableList<Answer> = arrayListOf()
//    private val binding: ActivityRdtBinding? = null


    fun getResults() = ResultManager().getResults(answers)

    companion object {
        const val EXTRA_RESULT = "EXTRA_RESULT"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        intent.extras?.getSerializable(EXTRA_RESULT)

        val res: ResultType = intent.extras?.getSerializable(EXTRA_RESULT) as ResultType

//
//       if (intent.getStringExtra("resulti").equals("CASE1")){
//           btn_proceedtest.setVisibility(View.GONE)
//       }else{
//           btn_proceedtest.setVisibility(View.VISIBLE)
//       }

        result_text.text =
            "Names : " + intent.getStringExtra("firstname") + " " + intent.getStringExtra("lastname") + "\n" + "\n" +
                    "Gender:  " + intent.getStringExtra("gender") + "\n" + "\n" +
                    "Telephone : " + intent.getStringExtra("telephone") + "\n" + "\n" +
                    "Date of Birth : " + intent.getStringExtra("dob") + "\n" + "\n" +
                    "Address : " + intent.getStringExtra("province") + "," + intent.getStringExtra("district") + "," + intent.getStringExtra(
                "sector"
            ) + "," + intent.getStringExtra("cell") + "," + intent.getStringExtra("village") + "\n" + "\n" +
                    "National ID  : " + intent.getStringExtra("nid") + "\n" + "\n" +
                    "Results with e-ASCOV :                        " +

                    getString(
                        when (res) {
                            ResultType.CASE1 -> R.string.result1
                            ResultType.CASE2 -> R.string.result2
                            ResultType.CASE3 -> R.string.result3
                            ResultType.CASE3bis -> R.string.result3bis
                            ResultType.CASE4 -> R.string.result4
                            ResultType.CASE5 -> R.string.result5


                        }

                    ) + "\n" + "\n" + "\n" + "RDT Toolkit test type" + "        COVID-19 Ag Test"

        result2_text1.setText( "Results with RDT Toolkit : ")
        result2_text2.setText("No RDT result available yet")


        btn_start.setOnClickListener {

            startActivity(Intent(this, IntroActivity::class.java))
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
             finish()
        }



        PatientManager().savePatientAscov_Result(
            this,
            getString(
                when (res) {
                    ResultType.CASE1 -> R.string.result1
                    ResultType.CASE2 -> R.string.result2
                    ResultType.CASE3 -> R.string.result3
                    ResultType.CASE3bis -> R.string.result3bis
                    ResultType.CASE4 -> R.string.result4
                    ResultType.CASE5 -> R.string.result5


                }

            )
        )


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

          RdtIntentBuilder.forCapture()
            .setSessionId(nid) //Populated during provisioning callout, or result

        this.startActivityForResult(i, 1)

        Handler().postDelayed({
            val iz = RdtIntentBuilder.forCapture()
                .setSessionId(intent.getStringExtra("firstname")+" "+intent.getStringExtra("firstname")+"-"+nid) //Populated during provisioning callout, or result
                .build()
            this.startActivityForResult(iz, 2)
        }, 6000)




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
            Toast.makeText(
                this,
                "Activity-Result: " + session.timeResolved.toString(),
                Toast.LENGTH_LONG
            ).show()
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            val sess = getRdtSession(data!!)
            val result: TestSession.TestResult? = sess!!.result
            val red = String.format("result is  %s", sess.result.toString())

            val u: List<String> = Pattern.compile(",").split(red).toList()

            val ibisubizoo = u[4].substring(u[4].indexOf("{") + 1, u[4].indexOf("}"))
            val str: String
            str = if (ibisubizoo == "sars_cov2=sars_cov2_pos") {
                result2_text2.setText("POSITIVE").toString();
            } else if (ibisubizoo == "sars_cov2=sars_cov2_neg") {

                result2_text2.setText("NEGATIVE").toString();
            } else if (ibisubizoo == "sars_cov2=universal_control_failure") {

                result2_text2.setText("Invalid Test").toString()
            } else {
                result2_text2.setText("Error occured").toString()
            }

//            val builder = AlertDialog.Builder(this)
//            builder.setMessage("IGISUBIZO NI : $str")
//            builder.setCancelable(false)
//            builder.setPositiveButton(
//                "OK"
//            ) { dialog, which -> }
//            builder.show()
//


        }
    }


    fun createPo(context: Context): Poll {

        var poll = Poll()

        // Poll's id
        poll.pollId = 1

        // Poll's date
        var dateFormat = context.getString(R.string.datetime_format)
        poll.date = SimpleDateFormat(dateFormat).format(Calendar.getInstance().time)

        // Poll's agent phone number
        poll.agent = AgentManager().getAgentNumber(context)


        // Poll's agent names
        poll.agentname = AgentManager().getAgentName(context)

        poll.firstname=PatientManager().getPatientFirstname(context)
        poll.lastname=PatientManager().getPatientLastname(context)
        poll.national_ID=PatientManager().getPatientNational_ID(context)
        poll.patientgender=PatientManager().getPatientGender(context)
        poll.patienttelephone=PatientManager().getPatientTelephone(context)
        poll.dob=PatientManager().getPatientDob(context)
        poll.occupation=PatientManager().getPatientOccupation(context)
        poll.nationality=PatientManager().getPatientNationality(context)
        poll.residence=PatientManager().getPatientResidence(context)
        poll.province=PatientManager().getPatientProvince(context)
        poll.district=PatientManager().getPatientDistrict(context)
        poll.sector=PatientManager().getPatientSector(context)
        poll.cell=PatientManager().getPatientCell(context)
        poll.village=PatientManager().getPatientVillage(context)
        poll.ascov_result=PatientManager().getPatientAscov_Result(context)


        // Poll's result
        var result = getResults()
        poll.result = PollResult(
            result.ordinal + 1, context.getString(
                when (result) {
                    ResultType.CASE1 -> R.string.result1
                    ResultType.CASE2 -> R.string.result2
                    ResultType.CASE3 -> R.string.result3
                    ResultType.CASE3bis -> R.string.result3bis
                    ResultType.CASE4 -> R.string.result4
                    ResultType.CASE5 -> R.string.result5
                }
            )
        )


        return poll
    }




}