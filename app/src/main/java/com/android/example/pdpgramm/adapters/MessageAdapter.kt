package com.android.example.pdpgramm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.example.pdpgramm.databinding.ItemMessageFromBinding
import com.android.example.pdpgramm.databinding.ItemMessageToBinding
import com.android.example.pdpgramm.models.Message

class MessageAdapter(private val list: List<Message>, private val currentAccountNumber: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class FromVh(private val itemMessageFromBinding: ItemMessageFromBinding) :
        RecyclerView.ViewHolder(itemMessageFromBinding.root) {
        fun onBind(message: Message) {
            itemMessageFromBinding.message.text = message.message
            itemMessageFromBinding.date.text = message.date
        }
    }

    inner class ToVh(private val itemMessageToBinding: ItemMessageToBinding) :
        RecyclerView.ViewHolder(itemMessageToBinding.root) {
        fun onBind(message: Message) {
            itemMessageToBinding.message.text = message.message
            itemMessageToBinding.date.text = message.date
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
                ItemMessageToBinding.inflate(
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
        if (list[position].fromUNumber == currentAccountNumber) {
            return 1
        }
        return 2
    }

    override fun getItemCount(): Int = list.size
}