package com.nachofm.evl

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.nachofm.evl.vet.Vet

class NewVetActivity : AppCompatActivity() {

    private lateinit var editVetView: EditText

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_vet)
        editVetView = findViewById(R.id.edit_vet)

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editVetView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val word = editVetView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, word)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.vetlistsql.REPLY"
    }
}