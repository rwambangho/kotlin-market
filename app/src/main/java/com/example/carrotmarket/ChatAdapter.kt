package com.example.carrotmarket

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.util.*

class ChatAdapter() : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val missData = db.collection("chats")
    private var itemlist: ArrayList<ChatListItem> = arrayListOf()
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    init {auth.currentUser?.uid.orEmpty()
        missData
            .addSnapshotListener { querySnapshot, _ ->
                itemlist.clear()
                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ChatListItem::class.java)

                    if(item != null){
                        val data = item?.sellerid
                        if(data != null){
                            if(data.contains(auth.currentUser?.uid.orEmpty(),ignoreCase = true)){
                                itemlist.add(item!!)
                            }
                        }
                    }



                }
                notifyDataSetChanged()
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val msg: TextView = view.findViewById(R.id.messagecontent)
        val name: TextView = view.findViewById(R.id.buyertext)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var viewholder = (holder as ViewHolder).itemView
        Log.e("kss",itemlist.size.toString())
        holder.msg.text = "채팅내용 : " + itemlist[position].content
        holder.name.text = "구매자 ID : " + itemlist[position].buyerid

    }


    override fun getItemCount(): Int {
        return itemlist.size
    }

}
