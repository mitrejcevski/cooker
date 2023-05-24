package nl.jovmit.cooker.model

class RecipeItemBuilder {

    private var id: Long = 0L
    private var title: String = ""
    private var readyInMinutes: Int = 0
    private var image: String = ""
    private var summary: String = ""
    private var isVegetarian: Boolean = false
    private var isVegan: Boolean = false
    private var healthScore: Int = 0
    private var servings: Int = 0
    private var sourceUrl: String = ""
    private var isFavorite: Boolean = false
    private var instructions: List<RecipeItem.Instruction> = emptyList()
    private var ingredients: List<RecipeItem.Ingredient> = emptyList()

    fun withId(id: Long) = apply {
        this.id = id
    }

    fun withTitle(title: String) = apply {
        this.title = title
    }

    fun withReadyInMinutes(readyInMinutes: Int) = apply {
        this.readyInMinutes = readyInMinutes
    }

    fun withImage(image: String) = apply {
        this.image = image
    }

    fun withSummary(summary: String) = apply {
        this.summary = summary
    }

    fun setIsVegetarian(isVegetarian: Boolean) = apply {
        this.isVegetarian = isVegetarian
    }

    fun setIsVegan(isVegan: Boolean) = apply {
        this.isVegan = isVegan
    }

    fun withHealthScore(healthScore: Int) = apply {
        this.healthScore = healthScore
    }

    fun withServings(servings: Int) = apply {
        this.servings = servings
    }

    fun withSourceUrl(sourceUrl: String) = apply {
        this.sourceUrl = sourceUrl
    }

    fun setIsFavorite(isFavorite: Boolean) = apply {
        this.isFavorite = isFavorite
    }

    fun withInstructions(instructions: List<RecipeItem.Instruction>) = apply {
        this.instructions = instructions
    }

    fun withIngredients(ingredients: List<RecipeItem.Ingredient>) = apply {
        this.ingredients = ingredients
    }

    fun build(): RecipeItem {
        return RecipeItem(
            id,
            title,
            readyInMinutes,
            image,
            summary,
            isVegetarian,
            isVegan,
            healthScore,
            servings,
            sourceUrl
        )
    }

    companion object {

        fun aRecipeItem(): RecipeItemBuilder {
            return RecipeItemBuilder()
        }
    }
}