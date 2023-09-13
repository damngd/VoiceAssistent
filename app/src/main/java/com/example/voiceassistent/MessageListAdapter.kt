package com.example.voiceassistent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MessageListAdapter: RecyclerView.Adapter<MessageViewHolder>() {
    var messageList: ArrayList<Message> = ArrayList()
    private val ASSISTANT_TYPE = 0
    private val USER_TYPE = 1

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messageList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view: View

        if (viewType == USER_TYPE) {
            //создание сообщения от пользователя
            view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_message,parent,false);

        }
        else {
            //создание сообщения от ассистента
            view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assistant_message,parent,false);
        }
        return MessageViewHolder(view);

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(index: Int): Int {
        val message = messageList[index]
        return if (message.isSend) {
            USER_TYPE
        } else ASSISTANT_TYPE
    }


}