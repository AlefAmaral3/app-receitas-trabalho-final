package com.aleftrabalhofinal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RecipeRepository
    val savedRecipes: LiveData<List<RecipeEntity>>
    val favoriteRecipes: LiveData<List<RecipeEntity>>
    private val _searchResults = MutableLiveData<List<RecipeEntity>>()
    val searchResults: LiveData<List<RecipeEntity>> = _searchResults
    data class MealDetailUiState(val loading: Boolean = false, val error: String? = null, val recipe: RecipeEntity? = null)
    private val _detailState = MutableLiveData(MealDetailUiState())
    val detailState: LiveData<MealDetailUiState> = _detailState

    init {
        val db = AppDatabase.getInstance(application)
        repository = RecipeRepository(db.receitasDao(), TheMealDbApi.create())
        savedRecipes = repository.savedRecipes
        favoriteRecipes = repository.favoriteRecipes
    }

    fun searchRecipes(query: String) {
        if (query.trim().length < 2) return
        viewModelScope.launch { _searchResults.value = repository.searchRecipes(query.trim()) }
    }

    fun loadMealDetails(id: String) = viewModelScope.launch {
        _detailState.value = MealDetailUiState(loading = true)
        _detailState.value = repository.getMealDetails(id)?.let { MealDetailUiState(recipe = it) } ?: MealDetailUiState(error = "Erro ao carregar")
    }

    fun toggleFavorite(recipe: RecipeEntity) = viewModelScope.launch { repository.toggleFavorite(recipe.copy(eFavorito = !recipe.eFavorito, eCriadoPeloUtilizador = true)) }

    fun insertRecipe(nome: String, categoria: String, instrucoes: String, imagemUrl: String = "") = viewModelScope.launch {
        repository.insertRecipe(RecipeEntity(nome = nome, categoria = categoria, instrucoes = instrucoes, imagemUrl = imagemUrl.ifBlank { null }, eCriadoPeloUtilizador = true))
    }

    fun deleteRecipe(recipe: RecipeEntity) = viewModelScope.launch { repository.deleteRecipe(recipe) }
    fun saveRecipe(recipe: RecipeEntity) = viewModelScope.launch { repository.insertRecipe(recipe) }
    fun updateRecipe(recipe: RecipeEntity) = viewModelScope.launch { repository.insertRecipe(recipe) }
}

class RecipeViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = RecipeViewModel(app) as T
}
