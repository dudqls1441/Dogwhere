import com.capstone.dogwhere.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.BBS_Common_Post
import com.capstone.dogwhere.BBS_Common_Writing
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.common_bbs_item.view.*
import kotlinx.android.synthetic.main.fragment_common_bbs.*


class BBS_CommonBBS(var tab: String) : Fragment() {

    private val TAG = BBS_CommonBBS::class.java.simpleName
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_common_bbs, container, false)

        val db = FirebaseFirestore.getInstance()
        adapter = GroupAdapter<GroupieViewHolder>()

        db.collection(tab).get().addOnSuccessListener { result ->
            for (document in result) {
                adapter.add(
                    CommonItem(
                        document.get("title").toString(),
                        document.get("content").toString(),
                        document.get("username").toString(),
                        document.get("time").toString(),
                        document.get("uid").toString()
                    )
                )
                Log.d("checkMessageList", document.get("userName").toString())
                Log.d("데이터베이스읽기성공", "${document.id}=>${document.data}")
            }
            bbs_imformation_recyclerview?.adapter = adapter
        }
            .addOnFailureListener {
                Log.w("데이터베이스읽기실패", "Error getting document", it)
            }
        adapter.setOnItemClickListener { item, view ->
            Log.d("title", (item as CommonItem).title)
            Intent(context, BBS_Common_Post::class.java).apply {
                    putExtra("title", (item).title)
                    putExtra("content", (item).content)
                    putExtra("name", (item).username)
                    putExtra("time", (item).time)
                    putExtra("uid", (item).uid)
                }.run { context?.startActivity(this) }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_imformation_writing.setOnClickListener {
            startActivity(Intent(context, BBS_Common_Writing()::class.java).apply {
                putExtra(
                    "tab",
                    tab
                )
            })

            Log.d("joo", "tab0:" + tab)

        }

    }
    class CommonItem(val title :String,val content:String, val username:String,val time:String, val uid:String) : Item<GroupieViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.common_bbs_item
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.free_bbs_title.setText(title)
            viewHolder.itemView.free_bbs_content.setText(content)
            viewHolder.itemView.free_bbs_name.setText(username)
            viewHolder.itemView.free_bbs_time.setText(time)
        }
    }
}