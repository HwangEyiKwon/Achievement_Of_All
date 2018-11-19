package adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.parkseunghyun.achievementofall.ContentsHomeActivity
import com.example.parkseunghyun.achievementofall.GlobalVariables
import com.example.parkseunghyun.achievementofall.R
import com.example.parkseunghyun.achievementofall.RecyclerViewClickListener
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.startActivity

/**
 * Created by A on 23-03-2018.
 */

class JoinedContentsAdapter(private val context: Context, private val joinedContentsModels: List<model.JoinedContentsModel>, itemListener: RecyclerViewClickListener) : RecyclerView.Adapter<JoinedContentsAdapter.ViewHolder>() {

    // 서버 ip 주소
    private var globalVariables: GlobalVariables ?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress

//    init {
//        itemListener = itemListener
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stories_view, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // 사용자 참여 컨텐츠 이미지 받아오기
        println("이미지 이름"+joinedContentsModels[position].name)
        val contentImage = joinedContentsModels[position].name
        Glide.with(this.context).load("${ipAddress}/getContentImage/${contentImage}").into(holder.profile)
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

            // 해당 컨텐츠 홈으로 이동
            Toast.makeText(v.context, "You clicked "+ name.text, Toast.LENGTH_SHORT).show()
            context.startActivity<ContentsHomeActivity>(
                    // 컨텐츠 이름 넘기기
                    "contentName" to  name.text
            )
        }

    }

    companion object {
        private val itemListener: RecyclerViewClickListener? = null
    }

}