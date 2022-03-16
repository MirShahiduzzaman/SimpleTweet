package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var client: TwitterClient
    lateinit var tvCount: TextView

    // Handling the user's click on the tweet button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        tvCount = findViewById(R.id.tvCharCount)
        tvCount.setText(etCompose.length().toString() + "/280")

        client = TwitterApplication.getRestClient(this)

        etCompose.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Fires right as the text is being changed (even supplies the range of text)
                tvCount.setText(etCompose.length().toString() + "/280")
                if (etCompose.length() > 280) {
                    tvCount.setTextColor(Color.RED)
                    btnTweet.isEnabled = false
                    btnTweet.isClickable = false
                } else {
                    tvCount.setTextColor(Color.GRAY)
                    btnTweet.isEnabled = true
                    btnTweet.isClickable = true
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Fires right before text is changing
            }

            override fun afterTextChanged(s: Editable) {
                // Fires right after the text has changed
            }
        })

        btnTweet.setOnClickListener() {

            // Grab the content of edittext (etCompose)
            val tweetContent = etCompose.text.toString()

            // 1. Make sure the tweet isn't empty
            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Empty tweets not allowed!", Toast.LENGTH_SHORT).show()
                // Look into displaying SnackBar message
            }

            // 2. Tweet is under character count as checked while typing
            //    Now submit request to publish Tweet
            else {
                // Toast.makeText(this, tweetContent, Toast.LENGTH_SHORT).show()
                client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                        Log.i(TAG, "Successfully published tweet!!!")

                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)
                        finish()

                    }

                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to publish tweet", throwable)
                    }

                })
            }
        }
    }

    companion object {
        val TAG = "ComposeActivity"
    }
}