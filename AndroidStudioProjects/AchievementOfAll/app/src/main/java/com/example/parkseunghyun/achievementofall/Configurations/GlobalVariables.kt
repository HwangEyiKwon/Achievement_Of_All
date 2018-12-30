package com.example.parkseunghyun.achievementofall.Configurations

import android.app.Application

/**
    REFACTORED
 */

// GlobalVariables
// 서버 ip 주소를 전역 변수로 설정합니다.
class GlobalVariables : Application() {

    var ipAddress: String = "http://192.168.8.97:3000"
//    var ipAddress: String = "http://54.180.32.212" /** Amazon EC2 Server */

}
