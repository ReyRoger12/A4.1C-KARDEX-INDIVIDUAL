package com.example.a22mikardex.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materias")
data class Materia(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val semestre: String,
    val calificacionNormal: Double,
    val calificacionRepeticion: Double,
    val calificacionEspecial: Double
) {
    /**
     * Para el promedio se toma la calificación efectiva:
     * Especial > Repetición > Normal, ignorando valores no capturados (0).
     */
    fun calificacionFinal(): Double {
        return when {
            calificacionEspecial > 0.0 -> calificacionEspecial
            calificacionRepeticion > 0.0 -> calificacionRepeticion
            else -> calificacionNormal
        }
    }
}
