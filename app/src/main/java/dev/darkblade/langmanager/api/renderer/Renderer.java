package dev.darkblade.langmanager.api.renderer;

import dev.darkblade.langmanager.api.template.TemplateContext;

public interface Renderer<T> {
    /**
     * Encapsula la lógica final de renderizado.
     *
     * @param template El template precompilado que será interpolado.
     * @param context El contexto de las variables para interpolar.
     * @return El output renderizado final para la plataforma específica.
     */
    T render(CompiledTemplate<T> template, TemplateContext context);
}
