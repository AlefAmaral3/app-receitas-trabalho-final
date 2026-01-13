package com.aleftrabalhofinal

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.aleftrabalhofinal.ui.theme.AleftrabalhofinalTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AleftrabalhofinalTheme {
                ProgramaPrincipal()
            }
        }
    }
}

data class NavItem(val route: String, val icon: ImageVector, val title: String)
val navItems = listOf(NavItem("home", Icons.Filled.Home, "Início"), NavItem("search", Icons.Filled.Search, "Procurar"), NavItem("favoritos", Icons.Filled.Favorite, "Favoritos"), NavItem("criar", Icons.Filled.Add, "Criar"))

@Composable
fun ProgramaPrincipal() {
    val navController = rememberNavController()
    val viewModel: RecipeViewModel = viewModel(factory = RecipeViewModelFactory(LocalContext.current.applicationContext as Application))
    Scaffold(bottomBar = { BottomNavigationBar(navController) }) { p ->
        NavHost(navController, "home", Modifier.padding(p)) {
            composable("home") { HomeScreen(viewModel) { navController.navigate("detalhes/$it") } }
            composable("search") { SearchScreen(viewModel) { navController.navigate("detalhes/$it") } }
            composable("favoritos") { FavoritesScreen(viewModel) { navController.navigate("detalhes/${it.idApi ?: it.id}") } }
            composable("criar") { CriarScreen(viewModel) }
            composable("detalhes/{mealId}") { DetailsScreen(navController, it.arguments?.getString("mealId"), viewModel) }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: "home"
    BottomNavigation(backgroundColor = MaterialTheme.colorScheme.primaryContainer) {
        navItems.forEach { item ->
            BottomNavigationItem(icon = { Icon(item.icon, item.title) }, label = { Text(item.title, fontSize = 9.sp) }, selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) { navController.graph.startDestinationRoute?.let { popUpTo(it) { saveState = true } }; launchSingleTop = true; restoreState = true } },
                selectedContentColor = MaterialTheme.colorScheme.primary, unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun RecipeItem(recipe: RecipeEntity, onFavoriteClick: () -> Unit, onClick: () -> Unit, onDelete: (() -> Unit)? = null) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) AlertDialog(
        onDismissRequest = { showDialog = false },
        title = { Text("Apagar Receita?") },
        text = { Text("Tens certeza que queres apagar '${recipe.nome}'?") },
        confirmButton = { Button({ showDialog = false; onDelete?.invoke() }) { Text("Apagar") } },
        dismissButton = { OutlinedButton({ showDialog = false }) { Text("Cancelar") } }
    )
    Card(Modifier.fillMaxWidth().padding(8.dp).clickable(onClick = onClick), elevation = CardDefaults.cardElevation(4.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(recipe.imagemUrl, recipe.nome, Modifier.size(100.dp), contentScale = ContentScale.Crop)
            Column(Modifier.weight(1f).padding(8.dp)) { Text(recipe.nome, style = MaterialTheme.typography.titleMedium); Text(recipe.categoria, style = MaterialTheme.typography.bodySmall) }
            onDelete?.let { IconButton({ showDialog = true }) { Icon(Icons.Filled.Delete, "Apagar", tint = MaterialTheme.colorScheme.error) } }
            IconButton(onFavoriteClick) { Icon(if (recipe.eFavorito) Icons.Filled.Favorite else Icons.Default.FavoriteBorder, "Favorito", tint = if (recipe.eFavorito) Color.Red else Color.Gray) }
        }
    }
}

@Composable
fun HomeScreen(viewModel: RecipeViewModel, onRecipeClick: (String) -> Unit) {
    val savedRecipes by viewModel.savedRecipes.observeAsState(emptyList())
    var searchQuery by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Receitas", style = MaterialTheme.typography.headlineSmall)
            Text("${savedRecipes.size} receitas", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it }, label = { Text("Procurar por nome") }, modifier = Modifier.fillMaxWidth(),
            trailingIcon = { if (searchQuery.isNotEmpty()) IconButton({ searchQuery = "" }) { Icon(Icons.Filled.Delete, "Limpar") } })
        Spacer(Modifier.height(12.dp))
        RecipeList(savedRecipes.filter { it.nome.contains(searchQuery, true) }, { recipe -> onRecipeClick(recipe.idApi ?: recipe.id.toString()) }, { viewModel.toggleFavorite(it) }, { viewModel.deleteRecipe(it) })
    }
}

@Composable
fun SearchScreen(viewModel: RecipeViewModel, onRecipeClick: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    val results by viewModel.searchResults.observeAsState(emptyList())
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Procurar receitas (ex: chicken)") }, modifier = Modifier.fillMaxWidth(),
            trailingIcon = { IconButton({ if (query.isNotBlank()) viewModel.searchRecipes(query) }) { Icon(Icons.Default.Search, "Pesquisar") } })
        Spacer(Modifier.height(16.dp))
        RecipeList(results, { it.idApi?.let(onRecipeClick) }, { viewModel.toggleFavorite(it) })
    }
}

