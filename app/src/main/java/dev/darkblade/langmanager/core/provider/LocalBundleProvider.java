package dev.darkblade.langmanager.core.provider;

import dev.darkblade.langmanager.api.provider.TranslationProvider;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class LocalBundleProvider implements TranslationProvider {

    private final String baseName;
    private final Set<Locale> supportedLocales;

    public LocalBundleProvider(String baseName, Set<Locale> supportedLocales) {
        this.baseName = baseName;
        this.supportedLocales = supportedLocales;
    }

    @Override
    public CompletableFuture<Map<Locale, Map<String, String>>> load() {
        return CompletableFuture.supplyAsync(() -> {
            Map<Locale, Map<String, String>> result = new HashMap<>();

            for (Locale locale : supportedLocales) {
                try {
                    ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale, ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
                    Map<String, String> localeMap = new HashMap<>();
                    
                    for (String key : bundle.keySet()) {
                        localeMap.put(key, bundle.getString(key));
                    }
                    
                    result.put(locale, localeMap);
                } catch (MissingResourceException e) {
                    // Si no existe un archivo para ese locale específico, lo ignoramos.
                }
            }

            return result;
        });
    }
}
