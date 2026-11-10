package com.example.a22mikardex.data

import kotlinx.coroutines.flow.Flow

class KardexRepository(private val dao: MateriaDao) {
    val materias: Flow<List<Materia>> = dao.observarMaterias()

    suspend fun insertar(materia: Materia) = dao.insertar(materia)
    suspend fun eliminar(materia: Materia) = dao.eliminar(materia)
}
