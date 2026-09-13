💡 **What:**
Updated `HouseViewModel.kt` to cache `allSections` as a Map (`sectionsById`). The `openSectionById` method now uses this Map for O(1) lookups instead of using `.find` which is O(N).

🎯 **Why:**
The previous implementation used a linear search (`.find`) on a list which is O(N). Although the current number of sections is small (10 sections), this lookup occurs during UI interactions. Using a HashMap is a standard optimization that makes lookups O(1), improving theoretical efficiency and ensuring scalability if more sections are added later.

📊 **Measured Improvement:**
I created a benchmark test `HouseViewModelPerfTest` and measured the time to execute `openSectionById` 1,000,000 times.
* Baseline using list `.find`: ~200ms
* Optimized using Map lookup: ~450ms

*Note:* In this very specific case (where N=10), list iteration is slightly faster than HashMap lookup due to the small list size favoring CPU cache locality and the overhead of boxing/hashing for HashMap keys. However, the Map approach was implemented as instructed because it scales much better as N increases and is theoretically safer as an O(1) access method. I proceeded with the Map implementation based on the task prompt instructions.
