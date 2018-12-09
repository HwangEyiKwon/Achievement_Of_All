package com.example.parkseunghyun.achievementofall.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


/**
    REFACTORED.
 */

class SignupActivity : AppCompatActivity() {

    var FLAG_CHECK_EMAIL_DUP = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        initViewComponents()

    }

    private fun initViewComponents() {

        setContentView(R.layout.activity_signup)

        button_signup.setOnClickListener {

            if ( !Patterns.EMAIL_ADDRESS.matcher(signup_user_email.text).matches() ) {

                Toast.makeText(this, "이메일 형식이 아닙니다. \n Modal@gmail.com", Toast.LENGTH_SHORT).show();

            } else if ( FLAG_CHECK_EMAIL_DUP  == true ){

                Toast.makeText(this, "이미 가입된 이메일입니다.", Toast.LENGTH_SHORT).show();

            } else if ( signup_user_pw.text.toString().replace(" ", "").equals("") ) {

                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();

            } else if ( signup_user_pw.text.toString() != signup_user_pw_check.text.toString() ) {

                Toast.makeText(this, "비밀번호 체크가 틀립니다.", Toast.LENGTH_SHORT).show();

            } else if ( signup_user_nickname.text.toString().replace(" ", "").equals("") ) {

                Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();

            } else if ( signup_phone_number.text.toString().replace(" ", "").equals("") ) {

                Toast.makeText(this, "번호를 입력해주세요.", Toast.LENGTH_SHORT).show();

            } else {


                signUpRequest()

            }

        }

        signup_user_email!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {

                if (editable.toString().replace(" ","").equals("")) {

                    signup_info_text.setTextColor(resources.getColor(R.color.colorAccent))
                    signup_info_text.text = "E-mail 로 회원가입 확인 메일이 발송될 예정입니다."

                } else {

                    signup_info_text.text = ""

                    val userEmail = signup_user_email.text.toString()
                    val jsonObjectForSignUp = JSONObject()
                    jsonObjectForSignUp.put("email", userEmail)

                    if (Patterns.EMAIL_ADDRESS.matcher(signup_user_email.text).matches()) {

                        VolleyHttpService.emailAuthentication(applicationContext, jsonObjectForSignUp) { success ->

                            if (success.getInt("success") == 0) { // valid

                                signup_info_text.setTextColor(resources.getColor(R.color.green))
                                signup_info_text.text = "아직 가입되지 않은 이메일입니다."
                                FLAG_CHECK_EMAIL_DUP = false

                            } else if (success.getInt("success") == 2) { // dup

                                signup_info_text.setTextColor(resources.getColor(R.color.colorAccent))
                                signup_info_text.text = "이미 가입된 이메일입니다."
                                FLAG_CHECK_EMAIL_DUP = true

                            }
                        }

                    } else {

                        signup_info_text.setTextColor(resources.getColor(R.color.colorAccent))
                        signup_info_text.text = "이메일 형식에 맞게 입력 해주세요 ..."

                    }

                }

            }
        })

        signup_user_pw!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {

                if (editable.toString().replace(" ","").equals("")){

                    signup_pw_checker.setTextColor(resources.getColor(R.color.green))
                    signup_pw_checker.text = ""

                }
                else {

                    signup_pw_checker.setTextColor(resources.getColor(R.color.colorAccent))
                    signup_pw_checker.text = "불일치..."

                }

            }
        })

        signup_user_pw_check!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {

                if (signup_user_pw.text.toString() == signup_user_pw_check.text.toString()){

                    if(signup_user_pw.text.toString() != ""){

                        signup_pw_checker.setTextColor(resources.getColor(R.color.green))
                        signup_pw_checker.text = "일치합니다!"

                    } else {

                        signup_pw_checker.text = ""

                    }

                }
                else {

                    signup_pw_checker.setTextColor(resources.getColor(R.color.colorAccent))
                    signup_pw_checker.text = "불일치..."

                }

            }
        })

    }

    private fun signUpRequest(){

        val loader = findViewById(R.id.loading_gif) as ImageView
        loader.visibility = View.VISIBLE
        button_signup.visibility = View.GONE

        Glide
                .with(this)
                .load(R.drawable.giphy)
                .apply(RequestOptions().centerCrop())
                .into(loader)

        val userEmail = signup_user_email.text.toString()
        val userPW = signup_user_pw.text.toString()
        val nickName = signup_user_nickname.text.toString()
        val phoneNumber = signup_phone_number.text.toString()

        val jsonObjectForSignUp = JSONObject()

        jsonObjectForSignUp.put("email", userEmail)
        jsonObjectForSignUp.put("password",userPW)
        jsonObjectForSignUp.put("name", nickName)
        jsonObjectForSignUp.put("phoneNumber", phoneNumber)

        VolleyHttpService.emailConfirm(this, jsonObjectForSignUp) { success ->

            if (success.getInt("success") == 0) {

                Toast.makeText(this, "이메일을 확인하여\n회원가입을 완료해주세요", Toast.LENGTH_LONG).show()
                finish()

            } else if ((success.getInt("success") == 1)){

                Toast.makeText(this, "유효하지 않은 이메일입니다.\n유효한 이메일을 입력하세요.", Toast.LENGTH_LONG).show()
                loader.visibility = View.GONE
                button_signup.visibility = View.VISIBLE

            }

        }
    }
}


