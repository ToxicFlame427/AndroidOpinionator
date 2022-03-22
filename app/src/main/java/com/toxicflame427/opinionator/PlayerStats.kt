package com.toxicflame427.opinionator

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

var userAgreedVotes : Int = 0
var userDisagreedVotes : Int = 0

class PlayerStats : AppCompatActivity() {
    //TODO: If errors occur, put variables outside of the class
    private lateinit var usernameText : TextView
    private lateinit var agreedText : TextView
    private lateinit var disagreedText : TextView
    private lateinit var totalText : TextView

    private lateinit var changeUsernameButton : Button
    private lateinit var changePasswordButton : Button

    private lateinit var clearAccount : Button
    private lateinit var deleteAccount : Button

    private lateinit var username : String
    private lateinit var password : String

    //Declare the interstitial ad
    private var mInterstitialAd : InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_stats)

        //Initialize the use of advertisements
        MobileAds.initialize(this@PlayerStats)
        //Get a request to load the advertisement
        val adRequest = AdRequest.Builder().build()

        //Create the interstitial ad unit and load the data
        //load(context, ad unit id, request for ad, loading callback)
        InterstitialAd.load(this, "ca-app-pub-9027801021996248/2147194452", adRequest, object : InterstitialAdLoadCallback(){
            override fun onAdFailedToLoad(p0: LoadAdError) {
                Log.d("Error loading ad:", p0.message)
                mInterstitialAd = null
            }
            override fun onAdLoaded(p0: InterstitialAd) {
                mInterstitialAd = p0
            }
        })

        connectViewsGetSet()
    }

    private fun connectViewsGetSet(){
        //Get data from previous activity
        username = intent.getStringExtra("username").toString()
        password = intent.getStringExtra("password").toString()

        usernameText = findViewById(R.id.usernameText)
        agreedText = findViewById(R.id.agreedText)
        disagreedText = findViewById(R.id.disagreedText)
        totalText = findViewById(R.id.totalText)

        changeUsernameButton = findViewById(R.id.changeUsernameButton)
        changePasswordButton = findViewById(R.id.changePasswordButton)

        changeUsernameButton.setOnClickListener{
            val intent = Intent(this, ChangeCreditials::class.java)
            intent.putExtra("bool", true)
            startActivity(intent)

            showInterstitialAd()
        }

        changePasswordButton.setOnClickListener{
            val intent = Intent(this, ChangeCreditials::class.java)
            intent.putExtra("bool", false)
            startActivity(intent)

            showInterstitialAd()
        }

        clearAccount = findViewById(R.id.clearAccountButton)
        deleteAccount = findViewById(R.id.deleteAccountButton)

        clearAccount.setOnClickListener{
            //Create an alert to warn the user
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Clear account data?")
                .setMessage("Only records on your voted opinions will be cleared. This data cannot be recovered. Would you like to continue?")
                .setCancelable(false)
                .setNegativeButton("No", DialogInterface.OnClickListener{
                    dialog, id -> dialog.cancel()
                })
                .setPositiveButton("Yes", DialogInterface.OnClickListener{
                    dialog, id -> database.child("users").child(accountNumber.toString()).setValue(AccountAdder(username, password, 0, 0))
                    Toast.makeText(this, "Account records successfully cleared!", Toast.LENGTH_SHORT).show()
                    finish()
                })

            val alert = builder.create()
            alert.show()
        }

        deleteAccount.setOnClickListener{
            //Create an alert to warn the user
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Opinionator account?")
                .setMessage("WARNING: This will erase your entire account and cannot be undone! Would you like to continue?")
                .setCancelable(false)
                .setNegativeButton("No", DialogInterface.OnClickListener{
                        dialog, id -> dialog.cancel()
                })
                .setPositiveButton("Yes", DialogInterface.OnClickListener{
                        dialog, id -> database.child("users").child(accountNumber.toString()).removeValue()
                        Toast.makeText(this, "Account successfully deleted!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                })

            val alert = builder.create()
            alert.show()
        }

        //Get the users current vote count
        database.child("users").child(accountNumber.toString()).get().addOnSuccessListener{
            userAgreedVotes = it.value.toString().substringAfter("agreedCount=").substringBefore(",").toInt()
            userDisagreedVotes = it.value.toString().substringAfter("disagreedCount=").substringBefore(",").toInt()
        }.addOnFailureListener{
            Toast.makeText(this, "Database cannot be reached at this time. Make sure your device is connected to the internet and try again later.", Toast.LENGTH_LONG).show()
        }.addOnCompleteListener{
            usernameText.text = username
            agreedText.text = "You have agreed with $userAgreedVotes opinions"
            disagreedText.text = "You have disagreed with $userDisagreedVotes opinions"
            totalText.text = "You have voted on ${userAgreedVotes + userDisagreedVotes} total opinions"
        }
    }

    fun back(view : View){
        finish()
    }

    fun showInterstitialAd(){
        //Check if the ad was loaded or not
        if(mInterstitialAd != null){
            //Show the ad if it was loaded
            mInterstitialAd?.show(this)
        } else {
            Log.d("Ad:", "Ad was not ready or could not be loaded")
        }
    }
}