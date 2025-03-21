```markdown
# 🔐 SecurePreference — Защищённая обёртка над SharedPreferences

SecurePreference — это безопасная и простая в использовании обёртка над `SharedPreferences`, которая использует `EncryptedSharedPreferences` из `androidx.security.crypto` для шифрования данных. Она предоставляет удобный API с поддержкой `Flow`, а также безопасное хранение ключей с использованием Android Keystore.

## 🚀 Возможности

- **🔑 Полное шифрование** — Все данные хранятся в `EncryptedSharedPreferences`, что предотвращает их компрометацию.
- **📡 Поддержка Flow** — Получайте обновления данных в реальном времени.
- **⚡ Простота использования** — Лёгкий API без сложных конфигураций.
- **🔄 Автоматические обновления** — Отслеживание изменений данных без лишнего кода.
- **📌 Поддержка различных типов данных** — `Boolean`, `Int`, `String`, `Float`, `Long`, `Double`, `Set<String>`.
- **💨 Асинхронность** — Работа с `Flow` и `Dispatchers.IO` для высокой производительности.
- **💾 Массовая запись и удаление данных** — Возможность сохранения нескольких значений за раз.
- **🛠️ Гибкость** — Можно использовать несколько `SecurePreference`-хранилищ с разными ключами.

## 📦 Установка

Добавьте зависимость в `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.droidbaza:securepreference:1.0.0")
}
```

## 🛠 Использование

### 🔹 Инициализация
```kotlin
class MyApp : Application() {
    val securePrefs by SecurePrefs(this)
}
```

### 🔹 Сохранение данных
```kotlin
securePreference.put("user_token", "123456")
securePreference.put("isLoggedIn", true)
securePreference.put("settings", setOf("dark_mode", "notifications"))
```

### 🔹 Получение данных
```kotlin
val token: String? = securePrefs.get("user_token")
val isLoggedIn: Boolean = securePrefs.get("isLoggedIn", false)
```

### 🔹 Получение данных в `Flow`
```kotlin
securePreference.keyResult("user_token", "").collect { token ->
    println("Текущий токен: $token")
}
```

### 🔹 Удаление данных
```kotlin
securePreference.clear("user_token")
securePreference.clear("isLoggedIn", "settings")
```

### 🔹 Отслеживание всех изменений
```kotlin
securePreference.keys().collect { key ->
    println("Изменён ключ: $key")
}
```

## 🔄 SecurePreference vs SharedPreferences vs DataStore

| Функция                     | SecurePreference | SharedPreferences | DataStore |
|-----------------------------|------------------|-------------------|----------|
| **Шифрование данных**       | ✅ Да             | ❌ Нет            | ✅ Да     |
| **Поддержка Flow**          | ✅ Да             | ❌ Нет            | ✅ Да     |
| **Работа с Keystore**       | ✅ Да             | ❌ Нет            | ❌ Нет    |
| **Простота API**            | ✅ Да             | ✅ Да             | ❌ Нет    |
| **Производительность**      | ⚡ Высокая        | ⚡ Высокая        | 🐢 Медленная |
| **Поддержка сложных структур** | ❌ Нет            | ❌ Нет            | ✅ Да     |

## 🤝 Поддержка сообщества

Любой вклад приветствуется! 🚀  
Вы можете помочь проекту несколькими способами:

- 📌 **Сообщить о баге** — откройте issue, если нашли проблему.
- 🛠 **Развивать код** — присылайте PR с улучшениями.
- ⭐ **Поставить звезду** — помогите проекту расти!
- 📢 **Рассказать о библиотеке** — поделитесь с коллегами.

## 📝 Лицензия

Этот проект распространяется под лицензией **MIT**.  
Happy coding! 💻✨
```