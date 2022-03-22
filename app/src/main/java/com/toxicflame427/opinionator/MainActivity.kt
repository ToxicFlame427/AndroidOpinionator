package com.toxicflame427.opinionator

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.FirebaseDatabase

//Instance of the main database
//This is also used in MainActivity
var database = FirebaseDatabase.getInstance().reference

interface LoginListener{
    fun onNext(num : Int)
}

//Initialize banner ad variable
private lateinit var bannerAdHome : AdView

var gottenUsername : String = ""
var gottenPassword : String = ""
private var accountNumber1 : Int = 0

class MainActivity : AppCompatActivity() , LoginListener{
    private lateinit var mainLayout : LinearLayout
    private lateinit var loginButton : Button
    private lateinit var usernameUI : AppCompatEditText
    private lateinit var passwordUI : AppCompatEditText
    private var canLoadNextActivity : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectViews()

        //Initialize the banner and load the requested ad
        MobileAds.initialize(this@MainActivity)
        val adRequest = AdRequest.Builder().build()
        bannerAdHome.loadAd(adRequest)

        //TODO: Create more opinions to add to the database (start at 50, that is where I left off)
        //database.child("opinions").child("50").setValue(OpinionEditor("", 0, 0))

        val backgroundAnimation = mainLayout.background as AnimationDrawable
        backgroundAnimation.setEnterFadeDuration(7)
        backgroundAnimation.setExitFadeDuration(2000)
        backgroundAnimation.start()
    }

    override fun onNext(num : Int){
        checkLogin(num, this)
    }

    private fun connectViews(){
        mainLayout = findViewById(R.id.mainLayout)
        loginButton = findViewById(R.id.loginButton)
        usernameUI = findViewById(R.id.username)
        passwordUI = findViewById(R.id.password)

        bannerAdHome = findViewById(R.id.bannerAdHome)

        loginButton.setOnClickListener{
            checkLogin(1, this)
        }
    }

    private fun checkLogin(num : Int, listener : LoginListener){
        canLoadNextActivity = false
        database.child("users").child(num.toString()).get().addOnSuccessListener {
            gottenUsername = it.value.toString().substringBefore("}").substringAfter("username=")
            gottenPassword = it.value.toString().substringBefore(",").substringAfter("password=")
            if(usernameUI.text.toString() == "" || passwordUI.text.toString() == ""){
                Toast.makeText(this, "Please fill out the login form to sign in", Toast.LENGTH_LONG).show()
            }
            //Gets value and check if they both work together
            else if(gottenUsername == usernameUI.text.toString() && gottenPassword == passwordUI.text.toString()) {
                canLoadNextActivity = true
                accountNumber1 = num
            }
            //If the username and password do not match, run until it is found
            else {
                if(it.value == "" || it.value == null || it.value == "null"){
                    Toast.makeText(this, "Username and/or password is incorrect. Try again", Toast.LENGTH_LONG).show()
                } else {
                    if (num <= numOfMaxAccounts) {
                        listener.onNext(num + 1)
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Database cannot be reached at this time. Please try again later", Toast.LENGTH_LONG).show()
            Log.v("Connection Failed", exception.toString())
        }.addOnCompleteListener {
            if (canLoadNextActivity) {
                val intent = Intent(this, Home::class.java)
                intent.putExtra("username", gottenUsername)
                intent.putExtra("password", gottenPassword)
                intent.putExtra("accNumber", accountNumber1)

                usernameUI.setText("")
                passwordUI.setText("")

                startActivity(intent) //Starts the home activity
            }
        }
    }

    //Changes activity to create a new account
    fun newAccountSender(view : View) {
        val intent = Intent(this, NewAccount::class.java)
        startActivity(intent)
    }
}