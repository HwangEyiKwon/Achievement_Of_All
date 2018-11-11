package adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.example.parkseunghyun.achievementofall.HomeActivity
import com.example.parkseunghyun.achievementofall.R
import com.example.parkseunghyun.achievementofall.RecyclerViewClickListener

import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by A on 23-03-2018.
 */

class JoinedContentsAdapter(private val context: Context, private val joinedContentsModels: List<model.JoinedContentsModel>, itemListener: RecyclerViewClickListener) : RecyclerView.Adapter<JoinedContentsAdapter.ViewHolder>() {

//
//    init {
//        itemListener = itemListener
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stories_view, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.profile.setImageResource(joinedContentsModels[position].profile!!)
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
            val pos = adapterPosition

            Toast.makeText(v.context, "You clicked "+ name.text, Toast.LENGTH_SHORT).show()

            val intent = Intent(v.context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            v.context.startActivity(intent)

            //itemListener.recyclerViewListClicked(v, this.getLayoutPosition());

        }

    }

    companion object {
        private val itemListener: RecyclerViewClickListener? = null
    }

}
