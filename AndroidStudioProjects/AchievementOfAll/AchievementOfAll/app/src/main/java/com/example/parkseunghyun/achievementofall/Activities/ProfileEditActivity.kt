package com.example.parkseunghyun.achievementofall.Activities

import android.Manifest
import android.app.Activity
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
import com.example.parkseunghyun.achievementofall.Configurations.*
import com.example.parkseunghyun.achievementofall.Interfaces.ImageUploadInterface
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.activity_profile_edit.*
import okhttp3.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.net.URLEncoder


/**
    REFARCTORED
    TODO: Glide Placeholder line 126 ~
 */

class ProfileEditActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    var jwtToken: String?= null
    var userImage: ImageView?= null

    var nickName: String? = null
    var phoneNumber: String ?= null

    var editedNickName: String ?= null
    var editedPhoneNumber: String? = null

    private var uri: Uri? = null
    private var pathToStoredImage: String? = null

    private var globalVariables: GlobalVariables?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress

    private var readImageIntent: Intent? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        initViewComponents()

        initButtonListener()


    }

    private fun initButtonListener() {
        button_confirm_edit.setOnClickListener {

            if (edit_nickname.text.toString() == "") {

                Toast.makeText(this, "이름을 적어주세요", Toast.LENGTH_LONG).show()

            } else if (edit_phone_number.text.toString() == "") {

                Toast.makeText(this, "번호를 적어주세요", Toast.LENGTH_LONG).show()

            } else {

                if (pathToStoredImage == null) {

                    editProfileWithoutImage()

                } else {

                    editProfileWithImage(pathToStoredImage!!)

                }
            }

        }

        button_image_select.setOnClickListener {

            if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {

                startActivityForResult(readImageIntent, RequestCodeCollection.REQUEST_RETURN_FROM_PROFILE_IMAGE_SELECTION)

            } else {

                EasyPermissions.requestPermissions(this, getString(R.string.read_file), RequestCodeCollection.GRANT_REQUEST_READ, Manifest.permission.READ_EXTERNAL_STORAGE)

            }

        }

        goPasswordEdit.setOnClickListener {

            startActivity<PasswordEditActivity>(

                    "name" to nickName,
                    "phoneNumber" to phoneNumber

            )

            finish()

        }

    }

    private fun initViewComponents() {

        userImage = findViewById(R.id.userImage)

        nickName = intent.getStringExtra("name")
        phoneNumber = intent.getStringExtra("phoneNumber")

        edit_nickname.setText(nickName)
        edit_phone_number.setText(phoneNumber)

        readImageIntent = Intent()
        readImageIntent!!.type = "image/*"
        readImageIntent!!.action = Intent.ACTION_PICK

        if(GlideLoadingFlag.profileURI != null){

            Glide
                    .with(this)
                    .load(GlideLoadingFlag.profileURI)
                    .apply(RequestOptions().fitCenter())
                    .apply(RequestOptions().centerCrop())
                    .into(userImage)

        } else {

            Glide
                    .with(this)
                    .load("${ipAddress}/getUserImage/" + loadJWTToken())
                    .apply(RequestOptions().fitCenter())
                    .apply(RequestOptions().centerCrop())
                    .apply(RequestOptions().skipMemoryCache(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(userImage)

        }




    }

    private fun editProfileWithoutImage (){

        editedNickName = edit_nickname.text.toString()
        editedPhoneNumber = edit_phone_number.text.toString()

        val jsonObjectToEditProfile = JSONObject()
        jsonObjectToEditProfile.put("name", URLEncoder.encode(editedNickName, "utf-8"))
        jsonObjectToEditProfile.put("phone_number", editedPhoneNumber)
        jsonObjectToEditProfile.put("jwt_token", loadJWTToken())

        VolleyHttpService.editProfileWithoutImage(this, jsonObjectToEditProfile) { success ->

            if (success.get("success") == true) {

                Toast.makeText(this, "프로필 수정 완료", Toast.LENGTH_LONG).show()
                GlideLoadingFlag.setProfileWithOutImageFlag(GlideLoadingFlag.FLAG_UPDATED)

                finish()

            } else {

                Toast.makeText(this, "프로필 수정 실패", Toast.LENGTH_LONG).show()

            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if ( resultCode == -1 ) {

            when(requestCode) {

                RequestCodeCollection.REQUEST_RETURN_FROM_PROFILE_IMAGE_SELECTION -> {

                    uri = data!!.data
                    pathToStoredImage = getRealPathFromURIPath(uri!!, this)
                    saveUriForSelfCaching(uri.toString())

                    Glide
                            .with(this)
                            .load(uri)
                            .apply(RequestOptions().fitCenter())
                            .apply(RequestOptions().centerCrop())
                            .into(userImage)

                }

            }
        }
        else if ( resultCode == Activity.RESULT_CANCELED ) {

            return

        }

    }

    private fun editProfileWithImage(pathToVideoFile: String) {

        val videoFile = File(pathToVideoFile)
        val videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile)
        val multipartVideoFile = MultipartBody.Part.createFormData("video", videoFile.name, videoBody)

        editedNickName = edit_nickname.text.toString()
        editedPhoneNumber = edit_phone_number.text.toString()

        val httpClient = OkHttpClient.Builder()

        httpClient.addInterceptor(object: Interceptor {

            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {

                val original = chain!!.request()
                val request = original.newBuilder()
                        .header("jwt_token", loadJWTToken())
                        .header("name", URLEncoder.encode(editedNickName, "utf-8"))
                        .header("phone_number", editedPhoneNumber.toString())
                        .method(original.method(), original.body())
                        .build()

                return chain.proceed(request)

            }
        })

        val client = httpClient.build()
        val retrofit = Retrofit.Builder()
                .baseUrl(ipAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        val vInterface = retrofit.create(ImageUploadInterface::class.java)
        val serverCom = vInterface.uploadImageToServer(multipartVideoFile)

        serverCom.enqueue(object : Callback<ResultObjectFromRetrofit2> {
            override fun onResponse(call: Call<ResultObjectFromRetrofit2>, response: Response<ResultObjectFromRetrofit2>) {

                val result = response.body()

                if (!TextUtils.isEmpty(result.success)) {

                    GlideLoadingFlag.setProfileWithImageFlag(GlideLoadingFlag.FLAG_UPDATED)
                    Toast.makeText(applicationContext, "프로필 수정 완료", Toast.LENGTH_SHORT).show()
                    finish()

                }

            }
            override fun onFailure(call: Call<ResultObjectFromRetrofit2>, t: Throwable) {

                Toast.makeText(applicationContext, "프로필 수정 실패", Toast.LENGTH_SHORT).show()

            }

        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

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


    private fun loadJWTToken(): String{

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getString("token", "")

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {

        when (requestCode) {

            RequestCodeCollection.GRANT_REQUEST_READ -> {

                startActivityForResult(readImageIntent, RequestCodeCollection.REQUEST_RETURN_FROM_PROFILE_IMAGE_SELECTION)

            }

        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {

        Toast.makeText(applicationContext, "이미지 수정을 위해 권한이 필요합니다.", Toast.LENGTH_SHORT).show()

    }

    private fun saveUriForSelfCaching(imageUri: String){

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editorForSaveImageUri = sharedPref.edit()

        editorForSaveImageUri
                .putString("imageUri", imageUri)
                .apply()

    }

}
