package com.toxicflame427.opinionator

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import java.lang.Exception

//If true, setting the username. If false, setting the password.
var settingBool : Boolean = true

const val usernameSetText : String = "Please enter and confirm your new username. (After the username is changed, you must now login under the new username. Your password will still be the same) (Max length of 20 characters)"
const val passwordSetText : String = "Please enter and confirm your new password. (After the password is changed, you must now login under the same username with the new password.) (Max length of 20 characters)"

class ChangeCreditials : AppCompatActivity() {
    private lateinit var textbox1 : AppCompatEditText
    private lateinit var textbox2 : AppCompatEditText
    private lateinit var changeCredentialButton : Button

    private lateinit var setterText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_creditials)

        connectViews()
    }

    fun backClick(view : View){
        finish()
    }

    private fun connectViews(){
        //Get if the username or password is being set
        settingBool = intent.getBooleanExtra("bool", true)

        textbox1 = findViewById(R.id.textBox1)
        textbox2 = findViewById(R.id.textBox2)
        changeCredentialButton = findViewById(R.id.chnageCredentialButton)

        setterText = findViewById(R.id.setterText)

        if(settingBool){
            setterText.text = usernameSetText
            textbox1.hint = "Username"
            textbox2.hint = "Confirm Username"
            changeCredentialButton.text = "Change Username"

            changeCredentialButton.setOnClickListener{
                if(textbox1.text.toString() == textbox2.text.toString() && textbox1.text.toString() != "" && textbox2.text.toString() != "") {
                    var alertBuilder = AlertDialog.Builder(this)
                    alertBuilder.setTitle("Change username?")
                        .setCancelable(false)
                        .setMessage("Are you sure you want to change your username? This can be done again later.")
                        .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                            dialog.cancel()
                        }).setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                            try {
                                database.child("users").child(accountNumber.toString()).setValue(AccountAdder(textbox1.text.toString(), gottenPassword, userAgreedVotes, userDisagreedVotes))

                                Toast.makeText(this, "Username successfully changed!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            } catch (e : Exception){
                                Toast.makeText(this, "Database cannot be reached at this time. Please check that you have an internet connection!", Toast.LENGTH_LONG).show()
                                Log.v("Database issue:", e.toString())
                            }
                        }).show()
                } else if(textbox1.text.toString() != textbox2.text.toString()){
                    Toast.makeText(this, "Usernames do not match!", Toast.LENGTH_LONG).show()
                } else if(textbox1.text.toString() == "" || textbox2.text.toString() == ""){
                    Toast.makeText(this, "Please fill out the form", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            setterText.text = passwordSetText
            textbox1.hint = "Password"
            textbox2.hint = "Confirm Password"
            textbox2.transformationMethod = PasswordTransformationMethod.getInstance()
            changeCredentialButton.text = "Change Password"

            changeCredentialButton.setOnClickListener{
                if(textbox1.text.toString() == textbox2.text.toString() && textbox1.text.toString() != "" && textbox2.text.toString() != "") {
                    var alertBuilder = AlertDialog.Builder(this)
                    alertBuilder.setTitle("Change password?")
                        .setCancelable(false)
                        .setMessage("Are you sure you want to change your password? This can be done again later.")
                        .setNegativeButton("No", DialogInterface.OnClickListener { dialog, _ ->
                            dialog.cancel()
                        }).setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                            try {
                                database.child("users").child(accountNumber.toString()).setValue(AccountAdder(gottenUsername, textbox1.text.toString(), userAgreedVotes, userDisagreedVotes))

                                Toast.makeText(this, "Password successfully changed!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            } catch (e : Exception){
                                Toast.makeText(this, "Database cannot be reached at this time. Please check that you have an internet connection!", Toast.LENGTH_LONG).show()
                                Log.v("Database issue:", e.toString())
                            }
                        }).show()
                } else if(textbox1.text.toString() != textbox2.text.toString()){
                    Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_LONG).show()
                } else if(textbox1.text.toString() == "" || textbox2.text.toString() == ""){
                    Toast.makeText(this, "Please fill out the form", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}