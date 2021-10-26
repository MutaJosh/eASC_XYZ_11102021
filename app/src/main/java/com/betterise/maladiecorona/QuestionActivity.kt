package com.betterise.maladiecorona

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.betterise.maladiecorona.managers.AgentManager
import com.betterise.maladiecorona.managers.GeolocManager
import com.betterise.maladiecorona.managers.PollManager
import com.betterise.maladiecorona.managers.QuestionManager
import com.betterise.maladiecorona.model.Question
import com.betterise.maladiecorona.model.QuestionType
import com.betterise.maladiecorona.model.ResultType
import com.betterise.maladiecorona.model.out.PollResult
import kotlinx.android.synthetic.main.activity_question.*
import kotlinx.android.synthetic.main.question_bullet.view.*
import kotlinx.android.synthetic.main.question_city.view.*
import kotlinx.android.synthetic.main.question_digit.view.*
import kotlinx.android.synthetic.main.question_digit.view.unity
import kotlinx.android.synthetic.main.question_digit.view.value
import kotlinx.android.synthetic.main.question_digit_forced.view.*
import kotlinx.android.synthetic.main.question_rdt.view.*
import kotlinx.android.synthetic.main.question_tel.view.*
import org.rdtoolkit.support.interop.RdtIntentBuilder
import org.rdtoolkit.support.interop.RdtUtils
import org.rdtoolkit.support.interop.getRdtSession
import java.util.*


class QuestionActivity : AppCompatActivity(), View.OnClickListener, GeolocManager.GeolocListener {

    var questionManager: QuestionManager? = null
    var group: ViewGroup? = null

    var group_rdt: ViewGroup? = null



    private val RDTOOLKIT_ACTIVITY_REQUEST_CODE = 1
    private val RDTOOLKIT_CAPTURE_RESULT_REQUEST_CODE = 2

    private val ACTIVITY_PROVISION = 1
    private val ACTIVITY_CAPTURE = 2
    private val CW_SESSION_ID: String = UUID.randomUUID().toString()
    private val CLOUDWORKS_DSN = "https://vmi682749.contaboserver.net/ingest/0aa05fe0a88e1ee06df6fa7d74fbcce276d7eadb"
    private val COVID_TEST_PROFILE = "sd_standard_q_c19"
    private val RDT_PENDING_STATUS = "pending"


    companion object {
        const val EXTRA_RESULTQ = "EXTRA_RESULTQ"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_question)

        btn_next.visibility = VISIBLE

        AgentManager().savefirstname(this, intent.getStringExtra("firstname"))
        AgentManager().savelastname(this, intent.getStringExtra("lastname"))
        AgentManager().savenational_ID(this, intent.getStringExtra("national_ID"))
        AgentManager().savegender(this, intent.getStringExtra("patientgender"))
        AgentManager().savetelephone(this, intent.getStringExtra("patienttelephone"))
        AgentManager().savedob(this, intent.getStringExtra("dob"))
        AgentManager().saveoccupation(this, intent.getStringExtra("occupation"))
        AgentManager().savenationality(this, intent.getStringExtra("residence"))
        AgentManager().saveresidence(this, intent.getStringExtra("nationality"))
        AgentManager().saveprovince(this, intent.getStringExtra("province"))
        AgentManager().savedistrict(this, intent.getStringExtra("district"))
        AgentManager().savesector(this, intent.getStringExtra("sector"))
        AgentManager().savecell(this, intent.getStringExtra("cell"))
        AgentManager().savevillage(this, intent.getStringExtra("village"))
        AgentManager().saveNumberhousehold(this, intent.getStringExtra("number_household"))
        AgentManager().savevaccine_type(this, intent.getStringExtra("vaccine_type"))
        AgentManager().savevaccine_dose(this, intent.getStringExtra("vaccine_dose"))

        questionManager = QuestionManager(
            resources.getStringArray(R.array.questions),
            resources.getStringArray(R.array.choices),
            resources.getStringArray(R.array.question_types)
        )
        loadQuestion()

