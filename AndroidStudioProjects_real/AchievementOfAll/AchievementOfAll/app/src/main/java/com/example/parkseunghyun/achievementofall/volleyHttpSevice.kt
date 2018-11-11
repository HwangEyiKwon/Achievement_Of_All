package com.example.parkseunghyun.achievementofall

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException


object VolleyHttpService{

    val address : String? = "http://172.30.1.44:3000" // 테스트용 주소

    fun jwtCheck(context: Context, jsonObject: JSONObject, success: (Boolean)->Unit){

        var jwtCheckRequest = object : JsonObjectRequest(Request.Method.POST,"${address}/jwtCheck", jsonObject, Response.Listener{ response ->
            println("서버 수신: $response")
            success(response.getString("success").toBoolean())

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
            success(false)
        }){
        }
        Volley.newRequestQueue(context).add(jwtCheckRequest)
    }

    fun login(context: Context, jsonObject: JSONObject,success: (JSONObject) -> Unit){

        val loginRequest = object : JsonObjectRequest(Request.Method.POST, "${address}/login", jsonObject, Response.Listener{ response ->
            println("수신 성공: $response")
            success(response)

        }, Response.ErrorListener {error ->
            println("수신 에러: $error")
        }){

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String,String> {
                var headers = HashMap<String,String>()
                return headers
            }

            override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
                try {
                    println(response)
                    val jsonString = String(response.data)
                    val jsonResponse = JSONObject(jsonString)
                    jsonResponse.put("headers", JSONObject(response.headers))
                    return Response.success(jsonResponse,
                            HttpHeaderParser.parseCacheHeaders(response))
                } catch (e: UnsupportedEncodingException) {
                    return Response.error(ParseError(e))
                } catch (je: JSONException) {
                    return Response.error(ParseError(je))
                }
            }
        }

        Volley.newRequestQueue(context).add(loginRequest)
    }
    fun logout(context: Context, jsonObject: JSONObject, success: (Boolean)->Unit){

        var logoutRequest = object : JsonObjectRequest(Request.Method.POST,"${address}/logout", jsonObject, Response.Listener{ response ->
            println("서버 수신: $response")
            success(response.getString("success").toBoolean())

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
            success(false)
        }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String,String> {
                var headers = HashMap<String,String>()
                return headers
            }
        }
        Volley.newRequestQueue(context).add(logoutRequest)
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
    fun getSearchData(context: Context, success: (JSONObject)->Unit){

        var searchDataRequest = object : JsonObjectRequest(Request.Method.GET,"${address}/getSearchData",null, Response.Listener{ response ->
            println("서버 수신 getSearchData: $response")
            success(response)

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
        }){
        }
        Volley.newRequestQueue(context).add(searchDataRequest)
    }
    fun getAppInfo(context: Context, success: (JSONObject)->Unit){

        var appInfoRequest = object : JsonObjectRequest(Request.Method.GET,"${address}/getAppInfo",null, Response.Listener{ response ->
            println("서버 수신 getInfoInfo: $response")
            success(response)

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
        }){
        }
        Volley.newRequestQueue(context).add(appInfoRequest)
    }


}