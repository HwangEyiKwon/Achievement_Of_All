package com.example.parkseunghyun.achievementofall

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.startActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        startActivity<HomeActivity>()
        startActivity<LoginActivity>()

    }
//    override fun onStart() {
//        super.onStart()
//        initializePlayer()
//    }
//    fun initializePlayer(){
//        // Create a default TrackSelector
//        val bandwidthMeter =  DefaultBandwidthMeter();
//        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter);
//        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory);
//
//        //Initialize the player
//        val player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
//
//        //Initialize simpleExoPlayerView
//        val simpleExoPlayerView = findViewById(R.id.simpleExoPlayerView) as SimpleExoPlayerView
//
//           simpleExoPlayerView.setPlayer(player)
//
//                // Produces DataSource instances through which media data is loaded.
//        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "CloudinaryExoplayer"));
//
//        // Produces Extractor instances for parsing the media data.
//        val extractorsFactory = DefaultExtractorsFactory();
//
//        // This is the MediaSource representing the media to be played.
//        val videoUri = Uri.parse("http://192.168.3.211:3000/video");
//        val videoSource =  ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
//
//        // Prepare the player with the source.
//        player.prepare(videoSource);
//
//    }

}