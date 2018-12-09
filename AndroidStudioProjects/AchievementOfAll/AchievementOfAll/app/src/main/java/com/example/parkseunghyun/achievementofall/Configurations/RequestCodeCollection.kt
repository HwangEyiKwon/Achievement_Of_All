package com.example.parkseunghyun.achievementofall.Configurations

/**
    REFACTORED
    TODO: 오류생기는거 없는지 체크
 */

object RequestCodeCollection {

    const val REQUEST_DEFAULT_CREATION = 0
    const val REQUEST_RETURN_FROM_JOINED_CONTENT = 1
    const val REQUEST_RETURN_FROM_SEARCH = 2
    const val REQUEST_RETURN_FROM_PROFILE_EDIT = 3
    const val REQUEST_RETURN_FROM_PROFILE_IMAGE_SELECTION = 4
    const val REQUEST_RETURN_FRON_CONFIRM_JOIN = 5
    const val REQUEST_RETURN_FROM_EXOPLAYER = 6
    const val REQUEST_RETURN_FROM_CONTENT_PENALTY = 7
    const val REQUEST_RETURN_FROM_CONTENT_REWARD = 8
    const val REQUEST_RETURN_FROM_CONTENT_REPORT = 9
    const val REQUEST_RETURN_FROM_VIDEO_RECORD = 10

    var IS_FCM_FLAG = false

    const val GRANT_REQUEST_READ = 100
    const val GRANT_REQUEST_WRITE = 101
    const val GRANT_REQUEST_CAMERA = 102

}