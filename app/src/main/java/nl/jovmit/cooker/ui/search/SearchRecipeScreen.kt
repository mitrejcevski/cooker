package nl.jovmit.cooker.ui.search

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import nl.jovmit.cooker.R
import nl.jovmit.cooker.extensions.ShowSnackbar
import nl.jovmit.cooker.model.RecipeItem
import nl.jovmit.cooker.ui.composables.HtmlText
import nl.jovmit.cooker.ui.composables.LoadingIndicator
import nl.jovmit.cooker.ui.search.state.SearchRecipeScreenState
import nl.jovmit.cooker.ui.theme.CookerTheme

@Composable
fun SearchRecipeScreen(
    searchViewModel: SearchRecipeViewModel = hiltViewModel(),
    onRecipeClick: (recipeId: Long) -> Unit,
    onSettingsClick: () -> Unit
) {
    val state by searchViewModel.searchScreenState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { searchViewModel.loadRecentRecipes() }

    SearchRecipeScreenContent(
        modifier = Modifier.fillMaxSize(),
        screenState = state,
        onQueryUpdate = { searchViewModel.updateQuery(it) },
        onSearch = { searchViewModel.findRecipesFor(it) },
        onClearQuery = { searchViewModel.clearQuery() },
        onRecipeClick = onRecipeClick,
        onSettingsClick = onSettingsClick,
        onLoadMoreItems = { searchViewModel.loadMore(state.recipesFound.count()) },
        onLoadRecentRecipesErrorDismissed = { searchViewModel.dismissLoadRecentRecipesError() },
        onSearchingErrorDismissed = { searchViewModel.dismissSearchError() },
        onConnectionErrorDismissed = { searchViewModel.dismissConnectionError() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchRecipeScreenContent(
    modifier: Modifier = Modifier,
    screenState: SearchRecipeScreenState,
    onQueryUpdate: (newValue: String) -> Unit,
    onSearch: (query: String) -> Unit,
    onClearQuery: () -> Unit,
    onRecipeClick: (recipeId: Long) -> Unit,
    onLoadMoreItems: () -> Unit,
    onSettingsClick: () -> Unit,
    onLoadRecentRecipesErrorDismissed: () -> Unit,
    onSearchingErrorDismissed: () -> Unit,
    onConnectionErrorDismissed: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(12.dp),
                    action = {
                        TextButton(onClick = { data.dismiss() }) {
                            Text(text = stringResource(id = R.string.ok))
                        }
                    },
                ) {
                    Text(text = data.visuals.message)
                }
            }
        }
    ) { paddingValues ->
        if (screenState.isLoadingRecentRecipesError) {
            snackbarHostState.ShowSnackbar(
                message = stringResource(id = R.string.error_loading_recent_recipes),
                onDismissed = onLoadRecentRecipesErrorDismissed
            )
        }
        if (screenState.isSearchingError) {
            snackbarHostState.ShowSnackbar(
                message = stringResource(id = R.string.error_searching),
                onDismissed = onSearchingErrorDismissed
            )
        }
        if (screenState.isConnectionError) {
            snackbarHostState.ShowSnackbar(
                message = stringResource(id = R.string.connection_error),
                onDismissed = onConnectionErrorDismissed
            )
        }
        Box(modifier = Modifier.padding(paddingValues)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SearchBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    query = screenState.query,
                    onQueryUpdate = onQueryUpdate,
                    onSearch = { onSearch(screenState.query) },
                    onClearQuery = onClearQuery
                )
                RecipeList(
                    modifier = Modifier.fillMaxWidth(),
                    items = screenState.recipesFound.ifEmpty { screenState.recentRecipes },
                    enableLoadMoreTrigger = screenState.canLoadMore && screenState.query.isNotBlank(),
                    onRecipeClick = onRecipeClick,
                    onLoadMoreItems = onLoadMoreItems
                )
            }
            AnimatedVisibility(
                visible = screenState.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LoadingIndicator(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RecipeList(
    modifier: Modifier = Modifier,
    items: List<RecipeItem>,
    enableLoadMoreTrigger: Boolean,
    onRecipeClick: (recipeId: Long) -> Unit,
    onLoadMoreItems: () -> Unit
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { recipe ->
            RecipeListItem(
                modifier = Modifier
                    .animateItemPlacement()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onRecipeClick(recipe.id) }
                    .semantics(true) {
                        contentDescription = recipe.title
                    },
                recipe = recipe
            )
        }
    }
    if (items.isEmpty()) {
        EmptyListIndicator(
            modifier = Modifier.fillMaxSize()
        )
    }
    if (enableLoadMoreTrigger) {
        listState.OnBottomReached(itemsBeforeLast = 1) {
            onLoadMoreItems()
        }
    }
}

