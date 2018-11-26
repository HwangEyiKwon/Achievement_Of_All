package com.example.parkseunghyun.achievementofall.Interfaces

import com.example.parkseunghyun.achievementofall.Configurations.ResultObject
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import com.google.android.gms.tasks.Task



interface VideoSendingInterface {

    fun getTasks(@Header("Content-Range") contentRange: String): Call<List<Task<*>>>
    @Multipart
    @POST("/sendVideo")
    fun uploadVideoToServer(@Part video: MultipartBody.Part): Call<ResultObject>

}
