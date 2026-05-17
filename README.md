# LangManager

LangManager es una API de internacionalización (i18n) moderna, de alto rendimiento y fuertemente tipada, diseñada para el ecosistema de Minecraft (basada en **Adventure** y **MiniMessage**).

A diferencia de los sistemas tradicionales, LangManager:
- **Separa el LookUp del Rendering**: Parsea las plantillas de MiniMessage *una sola vez* y las guarda en caché (Caffeine) para evitar la penalización de rendimiento en el chat.
- **Es Type-Safe**: Emplea una jerarquía `sealed` para los placeholders, evitando los errores de runtime comunes por casts inseguros u objetos genéricos.
- **Seguridad en Tiempo de Compilación**: Proporciona un Plugin de Gradle que autogenera tu clase/enum de llaves basándose en tu archivo de traducciones, eliminando las llaves escritas a mano y los errores de tipografía.
- **Soporta Fallbacks Complejos**: Maneja cadenas de fallback asíncronas de locales (ej. `es_AR` -> `es` -> `en_US`).

---

## 🏗 Arquitectura y Módulos

El proyecto se divide en dos módulos principales:
- **`app` (Core & API)**: Contiene la lógica del motor i18n, interfaces, implementación asíncrona de proveedores (`LocalBundleProvider`), el compilador de templates para Adventure y la capa de caché.
- **`gradle-plugin`**: Plugin de Gradle de uso en el ciclo de vida de desarrollo. Genera tus llaves (Implementaciones de `MessageKey`) automáticamente durante la tarea `compileJava`.

---

## 🚀 Uso del Gradle Plugin (Compile-Time Keys)

El plugin de Gradle escanea un archivo `.properties` base que tú elijas y genera un `Enum` de Java implementando la interfaz estricta `MessageKey`.

### Configuración en `build.gradle.kts`

Aplica el plugin en el proyecto donde uses LangManager (por ejemplo, en `MultiBlockEngine`):

```kotlin
plugins {
    id("dev.darkblade.langmanager") version "1.0.0"
}

langManager {
    // El namespace de tu módulo o plugin (ej. "core", "mbe", "addon")
    namespace.set("mbe_core")
    
    // Ruta al archivo de idiomas fuente desde el que se extraerán las llaves
    sourceFile.set("src/main/resources/messages_en.properties")
    
    // Paquete en el que se generará la clase Java
    targetPackage.set("dev.darkblade.mbe.core.i18n")
    
    // Nombre de la clase generada (opcional, por defecto es "Messages")
    className.set("CoreMessages")
}
```

Al ejecutar `./gradlew build` o `./gradlew compileJava`, se creará automáticamente el archivo `CoreMessages.java` en `build/generated/sources/langmanager/java/main/...`. Este archivo es agregado al classpath, por lo que podrás usar `CoreMessages.COMANDO_SIN_PERMISO` directamente en tu código Java.

---

## 🛠 Uso de la API Core

### 1. Inicialización del Translator

Evitamos el uso de Singletons globales. Puedes instanciar tu sistema de traducción mediante el patrón Builder proporcionado:

```java
import dev.darkblade.langmanager.api.Translator;
import dev.darkblade.langmanager.api.LocaleFallbackStrategy;
import dev.darkblade.langmanager.core.SimpleLocaleFallbackStrategy;
import dev.darkblade.langmanager.core.renderer.AdventureRenderer;
import dev.darkblade.langmanager.core.renderer.AdventureTemplateCompiler;
import dev.darkblade.langmanager.core.provider.LocalBundleProvider;
import net.kyori.adventure.text.Component;

import java.util.Locale;
import java.util.Set;

// 1. Instanciamos el Renderer y Compiler de Adventure
AdventureRenderer renderer = new AdventureRenderer();
AdventureTemplateCompiler compiler = new AdventureTemplateCompiler();

// 2. Construimos el Translator
Translator<Component> translator = Translator.builder(renderer)
    .withCompiler(compiler)
    .withFallbackStrategy(new SimpleLocaleFallbackStrategy(Locale.US))
    // Soportamos .properties nativos o .yml usando YamlDirectoryProvider
    .addProvider(new YamlDirectoryProvider(new File("plugins/MiPlugin/lang"), "messages"))
    .build();

// Iniciar carga asíncrona de archivos
translator.reload().thenAccept(v -> {
    System.out.println("¡Traducciones cargadas correctamente!");
});
```

### 2. Contexto de Plantillas (Template Context)

Los reemplazos (Placeholders) en MiniMessage (ej. `<player>` o `<amount>`) se construyen usando un `TemplateContext`. Gracias a los tipos sellados (`sealed`), está protegido en tiempo de compilación.

```java
import dev.darkblade.langmanager.api.template.TemplateContext;

TemplateContext context = TemplateContext.builder()
    .put("player", "Darkblade")                // StringValue
    .put("amount", 500)                        // NumberValue
    .put("status", Component.text("Activo"))   // ComponentValue
    .build();
```

### 3. Traduciendo Mensajes

Una vez construido tu contexto y asumiendo que el plugin de Gradle generó tus llaves, traducir y renderizar el mensaje es muy sencillo:

```java
import net.kyori.adventure.text.Component;
import java.util.Locale;

// Locale extraído dinámicamente de tu jugador (ej. Player#locale())
Locale targetLocale = Locale.of("es", "ES"); 

// Traducimos
Component message = translator.translate(targetLocale, CoreMessages.ECONOMY_BALANCE, context);

// Enviamos al jugador usando Adventure
player.sendMessage(message);
```

### 4. Diagnósticos y Errores (Observabilidad)

Puedes inyectar tu propio manejador de diagnósticos en el Builder (`withDiagnostics(...)`) para registrar en logs o monitoreos cuando falte una traducción, si un placeholder es inválido, o si un archivo `.properties` se corrompió. LangManager provee por defecto `LoggingTranslationDiagnostics` en el paquete `core`.
