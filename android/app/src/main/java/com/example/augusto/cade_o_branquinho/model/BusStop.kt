package com.example.augusto.cade_o_branquinho.model

import com.example.augusto.cade_o_branquinho.utils.DepartureTime
import com.google.android.gms.maps.model.LatLng

enum class BusStop {
    TERMINAL, REPRESA, MARITIMO, IPH, BENTO, APLICACAO, PONTE, POLAR, FAURGS, TECNOLOGOS, BLOCO_4, CREAL;

    var nextTime: DepartureTime = DepartureTime(0,0)

    fun getName(): String {

        return when (this) {
            TERMINAL -> "Terminal"
            REPRESA -> "Represa"
            MARITIMO -> "Marítimo"
            IPH -> "IPH"
            BENTO-> "Bento"
            APLICACAO -> "Aplicação"
            PONTE -> "Ponte"
            POLAR -> "Polar"
            FAURGS -> "FAURGS"
            TECNOLOGOS -> "Tecnólogos"
            BLOCO_4 -> "Bloco 4"
            CREAL -> "CREAL"
        }

    }

    fun getLocation(): LatLng {

        return when (this) {
            TERMINAL -> LatLng(-30.072246, -51.117995)
            REPRESA -> LatLng(-30.074543, -51.119110)
            MARITIMO -> LatLng(-30.075572, -51.116027)
            IPH -> LatLng(-30.076673, -51.113491)
            BENTO-> LatLng(-30.077885, -51.115925)
            APLICACAO -> LatLng(-30.075979, -51.124612)
            PONTE -> LatLng(-30.075478, -51.122539)
            POLAR -> LatLng(-30.073951, -51.123435)
            FAURGS -> LatLng(-30.070526, -51.121286)
            TECNOLOGOS -> LatLng(-30.067979, -51.122393)
            BLOCO_4 -> LatLng(-30.067216, -51.120015)
            CREAL -> LatLng(-30.070037,  -51.118215)
        }

    }

    fun getId(): String {

        return when (this) {
            TERMINAL ->"TERMINAL"
            REPRESA ->"REPRESA"
            MARITIMO ->"MARITIMO"
            IPH ->"IPH"
            BENTO->"BENT"
            APLICACAO ->"APLICACAO"
            PONTE ->"PONTE"
            POLAR ->"POLAR"
            FAURGS ->"FAURGS"
            TECNOLOGOS ->"TECNOLOGOS"
            BLOCO_4 -> "BLOCO_4"
            CREAL -> "CREAL"
        }

    }

    companion object {

        fun all(): ArrayList<BusStop> {
            return arrayListOf(TERMINAL, REPRESA, MARITIMO, IPH, BENTO, APLICACAO, PONTE, POLAR, FAURGS, TECNOLOGOS, BLOCO_4, CREAL)
        }

    }

}
