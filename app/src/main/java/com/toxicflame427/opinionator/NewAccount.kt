package com.toxicflame427.opinionator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText

private var capturedUsername : String = ""
private var capturedPassword : String = ""

//Also used in MainActivity
var numOfMaxAccounts : Long = 10000

private var usernameExists : Boolean = false

//Adds the function to the current class
interface NextCallListener{
    fun onNext(num : Int)
}

class NewAccount : AppCompatActivity() , NextCallListener{
    //Views in XML to connect
    private lateinit var username : AppCompatEditText
    private lateinit var password : AppCompatEditText
    private lateinit var confirmPassword : AppCompatEditText
    private lateinit var createAccountButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_account)

        connectViews()
    }

    override fun onNext(num : Int){
        setDatabase(num, this)
    }

    private fun connectViews(){
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirm_password)
        createAccountButton = findViewById(R.id.createAccountButton)

        createAccountButton.setOnClickListener{
            createAccount()
        }
    }

    //Used to go back to the previous activity
    fun back(view : View){
        finish()
    }

    //Looks at te database to add a new user to it
    private fun setDatabase(num : Int, listener: NextCallListener){
        database.child("users").child(num.toString()).get().addOnSuccessListener {
            //If empty, assign data to the correct key
            if(it.value == "" || it.value == null || it.value == "null") {
                database.child("users").child(num.toString()).setValue(AccountAdder(username.text.toString(), password.text.toString(), 0, 0))
            }
            //If not empty, check the next key
            else {
                if(num <= numOfMaxAccounts){
                    listener.onNext(num + 1)
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Database cannot be reached at this time. Please try again later", Toast.LENGTH_LONG).show()
            Log.v("Connection Failed", exception.toString());
        }
    }

    private fun createAccount(){
        if (username.text.toString() == "" || password.text.toString() == "" || confirmPassword.text.toString() == "") {
            Toast.makeText(this, "Please complete the form", Toast.LENGTH_LONG).show()
        } else {
            if (password.text.toString() != confirmPassword.text.toString()) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show()
            } else {
                setDatabase(1, this)

                Toast.makeText(this, "Thank you for creating a new account with us!", Toast.LENGTH_LONG).show()
                //After the account is created, this activity will close and return to the previous
                finish()
            }
        }
    }
}