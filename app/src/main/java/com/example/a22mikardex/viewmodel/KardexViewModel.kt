package com.example.a22mikardex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.a22mikardex.data.KardexDatabase
import com.example.a22mikardex.data.KardexRepository
import com.example.a22mikardex.data.Materia
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KardexViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = KardexRepository(
        KardexDatabase.getInstance(application).materiaDao()
    )

    val materias = repository.materias.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun agregarMateria(
        nombre: String,
        semestre: String,
        normal: Double,
        repeticion: Double,
        especial: Double
    ) {
        viewModelScope.launch {
            repository.insertar(
                Materia(
                    nombre = nombre.trim(),
                    semestre = semestre.trim(),
                    calificacionNormal = normal,
                    calificacionRepeticion = repeticion,
                    calificacionEspecial = especial
                )
            )
        }
    }

    fun eliminarMateria(materia: Materia) {
        viewModelScope.launch { repository.eliminar(materia) }
    }
}
