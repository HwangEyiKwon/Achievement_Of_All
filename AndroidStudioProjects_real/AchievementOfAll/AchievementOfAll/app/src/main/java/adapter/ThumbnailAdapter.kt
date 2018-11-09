package adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.example.parkseunghyun.achievementofall.R

import model.ThumbnailModel


/**
 * Created by A on 23-03-2018.
 */

class ThumbnailAdapter(private val context: Context, private val thumbnailModels: List<ThumbnailModel>) : RecyclerView.Adapter<ThumbnailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.account_pic_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.thumbnailView.setImageResource(thumbnailModels[position].accountpic!!)
    }

    override fun getItemCount(): Int {
        return thumbnailModels.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var thumbnailView: ImageView

        init {
            thumbnailView = itemView.findViewById(R.id.accountpic)
        }
    }
}
