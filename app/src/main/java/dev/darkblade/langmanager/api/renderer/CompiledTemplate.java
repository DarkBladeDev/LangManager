package dev.darkblade.langmanager.api.renderer;

import dev.darkblade.langmanager.api.template.TemplateContext;

public interface CompiledTemplate<T> {
    /**
     * Interpola el template pre-compilado con el contexto para generar el output final.
     *
     * @param context El contexto que contiene los valores de los placeholders.
     * @return El resultado final de la interpolación.
     */
    T render(TemplateContext context);
}
