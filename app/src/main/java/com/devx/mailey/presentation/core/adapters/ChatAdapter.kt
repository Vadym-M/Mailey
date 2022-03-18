package com.devx.mailey.presentation.core.adapters

import android.content.Context
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devx.mailey.R
import com.devx.mailey.data.model.Message
import com.devx.mailey.presentation.core.chat.ChatItems
import com.devx.mailey.util.Constants

class ChatAdapter() : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view)  {

        fun bind(content: ChatItems<Message>){
            when(content){
                is ChatItems.UserLeft ->{bindLeftMessage(content.data)}
                is ChatItems.UserRight ->{bindRightMessage(content.data)}
                is ChatItems.Other -> {bindOtherMessage(content.data)}
            }
        }

        private fun bindLeftMessage(data: Message?){
          val text = view.findViewById<TextView>(R.id.left_message_text)
          val time = view.findViewById<TextView>(R.id.left_message_time)
          val image = view.findViewById<ImageView>(R.id.left_message_user_image)
            text.text = data?.text
            time.text = data?.timestamp
            Glide.with(image.rootView)
                .load(data?.imageUrl ?: Constants.IMAGE_BLANK_URL)
                .circleCrop()
                .into(image)

        }
        private fun bindRightMessage(data: Message?){
            val text = view.findViewById<TextView>(R.id.right_message_text)
            val time = view.findViewById<TextView>(R.id.right_message_time)
            val image = view.findViewById<ImageView>(R.id.right_message_user_image)
            text.text = data?.text
            time.text = data?.timestamp
            Glide.with(image.rootView)
                .load(data?.imageUrl ?: Constants.IMAGE_BLANK_URL)
                .circleCrop()
                .into(image)

        }
        private fun bindOtherMessage(data: Message?){

        }
    }
    private val diffCallback = object : DiffUtil.ItemCallback<ChatItems<Message>>(){
        override fun areItemsTheSame(oldItem: ChatItems<Message>, newItem: ChatItems<Message>): Boolean {
            return oldItem.data == newItem.data
        }

        override fun areContentsTheSame(oldItem: ChatItems<Message>, newItem: ChatItems<Message>): Boolean {
            return  newItem == oldItem
        }

    }
    lateinit var context: Context
    private val differ = AsyncListDiffer(this, diffCallback)
    var messeges: MutableList<ChatItems<Message>>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = when (viewType) {
            LEFT_ITEM -> {
                LayoutInflater.from(context)
                    .inflate((R.layout.item_recycler_left_chat), parent, false)
            }
            RIGHT_ITEM -> {
                LayoutInflater.from(context)
                    .inflate((R.layout.item_recycler_right_chat), parent, false)
            }
            OTHER_ITEM -> {
                LayoutInflater.from(context).inflate((R.layout.item_recycler_blank), parent, false)
            }
            else -> throw IllegalArgumentException("Invalid type")
        }
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return  when(messeges[position]){
            is ChatItems.UserLeft -> LEFT_ITEM
            is ChatItems.UserRight -> RIGHT_ITEM
            is ChatItems.Other -> OTHER_ITEM
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = messeges[position]
        holder.bind(item)
    }

    override fun getItemCount() = messeges.size

    companion object{
        const val LEFT_ITEM = 0
        const val RIGHT_ITEM = 1
        const val OTHER_ITEM = 2
    }
}