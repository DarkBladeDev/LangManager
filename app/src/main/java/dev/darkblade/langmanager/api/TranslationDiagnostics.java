package dev.darkblade.langmanager.api;

import java.util.Locale;

public interface TranslationDiagnostics {
    void onMissingKey(MessageKey key, Locale locale);
    
    void onInvalidPlaceholder(MessageKey key, String placeholderKey, Throwable error);
    
    void onRenderFailure(MessageKey key, Locale locale, Throwable error);
    
    void onCorruptLocaleData(Locale locale, String detail);
}
