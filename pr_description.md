## 🧹 [Code Health] Remove unused imports in GoogleMapView.kt

### 🎯 **What:**
Removed unused imports (`ClipData`, `ClipboardManager`, and `Context`) from `app/src/main/java/com/example/ui/components/GoogleMapView.kt`.

### 💡 **Why:**
Unused imports bloat the source file without adding any value. Removing them improves the codebase's readability, cleanliness, and overall maintainability while preserving the existing behavior.

### ✅ **Verification:**
- Validated via automated unit tests (`./gradlew test`) - 42 actionable tasks executed properly.
- Validated via lint checks (`./gradlew lint`) - no new warnings or issues found.
- The modifications target only unused declarations and won't affect compilation or execution.

### ✨ **Result:**
Cleaner code structure in `GoogleMapView.kt`, avoiding confusion when reading dependencies.
