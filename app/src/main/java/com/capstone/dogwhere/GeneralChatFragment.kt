
import com.capstone.dogwhere.R

import android.content.Intent
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.Chatting_Room_Activity
import com.capstone.dogwhere.MessageListItem
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.general_chat_fragment.*



class GeneralChatFragment : Fragment() {

    private val TAG = GeneralChatFragment::class.java.simpleName
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.general_chat_fragment, container, false)

        val db = FirebaseFirestore.getInstance()
        adapter = GroupAdapter<GroupieViewHolder>()
        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                adapter.add(
                    MessageListItem(
                        document.get("userName").toString(),
                        "users",
                        document.get("uid").toString(),
                        "19:00"
                    )
                )
                Log.d("checkMessageList", document.get("userName").toString())
                Log.d("데이터베이스읽기성공", "${document.id}=>${document.data}")
            }
            recycler_chatlist_list.adapter = adapter
        }
            .addOnFailureListener {
                Log.w("데이터베이스읽기실패", "Error getting document", it)
            }
        adapter.setOnItemClickListener { item, view ->
            Log.d("이름", (item as MessageListItem).name)
            val name = (item).name

            val intent = Intent(context,Chatting_Room_Activity::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
        }
        return view
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }
}