        btn_next.setOnClickListener(this)
        btn_back_to_home.setOnClickListener(this)
        btn_back_to_home.setOnClickListener {


            AgentManager().saverdt_result(this,"null")
            var poll = questionManager!!.createPoll(this)
            PollManager().addPoll(this, poll)

            val intent = Intent(this, ActivityChooseCategory::class.java)
            startActivity(intent)
            finish()
        }
        btn_back.setOnClickListener {
            if (questionManager!!.canGoBack()) {
                questionManager?.previousQuestion()
                loadQuestion()
            } else {
                val intent = Intent(this, ActivityChooseCategory::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    override fun onBackPressed() {
        if (!goBack())
            super.onBackPressed()
    }

    override fun onClick(view: View) {
        var result = questionManager?.getResults()

        if (validate()) {
            if (questionManager!!.hasMoreQuestion()) {
                questionManager!!.nextQuestion()
                loadQuestion()
            } else {

                var user_id = questionManager?.getSpecificAnswer(0)?.text.toString()
                var rdt_result = questionManager?.getSpecificAnswer(19)?.text.toString()

                var poll = questionManager!!.createPoll(this)
                PollManager().addPoll(this, poll)

                var result = questionManager!!.getResults()
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra(ResultActivity.EXTRA_RESULT, result)

                intent.putExtra(ResultActivity.EXTRA_USER_ID, user_id)
                intent.putExtra("patient_phone_number",getIntent().getStringExtra("patienttelephone"))
                intent.putExtra("Indexi",getIntent().getStringExtra("Indexi"))
                intent.putExtra(ResultActivity.EXTRA_RDT_RESULT, rdt_result)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun loadQuestion() {

        question.text = questionManager?.getCurrentQuestion()
        progress.text = String.format(
            getString(R.string.question_progress),
            (questionManager?.getCurrentIndex()?.plus(1)),
            questionManager?.getQuestionCount()
        )
        btn_next.text =
            getString(if (questionManager!!.hasMoreQuestion()) R.string.next_question else R.string.save_and_continue)
        btn_next.isEnabled = false

       var qi:String=(questionManager?.getCurrentIndex()?.plus(1).toString())

        //Toast.makeText(baseContext, "ASCOV result: "+ test_resultascov.text, Toast.LENGTH_SHORT).show()


        if (qi.equals("20")) {

             // Toast.makeText(baseContext, "test btn rdt removal", Toast.LENGTH_LONG).show()
            var result_ascoov=  questionManager!!.getResults()

            test_resultascov.visibility=VISIBLE
            test_resultascov.text="Index : "+getIntent().getStringExtra("Indexi")+"\n \n \n"+baseContext.getString(
                when (result_ascoov){
                    ResultType.CASE1 -> R.string.result1
                    ResultType.CASE2 -> R.string.result2
                    ResultType.CASE3 -> R.string.result3
                    ResultType.CASE3bis -> R.string.result3bis
                    ResultType.CASE4 -> R.string.result4
                    ResultType.CASE5 -> R.string.result5
                }
            )

            if ((baseContext.getString(when (result_ascoov){
                        ResultType.CASE1 -> R.string.result1
                        ResultType.CASE2 -> R.string.result2
                        ResultType.CASE3 -> R.string.result3
                        ResultType.CASE3bis -> R.string.result3bis
                        ResultType.CASE4 -> R.string.result4
                        ResultType.CASE5 -> R.string.result5 }) as String).equals(getString(R.string.result1))){


              //  val rl = findViewById<View>(R.id.container) as RelativeLayout
              //  val mybutton = rl.findViewById<View>(R.id.rdt_action) as Button
               // mybutton.visibility= GONE
                btn_back_to_home.visibility= VISIBLE
                Toast.makeText(baseContext, "Nimuzima ", Toast.LENGTH_LONG).show()
                btn_next.visibility=GONE

                group_rdt== View.inflate(this, R.layout.question_rdt, null) as ViewGroup?
                group_rdt?.rdt_action?.visibility = GONE


            }else{
                btn_back_to_home.visibility=GONE
                btn_next.visibility=VISIBLE
            }

        }

        when (questionManager?.getCurrentQuestionType()) {
           // QuestionType.CITY -> loadCity()
            QuestionType.DIGIT -> loadDigitChoice()
            QuestionType.BINARY -> loadYesNo()
            QuestionType.DOUBLE -> loadTwoBullets()
            QuestionType.TERNARY -> loadThreeBullets()
          //  QuestionType.TELEPHONE -> loadTelephone()
            QuestionType.DIGIT_FORCED -> loadDigitForced()
            QuestionType.RDT -> loadRDT()
        }


    }


    private fun goBack(): Boolean {
        if (questionManager!!.canGoBack()) {
            questionManager?.previousQuestion()
            loadQuestion()
            return true
        }

        finish()
        return false
    }

    private fun loadCity() {
        answer_container.removeAllViews()
        group = View.inflate(this, R.layout.question_city, null) as ViewGroup

        val answer = questionManager!!.getAnswer()
        btn_next.isEnabled = true

        group?.city?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                group?.city?.setBackgroundResource(R.drawable.edit_bg_focused)

            btn_next.isEnabled = true
            group?.errorCity?.visibility = INVISIBLE
            group?.city?.setBackgroundResource(R.drawable.edit_bg_focused)

        }
        group?.city?.doOnTextChanged { text, _, _, _ ->
            btn_next.isEnabled = true
            group?.errorCity?.visibility = INVISIBLE
            group?.city?.setBackgroundResource(R.drawable.edit_bg_focused)
        }

        group?.city?.setText(answer.text)

        group?.geoloc?.setOnClickListener { requestGeoloc() }
        answer_container.addView(group)
    }

    private fun loadYesNo() {
        group = View.inflate(this, R.layout.question_bullet, null) as ViewGroup?

        group?.radio1?.setText(R.string.yes)
        group?.radio2?.setText(R.string.no)
        group?.radio3?.visibility = View.GONE

        val answer = questionManager!!.getAnswer()

        btn_next.isEnabled = true

        when (answer.value) {
            1 -> group?.radio1?.isChecked = true
            2 -> group?.radio2?.isChecked = true
        }

        group?.radio1?.setOnCheckedChangeListener { _, _ ->
            btn_next.isEnabled = true
            group?.errorBullet?.visibility = INVISIBLE
        }
        group?.radio2?.setOnCheckedChangeListener { _, _ ->
            btn_next.isEnabled = true
            group?.errorBullet?.visibility = INVISIBLE
        }

        answer_container.removeAllViews()
        answer_container.addView(group)
    }

    private fun loadTwoBullets() {
        group = View.inflate(this, R.layout.question_bullet, null) as ViewGroup?
        var answers = questionManager?.getChoices()


        group?.radio1?.text = answers!![0]
        group?.radio2?.text = answers!![1]
        group?.radio3?.visibility = View.GONE

        var answer = questionManager!!.getAnswer()
        btn_next.isEnabled = true

        when (answer.value) {
            1 -> group?.radio1?.isChecked = true
            2 -> group?.radio2?.isChecked = true
        }

        group?.radio1?.setOnClickListener {
            group?.errorBullet?.visibility = INVISIBLE
            btn_next.isEnabled = true
        }

        group?.radio2?.setOnClickListener {
            group?.errorBullet?.visibility = INVISIBLE
            btn_next.isEnabled = true
        }

        answer_container.removeAllViews()
        answer_container.addView(group)
    }

    private fun loadThreeBullets() {
        group = View.inflate(this, R.layout.question_bullet, null) as ViewGroup?
        var choices = questionManager?.getChoices()

            group?.radio1?.text = choices!![0]
            group?.radio2?.text = choices!![1]
            group?.radio3?.text = choices!![2]

        var answer = questionManager!!.getAnswer()
        btn_next.isEnabled = true

            when (answer.value) {
                1 -> group?.radio1?.isChecked = true
                2 -> group?.radio2?.isChecked = true
                3 -> group?.radio3?.isChecked = true }


        if (intent.getStringExtra("patientgender").equals("Male")) {
            group?.radio1?.visibility= INVISIBLE
            group?.radio2?.visibility= INVISIBLE

        }else if(intent.getStringExtra("patientgender").equals("Female")){
            group?.radio3?.visibility= INVISIBLE
        }

        group?.radio1?.setOnClickListener {
            group?.errorBullet?.visibility = INVISIBLE
            btn_next.isEnabled = true
        }
        group?.radio2?.setOnClickListener {
            group?.errorBullet?.visibility = INVISIBLE
            btn_next.isEnabled = true
        }
        group?.radio3?.setOnClickListener {
            group?.errorBullet?.visibility = INVISIBLE
            btn_next.isEnabled = true
        }
        answer_container.removeAllViews()
        answer_container.addView(group)


    }

    private fun loadDigitChoice() {
        group = View.inflate(this, R.layout.question_digit, null) as ViewGroup?
        val choices = questionManager?.getChoices()

        group?.unity?.text = choices!![0]

        val answer = questionManager!!.getAnswer()
        btn_next.isEnabled = true

        group?.value?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                group?.value?.setBackgroundResource(R.drawable.edit_bg_focused)

            btn_next.isEnabled = true
            group?.errorDigit?.visibility = INVISIBLE
            group?.value?.setBackgroundResource(R.drawable.edit_bg_focused)
        }

        if (answer.value > 1) {
            group?.value!!.setText(answer.value.toString())
            group?.not_known?.setImageResource(R.drawable.shape_radio_off)
            group?.not_known?.tag = answer.value
        } else if (answer.value == 1) {
            group?.not_known?.tag = 1
            group?.not_known?.setImageResource(R.drawable.shape_radio_on)
        }

        group?.not_known?.setOnClickListener {
            group?.value?.text = null
            group?.not_known?.tag = 1
            group?.not_known?.setImageResource(R.drawable.shape_radio_on)
            btn_next.isEnabled = true

            btn_next.isEnabled = true
            group?.errorDigit?.visibility = INVISIBLE
            group?.value?.setBackgroundResource(R.drawable.edit_bg)
        }


        group?.value?.doOnTextChanged { text, _, _, _ ->
            if (text != null && text.isNotEmpty()) {
                group?.not_known?.tag = text.toString().toInt()
                group?.not_known?.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.shape_radio_off,
                        null
                    )
                )

            }
            btn_next.isEnabled = true
            group?.errorDigit?.visibility = INVISIBLE
            group?.value?.setBackgroundResource(R.drawable.edit_bg_focused)

        }

        answer_container.removeAllViews()
        answer_container.addView(group)
    }

    private fun loadTelephone() {
        answer_container.removeAllViews()
        group = View.inflate(this, R.layout.question_tel, null) as ViewGroup?
        val answer = questionManager!!.getAnswer()
        btn_next.isEnabled = true

        group?.phone?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                group?.phone?.setBackgroundResource(R.drawable.edit_bg_focused)
            btn_next.isEnabled = true
            group?.error?.visibility = INVISIBLE
            group?.phone?.setBackgroundResource(R.drawable.edit_bg_focused)
        }


        group?.phone?.doOnTextChanged { _, _, _, _ ->
            btn_next.isEnabled = true
            group?.error?.visibility = INVISIBLE
            group?.phone?.setBackgroundResource(R.drawable.edit_bg_focused)
        }

        if (answer.text.isNotEmpty())
            group?.phone?.setText(answer.text)
        else {
            group?.phone?.setText(getString(R.string.phone_start) + " ")
            group?.phone?.setSelection(4)
        }

        answer_container.addView(group)
    }

    private fun loadDigitForced() {
        answer_container.removeAllViews()
        group = View.inflate(this, R.layout.question_digit_forced, null) as ViewGroup?

        val choices = questionManager?.getChoices()
        group?.unity?.text = choices!![0]


        val answer = questionManager!!.getAnswer()
        btn_next.isEnabled = true

        group?.value?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                group?.value?.setBackgroundResource(R.drawable.edit_bg_focused)
            btn_next.isEnabled = true
            group?.errorDigitForced?.visibility = INVISIBLE
        }

        group?.value?.doOnTextChanged { _, _, _, _ ->
            btn_next.isEnabled = true
            group?.errorDigitForced?.visibility = INVISIBLE
            group?.value?.setBackgroundResource(R.drawable.edit_bg_focused)

        }

        if (answer.value >= 0)
            group?.value?.setText(answer.value.toString())

        answer_container.addView(group)
    }

