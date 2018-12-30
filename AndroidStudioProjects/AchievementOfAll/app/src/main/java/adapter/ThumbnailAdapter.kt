package adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.parkseunghyun.achievementofall.Configurations.GlobalVariables
import com.example.parkseunghyun.achievementofall.ExoplayerActivity
import com.example.parkseunghyun.achievementofall.R
import model.ThumbnailModel
import org.jetbrains.anko.startActivity

/**
    REFARCTORED
 */

// ThumbnailAdapter
// 동영상 리스트에 관련된 어댑터입니다.
class ThumbnailAdapter(private val context: Context, private val thumbnailModels: List<ThumbnailModel>) : RecyclerView.Adapter<ThumbnailAdapter.ViewHolder>() {

    private var globalVariables: GlobalVariables?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress
    private var videoFullName: TextView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_thumbnail, parent, false)
        videoFullName = view.findViewById(R.id.video_name)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val who = thumbnailModels[position].who

        if(who == "me"){

            val videoPath = thumbnailModels[position].videoPath!!.getString("path")

            val jwtToken = thumbnailModels[position].userToken
            val contentName = thumbnailModels[position].contentName

            Glide
                    .with(context)
                    .load("${ipAddress}/getVideo/${jwtToken}/${contentName}/${videoPath}")
                    .apply(RequestOptions().fitCenter())
                    .apply(RequestOptions().centerCrop())
                    .apply(RequestOptions().transform(RoundedCorners(20)))
                    .into(holder.thumbnailView)

            holder.videoName = videoPath

            var videoDateAndName:String = holder.videoName.toString()
            videoDateAndName += "\n"
            videoDateAndName += contentName

            videoFullName?.text = videoDateAndName


        } else if(who == "other"){

            val videoPath = thumbnailModels[position].videoPath!!.getString("path")

            val email = thumbnailModels[position].userEmail
            val contentName = thumbnailModels[position].contentName

            Glide
                    .with(context)
                    .load("${ipAddress}/getOtherUserVideo/${email}/${contentName}/${videoPath}")
                    .apply(RequestOptions().fitCenter())
                    .apply(RequestOptions().centerCrop())
                    .apply(RequestOptions().transform(RoundedCorners(20)))
                    .into(holder.thumbnailView)

            holder.videoName = videoPath

            var videoDateAndName:String = holder.videoName.toString()
            videoDateAndName += "\n"
            videoDateAndName += contentName

            videoFullName?.text = videoDateAndName

        }

    }

    override fun getItemCount(): Int {

        return thumbnailModels.size

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener {

        internal var thumbnailView: ImageView
        internal var videoName: String ?= null

        init {

            itemView.setOnClickListener(this)
            thumbnailView = itemView.findViewById(R.id.accountpic)

        }

        override fun onClick(v: View) {

            val clickedPos = adapterPosition
            val who = thumbnailModels[clickedPos].who

            if(who == "me"){

                val jwtToken = thumbnailModels[clickedPos].userToken
                val contentName = thumbnailModels[clickedPos].contentName
                val videoPath = thumbnailModels[clickedPos].videoPath!!.getString("path")
                val isAuthen = thumbnailModels[clickedPos].videoPath!!.getInt("authen")

                context.startActivity<ExoplayerActivity>(

                        "who" to "me",
                        "token" to jwtToken,
                        "contentName" to contentName,
                        "videoPath" to videoPath,
                        "isAuthen" to isAuthen

                )

            } else if(who == "other"){

                val email = thumbnailModels[clickedPos].userEmail
                val contentName = thumbnailModels[clickedPos].contentName
                val videoPath = thumbnailModels[clickedPos].videoPath!!.getString("path")
                val isAuthen = thumbnailModels[clickedPos].videoPath!!.getInt("authen")

                context.startActivity<ExoplayerActivity>(

                        "who" to "other",
                        "email" to email,
                        "contentName" to contentName,
                        "videoPath" to videoPath,
                        "isAuthen" to isAuthen

                )
            }
        }

    }

}
