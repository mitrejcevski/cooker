### Instructions
Just load the project into Android Studio (I used the latest beta - "Android Studio Giraffe | 2022.3.1 Beta 4")
and run.

### Assumptions
 - We will have a go-through the codebase where I will have a chance to reason about 
the things done, pros, cons, different ways etc.
 - The app should cache only the recipes opened in details
 - The search works only online
 - The separation to Domain - Data - Model - UI should reflect on the packaging

### Decisions
 - Went with a single module instead of modularizing - would be nice to talk about what modularization
approach would be a nice fit.

 - Went with Sociable tests rather than Solitaire, and I'd be glad to discuss why, pros and cons 
of one over the other etc.

 - Avoided the Paging library because it's still not working 100% well with compose,
and it still has bugs related to the state restoration.

 - Went with more pragmatic decision regarding the architecture,
instead of going "by-the-book" as it is suggested by Google - happy to chat on that.

### Points for improvements
 - AppSettingsRepository - extract mapping to comply with SRP
 - Contract tests for the RecipeDetailsRepository 
(the ones for the SearchRecipeRepository should be enough to explain the idea)
 - Use MockWebServer and InMemoryDatabase in the contract tests for more thorough coverage