    private fun loadRDT() {
        answer_container.removeAllViews()
        group = View.inflate(this, R.layout.question_rdt, null) as ViewGroup

        val answer = questionManager!!.getAnswer()
        btn_next.isEnabled = true


        if ((answer.text.isNotEmpty()) and (answer.text != RDT_PENDING_STATUS)) {
            group?.rdt_result?.setText(answer.text)
            group?.rdt_result_label2?.visibility = VISIBLE
            group?.rdt_result?.visibility = VISIBLE
            group?.rdt_action?.isEnabled = false
            group?.rdt_action?.setBackgroundColor(getResources().getColor(R.color.grey_9f9f9f))
        }

        if (answer.text == RDT_PENDING_STATUS) {
            group?.rdt_action?.setText(getString(R.string.capture_result))
            group?.countdownTimer?.visibility = VISIBLE
            group?.rdt_result?.setText(answer.text)
        }

        group?.rdt_result?.doOnTextChanged { _, _, _, _ ->
            btn_next.isEnabled = true
            group?.errorRDT?.visibility = INVISIBLE
        }

        group?.rdt_action?.setOnClickListener { lunchRDTapp() }
        answer_container.addView(group)
    }

    private fun validate() = when (questionManager?.getCurrentQuestionType()) {
        //QuestionType.CITY -> validateCity()
        QuestionType.DIGIT -> validateDigit()
        QuestionType.DOUBLE -> validate2Bullets()
        QuestionType.TERNARY -> validate3Bullets()
      //  QuestionType.TELEPHONE -> validateTelephone()
        QuestionType.DIGIT_FORCED -> validateDigitForced()
        QuestionType.RDT -> validateRDT()
        else -> validate2Bullets()
    }


