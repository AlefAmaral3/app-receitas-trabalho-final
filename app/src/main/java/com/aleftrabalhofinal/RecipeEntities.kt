package com.aleftrabalhofinal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receitas")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "id_api") val idApi: String? = null,
    @ColumnInfo(name = "nome") val nome: String,
    @ColumnInfo(name = "categoria") val categoria: String,
    @ColumnInfo(name = "instrucoes") val instrucoes: String,
    @ColumnInfo(name = "imagem_url") val imagemUrl: String?,
    @ColumnInfo(name = "e_favorito") var eFavorito: Boolean = false,
    @ColumnInfo(name = "e_criado_pelo_utilizador") val eCriadoPeloUtilizador: Boolean = false
)
