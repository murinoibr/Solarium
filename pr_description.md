🎯 **What:** The code health issue addressed was an overly long Compose function (`ChatSupportScreen`). The function was split up into several smaller, specialized composable functions.

💡 **Why:** Smaller composable functions improve maintainability and readability by abstracting away complex UI blocks and encapsulating their rendering logic and local state into named functions with clear inputs and outputs. This makes `ChatSupportScreen` a lot easier to parse and modify in the future.

✅ **Verification:**
* Extracted functions `ChatHeader`, `WhatsAppDirectSection`, `MessagesList`, `QuickSuggestions`, and `ChatInputField` properly.
* Ensured they passed the existing data appropriately without breaking state or logic flow.
* Ran `bash ./gradlew lint` to check syntax.
* Ran `bash ./gradlew test` to make sure all tests still pass and no behavior changes are introduced.

✨ **Result:** The code for `ChatSupportScreen` is much shorter and acts mainly as a structural coordinator, improving overall readability and code health.