    private fun validate2Bullets(): Boolean {
        if (group?.radio1!!.isChecked || group?.radio2!!.isChecked) {
            questionManager!!.setAnswer(
                if (group?.radio1!!.isChecked) 1
                else 2
            )

            return true
        }

        btn_next.isEnabled = false
        group?.errorBullet?.visibility = VISIBLE
        return false
    }

    private fun validate3Bullets(): Boolean {
        if (group?.radio1!!.isChecked || group?.radio2!!.isChecked || group?.radio3!!.isChecked) {
            questionManager!!.setAnswer(
                if (group?.radio1!!.isChecked) 1
                else if (group?.radio2!!.isChecked) 2
                else 3
            )

            return true
        }

        btn_next.isEnabled = false
        group?.errorBullet?.visibility = VISIBLE
        return false
    }

    private fun validateDigit(): Boolean {

        if (group?.not_known!!.tag == 1) {
            questionManager?.setAnswer(1)
            return true
        } else if (testRange(30, 220)) {
            var intVal = group?.value?.text.toString().toInt()
            questionManager?.setAnswer(intVal)
            return true
        }

        var errorDigit = findViewById<TextView>(R.id.errorDigit)
        errorDigit.setText(if (questionManager?.getCurrentIndex() == Question.HEIGHT) R.string.height_error else R.string.weight_error)
        errorDigit.visibility = VISIBLE
        group?.value?.setBackgroundResource(R.drawable.edit_bg_error)
        btn_next.isEnabled = false
        return false
    }

