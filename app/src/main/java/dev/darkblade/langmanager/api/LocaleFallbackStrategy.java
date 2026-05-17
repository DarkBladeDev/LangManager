package dev.darkblade.langmanager.api;

import java.util.List;
import java.util.Locale;

public interface LocaleFallbackStrategy {
    /**
     * Resuelve la cadena de idiomas a consultar, en orden de prioridad.
     * @param requestedLocale El locale solicitado inicialmente
     * @return Lista de Locales en orden (el primero es el de mayor prioridad)
     */
    List<Locale> getFallbackChain(Locale requestedLocale);
}
