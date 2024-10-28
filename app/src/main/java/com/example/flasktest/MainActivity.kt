package com.example.flasktest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var textFieldMessage: EditText
    private lateinit var buttonSendPost: Button
    private lateinit var buttonSendGet: Button
    private lateinit var textViewResponse: TextView
    private val url = "http://172.29.50.74:5000" // ****Put your URL here******
    private val POST = "POST"
    private val GET = "GET"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textFieldMessage = findViewById(R.id.txtField_message)
        buttonSendPost = findViewById(R.id.button_send_post)
        buttonSendGet = findViewById(R.id.button_send_get)
        textViewResponse = findViewById(R.id.textView_response)

        buttonSendPost.setOnClickListener {
            val text = textFieldMessage.text.toString()
            if (text.isEmpty()) {
                textFieldMessage.error = "This cannot be empty for post request"
            } else {
                sendRequest(POST, "getname", "name", text)
            }
        }

        buttonSendGet.setOnClickListener {
            sendRequest(GET, "getfact", null, null)
        }
    }

    private fun sendRequest(type: String, method: String, paramName: String?, param: String?) {
        val fullURL = "$url/$method${param?.let { "/$it" } ?: ""}"
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        val request: Request = if (type == POST) {
            val formBody: RequestBody = FormBody.Builder()
                .add(paramName ?: "", param ?: "")
                .build()

            Request.Builder()
                .url(fullURL)
                .post(formBody)
                .build()
        } else {
            Request.Builder()
                .url(fullURL)
                .build()
        }

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string() ?: ""

                runOnUiThread {
                    textViewResponse.text = responseData
                }
            }
        })
    }
}
