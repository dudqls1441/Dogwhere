
import com.capstone.dogwhere.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.BBS_Transaction_Post
import com.capstone.dogwhere.BBS_Transaction_Writing
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_transaction_bbs.*
import kotlinx.android.synthetic.main.transaction_bbs_item.view.*


class BBS_TransactionBBS(var tab:String) : Fragment() {

    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaction_bbs, container, false)

        adapter = GroupAdapter<GroupieViewHolder>()
        val db = FirebaseFirestore.getInstance()
        adapter = GroupAdapter<GroupieViewHolder>()

        db.collection(tab).get().addOnSuccessListener { result ->
            for (document in result) {
                adapter.add(
                    TransItem(
                        document.get("title").toString(),
                        document.get("content").toString(),
                        document.get("username").toString(),
                        document.get("time").toString(),
                        document.get("price").toString(),
                        document.get("uid").toString()
                    )
                )
                Log.d("checkMessageList", document.get("userName").toString())
                Log.d("데이터베이스읽기성공", "${document.id}=>${document.data}")
            }
            bbs_imformation_recyclerview.adapter = adapter
        }
            .addOnFailureListener {
                Log.w("데이터베이스읽기실패", "Error getting document", it)
            }
        adapter.setOnItemClickListener { item, view ->
            Log.d("title", (item as TransItem).title)
            Intent(context, BBS_Transaction_Post::class.java).apply {
                putExtra("title", (item).title)
                putExtra("content", (item).content)
                putExtra("name", (item).username)
                putExtra("time", (item).time)
                putExtra("price", (item).price)
                putExtra("uid", (item).uid)
            }.run { context?.startActivity(this) }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_imformation_writing.setOnClickListener {
            startActivity(Intent(context, BBS_Transaction_Writing()::class.java).apply {putExtra("tab", tab)})

            Log.d("joo", "tab0:"+tab)

        }

    }
    class TransItem(val title :String,val content:String, val username:String,val time:String, val price:String, val uid:String) : Item<GroupieViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.transaction_bbs_item
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.imformation_bbs_title.setText(title)
            viewHolder.itemView.imformation_bbs_content.setText(content)
            viewHolder.itemView.imformation_bbs_name.setText(username)
            viewHolder.itemView.imformation_bbs_time.setText(time)
            viewHolder.itemView.imformation_bbs_price.setText(price)
        }
    }
}