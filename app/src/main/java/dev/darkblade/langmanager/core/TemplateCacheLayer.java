package dev.darkblade.langmanager.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.darkblade.langmanager.api.renderer.CompiledTemplate;
import dev.darkblade.langmanager.api.renderer.TemplateCompiler;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TemplateCacheLayer<T> {

    // Key format: "locale:namespace:path" (or just the raw string content as key)
    // If we use the raw string content as key, we don't need to invalidate if we reload,
    // as long as the content changes, the key changes. 
    // However, MiniMessage compilation doesn't depend on the locale normally unless
    // the compiler does specific locale stuff. Let's key by raw content to deduplicate identical messages.
    private final Cache<String, CompiledTemplate<T>> cache;
    private final TemplateCompiler<T> compiler;

    public TemplateCacheLayer(TemplateCompiler<T> compiler) {
        this.compiler = compiler;
        this.cache = Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .build();
    }

    public CompiledTemplate<T> getOrCompile(String rawContent, Locale locale) {
        // Cache by raw content. If the locale affects compilation, we'd need a composite key.
        // For MiniMessage, rawContent is enough.
        return cache.get(rawContent, key -> compiler.compile(key, locale));
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }
}
