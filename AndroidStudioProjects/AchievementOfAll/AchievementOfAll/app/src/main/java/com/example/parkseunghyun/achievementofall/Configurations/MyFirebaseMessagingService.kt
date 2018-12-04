package com.example.parkseunghyun.achievementofall.Configurations

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import com.example.parkseunghyun.achievementofall.Activities.HomeActivity
import com.example.parkseunghyun.achievementofall.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.ArrayList

class MyFirebaseMessagingService : FirebaseMessagingService() {

    var intent: Intent? = null
    var myTitle: String? = null
    var myBody: String? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        println("들어와라")
        sendNotification(remoteMessage?.getData()?.get("title"), remoteMessage?.getData()?.get("body"), remoteMessage?.data?.get("user"), remoteMessage?.getData()?.get("checkReason"));
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a fragment_dummy notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageTitle: String?, messageBody: String?, rejectUserArray: String?, rejectReasonArray: String?) {
        // 서버에서 오는거
        // 실패: Title - 실패, Body - 컨텐츠명
        // 인증: Title - 인증, Body - 컨텐츠명
        // 성공: Title - 성공, Body - 컨텐츠명
        // 비디오실패: Title - 비디오 실패, Body - 컨텐츠명

        if(messageTitle!!.equals("실패")){
            myBody = "목표 달성 실패 알림"
            myTitle = messageBody
        }else if(messageTitle!!.equals("인증")){
            myBody = "인증 시간이 얼마 남지 않았어요!"
            myTitle = messageBody
        }else if(messageTitle!!.equals("성공")){
            myBody = "목표 달성 성공 알림"
            myTitle = messageBody
        }else if(messageTitle!!.equals("비디오실패")){
            myBody = "과반수의 반대로 인증에 실패하셨습니다"
            myTitle = messageBody
            println("TESTINBLACK User = " + rejectUserArray)
            println("TESTINBLACK Reason = " + rejectReasonArray)
        }


        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("fcm_category", myBody)
        intent.putExtra("contentName", myTitle)
        intent.putExtra("rejectUserArray", rejectUserArray)
        intent.putExtra("rejectReasonArray", rejectReasonArray)

        val pendingIntent = PendingIntent.getActivity(this, 0 /* request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val pattern = longArrayOf(500, 500, 500, 500, 500)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, "notify_001")

                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(myTitle)
                .setContentText(myBody)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent) as NotificationCompat.Builder

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // TODO 밑에 빨간줄 오류 아닙니다
        var channel: NotificationChannel = NotificationChannel("notify_001", "채널", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
        notificationBuilder.setChannelId("notify_001")
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())

    }

    companion object {

        private val TAG = "MyFirebaseMsgService"
    }
}
