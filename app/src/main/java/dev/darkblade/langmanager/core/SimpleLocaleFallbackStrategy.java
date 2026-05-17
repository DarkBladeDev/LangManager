package dev.darkblade.langmanager.core;

import dev.darkblade.langmanager.api.LocaleFallbackStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SimpleLocaleFallbackStrategy implements LocaleFallbackStrategy {

    private final Locale defaultLocale;

    public SimpleLocaleFallbackStrategy(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override
    public List<Locale> getFallbackChain(Locale requestedLocale) {
        List<Locale> chain = new ArrayList<>();
        
        if (requestedLocale != null) {
            chain.add(requestedLocale);
            
            // Si el locale tiene país (es_AR), hacemos fallback a solo el idioma (es)
            if (!requestedLocale.getCountry().isEmpty()) {
                chain.add(Locale.of(requestedLocale.getLanguage()));
            }
        }
        
        // Finalmente agregamos el default absoluto si no estaba ya en la cadena
        if (!chain.contains(defaultLocale)) {
            chain.add(defaultLocale);
        }
        
        return chain;
    }
}
