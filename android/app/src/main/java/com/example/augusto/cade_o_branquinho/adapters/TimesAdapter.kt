package com.example.augusto.cade_o_branquinho.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.augusto.cade_o_branquinho.R
import com.example.augusto.cade_o_branquinho.model.DayType
import com.example.augusto.cade_o_branquinho.utils.DepartureTime
import com.example.augusto.cade_o_branquinho.utils.DepartureTimes
import com.example.augusto.cade_o_branquinho.utils.TimeManager
import kotlinx.android.synthetic.main.times_list_item.view.*


class TimesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private var mDayType: DayType
    private var mContext: Context? = null
    private var nextDepartureIndex: Int? = null
    private val timesList = arrayListOf<DepartureTime>()

    constructor(context: Context, dayType: DayType) {
        mContext = context
        mDayType = dayType
        timesList.clear()

        if (dayType == DayType.WEEKDAY) {
            timesList.addAll(DepartureTimes.weekdays)
        } else {
            timesList.addAll(DepartureTimes.saturdays)
        }

        updateNextDeparture()
    }

    fun updateNextDeparture() {
        var departure: DepartureTime? = null
        val timeManager = TimeManager()

        val today = timeManager.getTodayType()

        // se estiver na tela correspondente ao dia de hoje
        if (mDayType == today) {
            // pega o próximo horário de partida, de acordo com o dia atual
            if (mDayType == DayType.WEEKDAY) {
                departure = timeManager.getNextDepartureWeek()
            } else {
                departure = timeManager.getNextDepartureSaturday()
            }

            // pega o indice (se houver) do próximo horário de partida na lista
            val index = timesList.map {
                timesList.indexOf(it)
            }.firstOrNull {
                timesList[it].hour == departure?.hour && timesList[it].minute == departure?.minute
            }

            // se mudou, atualiza a lista, senão mostra igual
            if (index != this.nextDepartureIndex) {
                nextDepartureIndex = index
                notifyDataSetChanged()
            }
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(p0.context).inflate(R.layout.times_list_item, p0, false)
        val viewHolder = TimeViewHolder(itemView, mContext!!)

        viewHolder.nextDepartureIndex = this.nextDepartureIndex

        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, pos: Int) {
        (viewHolder as TimeViewHolder).bind(timesList.get(pos), pos)
    }

    override fun getItemCount(): Int {
        return timesList.size
    }

    class TimeViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

        var nextDepartureIndex: Int? = null

        fun bind(t: DepartureTime, i: Int) {
            itemView.times_list_item_index.text = makeIndexText(i)
            itemView.times_list_item_label.text = t.getFormatted()
            itemView.times_list_item_next_time.visibility = View.GONE

            // se for o índice do próximo horário de partida
            if (nextDepartureIndex == i) {
                itemView.times_list_item_next_time.visibility = View.VISIBLE
            }
        }

        private fun makeIndexText(pos: Int): String {
            return (pos + 1).toString() + "ª VIAGEM"
        }
    }
}

