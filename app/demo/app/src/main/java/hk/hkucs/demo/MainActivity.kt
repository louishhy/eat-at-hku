package hk.hkucs.demo

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject



class MainActivity : AppCompatActivity() {

    var checkLogInFlag:Boolean = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val signUp = findViewById<TextView>(R.id.signUp)
        val signUpLayout = findViewById<LinearLayout>(R.id.signUpLayout)
        val logIn = findViewById<TextView>(R.id.logIn)
        val logInLayout = findViewById<LinearLayout>(R.id.logInLayout)
        val logInButton = findViewById<Button>(R.id.logInButton)
        val infoBoard = findViewById<TextView>(R.id.infoBoard)
        val infoButton = findViewById<Button>(R.id.infoButton)

        signUp.setOnClickListener {
            signUp.background = resources.getDrawable(R.drawable.switch_t,null)
            signUp.setTextColor(resources.getColor(R.color.textColor,null))
            logIn.background = null
            signUpLayout.visibility = View.VISIBLE
            logInLayout.visibility = View.GONE
            logIn.setTextColor(resources.getColor(R.color.gray,null))
            logInButton.text = "Sign Up"
            infoBoard.text = "Please Create Your Account"
        }
        logIn.setOnClickListener {
            signUp.background = null
            signUp.setTextColor(resources.getColor(R.color.gray,null))
            logIn.background = resources.getDrawable(R.drawable.switch_t,null)
            signUpLayout.visibility = View.GONE
            logInLayout.visibility = View.VISIBLE
            logIn.setTextColor(resources.getColor(R.color.textColor,null))
            logInButton.text = "Log In"
            infoBoard.text = "Please Log In Your Account"
        }

        logInButton.setOnClickListener {
            if (logInButton.text == "Log In") {
                val loginName = findViewById<TextInputLayout>(R.id.username)
                val loginPwd = findViewById<TextInputLayout>(R.id.password)
                //log in clicked
                checkLogIn(loginName.editText?.text.toString(), loginPwd.editText?.text.toString())
                if (checkLogInFlag) {
                    val intent = Intent(this@MainActivity, PageActivity::class.java)
                    intent.putExtra("username",loginName.editText?.text.toString())
                    //intent.putExtra("username", "admin")
                    startActivity(intent)
                    overridePendingTransition(R.anim.left_enter,R.anim.left_exit)
                } else {
                    infoBoard.text = "Invalid Username or Password"
                }
            } else {
                val signupName = findViewById<EditText>(R.id.new_username)
                val signupPwd1 = findViewById<EditText>(R.id.password01)
                val signupPwd2 = findViewById<EditText>(R.id.password02)
                if (signupPwd1.text == signupPwd2.text) {

                } else {
                    infoBoard.text = "Please Check Your Password"
                }
                //sign up clicked
            }
        }

        infoButton.setOnClickListener() {
            val intent = Intent(this@MainActivity, InfoActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_enter,R.anim.right_exit)
        }
    }

    fun checkLogIn(username:String, pwd:String) {
        class Data(
            @Json(index = 1) val username: String,
            @Json(index = 2) val password: String
        )
        //Log.w("myTag", Klaxon().toJsonString(Data(username, pwd)))
        val url = "http://10.68.104.199:8081/login/verification"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, JSONObject(Klaxon().toJsonString(Data(username, pwd))),
            Response.Listener { response ->
                Log.w("myTag1", response.toString())
                if (response.get("result").toString() == "success") {
                    this.checkLogInFlag = true
                } else {
                    this.checkLogInFlag = false
                }
            }, Response.ErrorListener { error -> }
        )
        Volley.newRequestQueue(this).add(jsonObjectRequest)
        Log.w("myTag2", this.checkLogInFlag.toString())
    }


}