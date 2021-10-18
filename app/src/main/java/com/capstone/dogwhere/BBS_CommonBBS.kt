import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.BBS_Common_Post
import com.capstone.dogwhere.BBS_Common_Writing
import com.capstone.dogwhere.DTO.BBS_CommonItem
import com.capstone.dogwhere.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_common_bbs.*


class BBS_CommonBBS(var tab: String) : Fragment() {

    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_common_bbs, container, false)

        val db = FirebaseFirestore.getInstance()
        adapter = GroupAdapter<GroupieViewHolder>()

        db.collection(tab).orderBy("time", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    adapter.add(
                        BBS_CommonItem(
                            document.get("title").toString(),
                            document.get("content").toString(),
                            document.get("username").toString(),
                            document.get("time").toString(),
                            document.get("uid").toString(),
                            document.get("oid").toString(),
                            Integer.parseInt(document.get("heartCnt").toString()),
                            Integer.parseInt(document.get("visitCnt").toString())
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
            Log.d("title", (item as BBS_CommonItem).title)
            db.collection(tab).document((item).oid).update("visitCnt", FieldValue.increment(1))
                .addOnSuccessListener { Log.d("joo", "Success Plus Visit Count") }
                .addOnFailureListener { e -> Log.w("joo", "Error Visit Count", e) }
            Intent(context, BBS_Common_Post::class.java).apply {
                putExtra("tab", tab)
                putExtra("title", (item).title)
                putExtra("content", (item).content)
                putExtra("name", (item).username)
                putExtra("time", (item).time)
                putExtra("uid", (item).uid)
                putExtra("oid", (item).oid)
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

}