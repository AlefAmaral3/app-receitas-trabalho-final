package com.aleftrabalhofinal

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeRepository(private val dao: ReceitasDao, private val api: TheMealDbApi) {
    val savedRecipes = dao.getAllSavedReceitas()
    val favoriteRecipes = dao.getFavoriteReceitas()

    suspend fun searchRecipes(query: String): List<RecipeEntity> = try {
        api.searchMeals(query).meals?.map { RecipeEntity(idApi = it.idMeal, nome = it.strMeal, 
            categoria = it.strCategory, instrucoes = it.strInstructions, imagemUrl = it.strMealThumb) } ?: emptyList()
    } catch (e: Exception) { emptyList() }

    suspend fun getMealDetails(id: String): RecipeEntity? = try {
        id.toIntOrNull()?.let { localId -> dao.getReceitaById(localId) } ?:
        api.getMealById(id).meals?.firstOrNull()?.let { meal ->
            dao.getReceitaByApiId(meal.idMeal) ?: RecipeEntity(idApi = meal.idMeal, nome = meal.strMeal, 
                categoria = meal.strCategory, instrucoes = meal.strInstructions, imagemUrl = meal.strMealThumb)
        }
    } catch (e: Exception) { null }

    suspend fun toggleFavorite(recipe: RecipeEntity) {
        if (recipe.idApi != null) {
            dao.getReceitaByApiId(recipe.idApi)?.let { dao.updateFavoriteStatus(it.id, recipe.eFavorito) } 
                ?: dao.insertReceita(recipe.copy(eCriadoPeloUtilizador = true))
        } else {
            if (recipe.id != 0) dao.updateFavoriteStatus(recipe.id, recipe.eFavorito)
            else dao.insertReceita(recipe)
        }
    }

    suspend fun insertRecipe(recipe: RecipeEntity) = dao.insertReceita(recipe.copy(eCriadoPeloUtilizador = true))

    suspend fun deleteRecipe(recipe: RecipeEntity) = dao.deleteReceita(recipe)
}
