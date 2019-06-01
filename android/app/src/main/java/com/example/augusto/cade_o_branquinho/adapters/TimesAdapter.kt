package com.example.augusto.cade_o_branquinho.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.augusto.cade_o_branquinho.R
import kotlinx.android.synthetic.main.times_list_item.view.*

class TimesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private var mContext: Context? = null
    private val timesList = arrayListOf<String>()

    constructor(context: Context, list: ArrayList<String>) {
        mContext = context
        timesList.clear()
        timesList.addAll(list)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(p0: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(p0.context).inflate(R.layout.times_list_item, p0, false)
        return TimeViewHolder(itemView, mContext!!)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, pos: Int) {
        (viewHolder as TimeViewHolder).bind(timesList.get(pos), pos)
    }

    override fun getItemCount(): Int {
        return timesList.size
    }

    class TimeViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

        var isExpanded = false
        var onItemClickCompletion: ((Int) -> Unit)? = null

        fun bind(t: String, i: Int) {
            itemView.times_list_item_index.text = makeIndexText(i)
            itemView.times_list_item_label.text = t
        }

        private fun makeIndexText(pos: Int): String {
            return (pos + 1).toString() + "ª VIAGEM"
        }
    }
}

