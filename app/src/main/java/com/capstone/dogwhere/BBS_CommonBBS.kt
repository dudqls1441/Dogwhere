import com.capstone.dogwhere.BBS_post
import com.capstone.dogwhere.R

//package com.capstone.dogwhere




import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.dogwhere.BBS_Common_Writing
import com.capstone.dogwhere.DTO.BBS_Common
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_common_bbs.*
import kotlinx.android.synthetic.main.imformation_bbs_item.view.*
import kotlin.collections.ArrayList

class BBS_CommonBBS(var tab:String) : Fragment() {

    lateinit var information: ArrayList<BBS_Common>
    private val TAG = BBS_CommonBBS::class.java.simpleName
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_common_bbs, container, false)
        recyclerView =
            view.findViewById<RecyclerView>(R.id.bbs_imformation_recyclerview) as RecyclerView
        initData()
        Log.d("imformation==", information.size.toString())
        val adapter1 = RecyclerViewAdapter(context!!, information)
        recyclerView.adapter = adapter1
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter1.setOnItemClickListener(object :RecyclerViewAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: ArrayList<BBS_Common>, position: Int) {
                Toast.makeText(context!!,"넘어가나?",Toast.LENGTH_LONG).show()
                Intent(context,BBS_post::class.java).apply {
                    putExtra("data",data)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run{startActivity(this)}
            }
        })

        return view
    }


    private fun initData() {
        val db = Firebase.firestore
        information = arrayListOf()
        information.clear()

        db.collection(tab).get()
            .addOnSuccessListener {
                for (document in it) {
                    val item = document.toObject(BBS_Common::class.java)
                    information.add(item)

                    Log.d("imformation=", information.toString())
                    Log.d(TAG, "addImformationSuccees")
                }


            }.addOnFailureListener {
                it.localizedMessage
                Log.d("insert", "fail")
            }

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_imformation_writing.setOnClickListener {
            startActivity(Intent(context, BBS_Common_Writing()::class.java).apply {putExtra("tab", tab)})

            Log.d("joo", "tab0:"+tab)

        }

        btnserch.setOnClickListener {
            (recyclerView.adapter as RecyclerViewAdapter).search(
                searchword.text.toString(),
            )
        }


        recyclerView.setOnClickListener { object :RecyclerViewAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: ArrayList<BBS_Common>, position: Int) {
                Intent(context, BBS_post::class.java).apply {
                    putExtra("data",data)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { startActivity(this) }
            }

        } }



//        var searchOption = "name"
//        imformation_bbs_spinner.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener,
//                AdapterView.OnItemClickListener {
//                override fun onNothingSelected(p0: AdapterView<*>?) {
//
//                }
//
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    when (imformation_bbs_spinner.getItemAtPosition(position)) {
//                        "이름" -> {
//                            searchOption = "title"
//                        }
//                        "번호" -> {
//                            searchOption = "content"
//                        }
//                    }
//                }
//
//                override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                }
//
//            }



    }

    class RecyclerViewAdapter(
        val context: Context,
        val imformationitem: ArrayList<BBS_Common>
    ) :
        RecyclerView.Adapter<RecyclerViewAdapter.Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view =
                LayoutInflater.from(context).inflate(R.layout.imformation_bbs_item, parent, false)

            return Holder(view)
        }


        interface OnItemClickListener : SearchManager.OnCancelListener {
            fun onItemClick(v:View, data: ArrayList<BBS_Common>, position: Int)
            override fun onCancel() {

            }
        }
        private var listener : OnItemClickListener? =null


        fun setOnItemClickListener(listener: OnItemClickListener){
            this.listener = listener
        }



        override fun getItemCount(): Int {
            return imformationitem.size
        }

        fun search(serchword: String) {
            val db = Firebase.firestore
//            db.collection("impormation_bbs").addSnapshotListener { querySnapshot, error ->
//                imformationitem.clear()
//                for (snapshot in querySnapshot!!.documents) {
//                    if (snapshot!!.contains(serchword)) {
//                        var item = snapshot.toObject(BBS_Imformation::class.java)
//                        imformationitem.add(item!!)
//                    }
//                }
//                notifyDataSetChanged()
//            }
            db.collection("impormation_bbs").get()
                .addOnSuccessListener {
                    for (document in it){
                        if(document!!.contains(serchword)){
                            val item = document.toObject(BBS_Common::class.java)
                            imformationitem.add((item!!))
                        }
                    }
                    notifyDataSetChanged()
                }
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            var viewholder = (holder as RecyclerView.ViewHolder).itemView

            viewholder.imformation_bbs_title.text = imformationitem[position].title
            viewholder.imformation_bbs_content.text = imformationitem[position].content
            viewholder.imformation_bbs_name.text = imformationitem[position].username
            viewholder.imformation_bbs_time.text = imformationitem[position].time
        }



        inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        }
    }


//    class ListViewAdapter(private val items: MutableList<BBS_Imformation>) : BaseAdapter() {
//        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
//            var converView = view
//            if (converView == null) converView = LayoutInflater.from(parent?.context)
//                .inflate(R.layout.imformation_bbs_item, parent, false)
//            val item: BBS_Imformation = items[position]
//            converView!!.imformation_bbs_title.text = item.title
//            converView!!.imformation_bbs_name.text = item.username
//            converView!!.imformation_bbs_content.text = item.content
//            converView!!.imformation_bbs_time.text = item.time
//
//            return converView
//
//        }
//
//        override fun getItem(p0: Int): Any {
//            return 0
//        }
//
//        override fun getItemId(position: Int): Long {
//            return position.toLong()
//        }
//
//        override fun getCount(): Int {
//            return items.size
//        }
//
//    }
}