@Composable
private fun EmptyListIndicator(
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val noResultsMessage = stringResource(id = R.string.empty_list_message)
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(200.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .semantics(mergeDescendants = true) {
                    contentDescription = noResultsMessage
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(72.dp),
                imageVector = Icons.Default.NoFood,
                contentDescription = null
            )
        }
        Text(
            modifier = Modifier.padding(16.dp),
            text = noResultsMessage,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall.copy(
                lineBreak = LineBreak.Paragraph.copy(strategy = LineBreak.Strategy.Balanced)
            )
        )
    }
}

@Composable
private fun SearchBox(
    modifier: Modifier = Modifier,
    query: String,
    onQueryUpdate: (newValue: String) -> Unit,
    onSearch: () -> Unit,
    onClearQuery: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = modifier,
        value = query,
        onValueChange = onQueryUpdate,
        maxLines = 1,
        label = {
            Text(text = stringResource(R.string.search_hint))
        },
        trailingIcon = {
            val icon = if (query.isBlank()) Icons.Default.Search else Icons.Default.Close
            IconButton(onClick = {
                if (query.isNotBlank()) {
                    onClearQuery()
                }
            }) {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch()
            focusManager.clearFocus()
        })
    )
}

@Composable
private fun LazyListState.OnBottomReached(
    itemsBeforeLast: Int = 0,
    loadMore: () -> Unit
) {
    require(itemsBeforeLast >= 0) { "The buffer $itemsBeforeLast must be a positive number" }
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf true
            lastVisibleItem.index == layoutInfo.totalItemsCount - 1 - itemsBeforeLast
        }
    }
    LaunchedEffect(key1 = shouldLoadMore) {
        if (shouldLoadMore) loadMore()
    }
}

