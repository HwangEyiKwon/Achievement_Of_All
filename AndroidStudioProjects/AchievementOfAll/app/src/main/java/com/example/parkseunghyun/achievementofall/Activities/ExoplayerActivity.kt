package com.example.parkseunghyun.achievementofall

import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.parkseunghyun.achievementofall.Configurations.GlobalVariables
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import org.json.JSONObject
import org.w3c.dom.Text

class ExoplayerActivity : AppCompatActivity() {
    // 서버 ip 주소
    private var globalVariables: GlobalVariables?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress

    private var contentName: String?= null
    private var email: String?= null

    private var fab_open: Animation? = null
    private var fab_close: Animation? = null

    private var token: String?= null
    private var videoPath: String?= null
    private var isAuthen: Int?= null

    private var check: Int? = null
    private var checkReasonBox: LinearLayout ? = null
    private var checkReasonEditText: EditText? = null
    private var checkReason: String? = null
    private var time: Long = 0
    private var player:SimpleExoPlayer? = null

    private var isFabOpen:Boolean = false


    override fun onDestroy() {
        finish()
        super.onDestroy()
        println("DESTROY")
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() - time >= 2000){
            time = System.currentTimeMillis()
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 영상을 종료합니다.", Toast.LENGTH_SHORT).show();
        }
        else if(System.currentTimeMillis() - time < 2000){
            player!!.stop()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_exoplayer)

