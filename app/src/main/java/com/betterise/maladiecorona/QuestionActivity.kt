package com.betterise.maladiecorona

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.betterise.maladiecorona.managers.*
import com.betterise.maladiecorona.model.Question
import com.betterise.maladiecorona.model.QuestionType
import kotlinx.android.synthetic.main.activity_agent.*
import kotlinx.android.synthetic.main.activity_patient_details.*
import kotlinx.android.synthetic.main.activity_question.*
import kotlinx.android.synthetic.main.activity_question.btn_next
import kotlinx.android.synthetic.main.activity_question.question
import kotlinx.android.synthetic.main.question_bullet.view.*
import kotlinx.android.synthetic.main.question_city.view.*
import kotlinx.android.synthetic.main.question_digit.view.*
import kotlinx.android.synthetic.main.question_digit.view.unity
import kotlinx.android.synthetic.main.question_digit.view.value
import kotlinx.android.synthetic.main.question_digit_forced.view.*
import kotlinx.android.synthetic.main.question_tel.view.*
import java.util.*

/**
 * Created by MJC on 01/07/20.
 */
class QuestionActivity : AppCompatActivity(), View.OnClickListener, GeolocManager.GeolocListener {

    var questionManager: QuestionManager? = null
    var group: ViewGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_question)

        AgentManager().savefirstname(this,intent.getStringExtra("firstname"))
        AgentManager().savelastname(this,intent.getStringExtra("lastname"))
        AgentManager().savenational_ID(this,intent.getStringExtra("national_ID"))
        AgentManager().savegender(this,intent.getStringExtra("patientgender"))
        AgentManager().savetelephone(this,intent.getStringExtra("patienttelephone"))
        AgentManager().savedob(this,intent.getStringExtra("dob"))
        AgentManager().saveoccupation(this,intent.getStringExtra("occupation"))
        AgentManager().saveresidence(this,intent.getStringExtra("residence"))
        AgentManager().savenationality(this,intent.getStringExtra("nationality"))
        AgentManager().saveprovince(this,intent.getStringExtra("province"))
        AgentManager().savedistrict(this,intent.getStringExtra("district"))
        AgentManager().savesector(this,intent.getStringExtra("sector"))
        AgentManager().savecell(this,intent.getStringExtra("cell"))
        AgentManager().savevillage(this,intent.getStringExtra("village"))

        questionManager = QuestionManager(
            resources.getStringArray(R.array.questions),
            resources.getStringArray(R.array.choices),
            resources.getStringArray(R.array.question_types)
        )
        loadQuestion()

        btn_next.setOnClickListener(this)

        btn_back.setOnClickListener {
            if (questionManager!!.canGoBack()) {
                questionManager?.previousQuestion()
                loadQuestion()
            } else
                finish()

        }
    }

    override fun onBackPressed() {
        if (!goBack())
            super.onBackPressed()
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

        when (questionManager?.getCurrentQuestionType()) {
           // QuestionType.CITY -> loadCity()
            QuestionType.DIGIT -> loadDigitChoice()
            QuestionType.BINARY -> loadYesNo()
            QuestionType.DOUBLE -> loadTwoBullets()
            QuestionType.TERNARY -> loadThreeBullets()
           // QuestionType.TELEPHONE -> loadTelephone()
            QuestionType.DIGIT_FORCED -> loadDigitForced()
        }

    }

    override fun onClick(view: View) {

        var result = questionManager?.getResults()

        if (validate()) {
            if (questionManager!!.hasMoreQuestion()) {
                questionManager!!.nextQuestion()
                loadQuestion()
            } else {

              var poll = questionManager!!.createPoll(this)

              PollManager().addPoll(this, poll)

                var result = questionManager!!.getResults()
                val intenti = Intent(this, ResultActivity::class.java)

                intenti.putExtra("firstname", intent.getStringExtra("firstname").toString())
                intenti.putExtra("lastname", intent.getStringExtra("lastname").toString())
                intenti.putExtra("nid", intent.getStringExtra("national_ID").toString())
                intenti.putExtra("gender",intent.getStringExtra("patientgender"))
                intenti.putExtra("telephone", intent.getStringExtra("patienttelephone").toString())
                intenti.putExtra("dob", intent.getStringExtra("dob"))
                intenti.putExtra("occupation", intent.getStringExtra("occupation").toString())
                intenti.putExtra("residence", intent.getStringExtra("residence"))
                intenti.putExtra("nationality",intent.getStringExtra("nationality"))
                intenti.putExtra("province", intent.getStringExtra("province").toString())
                intenti.putExtra("district",intent.getStringExtra("district").toString())
                intenti.putExtra("sector",intent.getStringExtra("sector").toString())
                intenti.putExtra("cell", intent.getStringExtra("cell").toString())
                intenti.putExtra("village", intent.getStringExtra("village").toString())

                intenti.putExtra(ResultActivity.EXTRA_RESULT, result)

           startActivity(intenti)
       overridePendingTransition(R.anim.fadein,R.anim.fadeout);
   // finish()

            }
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



//    private fun loadCity() {
//        answer_container.removeAllViews()
//        group = View.inflate(this, R.layout.question_city, null) as ViewGroup
//
//        val answer = questionManager!!.getAnswer()
//        btn_next.isEnabled = true
//
//        group?.city?.setOnFocusChangeListener { _, hasFocus ->
//            if (hasFocus)
//                group?.city?.setBackgroundResource(R.drawable.edit_bg_focused)
//
//            btn_next.isEnabled = true
//            group?.errorCity?.visibility = INVISIBLE
//            group?.city?.setBackgroundResource(R.drawable.edit_bg_focused)
//
//        }
//        group?.city?.doOnTextChanged { text, _, _, _ ->
//            btn_next.isEnabled = true
//            group?.errorCity?.visibility = INVISIBLE
//            group?.city?.setBackgroundResource(R.drawable.edit_bg_focused)
//        }
//
//        group?.city?.setText(answer.text)
//
//        group?.geoloc?.setOnClickListener { requestGeoloc() }
//        answer_container.addView(group)
//    }

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
            3 -> group?.radio3?.isChecked = true
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

//    private fun loadTelephone() {
//        answer_container.removeAllViews()
//        group = View.inflate(this, R.layout.question_tel, null) as ViewGroup?
//        val answer = questionManager!!.getAnswer()
//        btn_next.isEnabled = true
//
//        group?.phone?.setOnFocusChangeListener { v, hasFocus ->
//            if (hasFocus)
//                group?.phone?.setBackgroundResource(R.drawable.edit_bg_focused)
//            btn_next.isEnabled = true
//            group?.error?.visibility = INVISIBLE
//            group?.phone?.setBackgroundResource(R.drawable.edit_bg_focused)
//        }
//
//
//        group?.phone?.doOnTextChanged { _, _, _, _ ->
//            btn_next.isEnabled = true
//            group?.error?.visibility = INVISIBLE
//            group?.phone?.setBackgroundResource(R.drawable.edit_bg_focused)
//        }
//
//        if (answer.text.isNotEmpty())
//            group?.phone?.setText(answer.text)
//        else {
//            group?.phone?.setText(getString(R.string.phone_start) + " ")
//            group?.phone?.setSelection(4)
//        }
//
//        answer_container.addView(group)
//    }

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

    private fun validate() = when (questionManager?.getCurrentQuestionType()) {
      //  QuestionType.CITY -> validateCity()
        QuestionType.DIGIT -> validateDigit()
        QuestionType.DOUBLE -> validate2Bullets()
        QuestionType.TERNARY -> validate3Bullets()
       // QuestionType.TELEPHONE -> validateTelephone()
        QuestionType.DIGIT_FORCED -> validateDigitForced()
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

//    private fun validateTelephone(): Boolean {
//        questionManager!!.setTextAnswer(group?.phone?.text.toString())
//        if (group?.phone?.text?.toString()?.replace(" ", "")?.length == 10)
//            return true
//
//        group?.error?.visibility = View.VISIBLE
//        group?.phone?.setBackgroundResource(R.drawable.edit_bg_error)
//        btn_next.isEnabled = false
//        return false
//    }
//
//    private fun validateCity(): Boolean {
//        questionManager!!.setTextAnswer(group?.city?.text.toString())
//        if (!group?.city?.text.isNullOrEmpty())
//            return true
//
//        group?.city?.setBackgroundResource(R.drawable.edit_bg_error)
//        group?.errorCity?.visibility = VISIBLE
//        btn_next.isEnabled = false
//        return false
//    }

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
                        Log.e(this.localClassName, "Sorry, Permission geolocation is not granted");
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