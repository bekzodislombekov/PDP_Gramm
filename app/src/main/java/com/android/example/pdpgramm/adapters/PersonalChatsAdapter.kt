package com.android.example.pdpgramm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.example.pdpgramm.databinding.ItemPersonalChatBinding
import com.android.example.pdpgramm.models.Account
import com.android.example.pdpgramm.models.Message
import com.squareup.picasso.Picasso

class PersonalChatAdapter(
    private val list: List<Account>,
    private val onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<PersonalChatAdapter.Vh>() {
    inner class Vh(private val itemPersonalChatBinding: ItemPersonalChatBinding) :
        RecyclerView.ViewHolder(itemPersonalChatBinding.root) {
        fun onBind(account: Account) {
            Picasso.get().load(account.accountPhoto).into(itemPersonalChatBinding.img)
            itemPersonalChatBinding.name.text = "${account.firstName} ${account.lastName}"
            itemPersonalChatBinding.message.text = ""
            itemPersonalChatBinding.date.text = ""
            if (!account.isOnline) {
                itemPersonalChatBinding.isOnline.visibility = View.INVISIBLE
            }
            itemPersonalChatBinding.container.setOnClickListener {
                onItemClickListener.onItemListener(account)
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
}

interface OnItemClickListener {
    fun onItemListener(account: Account)
}