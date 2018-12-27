package adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.parkseunghyun.achievementofall.Configurations.GlobalVariables
import com.example.parkseunghyun.achievementofall.Configurations.RequestCodeCollection
import com.example.parkseunghyun.achievementofall.ExoplayerActivity
import com.example.parkseunghyun.achievementofall.R
import de.hdodenhof.circleimageview.CircleImageView
import model.StoriesModel

/**
    REFACTORED
 */

class StoriesAdapter(private val context: Context, private val storiesModels: List<StoriesModel>) : RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {

    private var globalVariables: GlobalVariables?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_story, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val email = storiesModels[position].email

        Glide
                .with(this.context)
                .load("${ipAddress}/getOtherUserImage/$email")
                .apply(RequestOptions().fitCenter())
                .apply(RequestOptions().centerCrop())
                .apply(RequestOptions().skipMemoryCache(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(holder.profile)

        holder.name.text = storiesModels[position].name

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        internal var profile: CircleImageView
        internal var name: TextView

        init {

            itemView.setOnClickListener(this)
            profile = itemView.findViewById(R.id.profile_image)
            name = itemView.findViewById(R.id.txtname)

        }

        override fun onClick(v: View) {

            val pos = adapterPosition
            val email = storiesModels[pos].email
            val contentName = storiesModels[pos].contentName

            val goToExoPlayer = Intent(context, ExoplayerActivity::class.java)
            goToExoPlayer.putExtra("email", email)
            goToExoPlayer.putExtra("contentName", contentName)
            goToExoPlayer.putExtra("who", "others")
            val contextToActivity = context as Activity

            contextToActivity.startActivityForResult(goToExoPlayer, RequestCodeCollection.REQUEST_RETURN_FROM_EXOPLAYER)

        }
    }

    override fun getItemCount(): Int {

        return storiesModels.size

    }
}
