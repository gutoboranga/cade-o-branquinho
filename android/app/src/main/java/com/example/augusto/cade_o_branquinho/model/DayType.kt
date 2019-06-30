package com.example.augusto.cade_o_branquinho.model

enum class DayType {
    WEEKDAY, SATURDAY, SUNDAY;

    override fun toString(): String {
        return when(this){
            WEEKDAY -> "Dia de semana"
            SATURDAY -> "Sábado"
            SUNDAY -> "Domingo"
        }
    }
}