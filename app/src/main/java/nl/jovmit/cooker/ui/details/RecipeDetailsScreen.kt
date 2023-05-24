package nl.jovmit.cooker.ui.details

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import nl.jovmit.cooker.R
import nl.jovmit.cooker.extensions.ShowSnackbar
import nl.jovmit.cooker.ui.details.state.RecipeDetailsScreenState
import nl.jovmit.cooker.model.RecipeItem
import nl.jovmit.cooker.ui.composables.HtmlText
import nl.jovmit.cooker.ui.composables.LoadingIndicator
import nl.jovmit.cooker.ui.composables.Prompt
import nl.jovmit.cooker.ui.theme.CookerTheme


@Composable
fun RecipeDetailsScreen(
    recipeDetailsViewModel: RecipeDetailsViewModel = hiltViewModel(),
    recipeId: Long,
    onNavigateUp: () -> Unit
) {
    val screenState by recipeDetailsViewModel.recipeDetailsScreenState
        .collectAsStateWithLifecycle()
    LaunchedEffect(key1 = recipeId) {
        recipeDetailsViewModel.loadRecipeDetails(recipeId)
    }

    RecipeDetailsScreenContent(
        modifier = Modifier.fillMaxSize(),
        screenState = screenState,
        onNavigateUp = onNavigateUp,
        onToggleFavorite = { recipeDetailsViewModel.toggleFavorite(recipeId) },
        onDismissBackendError = { recipeDetailsViewModel.dismissBackendError() },
        onDismissConnectionError = { recipeDetailsViewModel.dismissConnectionError() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeDetailsScreenContent(
    modifier: Modifier = Modifier,
    screenState: RecipeDetailsScreenState,
    onNavigateUp: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDismissBackendError: () -> Unit,
    onDismissConnectionError: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.cd_navigate_up)
                        )
                    }
                },
                title = {
                    val title = screenState.recipeDetails?.title
                        ?: stringResource(id = R.string.app_name)
                    Text(text = title, maxLines = 1)
                },
                actions = {
                    IconButton(onClick = onToggleFavorite) {
                        val isFavorite = screenState.recipeDetails?.isFavorite ?: false
                        val icon =
                            if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder
                        val descriptionResource =
                            if (isFavorite) R.string.cd_favorite_off else R.string.cd_favorite_on
                        Icon(
                            imageVector = icon,
                            contentDescription = stringResource(id = descriptionResource),
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
        if (screenState.isBackendError) {
            if (screenState.recipeDetails == null) {
                Prompt(
                    title = stringResource(id = R.string.backend_error_title),
                    message = stringResource(id = R.string.error_loading_remote_recipe),
                    onDismissed = onNavigateUp
                )
            } else {
                snackbarHostState.ShowSnackbar(
                    message = stringResource(id = R.string.error_loading_remote_recipe),
                    onDismissed = onDismissBackendError
                )
            }
        }
        if (screenState.isConnectionError) {
            if (screenState.recipeDetails == null) {
                Prompt(
                    title = stringResource(id = R.string.connection_error_title),
                    message = stringResource(id = R.string.connection_error),
                    onDismissed = onNavigateUp
                )
            } else {
                snackbarHostState.ShowSnackbar(
                    message = stringResource(id = R.string.connection_error),
                    onDismissed = onDismissConnectionError
                )
            }
        }
        if (screenState.isRecipeNotFoundError) {
            Prompt(
                title = stringResource(id = R.string.recipe_not_found_error_title),
                message = stringResource(id = R.string.recipe_not_found_message),
                onDismissed = onNavigateUp
            )
        }
        Box(modifier = Modifier.padding(paddingValues)) {
            screenState.recipeDetails?.let { recipe ->
                RecipeContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    recipe = recipe
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

@Composable
private fun RecipeContent(
    modifier: Modifier = Modifier,
    recipe: RecipeItem
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        AsyncImage(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            model = ImageRequest.Builder(LocalContext.current)
                .data(recipe.image)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(24.dp))
        RecipeInformationBanner(
            modifier = Modifier.fillMaxWidth(),
            recipe = recipe
        )
        Spacer(modifier = Modifier.height(16.dp))
        HtmlText(
            modifier = Modifier.fillMaxWidth(),
            html = recipe.summary,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Ingredients(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            ingredients = recipe.ingredients
        )
        Instructions(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            instructions = recipe.instructions
        )
        ExternalSourceLink(sourceUrl = recipe.sourceUrl)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun RecipeInformationBanner(
    modifier: Modifier = Modifier,
    recipe: RecipeItem
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val vegetarianResource = when {
            recipe.isVegetarian -> R.string.vegetarian
            recipe.isVegan -> R.string.vegan
            else -> R.string.non_vegetarian
        }
        val vegetarianText = stringResource(id = vegetarianResource)
        Column(
            modifier = Modifier.semantics(mergeDescendants = true) {
                contentDescription = vegetarianText
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_vegetarian),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = vegetarianText,
                style = MaterialTheme.typography.labelSmall
            )
        }
        val servings = stringResource(id = R.string.servings, recipe.servings)
        Column(
            modifier = Modifier.semantics(mergeDescendants = true) {
                contentDescription = servings
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.People,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = servings,
                style = MaterialTheme.typography.labelSmall
            )
        }
        val healthScore = stringResource(id = R.string.health_score, recipe.healthScore)
        Column(
            modifier = Modifier.semantics(mergeDescendants = true) {
                contentDescription = healthScore
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.HealthAndSafety,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = healthScore,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun Ingredients(
    modifier: Modifier = Modifier,
    ingredients: List<RecipeItem.Ingredient>
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = stringResource(id = R.string.ingredients),
            style = MaterialTheme.typography.titleLarge
        )
        ingredients.forEach { ingredient ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(ingredient.image)
                        .build(),
                    contentDescription = null
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = ingredient.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = "${ingredient.amount} ${ingredient.unit}")
                }
            }
        }
        if (ingredients.isEmpty()) {
            Text(text = stringResource(id = R.string.no_ingredients_available))
        }
    }
}

@Composable
fun Instructions(
    modifier: Modifier,
    instructions: List<RecipeItem.Instruction>
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = stringResource(id = R.string.instructions),
            style = MaterialTheme.typography.titleLarge
        )
        instructions.forEach { step ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${step.number}",
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    modifier = Modifier.weight(1f),
                    text = step.step,
                )
            }
        }
        if (instructions.isEmpty()) {
            Text(text = stringResource(id = R.string.no_instructions_available))
        }
    }
}

