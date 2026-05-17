package dev.darkblade.langmanager.api;

import dev.darkblade.langmanager.api.provider.TranslationProvider;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TranslationRegistry {
    Optional<String> getRawMessage(Locale locale, MessageKey key);
    
    void registerProvider(TranslationProvider provider);
    
    CompletableFuture<Void> reloadAll();
}
