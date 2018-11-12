package com.example.parkseunghyun.achievementofall

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class ContentsFirst_pager : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view =  inflater!!.inflate(R.layout.contents_fragment_1, container, false)

        val bt = view.findViewById(R.id.button3) as Button

        bt.setOnClickListener {
//            println("conFirst")
//            (activity as HomeActivity).destroyAllFragment()
//            println("conFirst1")
//            (activity as HomeActivity).createHomePager()
//            println("conFirst2")
            initializePlayer(view)
        }
        return view

    }
    //    override fun onStart() {
//        super.onStart()
//        initializePlayer()
//    }

    fun initializePlayer(view: View){
        // Create a default TrackSelector
        val bandwidthMeter =  DefaultBandwidthMeter();
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter);
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory);

        //Initialize the player
        val player = ExoPlayerFactory.newSimpleInstance(this.context, trackSelector);

        //Initialize simpleExoPlayerView
        val simpleExoPlayerView = view.findViewById(R.id.simpleExoPlayerView) as SimpleExoPlayerView

        simpleExoPlayerView.setPlayer(player)

        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory = DefaultDataSourceFactory(this.context, Util.getUserAgent(this.context, "CloudinaryExoplayer"));

        // Produces Extractor instances for parsing the media data.
        val extractorsFactory = DefaultExtractorsFactory();

        // This is the MediaSource representing the media to be played.
        val videoUri = Uri.parse("http://192.168.8.97:3000/video");
        val videoSource =  ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);

        // Prepare the player with the source.
        player.prepare(videoSource);

    }

}