@Composable
private fun RecipeListItem(
    modifier: Modifier = Modifier,
    recipe: RecipeItem
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(recipe.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 2
                    )
                    Text(
                        text = stringResource(R.string.ready_in_minutes, recipe.readyInMinutes),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                }
            }
            HtmlText(
                modifier = Modifier.padding(8.dp),
                html = recipe.summary,
                maxLines = 5,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewSearch() {
    CookerTheme {
        SearchRecipeScreenContent(
            screenState = SearchRecipeScreenState(
                recipesFound = listOf(
                    RecipeItem(
                        id = 0,
                        title = "Pasta with Tuna",
                        readyInMinutes = 35,
                        image = "",
                        summary = "Pasta With Tunan is a <b>pescatarian</b> main course. This recipe serves 4. For <b>\$1.68 per serving</b>, this recipe <b>covers 28%</b> of your daily requirements of vitamins and minerals. One serving contains <b>423 calories</b>, <b>24g of protein</b>, and <b>10g of fat</b>. 2 people have made this recipe and would make it again. This recipe from Foodista requires flour, parsley, non-fat milk, and parmesan cheese. From preparation to the plate, this recipe takes around <b>45 minutes</b>. All things considered, we decided this recipe <b>deserves a spoonacular score of 92%</b>. This score is amazing. <a href=\"https://spoonacular.com/recipes/pasta-and-tuna-salad-ensalada-de-pasta-y-atn-226303\">Pastan and Tuna Salad (Ensalada de Pasta y Atún)</a>, <a href=\"https://spoonacular.com/recipes/tuna-pasta-565100\">Tuna Pasta</a>, and <a href=\"https://spoonacular.com/recipes/tuna-pasta-89136\">Tuna Pasta</a> are very similar to this recipe.",
                        isVegetarian = false,
                        isVegan = false,
                        healthScore = 12,
                        servings = 2,
                        sourceUrl = ""
                    ),
                    RecipeItem(
                        id = 1,
                        title = "Pasta Margherita",
                        readyInMinutes = 45,
                        image = "",
                        summary = "You can never have too many main course recipes, so give Pasta Margheritan a try. One serving contains <b>809 calories</b>, <b>34g of protein</b>, and <b>34g of fat</b>. This recipe serves 4. For <b>\$2.75 per serving</b>, this recipe <b>covers 25%</b> of your daily requirements of vitamins and minerals. It is brought to you by Pick Fresh Foods. 1 person were glad they tried this recipe. A mixture of kosher salt, mozzarella cheese, grape tomatoes, and a handful of other ingredients are all it takes to make this recipe so scrumptious. From preparation to the plate, this recipe takes about <b>45 minutes</b>. Taking all factors into account, this recipe <b>earns a spoonacular score of 67%</b>, which is good. If you like this recipe, take a look at these similar recipes: <a href=\"https://spoonacular.com/recipes/pasta-margherita-1372947\">Pasta Margherita</a>, <a href=\"https://spoonacular.com/recipes/pasta-margherita-with-rhubarb-and-apple-compote-613006\">Pasta margherita with rhubarb and apple compote</a>, and <a href=\"https://spoonacular.com/recipes/margherita-pizza-with-pesto-pasta-salad-31919\">Margherita Pizza With Pesto Pasta Salad</a>.",
                        isVegetarian = false,
                        isVegan = false,
                        healthScore = 12,
                        servings = 2,
                        sourceUrl = ""
                    ),
                    RecipeItem(
                        id = 2,
                        title = "Pasta On The Border",
                        readyInMinutes = 105,
                        image = "",
                        summary = "Need a <b>dairy free main course</b>? Pastan On The Border could be an outstanding recipe to try. One portion of this dish contains about <b>22g of protein</b>, <b>20g of fat</b>, and a total of <b>461 calories</b>. This recipe serves 4. For <b>\$2.98 per serving</b>, this recipe <b>covers 25%</b> of your daily requirements of vitamins and minerals. A mixture of mexican herb seasoning, chipotlé enchilada sauce, evoo, and a handful of other ingredients are all it takes to make this recipe so flavorful. 1 person has tried and liked this recipe. From preparation to the plate, this recipe takes about <b>45 minutes</b>. It is brought to you by Foodista. Taking all factors into account, this recipe <b>earns a spoonacular score of 70%</b>, which is pretty good. Similar recipes are <a href=\"https://spoonacular.com/recipes/south-of-the-border-chicken-pasta-skillet-281789\">South-of-the-Border Chicken & Pasta Skillet</a>, <a href=\"https://spoonacular.com/recipes/border-guacamole-101868\">Border Guacamole</a>, and <a href=\"https://spoonacular.com/recipes/border-guacamole-21289\">Border Guacamole</a>.",
                        isVegetarian = false,
                        isVegan = false,
                        healthScore = 12,
                        servings = 2,
                        sourceUrl = ""
                    ),
                )
            ),
            onQueryUpdate = {},
            onSearch = {},
            onClearQuery = {},
            onRecipeClick = {},
            onSettingsClick = {},
            onLoadMoreItems = {},
            onLoadRecentRecipesErrorDismissed = {},
            onSearchingErrorDismissed = {},
            onConnectionErrorDismissed = {}
        )
    }
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewEmptySearch() {
    CookerTheme {
        SearchRecipeScreenContent(
            screenState = SearchRecipeScreenState(),
            onQueryUpdate = {},
            onSearch = {},
            onClearQuery = {},
            onRecipeClick = {},
            onSettingsClick = {},
            onLoadMoreItems = {},
            onLoadRecentRecipesErrorDismissed = {},
            onSearchingErrorDismissed = {},
            onConnectionErrorDismissed = {}
        )
    }
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewLoading() {
    CookerTheme {
        SearchRecipeScreenContent(
            screenState = SearchRecipeScreenState(isLoading = true),
            onQueryUpdate = {},
            onSearch = {},
            onClearQuery = {},
            onRecipeClick = {},
            onSettingsClick = {},
            onLoadMoreItems = {},
            onLoadRecentRecipesErrorDismissed = {},
            onSearchingErrorDismissed = {},
            onConnectionErrorDismissed = {}
        )
    }
}