package com.nachofm.evl.vet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nachofm.evl.R

class VetListAdapter : ListAdapter<Vet, VetListAdapter.VetViewHolder>(WordsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VetViewHolder {
        return VetViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: VetViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.name)
    }

    class VetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wordItemView: TextView = itemView.findViewById(R.id.vetTitle)

        fun bind(text: String?) {
            wordItemView.text = text
        }

        companion object {
            fun create(parent: ViewGroup): VetViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return VetViewHolder(view)
            }
        }
    }

    class WordsComparator : DiffUtil.ItemCallback<Vet>() {
        override fun areItemsTheSame(oldItem: Vet, newItem: Vet): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Vet, newItem: Vet): Boolean {
            return oldItem.id == newItem.id
        }
    }
}