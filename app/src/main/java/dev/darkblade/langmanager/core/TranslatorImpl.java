package dev.darkblade.langmanager.core;

import dev.darkblade.langmanager.api.*;
import dev.darkblade.langmanager.api.provider.TranslationProvider;
import dev.darkblade.langmanager.api.renderer.CompiledTemplate;
import dev.darkblade.langmanager.api.renderer.Renderer;
import dev.darkblade.langmanager.api.renderer.TemplateCompiler;
import dev.darkblade.langmanager.api.template.TemplateContext;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class TranslatorImpl<T> implements Translator<T> {

    private final TranslationRegistry registry;
    private final TemplateCacheLayer<T> cacheLayer;
    private final Renderer<T> renderer;
    private final TranslationDiagnostics diagnostics;
    
    // Un Map o Strategy para resolver locales a partir de objetos
    // Por simplicidad, omitimos el LocaleResolver Registry real, pero aquí iría.

    TranslatorImpl(TranslationRegistry registry, 
                   TemplateCacheLayer<T> cacheLayer, 
                   Renderer<T> renderer, 
                   TranslationDiagnostics diagnostics) {
        this.registry = registry;
        this.cacheLayer = cacheLayer;
        this.renderer = renderer;
        this.diagnostics = diagnostics;
    }

    @Override
    public T translate(Locale locale, MessageKey key, TemplateContext context) {
        return registry.getRawMessage(locale, key)
            .map(raw -> {
                try {
                    CompiledTemplate<T> template = cacheLayer.getOrCompile(raw, locale);
                    return renderer.render(template, context);
                } catch (Exception e) {
                    diagnostics.onRenderFailure(key, locale, e);
                    // Devolvemos el raw crudo compilado como texto plano, o re-lanzamos
                    // Para simplificar, se asume que T puede no ser instanciable fácilmente, así que devolvemos null o re-lanzamos
                    throw new RuntimeException("Failed to render " + key.namespace() + ":" + key.path(), e);
                }
            })
            // Fallback si no hay llave: devolvemos un output crudo, aunque aquí retornamos null para simplificar
            // Lo correcto sería que el Renderer pueda generar un T a partir de un String puro
            .orElse(null);
    }

    @Override
    public <R> T translate(R receiver, MessageKey key, TemplateContext context) {
        // En una implementación real, se usaría un LocaleResolver asociado al tipo R
        // Ejemplo: Locale locale = localeResolverRegistry.resolve(receiver);
        return translate(Locale.US, key, context);
    }

    @Override
    public CompletableFuture<Void> reload() {
        return registry.reloadAll().thenAccept(v -> cacheLayer.invalidateAll());
    }
}
