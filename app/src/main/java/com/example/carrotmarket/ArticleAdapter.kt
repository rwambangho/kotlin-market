package com.example.carrotmarket

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ArticleAdapter(status: String) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val missData = db.collection("article")
    private var itemlist: ArrayList<ItemData> = arrayListOf()

    init {
        when (status) {
            "selling" -> {
                missData.orderBy("uploadtime", Query.Direction.DESCENDING)
                    .addSnapshotListener { querySnapshot, _ ->
                        itemlist.clear()
                        if (querySnapshot != null) {
                            for (snapshot in querySnapshot.documents) {
                                var item = snapshot.toObject(ItemData::class.java)
                                if (item != null) {
                                    val data = item.sellvalue
                                    if (data != null && data.contains("0", ignoreCase = true)) {
                                        itemlist.add(item)
                                    }
                                }
                            }
                            notifyDataSetChanged()
                        }
                    }
            }
            "complete" -> {
                missData.orderBy("uploadtime", Query.Direction.DESCENDING)
                    .addSnapshotListener { querySnapshot, _ ->
                        itemlist.clear()
                        if (querySnapshot != null) {
                            for (snapshot in querySnapshot.documents) {
                                var item = snapshot.toObject(ItemData::class.java)
                                if (item != null) {
                                    val data = item.sellvalue
                                    if (data != null && data.contains("1", ignoreCase = true)) {
                                        itemlist.add(item)
                                    }
                                }
                            }
                            notifyDataSetChanged()
                        }
                    }
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_article,parent,false)
        return ViewHolder(view)
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val title: TextView = view.findViewById(R.id.itemtitle)
        val price: TextView = view.findViewById(R.id.itemdate)
        val itemimg: ImageView = view.findViewById(R.id.itemimg)
        val value : TextView = view.findViewById(R.id.sellvalue)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemlist.getOrNull(position) // null이 아닌 경우에만 사용
        if (currentItem != null) {
            val viewholder = (holder as ViewHolder).itemView
            holder.title.text = currentItem.title
            holder.price.text = "${currentItem.price}원"

            if (currentItem.sellvalue == "0") {
                holder.value.text = "판매중"
            } else {
                holder.value.text = "판매완료"
            }

            Glide.with(holder.itemimg)
                .load(currentItem.uri)
                .into(holder.itemimg)

            viewholder.setOnClickListener {
                val context = viewholder.context
                val intent = Intent(context, DetailActivity::class.java)
                val inf = hashMapOf(
                    "title" to currentItem.title,
                    "price" to currentItem.price,
                    "uri" to currentItem.uri,
                    "id" to currentItem.sellerId,
                    "value" to currentItem.sellvalue, // 'value'가 아닌 'sellvalue'로 수정
                    "content" to currentItem.content,
                    "uid" to currentItem.uid,
                )
                intent.putExtra("info", inf)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemlist.size
    }
}