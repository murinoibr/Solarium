🔒 [Security Fix] Extract Hardcoded Wi-Fi Password to BuildConfig

### 🎯 What
Extracted the hardcoded Wi-Fi password (`WIFI_PASSWORD`) in `HouseRepository.kt` into the `.env` file and accessed it via `BuildConfig`.

### ⚠️ Risk
Hardcoding passwords, API keys, and other sensitive information in source code is a major security vulnerability. If the repository is leaked or accessed by unauthorized individuals, the credentials could be compromised.

### 🛡️ Solution
We securely moved the `WIFI_PASSWORD` value to a generated `BuildConfig` property. This ensures the password remains available at runtime but is securely configured through the `secrets` Gradle plugin loading from `.env` instead of being exposed directly in source control.
