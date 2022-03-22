package com.toxicflame427.opinionator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

class splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Hides the very top, UI Utility Bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        loadAnimations(1)

        Thread{
            Thread.sleep(2700)
            loadAnimations(2)
        }.start()

        Thread{
            Thread.sleep(3500)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }.start()
    }

    fun loadAnimations(statusCode : Int){
        //Get xml elements
        val image = findViewById<ImageView>(R.id.icon_image)
        val title = findViewById<TextView>(R.id.title_text)

        //Set animation resources
        val iconEnter = AnimationUtils.loadAnimation(this, R.anim.icon_in)
        val iconExit = AnimationUtils.loadAnimation(this, R.anim.icon_out)
        val titleEnter = AnimationUtils.loadAnimation(this, R.anim.title_in)
        val titleExit = AnimationUtils.loadAnimation(this, R.anim.title_out)

        //1 = load enter animations, !1 = load exit animations
        if(statusCode == 1){
            image.startAnimation(iconEnter)
            title.startAnimation(titleEnter)
        } else {
            image.startAnimation(iconExit)
            title.startAnimation(titleExit)

            image.visibility = View.INVISIBLE
            title.visibility = View.INVISIBLE
        }
    }
}