    private fun validateTelephone(): Boolean {
        questionManager!!.setTextAnswer(group?.phone?.text.toString())

        if (group?.phone?.text?.toString()?.replace(" ", "")?.length == 10)
            return true

        group?.error?.visibility = View.VISIBLE
        group?.phone?.setBackgroundResource(R.drawable.edit_bg_error)
        btn_next.isEnabled = false
        return false
    }

    private fun validateCity(): Boolean {
        questionManager!!.setTextAnswer(group?.city?.text.toString())
        if (!group?.city?.text.isNullOrEmpty())
            return true

        group?.city?.setBackgroundResource(R.drawable.edit_bg_error)
        group?.errorCity?.visibility = VISIBLE
        btn_next.isEnabled = false
        return false
    }

    private fun validateRDT(): Boolean {
        questionManager!!.setTextAnswer(group?.rdt_result?.text.toString())
        if ((!group?.rdt_result?.text.isNullOrEmpty()) and (group?.rdt_result?.text.toString() != RDT_PENDING_STATUS))
            return true

        group?.errorRDT?.visibility = VISIBLE
        btn_next.isEnabled = false
        return false
    }

    private fun validateDigitForced(): Boolean {

        if (questionManager?.getCurrentIndex() == Question.TEMPERATURE) {

            if (testRange(0, 35)) {
                questionManager?.setAnswer(group?.value?.text.toString().toInt())
                return true
            }
            group?.value?.setBackgroundResource(R.drawable.edit_bg_error)
            findViewById<TextView>(R.id.errorDigitForced).setText(R.string.days_error)
            findViewById<TextView>(R.id.errorDigitForced).visibility = VISIBLE
            btn_next.isEnabled = false
            return false

        } else {

            if (testRange(0, 120)) {
                questionManager?.setAnswer(group?.value?.text.toString().toInt())
                return true
            }
            group?.value?.setBackgroundResource(R.drawable.edit_bg_error)
            findViewById<TextView>(R.id.errorDigitForced).setText(R.string.age_error)
            findViewById<TextView>(R.id.errorDigitForced).visibility = VISIBLE
            btn_next.isEnabled = false
            return false
        }
    }

