package com.example.realtimeubicacion.pojos

class UbicacionPojo {
    var latitud: Double? = null
    var longitud: Double? = null
    var date: String?


    constructor(latitud: Double?, longitud: Double?, date: String?) {
        this.latitud = latitud
        this.longitud = longitud
        this.date = date
    }
}