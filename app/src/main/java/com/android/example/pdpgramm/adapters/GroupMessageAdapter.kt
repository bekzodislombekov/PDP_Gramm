package com.android.example.pdpgramm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.example.pdpgramm.databinding.ItemMessageFromBinding
import com.android.example.pdpgramm.databinding.ItemGroupMessageToBinding
import com.android.example.pdpgramm.models.GroupMessage
import com.squareup.picasso.Picasso

class GroupMessageAdapter(
    private val list: List<GroupMessage>,
    private val currentAccount: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class FromVh(private val itemMessageFromBinding: ItemMessageFromBinding) :
        RecyclerView.ViewHolder(itemMessageFromBinding.root) {
        fun onBind(groupMessage: GroupMessage) {
            itemMessageFromBinding.message.text = groupMessage.message
            itemMessageFromBinding.date.text = groupMessage.date
        }
    }

    inner class ToVh(private val itemMessageToBinding: ItemGroupMessageToBinding) :
        RecyclerView.ViewHolder(itemMessageToBinding.root) {
        fun onBind(groupMessage: GroupMessage) {
            itemMessageToBinding.message.text = groupMessage.message
            itemMessageToBinding.date.text = groupMessage.date
            itemMessageToBinding.name.text =
                "${groupMessage.fromUNumber?.firstName} ${groupMessage.fromUNumber?.lastName}"
            Picasso.get().load(groupMessage.fromUNumber?.accountPhoto)
                .into(itemMessageToBinding.img)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            return FromVh(
                ItemMessageFromBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return ToVh(
                ItemGroupMessageToBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1) {
            val fromVh = holder as FromVh
            fromVh.onBind(list[position])
        } else {
            val toVh = holder as ToVh
            toVh.onBind(list[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (list[position].fromUNumber?.phoneNumber == currentAccount) {
            return 1
        }
        return 2
    }

    override fun getItemCount() = list.size

}