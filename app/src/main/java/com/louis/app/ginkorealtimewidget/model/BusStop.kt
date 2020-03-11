package com.louis.app.ginkorealtimewidget.model

import androidx.room.Entity

// Représente un arrêt de bus
@Entity(tableName = "bus_stop")
class BusStop (val nom: String = "default")