    private fun testRange(low: Int, high: Int) =
        try {
            group?.value!!.text.toString().toInt() in (low..high)
        } catch (e: Exception) {
            false
        }


    private fun requestGeoloc() {
        try {
            if (GeolocManager().requestLastGeoloc(this))
                GeolocManager().getLastGeoloc(this, this)
        } catch (e: java.lang.Exception) {
            Log.e(this.localClassName, e.message)
        }
    }

    private fun lunchRDTapp(){
        if (group?.rdt_result?.text.toString() == RDT_PENDING_STATUS){
            captureRDTResults()
        } else {
            requestRDTScan()
        }
    }

    private fun requestRDTScan() {
        try {
            val intent = RdtIntentBuilder.forProvisioning()
                .setSessionId(CW_SESSION_ID)
                .setFlavorOne(getIntent().getStringExtra("firstname")+" "+getIntent().getStringExtra("lastname"))
                .setFlavorTwo(getIntent().getStringExtra("national_ID"))
                .setCloudworksBackend(CLOUDWORKS_DSN)
                .requestTestProfile(COVID_TEST_PROFILE)
                .setSecondaryCaptureRequirements("capture_windowed")
                .setInTestQaMode()
                .build();

            startActivityForResult(intent, RDTOOLKIT_ACTIVITY_REQUEST_CODE)
        } catch (e: java.lang.Exception) {
            Log.e(this.localClassName, e.message)
        }
    }

