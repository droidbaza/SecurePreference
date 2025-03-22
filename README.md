```markdown
# 🔐 Secure Preference — Secure Wrapper for SharedPreferences

SecurePreference is a safe and easy-to-use wrapper around `SharedPreferences`,
utilizing `EncryptedSharedPreferences` from `androidx.security.crypto` for data encryption.
It provides a convenient API with `Flow` support and secure key storage using the Android Keystore.

## 🚀 Features

- 🔑 Full Encryption — All data is stored in `EncryptedSharedPreferences`, preventing compromise.
- 📡 Flow Support — Receive real-time data updates.
- ⚡ Ease of Use — A lightweight API with no complex configurations.
- 🔄 Automatic Updates — Track data changes with minimal code.
- 📌 Support for Various Data Types — `Boolean`, `Int`, `String`, `Float`,
 `Long`, `Double`, `Set<String>`, `Serialazible` and `Parcelable`.
- 💨 Asynchronous Operations — Works with `Flow` for high performance.
- 💾 Batch Write and Delete — Save multiple values at once.
- 🛠️ Flexibility — Use multiple `SecurePreference` instances with different keys.
```

## 🔄 SecurePreference vs SharedPreferences vs DataStore

| Feature                     | SecurePreference | SharedPreferences | DataStore |
|-----------------------------|------------------|-------------------|----------|
| Data Encryption         | ✅ Yes           | ❌ No            | ✅ Yes     |
| Flow Support            | ✅ Yes           | ❌ No            | ✅ Yes     |
| Keystore Integration    | ✅ Yes           | ❌ No            | ❌ No     |
| Simple API              | ✅ Yes           | ✅ Yes           | ❌ No     |
| Performance             | ⚡ High           | ⚡ High          | 🐢 Slow   |
| Complex Data Structures | ✅ Yes            | ❌ No            | ✅ Yes     |


## 📦 Installation

Add the dependency to `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.droidbaza:securepreference:$latestVersion")
}
```
## 🛠 Usage

### 🔹 Initialization
```kotlin

   val securePrefs by SecurePrefs(this)
   or
   val sp:SecurePreference = SecurePreferenceImpl(context)
   or
   

```

### 🔹 Saving Data
```kotlin
securePreference.put("user_token", "123456")
securePreference.put("isLoggedIn", true)
securePreference.put("settings", setOf("dark_mode", "notifications"))
```

### 🔹 Retrieving Data
```kotlin
val token = securePrefs.get("user_token","")
val isLoggedIn = securePrefs.get("isLoggedIn", false)
```

### 🔹 Retrieving Data with `Flow`
```kotlin
securePreference.keyResult("user_token", "").collect { token ->
    println("Current token: $token")
}
```

### 🔹 Deleting Data
```kotlin
securePreference.clear("user_token")
securePreference.clear("isLoggedIn", "settings")
```

### 🔹 Tracking All Changes
```kotlin
securePreference.keys().collect { key ->
    println("Key changed: $key")
}
```



## 🤝 Community Support

Contributions are welcome! 🚀  
You can help the project in several ways:

- 📌 Report a Bug — Open an issue if you find a problem.
- 🛠 Contribute Code — Submit PRs with improvements.
- ⭐ Give a Star — Help the project grow!
- 📢 Spread the Word — Share it with your colleagues.

## 📝 License

This project is licensed under **MIT**.  
Happy coding! 💻✨
```