@Composable
fun FavoritesScreen(viewModel: RecipeViewModel, onRecipeClick: (RecipeEntity) -> Unit) {
    val favorites by viewModel.favoriteRecipes.observeAsState(emptyList())
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Favoritos", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        RecipeList(favorites, onRecipeClick, { viewModel.toggleFavorite(it) })
    }
}

@Composable
private fun RecipeList(recipes: List<RecipeEntity>, onItemClick: (RecipeEntity) -> Unit, onFavorite: (RecipeEntity) -> Unit, onDelete: ((RecipeEntity) -> Unit)? = null) {
    if (recipes.isEmpty()) Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) { Text("Nenhuma receita encontrada", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    else LazyColumn { items(recipes) { RecipeItem(it, { onFavorite(it) }, { onItemClick(it) }, onDelete?.let { del -> { del(it) } }) } }
}

@Composable
fun CriarScreen(viewModel: RecipeViewModel) {
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var instrucoes by remember { mutableStateOf("") }
    var imagemUrl by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Criar Nova Receita", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(nome, { nome = it }, Modifier.fillMaxWidth(), label = { Text("Nome da Receita") })
        OutlinedTextField(categoria, { categoria = it }, Modifier.fillMaxWidth(), label = { Text("Categoria") })
        OutlinedTextField(instrucoes, { instrucoes = it }, Modifier.fillMaxWidth(), label = { Text("Instruções") }, minLines = 3)
        OutlinedTextField(imagemUrl, { imagemUrl = it }, Modifier.fillMaxWidth(), label = { Text("URL da Imagem (opcional)") })
        Spacer(Modifier.height(16.dp))
        Button({ if (nome.isNotBlank() && categoria.isNotBlank()) { viewModel.insertRecipe(nome, categoria, instrucoes, imagemUrl.ifBlank { "https://via.placeholder.com/300" }); nome = ""; categoria = ""; instrucoes = ""; imagemUrl = "" } }, Modifier.fillMaxWidth()) { Text("Guardar Receita") }
    }
}

@Composable
fun DetailsScreen(navController: NavHostController, recipeId: String?, viewModel: RecipeViewModel) {
    val state by viewModel.detailState.observeAsState(RecipeViewModel.MealDetailUiState())
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showEditDialog by remember { mutableStateOf(false) }
    LaunchedEffect(recipeId) { recipeId?.let { viewModel.loadMealDetails(it) } }
    
    if (showEditDialog && state.recipe != null) {
        var nome by remember { mutableStateOf(state.recipe!!.nome) }
        var categoria by remember { mutableStateOf(state.recipe!!.categoria) }
        var instrucoes by remember { mutableStateOf(state.recipe!!.instrucoes) }
        var imagemUrl by remember { mutableStateOf(state.recipe!!.imagemUrl ?: "") }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar Receita") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(nome, { nome = it }, Modifier.fillMaxWidth(), label = { Text("Nome") })
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(categoria, { categoria = it }, Modifier.fillMaxWidth(), label = { Text("Categoria") })
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(instrucoes, { instrucoes = it }, Modifier.fillMaxWidth(), label = { Text("Instruções") }, minLines = 3)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(imagemUrl, { imagemUrl = it }, Modifier.fillMaxWidth(), label = { Text("Imagem URL") })
                }
            },
            confirmButton = { Button({ viewModel.updateRecipe(state.recipe!!.copy(nome = nome, categoria = categoria, instrucoes = instrucoes, imagemUrl = imagemUrl.ifBlank { null })); showEditDialog = false; scope.launch { snackbarHostState.showSnackbar("Receita atualizada!") } }) { Text("Salvar") } },
            dismissButton = { OutlinedButton({ showEditDialog = false }) { Text("Cancelar") } }
        )
    }
    
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
            when {
                state.loading -> CircularProgressIndicator()
                state.error != null -> Text(state.error!!)
                state.recipe != null -> Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    state.recipe?.imagemUrl?.let { AsyncImage(it, state.recipe!!.nome, Modifier.fillMaxWidth().height(220.dp), contentScale = ContentScale.Crop) }
                    Text(state.recipe!!.nome, style = MaterialTheme.typography.headlineSmall)
                    Text("Categoria: ${state.recipe!!.categoria}", style = MaterialTheme.typography.bodyMedium)
                    Text(state.recipe!!.instrucoes, style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (state.recipe!!.id != 0 || state.recipe!!.eCriadoPeloUtilizador) Button({ showEditDialog = true }) { Text("Editar") }
                        else Button({ viewModel.saveRecipe(state.recipe!!); scope.launch { snackbarHostState.showSnackbar("Receita guardada!") } }) { Text("Guardar") }
                        OutlinedButton({ navController.popBackStack() }) { Text("Voltar") }
                    }
                }
                else -> Text("Selecione uma receita")
            }
        }
    }
}
