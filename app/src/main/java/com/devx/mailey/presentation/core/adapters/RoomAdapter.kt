package com.devx.mailey.presentation.core.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.databinding.ItemRecyclerRoomBinding
import com.devx.mailey.domain.data.RoomItem
import com.devx.mailey.util.getLastMessage


class RoomAdapter() : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null
    inner class ViewHolder(val binding: ItemRecyclerRoomBinding): RecyclerView.ViewHolder(binding.root)  {
        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(rooms[adapterPosition].roomId)
            }
        }
    }
    private val diffCallback = object : DiffUtil.ItemCallback<RoomItem>(){
        override fun areItemsTheSame(oldItem: RoomItem, newItem: RoomItem): Boolean {
            return oldItem.roomId == newItem.roomId
        }

        override fun areContentsTheSame(oldItem: RoomItem, newItem: RoomItem): Boolean {
            return  newItem == oldItem
        }

    }
    lateinit var context: Context
    private val differ = AsyncListDiffer(this, diffCallback)
    var rooms: MutableList<RoomItem>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(ItemRecyclerRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room = rooms[position]
        holder.binding.apply {
            roomUserName.text = room.userName
            roomLastMsg.text = room.lastMessage
            roomLastMsgTimestamp.text = room.lastMessageTimestamp
        }
            Glide.with(context)
                .load(room.userUrl)
                .centerCrop()
                .into(holder.binding.roomImageView)

    }

    override fun getItemCount() = rooms.size


}