package com.example.parkseunghyun.achievementofall.Configurations

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import org.json.JSONException
import org.json.JSONObject

/**
    REFACTORED
 */

class FirebaseInstanceIDService : FirebaseInstanceIdService() {

    var jsonObjectForRefreshFCM : JSONObject ? = null

    override fun onTokenRefresh() {

        val refreshedToken = FirebaseInstanceId.getInstance().token
        jsonObjectForRefreshFCM = sendRegistrationToServer(refreshedToken)

    }

    private fun sendRegistrationToServer(token: String?): JSONObject {

        val jsonObjectForRequestFCM = JSONObject()

        try {

            jsonObjectForRequestFCM.put("fcmToken", token)

        } catch (e: JSONException) {

            e.printStackTrace()

        }

        return jsonObjectForRequestFCM

    }
    
}


