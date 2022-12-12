package com.example.smartfishbowl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfishbowl.database.BowlData
import com.example.smartfishbowl.databinding.BowlGridItemBinding
import java.util.ArrayList

class BowlAdapter : RecyclerView.Adapter<BowlAdapter.ItemViewHolder>(){
    private var items = ArrayList<BowlData>()

    fun setListData(data: ArrayList<BowlData>){
        this.items = data
    }

    interface OnItemClickListener{
        fun onItemClick(v: View, data: String, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = BowlGridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ItemViewHolder(binding: BowlGridItemBinding): RecyclerView.ViewHolder(binding.root){
        private val bowlId = binding.bowlId
        fun bind(data: BowlData){
            bowlId.text = data.bowlId
            val pos = bindingAdapterPosition
            if(pos != RecyclerView.NO_POSITION){
                itemView.setOnClickListener{
                    listener?.onItemClick(itemView, data.bowlId, pos)
                }
            }
        }
    }
}