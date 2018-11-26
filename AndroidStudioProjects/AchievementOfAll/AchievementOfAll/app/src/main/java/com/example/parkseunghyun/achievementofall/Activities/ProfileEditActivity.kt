package com.example.parkseunghyun.achievementofall.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.parkseunghyun.achievementofall.Configurations.GlobalVariables
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.activity_profile_edit.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import java.io.DataOutputStream
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.URL




class ProfileEditActivity : AppCompatActivity() {

    // jwt-token
    var jwtToken: String?= null
    var userImage: ImageView?= null

    var name: String ?= null
    var phoneNumber: String ?= null

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

        Glide.with(this).load("${ipAddress}/getUserImage/"+jwtToken).into(userImage)


        println("이미지이미지")
        println(userImage)

        bt_edit.setOnClickListener{
            edit()
        }

        bt_image.setOnClickListener {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(intent, 1)

            println("이미지이미지2")
            println(userImage)

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
        if (requestCode == 1) {
            // Make sure the request was successful

                try {
                    // 선택한 이미지에서 비트맵 생성
                    val `in` = contentResolver.openInputStream(data.data!!)
                    println(`in`)

                    val img = BitmapFactory.decodeStream(`in`)
                    `in`!!.close()
                    // 이미지 표시
                    println("개다")
                    println(data.data!!)

                    userImage!!.setImageBitmap(img)


                    val urlString = "${ipAddress}/editUserImage/$jwtToken"

//                    val proj = arrayOf(MediaStore.Images.Media.DATA)
//
//                    val cursor = getContentResolver().query(data.data!!, proj, null, null, null);
//                    val index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//
//                    cursor.moveToFirst();
//                    var path = cursor.getString(index);
//                    var absolutePath = path.substring(5);

//                    val proj = arrayOf(MediaStore.Images.Media.DATA)
//                    val cursor = contentResolver.query(data.data!!, proj, null, null, null)
//                    var column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                    println(column_index)
//                    cursor!!.moveToFirst()
//                    val absolutePath = cursor.getString(column_index)

//                    cursor.close()

                    println("앱솔" )
//                    println(getRealPathFromURI(data.data!!))
                    println(getRealPathFromURIPath( data.data!!,this))

                    var absolutePath = getRealPathFromURIPath( data.data!!,this)

                    DoFileUpload(urlString, absolutePath)


                } catch (e: Exception) {
                    e.printStackTrace()
                }


        }
    }


    private fun getRealPathFromURIPath(contentURI: Uri, activity: Activity): String {
        val cursor = activity.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {
            return contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return cursor.getString(idx)
        }
    }
    fun DoFileUpload(apiUrl: String, absolutePath: String) {
        println("개다2")
        HttpFileUpload(apiUrl, "", absolutePath);
    }

    var lineEnd = "\r\n";
    var twoHyphens = "--";
    var boundary = "*****";

    fun HttpFileUpload(urlString: String, params: String, fileName: String ) {

            println("개다3")
            var mFileInputStream = FileInputStream(fileName);
            var connectUrl = URL(urlString);
            Log.d("Test", "mFileInputStream  is " + mFileInputStream);
            println("개다4")
            // open connection
            var conn = connectUrl.openConnection() as HttpURLConnection;
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            println("개다5")


            // write data
            var dos = DataOutputStream(conn.outputStream);
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName+"\"" + lineEnd);
            dos.writeBytes(lineEnd);

            var bytesAvailable = mFileInputStream.available();
            var maxBufferSize = 1024;
            var bufferSize = Math.min(bytesAvailable, maxBufferSize);

            var buffer = byteArrayOf();
            var bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

            Log.d("Test", "image byte is " + bytesRead);

            // read image
            println("개다6")
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = mFileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
            }

            println("개다7")
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            Log.e("Test" , "File is written");
            mFileInputStream.close();
            dos.flush(); // finish upload...
            println("개다8")
            // get response
//            var ch = 0;
//            var i = conn.getInputStream();
//            var b = StringBuffer();
//            while( ( ch = i.read() ) != -1 ){
//                b.append( (char)ch );
//            }
//            var s= b.toString();
//            Log.e("Test", "result = " + s);
//            mEdityEntry.setText(s);
            dos.close();


    }


    private fun edit(){

        val name = edit_nickname.text.toString()
        val phoneNumber = edit_phone_number.text.toString()
        val token = jwtToken

        val jsonObject = JSONObject()

        jsonObject.put("name", name)
        jsonObject.put("phoneNumber", phoneNumber)
        jsonObject.put("token", token)

       VolleyHttpService.edit(this, jsonObject){success ->

           Toast.makeText(this, "수정 완료.", Toast.LENGTH_LONG).show()
           finish()

       }
    }
    private fun loadToken(): String{
        var auto = PreferenceManager.getDefaultSharedPreferences(this)

        return auto.getString("token", "")
    }
}