@Composable
private fun ExternalSourceLink(
    modifier: Modifier = Modifier,
    sourceUrl: String
) {
    Column(modifier = modifier) {
        val context = LocalContext.current
        Button(
            modifier = Modifier
                .padding(vertical = 8.dp),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sourceUrl))
                context.startActivity(intent)
            },
        ) {
            Text(text = stringResource(id = R.string.open_source_link))
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = null
            )
        }
    }
}

@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewRecipeDetails() {
    CookerTheme {
        RecipeDetailsScreenContent(
            screenState = RecipeDetailsScreenState(
                recipeDetails = RecipeItem(
                    id = 0,
                    title = "Pasta with Tuna",
                    readyInMinutes = 35,
                    image = "",
                    summary = "Pasta With Tuna is a <b>pescatarian</b> main course. This recipe serves 4. For <b>\$1.68 per serving</b>, this recipe <b>covers 28%</b> of your daily requirements of vitamins and minerals. One serving contains <b>423 calories</b>, <b>24g of protein</b>, and <b>10g of fat</b>. 2 people have made this recipe and would make it again. This recipe from Foodista requires flour, parsley, non-fat milk, and parmesan cheese. From preparation to the plate, this recipe takes around <b>45 minutes</b>. All things considered, we decided this recipe <b>deserves a spoonacular score of 92%</b>. This score is amazing. <a href=\"https://spoonacular.com/recipes/pasta-and-tuna-salad-ensalada-de-pasta-y-atn-226303\">Pasta and Tuna Salad (Ensalada de Pasta y Atún)</a>, <a href=\"https://spoonacular.com/recipes/tuna-pasta-565100\">Tuna Pasta</a>, and <a href=\"https://spoonacular.com/recipes/tuna-pasta-89136\">Tuna Pasta</a> are very similar to this recipe.",
                    isVegetarian = false,
                    isVegan = false,
                    healthScore = 45,
                    servings = 4,
                    sourceUrl = "https://source.com/url",
                    isFavorite = true,
                    instructions = listOf(
                        RecipeItem.Instruction(1, "Boil the pasta in a salty water"),
                        RecipeItem.Instruction(2, "Drain the pasta"),
                        RecipeItem.Instruction(3, "Add the tuna in the pasta and mix"),
                        RecipeItem.Instruction(4, "Add salad of choice"),
                        RecipeItem.Instruction(5, "Enjoy")
                    ),
                    ingredients = listOf(
                        RecipeItem.Ingredient("Garlic", "", 1.0, "clove"),
                        RecipeItem.Ingredient("Butter", "", 1.0, "touch"),
                        RecipeItem.Ingredient("Chicken", "", 2.0, "breasts"),
                        RecipeItem.Ingredient("Thyme", "", 1.0, "branch"),
                    )
                )
            ),
            onNavigateUp = {},
            onToggleFavorite = {},
            onDismissBackendError = {},
            onDismissConnectionError = {}
        )
    }
}