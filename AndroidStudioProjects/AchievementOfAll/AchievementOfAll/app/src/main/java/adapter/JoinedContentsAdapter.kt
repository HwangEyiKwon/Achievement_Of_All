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
import com.example.parkseunghyun.achievementofall.ContentsHomeActivity
import com.example.parkseunghyun.achievementofall.Configurations.GlobalVariables
import com.example.parkseunghyun.achievementofall.Configurations.RequestCodeCollection
import com.example.parkseunghyun.achievementofall.R
import de.hdodenhof.circleimageview.CircleImageView

/**
    REFARCTORED
 */

class JoinedContentsAdapter(private val context: Context, private val joinedContentsModels: List<model.JoinedContentsModel>) : RecyclerView.Adapter<JoinedContentsAdapter.ViewHolder>() {

    private var globalVariables: GlobalVariables?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_story, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val contentImage = joinedContentsModels[position].name

        Glide
                .with(this.context)
                .load("${ipAddress}/getContentImage/${contentImage}")
                .apply(RequestOptions().skipMemoryCache(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(holder.profile)

        holder.name.text = joinedContentsModels[position].name

    }

    override fun getItemCount(): Int {

        return this.joinedContentsModels.size

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

            val goToContents = Intent(context, ContentsHomeActivity::class.java)
            goToContents.putExtra("contentName", name.text)
            val contextToActivity = context as Activity
            contextToActivity.startActivityForResult(goToContents, RequestCodeCollection.REQUEST_RETURN_FROM_JOINED_CONTENT)

        }

    }

}
