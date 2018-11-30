package com.example.parkseunghyun.achievementofall.Activities

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.parkseunghyun.achievementofall.Configurations.GlobalVariables
import com.example.parkseunghyun.achievementofall.Configurations.ResultObject
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.Interfaces.ImageUploadInterface
import com.example.parkseunghyun.achievementofall.Interfaces.VideoSendingInterface
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.activity_profile_edit.*
import okhttp3.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.net.URLDecoder
import java.net.URLEncoder


class ProfileEditActivity : AppCompatActivity() {

    // jwt-token
    var jwtToken: String?= null
    var userImage: ImageView?= null

    var name: String ?= null
    var phoneNumber: String ?= null
    var editedPhoneNumber: String? = null
    private val REQUEST_FOR_IMAGE_EDIT = 1

    private var uri: Uri? = null
    private var pathToStoredImage: String? = null

    // 서버 ip 주소
    private var globalVariables: GlobalVariables?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        userImage = findViewById(R.id.userImage)

        name = intent.getStringExtra("name")
        phoneNumber = intent.getStringExtra("phoneNumber")

        edit_nickname.setText(name)
        edit_phone_number.setText(phoneNumber)

        jwtToken = loadToken()

        Glide
                .with(this)
                .load("${ipAddress}/getUserImage/"+jwtToken)
                .apply(RequestOptions().skipMemoryCache(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(userImage)


        println("이미지이미지")
        println(userImage)

        bt_edit.setOnClickListener{
//            edit()
            uploadImageToServer(pathToStoredImage!!)
        }

        bt_image.setOnClickListener {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(intent, REQUEST_FOR_IMAGE_EDIT)

        }
        goPasswordEdit.setOnClickListener {
            startActivity<PasswordEditActivity>(
                    "name" to name,
                    "phoneNumber" to phoneNumber
            )
            finish()
        }

    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // Check which request we're responding to
        println("ERROR CHECK__ " + resultCode)


        if ( resultCode == Activity.RESULT_OK){
            when(requestCode) {
                REQUEST_FOR_IMAGE_EDIT -> {
                    uri = data?.data
                    pathToStoredImage = getRealPathFromURIPath(uri!!, this)
                    userImage!!.setImageURI(uri)
                }
            }
        }else if ( resultCode == Activity.RESULT_CANCELED){
            return
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun uploadImageToServer(pathToVideoFile: String) {
        val videoFile = File(pathToVideoFile)
        val videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile)
        val vFile = MultipartBody.Part.createFormData("video", videoFile.name, videoBody)

        name = edit_nickname.text.toString()

        // TODO: 인코딩해서 보내야되네 싀벌...
        var name_encoded = URLEncoder.encode(name, "utf-8")
        name_encoded = URLDecoder.decode(name_encoded, "utf-8")
        println("한글테스트 " + name_encoded)
        editedPhoneNumber = edit_phone_number.text.toString()

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(object: Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                val original = chain!!.request()
                val request = original.newBuilder()
                        .header("jwt_token", jwtToken)
                        .header("name", name_encoded)
                        .header("phone_number", editedPhoneNumber.toString())
                        .method(original.method(), original.body())
                        .build()

                println("TESTERCHO---" + jwtToken + "_______" + name + "_______" + editedPhoneNumber.toString())
                return chain!!.proceed(request)
            }
        })

        val client = httpClient.build()
        val retrofit = Retrofit.Builder()
                .baseUrl(ipAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) // >>>>>>
                .build()
        val vInterface = retrofit.create(ImageUploadInterface::class.java)
        val serverCom = vInterface.uploadImageToServer(vFile)

        serverCom.enqueue(object : Callback<ResultObject> {
            override fun onResponse(call: Call<ResultObject>, response: Response<ResultObject>) {
                val result = response.body()

                if (!TextUtils.isEmpty(result.success)) {
                    Toast.makeText(applicationContext, "이미지 업로드 완료", Toast.LENGTH_SHORT).show()
                    finish() //여기다가 두는것이 핵심
                }
                else{
                }

            }
            override fun onFailure(call: Call<ResultObject>, t: Throwable) {
                Toast.makeText(applicationContext, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
            }
        })
    }





    private fun getRealPathFromURIPath(contentURI: Uri, activity: Activity): String? {
        val cursor = activity.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {

            return contentURI.path

        } else {

            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return cursor.getString(idx)

        }
    }



    private fun loadToken(): String{
        var auto = PreferenceManager.getDefaultSharedPreferences(this)

        return auto.getString("token", "")
    }
}
