package dev.darkblade.langmanager.api.renderer;

import java.util.Locale;

public interface TemplateCompiler<T> {
    /**
     * Ejecutado SOLO durante carga o cache miss.
     * Ej: Parsea tags de MiniMessage para generar un modelo interno.
     *
     * @param rawContent El texto crudo del archivo de idiomas.
     * @param locale El locale objetivo.
     * @return El template compilado listo para renderizar repetidamente.
     */
    CompiledTemplate<T> compile(String rawContent, Locale locale);
}
