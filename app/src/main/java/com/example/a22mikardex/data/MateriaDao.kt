package com.example.a22mikardex.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MateriaDao {
    @Query("SELECT * FROM materias ORDER BY semestre ASC, nombre ASC")
    fun observarMaterias(): Flow<List<Materia>>

    @Insert
    suspend fun insertar(materia: Materia)

    @Delete
    suspend fun eliminar(materia: Materia)

    @Query("DELETE FROM materias")
    suspend fun eliminarTodas()
}
