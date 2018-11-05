package com.example.parkseunghyun.achievementofall

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

object VolleyHttpService{

    val address : String? = "http://192.168.3.211:3000"

    fun login(context: Context, jsonObject: JSONObject,success: (Boolean) -> Unit){

        val loginRequest = object : JsonObjectRequest(Request.Method.POST, "${address}/login", jsonObject, Response.Listener{ response ->
            println("수신 성공: $response")
            success(response.getString("success").toBoolean())

        }, Response.ErrorListener {error ->
            println("수신 에러: $error")
            success(false)
        }){

        }

        Volley.newRequestQueue(context).add(loginRequest)
    }


    fun signup(context: Context, jsonObject: JSONObject, success: (Boolean)->Unit){

        var signupRequest = object : JsonObjectRequest(Request.Method.POST,"${address}/signup", jsonObject, Response.Listener{ response ->
            println("서버 수신: $response")
            success(response.getString("success").toBoolean())

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
            success(false)
        }){
        }
        Volley.newRequestQueue(context).add(signupRequest)
    }
    fun sendToken(context: Context, jsonObject: JSONObject, success: (Boolean)->Unit){

        var sendTokenRequest = object : JsonObjectRequest(Request.Method.POST,"${address}/sendToken", jsonObject, Response.Listener{ response ->
            println("서버 수신: $response")
            success(response.getString("success").toBoolean())

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
            success(false)
        }){
        }
        Volley.newRequestQueue(context).add(sendTokenRequest)
    }
    fun getUserInfo(context: Context, jsonObject: JSONObject, success: (JSONObject)->Unit){

        var userInfoRequest = object : JsonObjectRequest(Request.Method.POST,"${address}/getUserInfo", jsonObject, Response.Listener{ response ->
            println("서버 수신: $response")
            success(response)

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
        }){
        }
        Volley.newRequestQueue(context).add(userInfoRequest)
    }

    fun logout(context: Context, jsonObject: JSONObject, success: (Boolean)->Unit){

        var logoutRequest = object : JsonObjectRequest(Request.Method.POST,"${address}/logout", jsonObject, Response.Listener{ response ->
            println("서버 수신: $response")
            success(response.getString("success").toBoolean())

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
            success(false)
        }){
        }
        Volley.newRequestQueue(context).add(logoutRequest)
    }
}