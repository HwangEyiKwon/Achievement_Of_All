package com.example.parkseunghyun.achievementofall

import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.example.parkseunghyun.achievementofall.Configurations.GlobalVariables
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import org.json.JSONObject

class ExoplayerActivity : AppCompatActivity() {
    // 서버 ip 주소
    private var globalVariables: GlobalVariables?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress

    private var contentName: String?= null
    private var email: String?= null

    private var token: String?= null
    private var videoPath: String?= null
    private var isAuthen: Int?= null

    private var check: Int? = null

    override fun onDestroy() {
        super.onDestroy()
        println("DESTROY")

        finish()
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.exoplayer_view)


        val successButton = findViewById(R.id.success) as ImageView
        val failButton = findViewById(R.id.fail) as ImageView
        val authorizeButton = findViewById(R.id.authorize_button) as Button
        authorizeButton.isEnabled = false

        if(intent.getStringExtra("who")=="others"){
            contentName = intent.getStringExtra("contentName")
            email = intent.getStringExtra("email")
            var uri = "$ipAddress/getOthersVideo/$email/$contentName"
            token = loadToken()
            initializePlayer("other", uri)

            println("상대방" + email + token)


            successButton.setOnClickListener {

                successButton.layoutParams.height = 100
                successButton.layoutParams.width = 100

                failButton.layoutParams.height = 50
                failButton.layoutParams.width = 50

                successButton.requestLayout()
                failButton.requestLayout()

                authorizeButton.isEnabled = true

                check = 1
            }
            failButton.setOnClickListener {

                successButton.layoutParams.height = 50
                successButton.layoutParams.width = 50

                failButton.layoutParams.height = 100
                failButton.layoutParams.width = 100
                successButton.requestLayout()
                failButton.requestLayout()

                authorizeButton.isEnabled = true

                check = 0
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

            if(isAuthen == 1){
                failButton.visibility = View.GONE

            }else if(isAuthen == 0){
                successButton.visibility = View.GONE
            }
        }
        authorizeButton.setOnClickListener{

            checkVideo()
            this.finish()
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
        val player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

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
        player.prepare(videoSource);

    }
    private fun checkVideo(){

        val jsonObject = JSONObject()

        jsonObject.put("authenInfo", check)
        jsonObject.put("contentName",contentName)
        jsonObject.put("token",token)
        jsonObject.put("email",email)

        println("첵첵")
        VolleyHttpService.checkVideo(this, jsonObject) { success ->
            println(success)
        }
    }
}
