💡 **What:**
Added a test to `IconMapperTest.kt` to ensure that `getSectionIcon` treats input case sensitively, effectively returning the fallback `Help` icon for strings like "favorite" (lowercase) instead of a matching icon.

🎯 **Why:**
The previous code had an untested fallback edge case for handling unknown icon names. Providing explicit tests for case sensitivity guarantees that if a known icon's name is passed with wrong casing (e.g. "favorite" vs "Favorite"), the correct fallback behavior is validated, increasing overall code confidence and test coverage.

📊 **Coverage:**
* Added a new test `getSectionIcon_caseSensitive_returnsFallbackVector` mapping "favorite" string explicitly to `Icons.AutoMirrored.Filled.Help`.

✨ **Result:**
The test coverage in `IconMapperTest.kt` is improved, accurately protecting the default fallback return value against false positives.
