package com.cookandroid.sharemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.core.utilities.encoding.CustomClassMapper

class ChatAdapter(val postDataList : ArrayList<PostData>) : RecyclerView.Adapter<ChatAdapter.CustomViewHolder>(){

    private lateinit var mDataset : List<ChatData>



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        val vh : CustomViewHolder = CustomViewHolder(view)
        return vh
    }

    override fun onBindViewHolder(holder: ChatAdapter.CustomViewHolder, position: Int) {
        val chat : ChatData = mDataset.get(position)
    }

    override fun getItemCount(): Int {
        if(mDataset == null){
            return 0
        }else{
            return mDataset.size
        }
    }

    class CustomViewHolder(v : View) : RecyclerView.ViewHolder(v){
        lateinit var TextView_title : TextView
        lateinit var TextView_content : TextView
        lateinit var rootView : View

    }

    fun getChat(position : Int) : ChatData {
        if(mDataset != null){
            return mDataset.get(position)
        }else{
            return null!!
        }
    }




}