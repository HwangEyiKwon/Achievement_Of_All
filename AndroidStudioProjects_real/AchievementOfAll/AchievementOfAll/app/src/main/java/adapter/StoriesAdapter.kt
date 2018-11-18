package adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.parkseunghyun.achievementofall.ExoplayerActivity
import com.example.parkseunghyun.achievementofall.GlobalVariables
import com.example.parkseunghyun.achievementofall.R
import com.example.parkseunghyun.achievementofall.RecyclerViewClickListener
import de.hdodenhof.circleimageview.CircleImageView
import model.StoriesModel
import org.jetbrains.anko.startActivity

/**
 * Created by A on 23-03-2018.
 */

class StoriesAdapter(private val context: Context, private val storiesModels: List<StoriesModel>, itemListener: RecyclerViewClickListener) : RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {

    // 서버 ip 주소
    private var globalVariables: GlobalVariables?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stories_view, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var email = storiesModels[position].email
        Glide.with(this.context).load("${ipAddress}/getOthersImage/$email").into(holder.profile)
//        holder.profile.setImageResource(storiesModels[position].profile!!)
        holder.name.text = storiesModels[position].name
//        Glide.with(this).load("${ipAddress}/getUserImage/"+jwtToken).into(profile)

    }

    override fun getItemCount(): Int {
        return storiesModels.size
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
            var email = storiesModels[pos].email
            var contentName = storiesModels[pos].contentName

            // 클릭 처리
            Toast.makeText(v.context, "You clicked "+ name.text, Toast.LENGTH_SHORT).show()
            context.startActivity<ExoplayerActivity>(
                    "email" to email,
                    "contentName" to contentName,
                    "who" to "others"
            )


        }
    }

    companion object {
        private val itemListener: RecyclerViewClickListener? = null
    }
}
