package com.example.parkseunghyun.achievementofall

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class ExoplayerActivity : AppCompatActivity() {
    // 서버 ip 주소
    private var globalVariables: GlobalVariables ?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress

    private var contentName: String?= null
    private var email: String?= null

    private var token: String?= null
    private var videoPath: String?= null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.exoplayer_view)


        if(intent.getStringExtra("who")=="others"){
            contentName = intent.getStringExtra("contentName")
            email = intent.getStringExtra("email")
            var uri = "$ipAddress/getOthersVideo/$email/$contentName"
            initializePlayer("other", uri)
            println("상대방")
        }

        else if(intent.getStringExtra("who")=="me"){
            contentName = intent.getStringExtra("contentName")
            token = intent.getStringExtra("token")
            videoPath = intent.getStringExtra("videoPath")
            var uri = "$ipAddress/getVideo/$token/$contentName/$videoPath"
            initializePlayer("me", uri)
            println("내꺼")
        }



//        initializePlayer()

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
}
