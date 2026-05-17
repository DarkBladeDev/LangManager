package dev.darkblade.langmanager.api;

import dev.darkblade.langmanager.api.provider.TranslationProvider;
import dev.darkblade.langmanager.api.renderer.Renderer;
import dev.darkblade.langmanager.api.renderer.TemplateCompiler;
import dev.darkblade.langmanager.api.template.TemplateContext;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public interface Translator<T> {

    T translate(Locale locale, MessageKey key, TemplateContext context);

    <R> T translate(R receiver, MessageKey key, TemplateContext context);

    CompletableFuture<Void> reload();

    @SuppressWarnings("unchecked")
    static <T> Builder<T> builder(Renderer<T> renderer) {
        try {
            Class<?> builderClass = Class.forName("dev.darkblade.langmanager.core.TranslatorBuilderImpl");
            return (Builder<T>) builderClass.getDeclaredConstructor(Renderer.class).newInstance(renderer);
        } catch (Exception e) {
            throw new RuntimeException("Could not find implementation of Translator.Builder. Is the core module present?", e);
        }
    }

    interface Builder<T> {
        Builder<T> addProvider(TranslationProvider provider);
        
        Builder<T> withFallbackStrategy(LocaleFallbackStrategy strategy);
        
        Builder<T> withCompiler(TemplateCompiler<T> compiler);
        
        Builder<T> withDiagnostics(TranslationDiagnostics diagnostics);
        
        // Un placeholder para configuración de caché
        // Builder<T> withCacheSettings(CacheConfiguration config);
        
        Translator<T> build();
    }
}
