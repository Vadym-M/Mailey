package com.devx.mailey.presentation.core.adapters

import android.content.Context
import android.provider.SyncStateContract

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devx.mailey.databinding.ItemRecyclerRoomBinding
import com.devx.mailey.domain.data.LocalRoom
import com.devx.mailey.domain.data.RoomItem
import com.devx.mailey.util.Constants


class RoomAdapter() : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {

    var onItemClick: ((LocalRoom) -> Unit)? = null
    inner class ViewHolder(val binding: ItemRecyclerRoomBinding): RecyclerView.ViewHolder(binding.root)  {
        init {
            binding.root.setOnClickListener {
                val currentRoom = rooms[adapterPosition]
                val roomData = LocalRoom(currentRoom.roomId, currentRoom.userName, currentRoom.userId, currentRoom.userUrl?: Constants.IMAGE_BLANK_URL)
                onItemClick?.invoke(roomData)
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
    var rooms: List<RoomItem>
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