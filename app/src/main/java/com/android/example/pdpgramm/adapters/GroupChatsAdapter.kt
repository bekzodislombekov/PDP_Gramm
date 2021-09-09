package com.android.example.pdpgramm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.example.pdpgramm.databinding.ItemPersonalChatBinding
import com.android.example.pdpgramm.models.Group
import com.squareup.picasso.Picasso

class GroupChatsAdapter(
    private val list: List<Group>,
    private val onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<GroupChatsAdapter.Vh>() {
    inner class Vh(private val itemPersonalChatBinding: ItemPersonalChatBinding) :
        RecyclerView.ViewHolder(itemPersonalChatBinding.root) {
        fun onBind(group: Group) {
            Picasso.get().load(group.groupPhoto).into(itemPersonalChatBinding.img)
            itemPersonalChatBinding.date.text = ""
            itemPersonalChatBinding.message.text = ""
            itemPersonalChatBinding.name.text = group.name
            itemPersonalChatBinding.isOnline.visibility = View.GONE
            itemPersonalChatBinding.container.setOnClickListener {
                onItemClickListener.onItemListener(group)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh = Vh(
        ItemPersonalChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount() = list.size

    interface OnItemClickListener {
        fun onItemListener(group: Group)
    }
}