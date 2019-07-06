package com.example.augusto.cade_o_branquinho.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.augusto.cade_o_branquinho.R
import com.example.augusto.cade_o_branquinho.model.Warning
import kotlinx.android.synthetic.main.status_item.view.*
import kotlinx.android.synthetic.main.warnings_list_item.view.*

class WarningsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private var warningsList = arrayListOf<Warning>()
    private var mContext: Context

    private val STATUS_ITEM = 0
    private val WARNING_ITEM = 1

    private var mStatus: String = "undefined"

    constructor(context: Context, list: ArrayList<Warning>, status: String) {
        mContext = context
        update(list, status)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return STATUS_ITEM
        }
        return WARNING_ITEM
    }

    fun update(list: ArrayList<Warning>, status: String) {
        warningsList.clear()
        warningsList.addAll(list)
        mStatus = status
        println("tem ${warningsList.size} itens na lista")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (getItemViewType(i) == STATUS_ITEM) {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.status_item, p0, false)
            val viewHolder = StatusViewHolder(itemView, mContext!!)
            return viewHolder
        } else {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.warnings_list_item, p0, false)
            val viewHolder = WarningViewHolder(itemView, mContext!!)
            return viewHolder
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, pos: Int) {
        if (getItemViewType(pos) == STATUS_ITEM) {
            (viewHolder as StatusViewHolder).bind(mStatus)
        } else {
            (viewHolder as WarningViewHolder).bind(warningsList[pos - 1])
        }
    }

    override fun getItemCount(): Int {
        return warningsList.size + 1
    }

    class WarningViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

        fun bind(warning: Warning) {
            itemView.warning_date.text = warning.date
            itemView.warning_message.text = warning.message
            itemView.warning_line.visibility = View.VISIBLE
            itemView.warning_line.setBackgroundColor(context.resources.getColor(R.color.lineColor))
        }

    }

    class StatusViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

        fun bind(status: String) {
            if (status == "on") {
                itemView.status_icon.setImageResource(R.drawable.on)
                itemView.status_label.text = "APARENTEMENTE NORMAL"
                itemView.status_label.setTextColor(context.resources.getColor(R.color.greenOnColor))
            } else if (status == "off") {
                itemView.status_icon.setImageResource(R.drawable.off)
                itemView.status_label.text = "POSSIVELMENTE PARADO"
                itemView.status_label.setTextColor(context.resources.getColor(R.color.redOffColor))
            } else {
                itemView.status_icon.setImageResource(R.drawable.undefined)
                itemView.status_label.text = "NÃO IDENTIFICADO"
                itemView.status_label.setTextColor(context.resources.getColor(R.color.yellowUndefinedColor))
            }

            itemView.status_disclaimer.text = Html.fromHtml(context.resources.getString(R.string.status_disclaimer_text))
        }
    }
}
