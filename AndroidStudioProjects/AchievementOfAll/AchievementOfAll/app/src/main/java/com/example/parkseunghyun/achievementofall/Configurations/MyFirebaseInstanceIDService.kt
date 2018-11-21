package com.example.parkseunghyun.achievementofall.Configurations

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import org.json.JSONException
import org.json.JSONObject

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    var jsonObject : JSONObject ? = null

    // [START refresh_token]
    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        println("token: "+refreshedToken)
        Log.d(TAG, "Refreshed token: " + refreshedToken!!)


        jsonObject = sendRegistrationToServer(refreshedToken)

    }

    private fun sendRegistrationToServer(token: String?): JSONObject {
        // TODO: Implement this method to send token to your app server.
        val jsonObject = JSONObject()

        try {
            jsonObject.put("fcmToken", token)
            println("token" + token)



        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonObject
    }

    companion object {

        private val TAG = "MyFirebaseIIDService"
    }
}


