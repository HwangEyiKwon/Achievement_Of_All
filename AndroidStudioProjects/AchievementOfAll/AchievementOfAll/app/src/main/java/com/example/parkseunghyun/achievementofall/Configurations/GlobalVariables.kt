package com.example.parkseunghyun.achievementofall.Configurations

import android.app.Application

class GlobalVariables : Application() {

    // 서버 ip 주소 전역변수 설정

    var ipAddress: String = "http://10.42.0.169:3000"
//    var ipAddress: String = "http://54.180.32.212" // Amazon EC2 Server

}
