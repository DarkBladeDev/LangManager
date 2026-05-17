package dev.darkblade.langmanager.api.provider;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface TranslationProvider {
    /**
     * Retorna un futuro con los datos leídos del disco o red.
     * El mapa exterior es por Locale, el interior es KeyPath -> Texto crudo.
     */
    CompletableFuture<Map<Locale, Map<String, String>>> load();
}
