package com.betterise.maladiecorona

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.betterise.maladiecorona.managers.AgentManager
import com.betterise.maladiecorona.model.ResultType
import kotlinx.android.synthetic.main.activity_results.*

/**
 * Created by Alexandre on 24/06/20.
 */
class ResultActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_RESULT = "EXTRA_RESULT"
        const val EXTRA_USER_ID = "EXTRA_USER_ID"
        const val EXTRA_RDT_RESULT = "EXTRA_RDT_RESULT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val res : ResultType = intent.extras?.getSerializable(EXTRA_RESULT) as ResultType
        val user_id  = intent.extras?.getSerializable(EXTRA_USER_ID)
        val rdt_result  = intent.extras?.getSerializable(EXTRA_RDT_RESULT)

        user_id_text.text = intent.getStringExtra("Indexi")
        rdt_result_text.text = rdt_result.toString()


        result_text.text = getString(
            when (res){
                ResultType.CASE1 -> R.string.result1
                ResultType.CASE2 -> R.string.result2
                ResultType.CASE3 -> R.string.result3
                ResultType.CASE3bis -> R.string.result3bis
                ResultType.CASE4 -> R.string.result4
                ResultType.CASE5 -> R.string.result5
            }
        )

        btn_start.setOnClickListener {
            startActivity(Intent(this, ActivityChooseCategory::class.java))
            finish()
        }
    }
}