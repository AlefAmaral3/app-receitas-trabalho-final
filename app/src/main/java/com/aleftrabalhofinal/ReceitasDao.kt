package com.aleftrabalhofinal

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReceitasDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceita(receita: RecipeEntity)

    @Delete
    suspend fun deleteReceita(receita: RecipeEntity)

    @Query("SELECT * FROM receitas WHERE e_favorito = 1 OR e_criado_pelo_utilizador = 1")
    fun getAllSavedReceitas(): LiveData<List<RecipeEntity>>

    @Query("SELECT * FROM receitas WHERE e_favorito = 1")
    fun getFavoriteReceitas(): LiveData<List<RecipeEntity>>
    @Query("SELECT * FROM receitas WHERE id = :id")
    suspend fun getReceitaById(id: Int): RecipeEntity?
    @Query("SELECT * FROM receitas WHERE id_api = :apiId LIMIT 1")
    suspend fun getReceitaByApiId(apiId: String): RecipeEntity?

    @Query("UPDATE receitas SET e_favorito = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)
}
