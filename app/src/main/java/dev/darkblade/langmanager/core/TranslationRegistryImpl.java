package dev.darkblade.langmanager.core;

import dev.darkblade.langmanager.api.MessageKey;
import dev.darkblade.langmanager.api.TranslationDiagnostics;
import dev.darkblade.langmanager.api.TranslationRegistry;
import dev.darkblade.langmanager.api.provider.TranslationProvider;
import dev.darkblade.langmanager.api.LocaleFallbackStrategy;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TranslationRegistryImpl implements TranslationRegistry {

    private final List<TranslationProvider> providers = new CopyOnWriteArrayList<>();
    // Locale -> Namespace:Path -> String
    private final Map<Locale, Map<String, String>> messageData = new ConcurrentHashMap<>();
    
    private final LocaleFallbackStrategy fallbackStrategy;
    private final TranslationDiagnostics diagnostics;

    public TranslationRegistryImpl(LocaleFallbackStrategy fallbackStrategy, TranslationDiagnostics diagnostics) {
        this.fallbackStrategy = fallbackStrategy;
        this.diagnostics = diagnostics;
    }

    @Override
    public Optional<String> getRawMessage(Locale locale, MessageKey key) {
        List<Locale> chain = fallbackStrategy.getFallbackChain(locale);
        String fullPath = key.namespace() + ":" + key.path();
        
        for (Locale fallback : chain) {
            Map<String, String> localeMessages = messageData.get(fallback);
            if (localeMessages != null && localeMessages.containsKey(fullPath)) {
                return Optional.of(localeMessages.get(fullPath));
            }
        }
        
        diagnostics.onMissingKey(key, locale);
        return Optional.empty();
    }

    @Override
    public void registerProvider(TranslationProvider provider) {
        providers.add(provider);
    }

    @Override
    public CompletableFuture<Void> reloadAll() {
        if (providers.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        
        List<CompletableFuture<Map<Locale, Map<String, String>>>> futures = new ArrayList<>();
        for (TranslationProvider provider : providers) {
            futures.add(provider.load());
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenAccept(v -> {
                // Limpiamos los datos actuales
                messageData.clear();
                
                // Mezclamos los resultados de todos los proveedores
                for (CompletableFuture<Map<Locale, Map<String, String>>> future : futures) {
                    Map<Locale, Map<String, String>> result = future.join();
                    
                    for (Map.Entry<Locale, Map<String, String>> entry : result.entrySet()) {
                        messageData.computeIfAbsent(entry.getKey(), k -> new ConcurrentHashMap<>())
                                   .putAll(entry.getValue());
                    }
                }
            });
    }
}