        fab_open = AnimationUtils.loadAnimation(applicationContext!!, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(applicationContext!!, R.anim.fab_close);

        checkReasonEditText = findViewById(R.id.checkReason)

        val successButton = findViewById(R.id.success) as ImageView
        val failButton = findViewById(R.id.fail) as ImageView
        val notYetButton = findViewById(R.id.notYet) as ImageView
        checkReasonBox = findViewById(R.id.checkReasonLayout)

        val authorizeButton = findViewById(R.id.authorize_button) as Button
        authorizeButton.isEnabled = false

        if(intent.getStringExtra("who")=="others"){
            contentName = intent.getStringExtra("contentName")
            email = intent.getStringExtra("email")
            var uri = "$ipAddress/getOthersVideo/$email/$contentName"
            token = loadToken()
            initializePlayer("other", uri)

            println("상대방" + email + token)
            notYetButton.visibility = View.GONE

            successButton.setOnClickListener {

                successButton.setPadding(0, 0, 0, 0)

                failButton.setPadding(50, 50, 50, 50)

                successButton.requestLayout()
                failButton.requestLayout()

                checkReasonBox!!.visibility = View.GONE
                checkReasonBox?.startAnimation(fab_close)
                authorizeButton.isEnabled = true

                check = 1
            }

            failButton.setOnClickListener {

                successButton.setPadding(50, 50, 50, 50)


                failButton.setPadding(0, 0, 0, 0)


                successButton.requestLayout()
                failButton.requestLayout()

                checkReasonBox!!.visibility = View.VISIBLE
                checkReasonBox?.startAnimation(fab_open)
                checkReasonEditText!!.setMovementMethod(ScrollingMovementMethod())

                Toast.makeText(this, "실패 사유를 적어주세요.", Toast.LENGTH_SHORT).show()

                authorizeButton.isEnabled = true

                check = 0
            }

        }else if(intent.getStringExtra("who")=="other"){

            authorizeButton.visibility = View.GONE

            contentName = intent.getStringExtra("contentName")
            email = intent.getStringExtra("email")
            videoPath = intent.getStringExtra("videoPath")
            isAuthen = intent.getIntExtra("isAuthen",-1)

            var uri = "$ipAddress/getOtherUserVideo/$email/$contentName/$videoPath"
            initializePlayer("me", uri)

            if(isAuthen == 1){ // 성공
                failButton.visibility = View.GONE
                notYetButton.visibility = View.GONE

                successButton.isEnabled = true
                successButton.isClickable = true
                successButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.rotater2))

                successButton.setOnClickListener {
                    successButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.shaker))
                    println("왜안되니?")
                    anim("인증에 성공한 영상입니다.")
                    Toast.makeText(this, "인증에 성공한 영상입니다.", Toast.LENGTH_SHORT).show()
                }

            }else if(isAuthen == 0){ // 실패
                successButton.visibility = View.GONE
                notYetButton.visibility = View.GONE

                failButton.isEnabled = true
                failButton.isClickable = true
                failButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.rotater2))

                failButton.setOnClickListener {
                    failButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.shaker))
                    println("왜안되니?")
                    anim("인증에 실패한 영상입니다.")
                    Toast.makeText(this, "인증에 실패한 영상입니다.", Toast.LENGTH_SHORT).show()
                }

            }else{ // 대기
                successButton.visibility = View.GONE
                failButton.visibility = View.GONE

                notYetButton.isEnabled = true
                notYetButton.isClickable = true
                notYetButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.rotater2))

                notYetButton.setOnClickListener {
                    notYetButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.shaker))
                    println("왜안되니?")
                    anim("아직 인증받지 못한 영상입니다.")
                    Toast.makeText(this, "아직 인증받지 못한 영상입니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        else if(intent.getStringExtra("who")=="me"){

            authorizeButton.visibility = View.GONE

            contentName = intent.getStringExtra("contentName")
            token = intent.getStringExtra("token")
            videoPath = intent.getStringExtra("videoPath")
            isAuthen = intent.getIntExtra("isAuthen",-1)

            var uri = "$ipAddress/getVideo/$token/$contentName/$videoPath"
            initializePlayer("me", uri)

            if(isAuthen == 1){ // 성공
                failButton.visibility = View.GONE
                notYetButton.visibility = View.GONE

                successButton.isEnabled = true
                successButton.isClickable = true
                successButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.rotater2))

                successButton.setOnClickListener {
                    successButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.shaker))
                    println("왜안되니?")
                    anim("인증에 성공한 영상입니다.")
                    Toast.makeText(this, "인증에 성공한 영상입니다.", Toast.LENGTH_SHORT).show()
                }

            }else if(isAuthen == 0){ // 실패
                successButton.visibility = View.GONE
                notYetButton.visibility = View.GONE

                failButton.isEnabled = true
                failButton.isClickable = true
                failButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.rotater2))

                failButton.setOnClickListener {
                    failButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.shaker))
                    println("왜안되니?")
                    anim("인증에 실패한 영상입니다.")
                    Toast.makeText(this, "인증에 실패한 영상입니다.", Toast.LENGTH_SHORT).show()
                }

            }else{ // 대기
                successButton.visibility = View.GONE
                failButton.visibility = View.GONE

                notYetButton.isEnabled = true
                notYetButton.isClickable = true
                notYetButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.rotater2))

                notYetButton.setOnClickListener {
                    notYetButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.shaker))
                    println("왜안되니?")
                    anim("아직 인증받지 못한 영상입니다.")
                    Toast.makeText(this, "아직 인증받지 못한 영상입니다.", Toast.LENGTH_SHORT).show()
                }
            }

        }
        authorizeButton.setOnClickListener{

            if(check == 0){ // X 클릭 시
                if (checkReasonEditText!!.text.toString().replace(" ","").equals("")) {
                    Toast.makeText(this, "사유를 적어주셔야 합니다.", Toast.LENGTH_LONG).show()
                }
                else { // 사유까지 적고난 뒤
                    checkReason = checkReasonEditText!!.text.toString()
                    checkVideo()
                    player!!.stop() // 이게 꺼도 소리나는걸 방지.
                    Toast.makeText(this, "인증이 완료되었습니다.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
            else if (check == 1){ // O 클릭 시
                checkReason = ""
                checkVideo()
                player!!.stop() // 이게 꺼도 소리나는걸 방지.
                Toast.makeText(this, "인증이 완료되었습니다.", Toast.LENGTH_LONG).show()
                finish()
            }

        }

    }

    fun anim(alarm : String) {

        if (isFabOpen) {

            checkReasonBox!!.visibility = View.GONE
            checkReasonBox?.startAnimation(fab_close)
            isFabOpen = false

        } else {

            checkReasonBox!!.visibility = View.VISIBLE
            checkReasonEditText?.setText(alarm)
            checkReasonEditText?.isFocusable = false
            checkReasonEditText?.isClickable = false
            checkReasonEditText?.isEnabled = false

            checkReasonBox?.startAnimation(fab_open)
            isFabOpen = true

        }
    }

    private fun loadToken(): String{
        var auto = PreferenceManager.getDefaultSharedPreferences(this)

        return auto.getString("token", "")
    }



    fun initializePlayer(mo: String, uri: String){

        // Create a default TrackSelector
        val bandwidthMeter =  DefaultBandwidthMeter();
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter);
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory);

        //Initialize the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        //Initialize simpleExoPlayerView
        val simpleExoPlayerView = findViewById(R.id.simpleExoPlayerView) as SimpleExoPlayerView

        simpleExoPlayerView.setPlayer(player)

        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "CloudinaryExoplayer"));

        // Produces Extractor instances for parsing the media data.
        val extractorsFactory = DefaultExtractorsFactory();

        // This is the MediaSource representing the media to be played.
        var videoUri = Uri.parse(uri);


        val videoSource =  ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);

        // Prepare the player with the source.
        player!!.prepare(videoSource);

    }
    private fun checkVideo(){




        val jsonObject = JSONObject()

        jsonObject.put("authenInfo", check)
        jsonObject.put("checkReason", checkReason)
        jsonObject.put("contentName",contentName)
        jsonObject.put("token",token)
        jsonObject.put("email",email)

        println("첵첵")
        VolleyHttpService.checkVideo(this, jsonObject) { success ->
            println(success)
        }
    }
}
