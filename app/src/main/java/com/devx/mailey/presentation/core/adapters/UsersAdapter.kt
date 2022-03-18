package com.devx.mailey.presentation.core.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devx.mailey.data.model.User
import com.devx.mailey.databinding.ItemRecyclerUserBinding
import com.devx.mailey.util.Constants

class UsersAdapter() : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    var onItemClick: ((User) -> Unit)? = null
    inner class ViewHolder(val binding: ItemRecyclerUserBinding): RecyclerView.ViewHolder(binding.root)  {
        init {
            binding.itemUserMessageBtn.setOnClickListener {
                onItemClick?.invoke(users[adapterPosition])
            }
        }
    }
    private val diffCallback = object : DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return  newItem == oldItem
        }

    }
    lateinit var context: Context
    private val differ = AsyncListDiffer(this, diffCallback)
    var users: List<User>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(ItemRecyclerUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.binding.apply {
            itemUserName.text = user.fullName
        }
        if(user.imagesUrl.isNotEmpty()) {
            Glide.with(context)
                .load(user.imagesUrl.first())
                .circleCrop()
                .into(holder.binding.itemUserImage)
        }else{
            Glide.with(context)
                .load(Constants.IMAGE_BLANK_URL)
                .circleCrop()
                .into(holder.binding.itemUserImage)
        }
    }

    override fun getItemCount() = users.size

}