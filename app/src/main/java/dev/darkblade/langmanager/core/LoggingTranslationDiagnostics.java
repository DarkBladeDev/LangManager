package dev.darkblade.langmanager.core;

import dev.darkblade.langmanager.api.MessageKey;
import dev.darkblade.langmanager.api.TranslationDiagnostics;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingTranslationDiagnostics implements TranslationDiagnostics {

    private final Logger logger;

    public LoggingTranslationDiagnostics(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void onMissingKey(MessageKey key, Locale locale) {
        logger.warning(String.format("[LangManager] Missing key '%s:%s' for locale '%s'", 
            key.namespace(), key.path(), locale.toString()));
    }

    @Override
    public void onInvalidPlaceholder(MessageKey key, String placeholderKey, Throwable error) {
        logger.log(Level.WARNING, String.format("[LangManager] Invalid placeholder '%s' for key '%s:%s'", 
            placeholderKey, key.namespace(), key.path()), error);
    }

    @Override
    public void onRenderFailure(MessageKey key, Locale locale, Throwable error) {
        logger.log(Level.SEVERE, String.format("[LangManager] Failed to render key '%s:%s' for locale '%s'", 
            key.namespace(), key.path(), locale.toString()), error);
    }

    @Override
    public void onCorruptLocaleData(Locale locale, String detail) {
        logger.severe(String.format("[LangManager] Corrupt locale data for '%s': %s", 
            locale.toString(), detail));
    }
}
