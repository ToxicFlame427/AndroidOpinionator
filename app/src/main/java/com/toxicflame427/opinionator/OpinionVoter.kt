package com.toxicflame427.opinionator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import kotlin.random.Random

class OpinionVoter : AppCompatActivity() {
    private lateinit var backToHomeButton : Button
    private lateinit var opinionText : TextView
    private lateinit var agreebutton : Button
    private lateinit var disagreeButton : Button
    private lateinit var nextOpinionButton : Button
    private lateinit var skipOpinionButton : Button
    private lateinit var peopleVotesLayout : LinearLayout
    private lateinit var peopleAgree : TextView
    private lateinit var peopleDisagree : TextView

    private var minNumOpinions : Int = 1
    private var maxNumOpinions : Int = 101
    private var agreedVotes : Int = 0
    private var disagreedVotes : Int = 0
    private var opinion : String = ""
    private var randomNumOpinion : Int = 0

    //Variables used to set the new modified data of the players vote stats
    private lateinit var username : String
    private lateinit var password : String
    private var usersDisagreedVotes : Int = 0
    private var usersAgreedVotes : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opinion_voter)

        connectViews()

        //Generate a random number to get a random opinion
        randomNumOpinion = Random.nextInt(minNumOpinions, maxNumOpinions)
        getOpinionData(randomNumOpinion)
    }

    private fun connectViews(){
        backToHomeButton = findViewById(R.id.backButton)
        opinionText = findViewById(R.id.opinion)
        agreebutton = findViewById(R.id.agreeButton)
        disagreeButton = findViewById(R.id.disagreeButton)
        nextOpinionButton = findViewById(R.id.nextOpinionButton)
        skipOpinionButton = findViewById(R.id.skipOpinionButton)
        peopleVotesLayout = findViewById(R.id.peopleVotesLayout)
        peopleAgree = findViewById(R.id.peopleAgree)
        peopleDisagree = findViewById(R.id.peopleDisagree)

        username = intent.getStringExtra("username").toString()
        password = intent.getStringExtra("password").toString()

        //Get the users current vote count
        database.child("users").child(accountNumber.toString()).get().addOnSuccessListener{
            usersAgreedVotes = it.value.toString().substringAfter("agreedCount=").substringBefore(",").toInt()
            usersDisagreedVotes = it.value.toString().substringAfter("disagreedCount=").substringBefore(",").toInt()
        }.addOnCompleteListener{
            //Nothing happens
        }

        backToHomeButton.setOnClickListener{
            finish()
        }

        agreebutton.setOnClickListener{
            increaseAgree(randomNumOpinion)
            changeVoteText()

            usersAgreedVotes += 1
            increasePlayerStats(accountNumber)

            changeButtonUI(0)
        }

        disagreeButton.setOnClickListener{
            increaseDisagree(randomNumOpinion)
            changeVoteText()

            usersDisagreedVotes += 1
            increasePlayerStats(accountNumber)

            changeButtonUI(0)
        }

        skipOpinionButton.setOnClickListener{
            changeButtonUI(1)

            randomNumOpinion = Random.nextInt(minNumOpinions, maxNumOpinions)
            getOpinionData(randomNumOpinion)
        }

        nextOpinionButton.setOnClickListener{
            changeButtonUI(1)

            randomNumOpinion = Random.nextInt(minNumOpinions, maxNumOpinions)
            getOpinionData(randomNumOpinion)
        }
    }

    private fun getOpinionData(opinionNum : Int){
        database.child("opinions").child(opinionNum.toString()).get().addOnSuccessListener{
            agreedVotes = it.value.toString().substringBefore(",").substringAfter("agreedVotes=").toInt()
            disagreedVotes = it.value.toString().substringAfter("disagreedVotes=").substringBeforeLast(",").toInt()
            opinion = it.value.toString().substringAfter("opinion=").substringBefore("}")

            //Returns: {agreedVotes=0, disagreedVotes=0, opinion=A random opinion}
            Log.v("Opinion Data", "${it.value}")
        }.addOnFailureListener{ exception ->
            Log.v("Database Failure", exception.toString())
            Toast.makeText(this, "Database cannot be reached. Make sure you are connected to the internet.", Toast.LENGTH_LONG).show()
        }.addOnCompleteListener{
            opinionText.text = opinion
        }
    }

    //Increases the agree count and writes it to the database
    private fun increaseAgree(opinionDataNum : Int){
        agreedVotes += 1
        database.child("opinions").child(opinionDataNum.toString()).setValue(OpinionEditor(opinion, agreedVotes, disagreedVotes))
    }

    //Increases the disagree count ad writes it to the database
    private fun increaseDisagree(opinionDataNum : Int){
        disagreedVotes += 1
        database.child("opinions").child(opinionDataNum.toString()).setValue(OpinionEditor(opinion, agreedVotes, disagreedVotes))
    }

    private fun changeVoteText(){
        peopleAgree.text = "$agreedVotes people agree"
        peopleDisagree.text = "$disagreedVotes people disagree"
    }

    private fun changeButtonUI(changeCode : Int){
        if(changeCode == 0){
            //Makes the next button appear and the other buttons non-clickable
            agreebutton.isClickable = false
            disagreeButton.isClickable = false

            skipOpinionButton.visibility = View.GONE

            nextOpinionButton.visibility = View.VISIBLE
            peopleVotesLayout.visibility = View.VISIBLE
        } else if(changeCode == 1) {
            //Makes the next button disappear and the other buttons clickable
            agreebutton.isClickable = true
            disagreeButton.isClickable = true

            skipOpinionButton.visibility = View.VISIBLE

            nextOpinionButton.visibility = View.GONE
            peopleVotesLayout.visibility = View.GONE
        }
    }

    private fun increasePlayerStats(accountNumberOfPlayer : Int){
        database.child("users").child(accountNumberOfPlayer.toString()).setValue(AccountAdder(username, password, usersAgreedVotes, usersDisagreedVotes))
    }
}