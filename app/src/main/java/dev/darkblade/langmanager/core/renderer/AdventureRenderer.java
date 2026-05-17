package dev.darkblade.langmanager.core.renderer;

import dev.darkblade.langmanager.api.renderer.CompiledTemplate;
import dev.darkblade.langmanager.api.renderer.Renderer;
import dev.darkblade.langmanager.api.template.TemplateContext;
import net.kyori.adventure.text.Component;

public class AdventureRenderer implements Renderer<Component> {

    @Override
    public Component render(CompiledTemplate<Component> template, TemplateContext context) {
        return template.render(context);
    }
}
