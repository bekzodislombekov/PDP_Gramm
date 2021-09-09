package com.android.example.pdpgramm.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.example.pdpgramm.databinding.ItemAddGroupBinding
import com.android.example.pdpgramm.models.Account
import com.squareup.picasso.Picasso

class AddGroupAdapter(
    private val list: List<Account>,
    private val addedList: List<Account>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AddGroupAdapter.Vh>() {

    inner class Vh(private val itemAddGroupBinding: ItemAddGroupBinding) :
        RecyclerView.ViewHolder(itemAddGroupBinding.root) {

        fun onBind(account: Account, position: Int) {
            Picasso.get().load(account.accountPhoto).into(itemAddGroupBinding.img)
            itemAddGroupBinding.name.text = "${account.firstName} ${account.lastName}"
            itemAddGroupBinding.date.text = account.lastEnter
            itemAddGroupBinding.container.setOnClickListener {
                var b = false
                for (a in addedList) {
                    if (a.phoneNumber == account.phoneNumber) {
                        b = true
                        break
                    }
                }
                if (b) {
                    itemAddGroupBinding.container.setBackgroundColor(Color.WHITE)
                } else {
                    itemAddGroupBinding.container.setBackgroundColor(Color.parseColor("#E4E4E4"))
                }
                onItemClickListener.onItemListener(account, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh = Vh(
        ItemAddGroupBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount() = list.size

    interface OnItemClickListener {
        fun onItemListener(account: Account, position: Int)
    }
}