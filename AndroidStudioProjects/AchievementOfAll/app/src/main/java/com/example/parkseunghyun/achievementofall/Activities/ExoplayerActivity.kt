package com.example.parkseunghyun.achievementofall

import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
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

// ExoplayerActivity
// 동영상 재생 화면
class ExoplayerActivity : AppCompatActivity() {

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


    // onDestroy
    // 화면이 종료될 경우
    override fun onDestroy() {
        finish()
        super.onDestroy()
    }

    // onBackPressed (override)
    // 뒤로가기 버튼을 짧은 시간 내 두번 누르면 영상이 종료됩니다.
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

        // 사유
        checkReasonEditText = findViewById(R.id.checkReason)

        val successButton = findViewById(R.id.success) as ImageView
        val failButton = findViewById(R.id.fail) as ImageView
        val notYetButton = findViewById(R.id.notYet) as ImageView
        checkReasonBox = findViewById(R.id.checkReasonLayout)

        val authorizeButton = findViewById(R.id.authorize_button) as Button
        authorizeButton.isEnabled = false

        // 타 사용자 영상 인증
        if(intent.getStringExtra("who")=="others"){
            contentName = intent.getStringExtra("contentName")
            email = intent.getStringExtra("email")
            var uri = "$ipAddress/getOthersVideo/$email/$contentName"
            token = loadToken()
            initializePlayer("other", uri)

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

        }
        // 타 사용자 영상 열람
        else if(intent.getStringExtra("who")=="other"){

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
                    anim("아직 인증받지 못한 영상입니다.")
                    Toast.makeText(this, "아직 인증받지 못한 영상입니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // 본인 영상 열람
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
                    anim("아직 인증받지 못한 영상입니다.")
                    Toast.makeText(this, "아직 인증받지 못한 영상입니다.", Toast.LENGTH_SHORT).show()
                }
            }

        }
        // 인증 버튼을 누를 경우
        authorizeButton.setOnClickListener{

            if(check == 0){ // 실패의 경우
                if (checkReasonEditText!!.text.toString().replace(" ","").equals("")) {
                    Toast.makeText(this, "사유를 적어주셔야 합니다.", Toast.LENGTH_LONG).show()
                }
                else { // 사유
                    checkReason = checkReasonEditText!!.text.toString()
                    checkVideo()
                    player!!.stop() // 이게 꺼도 소리나는걸 방지.
                    Toast.makeText(this, "인증이 완료되었습니다.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
            else if (check == 1){ // 성공의 경우
                checkReason = ""
                checkVideo()
                player!!.stop() // 영상 종료시 소리 오류 해결
                Toast.makeText(this, "인증이 완료되었습니다.", Toast.LENGTH_LONG).show()
                finish()
            }

        }

    }
    // 애니메이션
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

    // loadToken
    // JWT 토큰을 SharedPreference에서 불러옵니다.
    private fun loadToken(): String{
        var auto = PreferenceManager.getDefaultSharedPreferences(this)

        return auto.getString("token", "")
    }


    // Exoplayer 설정
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

    // checkVideo
    // 실제 영상 인증 과정
    private fun checkVideo(){

        val jsonObject = JSONObject()

        jsonObject.put("authenInfo", check)
        jsonObject.put("checkReason", checkReason)
        jsonObject.put("contentName",contentName)
        jsonObject.put("token",token)
        jsonObject.put("email",email)

        VolleyHttpService.checkVideo(this, jsonObject) { success ->
            println(success)
        }
    }
}
