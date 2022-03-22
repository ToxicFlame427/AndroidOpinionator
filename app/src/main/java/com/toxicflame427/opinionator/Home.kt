package com.toxicflame427.opinionator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.toxicflame427.opinionator.databinding.ActivityHomeBinding

//Get data from MainActivity
private lateinit var usernameGet : String
private lateinit var passwordGet : String
var accountNumber : Int = 0

class Home : AppCompatActivity() {
    lateinit var binding : ActivityHomeBinding
    private lateinit var welcomeUser : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connectViewsAndSetData()
    }

    private fun connectViewsAndSetData(){
        usernameGet = intent.getStringExtra("username").toString()
        passwordGet = intent.getStringExtra("password").toString()
        accountNumber = intent.getIntExtra("accNumber", 0)

        welcomeUser = findViewById(R.id.welcomeUser)

        welcomeUser.text = "Welcome, $usernameGet"

        binding.reviewButton.setOnClickListener{
            reviewUser()
        }
    }

    fun play(view : View){
        val intent : Intent = Intent(this, OpinionVoter::class.java)
        intent.putExtra("username", usernameGet)
        intent.putExtra("password", passwordGet)
        intent.putExtra("accNumber", accountNumber)
        startActivity(intent)
    }

    fun playerStatsSender(view : View){
        val intent : Intent = Intent(this, PlayerStats::class.java)
        intent.putExtra("username", usernameGet)
        intent.putExtra("password", passwordGet)
        intent.putExtra("accNumber", accountNumber)
        startActivity(intent)
    }

    //This function is for displaying the in-app review system for the user
    private fun reviewUser(){
        val reviewManager : ReviewManager = ReviewManagerFactory.create(this)

        val reviewTask = reviewManager.requestReviewFlow()
        reviewTask.addOnCompleteListener{ request ->
            if (request.isSuccessful){
               val reviewInfo = request.result
                val launchRequest = reviewManager.launchReviewFlow(this, reviewInfo)
                launchRequest.addOnCompleteListener{
                    Log.v("Review task successful", "Task was a success")
                }
            } else{
                Toast.makeText(this, "Review task failed. Please try again later.", Toast.LENGTH_SHORT).show()
                Log.v("Request failed", "Could not load review request")
            }
        }
    }

    fun back(view: View) {
        finish()
    }
}