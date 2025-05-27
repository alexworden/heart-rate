# Product Requirements - HeartRate

HeartRate is a fun web application that allows users to add ratings for the things they love and those things they don't like also. 
There are a few screens or modes that the app can be in. These can be accessed via icon buttons at the top of the screen on the menu bar and will take the user to their corresponding page. The pages are as follows: 

## Home Screen

The default "home screen" presents the user with a stream of concepts or items for them to quickly add a 0-5 star rating for, presenting them with an image of the object and quickly allowing them to swipe to set their rating in a star-bar widget or click a button to say they don't know what an object is, or don't care about rating it al all. 

When there are no items to ask the user to rate, say so and offer a link to add items that will take the user to the Add Item Screen. 

## Add Item Screen

The "Add Item Screen" allows a user to add a new item to rate. They can enter a name, description, and upload a photograph of it easily from their desktop or phone. They can then easily add the item to a category by typing the name of the category to search. They can create a new category if an existing one is not found and can nest that category under a parent category by similarly searching for it. The app will help the user create and nest the category under a parent category, or allow them to create a new top-level category through a wizard dialog by asking them questions. The app will use OpenAI's LLM to automatically detect the type of object being added and match it to existing instances of that object if already in the system or an existing category if a similar one exists. The app will ask the user if the item they are adding is the same as the ones it has found and allow the user to make their item the same. It will encourage them to re-use existing items and categories before allowing them to add their item with a new photograph. 

In addition to categorizing an item, the user can give a location if it is relevant to the item. 

## Recommendation Screen

The "Recommended Screen" will suggest items to the user that the system thinks they will like. These recommendations will be generated based on several factors:

- **User's past ratings:** The system will analyze the items the user has rated highly (and lowly) to understand their preferences.
- **Ratings of similar users:** The system will identify users with similar rating patterns and suggest items that those users liked.
- **Item categories and attributes:** Recommendations will consider the categories, tags, and other attributes of items the user has engaged with.
- **Popularity and trending items:** The system may also suggest popular items or items that are currently trending within the community.

Users will be presented with a list or a scrollable interface of recommended items. For each recommended item, they will see the item's image, name, and potentially a brief description or the reason for the recommendation (e.g., "Because you liked [another item]"). Users will be able to:

- **Rate the recommended item:** Directly rate the item from the recommendation screen.
- **View item details:** Click on the item to see more details on the "Item Details Screen".
- **Dismiss the recommendation:** Indicate that they are not interested in the suggested item, which will help refine future recommendations.
- **Filter or sort recommendations:** (Future enhancement) Allow users to filter recommendations by category, popularity, or other criteria. 