    private fun captureRDTResults() {
        try {
            val intent = RdtIntentBuilder.forCapture()
                .setSessionId(CW_SESSION_ID)
                .build()

            startActivityForResult(intent, RDTOOLKIT_CAPTURE_RESULT_REQUEST_CODE)

        } catch (e: java.lang.Exception) {
            Log.e(this.localClassName, e.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ACTIVITY_PROVISION && resultCode == RESULT_OK) {
            group?.rdt_action?.setText(getString(R.string.capture_result))
            val session = data?.getRdtSession()
            val timeStarted = session?.timeStarted
            val timeResolved = session?.timeResolved
            val timeExpired = session?.timeExpired

            val startedResolvedDifference: Long = timeResolved!!.getTime() - timeStarted!!.getTime()
            val resolvedExpiredDifference: Long = timeExpired!!.getTime() - timeResolved!!.getTime()
            group?.rdt_result?.setText(RDT_PENDING_STATUS)
            group?.countdownTimer?.visibility = VISIBLE
            validate()
            startResultCaptureTimer(startedResolvedDifference, resolvedExpiredDifference)
        }
        if (requestCode == ACTIVITY_CAPTURE && resultCode == RESULT_OK) {
            val session = RdtUtils.getRdtSession(data!!);
            val result = session?.result



            group?.rdt_result?.text = getString(
                when (result?.results.toString()) {
                    "{sars_cov2=sars_cov2_pos}" -> R.string.rdt_result_pos
                    "{sars_cov2=sars_cov2_neg}" -> R.string.rdt_result_neg
                    else -> R.string.rdt_result_invalid
                }
            )
            //saving rdt results
            AgentManager().saverdt_result(baseContext, getString(
                when (result?.results.toString()) {
                    "{sars_cov2=sars_cov2_pos}" -> R.string.rdt_result_pos
                    "{sars_cov2=sars_cov2_neg}" -> R.string.rdt_result_neg
                    else -> R.string.rdt_result_invalid
                }
            ))

            group?.rdt_result_label2?.visibility = VISIBLE
            group?.rdt_result?.visibility = VISIBLE
            group?.rdt_action?.isEnabled = false
            group?.rdt_action?.setBackgroundColor(getResources().getColor(R.color.grey_9f9f9f))
            group?.countdownTimer?.visibility = INVISIBLE
            validate()
        }
    }

    private fun startResultCaptureTimer(startedResolvedDifference: Long, resolvedExpiredDifference: Long) {
        object : CountDownTimer(startedResolvedDifference, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val formatedTime = formatCountdownTime(millisUntilFinished)
                group?.countdownTimer?.setText(getString(R.string.time_remaining) + "\n" + formatedTime)
            }

            override fun onFinish() {
                group?.countdownTimer?.setTextColor(getResources().getColor(R.color.green))
                group?.rdt_action?.setBackgroundColor(getResources().getColor(R.color.green))
                object : CountDownTimer(resolvedExpiredDifference, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val formatedTime = formatCountdownTime(millisUntilFinished)
                        group?.countdownTimer?.setText(getString(R.string.reults_valid_for)+ "\n" + formatedTime)
                    }

                    override fun onFinish() {
                        group?.countdownTimer?.setTextColor(Color.RED)
                        group?.countdownTimer?.setText(getString(R.string.results_expired))
                    }
                }.start()
            }
        }.start()
    }

    private fun formatCountdownTime(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / 1000
        val p1: Long = seconds % 60
        var p2: Long = seconds / 60
        val p3 = p2 % 60
        return "${p3}m:${p1}s"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        try {
            when (requestCode) {
                GeolocManager.GEOLOC_CODE -> {

                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        requestGeoloc()
                    } else {
                        Log.e(this.localClassName, "Permission geoloc not granted");
                    }
                }


            }
        } catch (e: java.lang.Exception) {
            Log.e(this.localClassName, e.message)
        }
    }

    override fun onLocation(location: Location?) {
        try {
            if (location == null)
                GeolocManager().startLocationUpdate(this, this)
            else {
                var geoc = Geocoder(this)

                var adrr = geoc.getFromLocation(location.latitude, location.longitude, 1)
                if (adrr.isNotEmpty()) {
                    group?.city?.setText(adrr[0].locality)
                }

            }
        } catch (e: java.lang.Exception) {
            Log.e(this.localClassName, e.message)
        }

    }

    override fun onLocationFailed() {
        Log.e(this.localClassName, "FAILED")
    }

}