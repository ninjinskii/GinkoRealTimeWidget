package com.louis.app.ginkorealtimewidget.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

// Représente un trajet de bus
@Entity(tableName = "path")
data class Path(
        @PrimaryKey val id: Long,
        @Embedded val startingPoint: BusStop,
        @Embedded val endingPoint: BusStop) {

    /**
     * Vérifie la validité des arrêts saisis
     * Par exemple on ne veut pas que l'utilisateur rentre un arrêt d'arrivée antécédant au point
     * de départ.
     *
     * @return true si les arrêts sont viables, false si les arrêts ne sont pas viables
     */
    fun checkViability() : Boolean{
        // TODO: méthode de vérification de la cohérence des arrêts
        return true
    }

    /**
     * Retourne le nom du trajet à afficher à l'utilisateur
     * Exemple: Pôle Temis - Place du 8 septembre
     *
     * @return le nom du trajet à afficher à l'utilisateur
     */
    fun getName() = "${startingPoint.name} - ${endingPoint.name}"

}