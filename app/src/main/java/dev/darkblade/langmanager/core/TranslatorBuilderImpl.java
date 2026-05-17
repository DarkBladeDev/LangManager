package dev.darkblade.langmanager.core;

import dev.darkblade.langmanager.api.*;
import dev.darkblade.langmanager.api.provider.TranslationProvider;
import dev.darkblade.langmanager.api.renderer.Renderer;
import dev.darkblade.langmanager.api.renderer.TemplateCompiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TranslatorBuilderImpl<T> implements Translator.Builder<T> {

    private final Renderer<T> renderer;
    private final List<TranslationProvider> providers = new ArrayList<>();
    private LocaleFallbackStrategy fallbackStrategy = new SimpleLocaleFallbackStrategy(Locale.US);
    private TemplateCompiler<T> compiler;
    private TranslationDiagnostics diagnostics = null;

    public TranslatorBuilderImpl(Renderer<T> renderer) {
        this.renderer = renderer;
    }

    @Override
    public Translator.Builder<T> addProvider(TranslationProvider provider) {
        this.providers.add(provider);
        return this;
    }

    @Override
    public Translator.Builder<T> withFallbackStrategy(LocaleFallbackStrategy strategy) {
        this.fallbackStrategy = strategy;
        return this;
    }

    @Override
    public Translator.Builder<T> withCompiler(TemplateCompiler<T> compiler) {
        this.compiler = compiler;
        return this;
    }

    @Override
    public Translator.Builder<T> withDiagnostics(TranslationDiagnostics diagnostics) {
        this.diagnostics = diagnostics;
        return this;
    }

    @Override
    public Translator<T> build() {
        if (compiler == null) {
            throw new IllegalStateException("TemplateCompiler must be provided");
        }
        
        if (diagnostics == null) {
            // Un dummy diagnostics
            diagnostics = new TranslationDiagnostics() {
                @Override public void onMissingKey(MessageKey key, Locale locale) {}
                @Override public void onInvalidPlaceholder(MessageKey key, String placeholderKey, Throwable error) {}
                @Override public void onRenderFailure(MessageKey key, Locale locale, Throwable error) {}
                @Override public void onCorruptLocaleData(Locale locale, String detail) {}
            };
        }

        TranslationRegistryImpl registry = new TranslationRegistryImpl(fallbackStrategy, diagnostics);
        for (TranslationProvider provider : providers) {
            registry.registerProvider(provider);
        }

        TemplateCacheLayer<T> cacheLayer = new TemplateCacheLayer<>(compiler);

        return new TranslatorImpl<>(registry, cacheLayer, renderer, diagnostics);
    }
}
