package com.example.parkseunghyun.achievementofall.Configurations

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException


object VolleyHttpService{

    // 서버 ip 주소
    private var globalVariables: GlobalVariables?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress

    // 사용자 jwt-token 확인
//    fun jwtCheck(context: Context, jsonObject: JSONObject, success: (Boolean)->Unit){
//
//        var jwtCheckRequest = object : JsonObjectRequest(Request.Method.POST,"$ipAddress/jwtCheck", jsonObject, Response.Listener{ response ->
//            println("서버 수신: $response")
//            success(response.getString("success").toBoolean())
//
//        }, Response.ErrorListener { error ->
//            println("수신 에러: $error")
//            success(false)
//        }){
//        }
//        Volley.newRequestQueue(context).add(jwtCheckRequest)
//    }
    // 사용자 fcm-token
    fun sendToken(context: Context, jsonObject: JSONObject, success: (Boolean)->Unit){

        var sendTokenRequest = object : JsonObjectRequest(Request.Method.POST,"$ipAddress/sendToken", jsonObject, Response.Listener{ response ->
            println("서버 수신: $response")
            success(response.getString("success").toBoolean())

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
            success(false)
        }){
        }
        Volley.newRequestQueue(context).add(sendTokenRequest)
    }
    // 사용자 로그인
    fun login(context: Context, jsonObject: JSONObject,success: (JSONObject) -> Unit){

        val loginRequest = object : JsonObjectRequest(Request.Method.POST, "$ipAddress/login", jsonObject, Response.Listener{ response ->
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
    // 사용자 로그아웃
    fun logout(context: Context, jsonObject: JSONObject, success: (Boolean)->Unit){

        var logoutRequest = object : JsonObjectRequest(Request.Method.POST,"$ipAddress/logout", jsonObject, Response.Listener{ response ->
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

    // 사용자 회원가입
    fun signup(context: Context, jsonObject: JSONObject, success: (Boolean)->Unit){

        var signupRequest = object : JsonObjectRequest(Request.Method.POST,"$ipAddress/signup", jsonObject, Response.Listener{ response ->
            println("서버 수신: $response")
            success(response.getString("success").toBoolean())

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
            success(false)
        }){
        }
        Volley.newRequestQueue(context).add(signupRequest)
    }

    // 사용자 정보 받아오기
    fun getUserInfo(context: Context, jsonObject: JSONObject, success: (JSONObject)->Unit){

        var userInfoRequest = object : JsonObjectRequest(Request.Method.POST,"$ipAddress/getUserInfo", jsonObject, Response.Listener{ response ->
            println("서버 수신: $response")
            success(response)

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
        }){
        }
        Volley.newRequestQueue(context).add(userInfoRequest)
    }

    // 찾기 정보 받아오기
    fun getSearchContentData(context: Context, success: (JSONObject)->Unit){

        var searchDataRequest = object : JsonObjectRequest(Request.Method.GET,"$ipAddress/getSearchContentData",null, Response.Listener{ response ->
            println("서버 수신 getSearchData: $response")
            success(response)

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
        }){
        }
        Volley.newRequestQueue(context).add(searchDataRequest)
    }
    fun getSearchUserData(context: Context, success: (JSONObject)->Unit){

        var searchDataRequest = object : JsonObjectRequest(Request.Method.GET,"$ipAddress/getSearchUserData",null, Response.Listener{ response ->
            println("서버 수신 getSearchData: $response")
            success(response)

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
        }){
        }
        Volley.newRequestQueue(context).add(searchDataRequest)
    }


    // 앱 정보 받아오기
    fun getAppInfo(context: Context, success: (JSONObject)->Unit){

        var appInfoRequest = object : JsonObjectRequest(Request.Method.GET,"$ipAddress/getAppInfo",null, Response.Listener{ response ->
            println("서버 수신 getInfoInfo: $response")
            success(response)

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
        }){
        }
        Volley.newRequestQueue(context).add(appInfoRequest)
    }

    // 달력 정보 받아오기
    fun getCalendarInfo(context: Context, jsonObject: JSONObject, success: (JSONArray)->Unit){

        var token = jsonObject.getString("token")
        var contentName = jsonObject.getString(
                "contentName")
        println(token+contentName)
        var calendarInfoRequest = object : JsonArrayRequest(Request.Method.GET,"$ipAddress/getCalendarInfo/$token/$contentName",null, Response.Listener{ response ->
            println("서버 수신 getCalendar: $response")

            success(response)

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
        }){
        }
        Volley.newRequestQueue(context).add(calendarInfoRequest)
    }

    // 컨텐츠 달성율 받아오기
    fun getAchievementRate(context: Context,jsonObject: JSONObject, success: (JSONObject)->Unit){

        var token = jsonObject.getString(
                "token")
        var contentName = jsonObject.getString(
                "contentName")

        var acheivementRateRequest = object : JsonObjectRequest(Request.Method.GET,"$ipAddress/getAchievementRate/$token/$contentName",null, Response.Listener{ response ->
            println("서버 수신 getInfoInfo: $response")
            success(response)

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
        }){
        }
        Volley.newRequestQueue(context).add(acheivementRateRequest)
    }

    // 사용자 참가 유무 정보
    fun getParticipatedInfo(context: Context,jsonObject: JSONObject, success: (JSONObject)->Unit){

        var token = jsonObject.getString("token")
        var contentName = jsonObject.getString("contentName")

        var participatedInfoRequest = object : JsonObjectRequest(Request.Method.GET,"$ipAddress/isParticipated/$token/$contentName",null, Response.Listener{ response ->
            println("서버 수신 getparti: $response")
            success(response)

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
        }){
        }
        Volley.newRequestQueue(context).add(participatedInfoRequest)
    }

    // 인증 필요한 타 사용자 리스트 불러오기
    fun getOthers(context: Context,jsonObject: JSONObject, success: (JSONObject)->Unit){

        var token = jsonObject.getString("token")
        var contentName = jsonObject.getString("contentName")

        var othersInfoRequest = object : JsonObjectRequest(Request.Method.GET,"$ipAddress/getOthers/$token/$contentName",null, Response.Listener{ response ->
            println("서버 수신 others: $response")
            success(response)

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
        }){
        }
        Volley.newRequestQueue(context).add(othersInfoRequest)
    }

    // 컨텐츠 참가하기 -> 가능한 참가 시작일 받아오기
    fun contentJoin(context: Context,jsonObject: JSONObject, success: (JSONObject)->Unit){
        var contentName = jsonObject.getString("contentName")
        println("컨텐츠 조인")
        var contentJoinRequest = object : JsonObjectRequest(Request.Method.GET,"$ipAddress/contentJoin/$contentName",null, Response.Listener{ response ->
            println("서버 수신 others: $response")
            success(response)

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
        }){
        }
        Volley.newRequestQueue(context).add(contentJoinRequest)

    }

    // 컨텐츠 참가 완료
    fun contentJoinComplete(context: Context,jsonObject: JSONObject, success: (JSONObject)->Unit){

        println("컨텐츠 조인 완료")
        var contentJoinCompleteRequest = object : JsonObjectRequest(Request.Method.POST,"$ipAddress/contentJoinComplete",jsonObject, Response.Listener{ response ->
            println("서버 수신 others: $response")
            success(response)

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
        }){
        }
        Volley.newRequestQueue(context).add(contentJoinCompleteRequest)
    }

    // 영상 인증
    fun checkVideo(context: Context,jsonObject: JSONObject, success: (JSONObject)->Unit){

        println("컨텐츠 영상 인증")

        var checkVideoRequest = object : JsonObjectRequest(Request.Method.POST,"$ipAddress/checkVideo",jsonObject, Response.Listener{ response ->
            println("서버 수신 others: $response")
            success(response)

        }, Response.ErrorListener { error ->
            println("수신 에러: $error")
        }){
        }
        Volley.newRequestQueue(context).add(checkVideoRequest)
    }



}