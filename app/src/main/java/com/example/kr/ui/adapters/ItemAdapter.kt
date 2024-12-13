package com.example.kr.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kr.R
import com.example.kr.model.Item
import com.google.android.material.card.MaterialCardView

class ItemAdapter(
    var items: List<Item>,
    private val onItemClick: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    fun updateItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: MaterialCardView = view.findViewById(R.id.cardView)
        val nameTextView: TextView = view.findViewById(R.id.text_name)
        val descriptionTextView: TextView = view.findViewById(R.id.text_description)
        val storeTextView: TextView = view.findViewById(R.id.text_store)
        val priceTextView: TextView = view.findViewById(R.id.text_price)
        val validUntilTextView: TextView = view.findViewById(R.id.text_valid_until)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        holder.descriptionTextView.text = item.description
        holder.storeTextView.text = "Магазин: ${item.storeName}"
        holder.priceTextView.text = "Ціна: ${item.discountedPrice} грн (замість ${item.originalPrice} грн)"
        holder.validUntilTextView.text = "Діє до: ${item.validUntil}"
    }

    override fun getItemCount() = items.size
}
