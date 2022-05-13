package com.aakash.chatbot.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aakash.chatbot.R
import com.aakash.chatbot.entities.Chat

class ChatAdapter(private val dataSet: MutableList<Chat>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(){

    class ChatViewHolder(private val parentAdapter: ChatAdapter, private val containerView: View) : RecyclerView.ViewHolder(containerView) {
        var chat: Chat? = null
        val message: TextView = containerView.findViewById(R.id.chat_message)

        init {
            //Click Listeners and whatever else to manage View
        }
    }

    public fun addData(chat: Chat){
        dataSet.add(chat)
        notifyDataSetChanged()
    }

    //inflate the customView
    override fun onCreateViewHolder(parent: ViewGroup, customViewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(customViewType,parent, false) //false because the recycler will add to the view hierarchy when it is time
        return ChatViewHolder(this, view)
    }

    //Called by the layoutManager to replace the content(data) of the CustomView
    override fun onBindViewHolder(holder: ChatViewHolder, positionInDataSet: Int) {
        val currentData = dataSet[positionInDataSet]
        holder.chat = currentData
        holder.message.text = currentData.message
    }

    override fun getItemCount() = dataSet.size

    override fun getItemViewType(position: Int): Int {
        val currentData = dataSet[position]
        if (currentData.sender == "user")
            return R.layout.activity_user_chat
        return R.layout.activity_bot_chat
    }
}