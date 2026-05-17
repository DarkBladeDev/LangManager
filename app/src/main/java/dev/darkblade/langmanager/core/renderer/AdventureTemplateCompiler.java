package dev.darkblade.langmanager.core.renderer;

import dev.darkblade.langmanager.api.renderer.CompiledTemplate;
import dev.darkblade.langmanager.api.renderer.TemplateCompiler;
import dev.darkblade.langmanager.api.template.PlaceholderValue;
import dev.darkblade.langmanager.api.template.TemplateContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdventureTemplateCompiler implements TemplateCompiler<Component> {

    private final MiniMessage miniMessage;

    public AdventureTemplateCompiler(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    public AdventureTemplateCompiler() {
        this.miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public CompiledTemplate<Component> compile(String rawContent, Locale locale) {
        // En una implementación hiper-optimizada, MiniMessage nos permitiría parsear un Node tree una vez
        // y luego inyectar los tags. Como MM 4.x asocia tags en el deserialize, almacenamos el String
        // pero podríamos pre-validarlo.
        return new AdventureCompiledTemplate(miniMessage, rawContent);
    }

    private static class AdventureCompiledTemplate implements CompiledTemplate<Component> {
        private final MiniMessage mm;
        private final String rawContent;

        public AdventureCompiledTemplate(MiniMessage mm, String rawContent) {
            this.mm = mm;
            this.rawContent = rawContent;
        }

        @Override
        public Component render(TemplateContext context) {
            if (context == null || context.asMap().isEmpty()) {
                return mm.deserialize(rawContent);
            }

            List<TagResolver> resolvers = new ArrayList<>();
            for (Map.Entry<String, PlaceholderValue> entry : context.asMap().entrySet()) {
                String key = entry.getKey();
                PlaceholderValue value = entry.getValue();

                if (value instanceof PlaceholderValue.StringValue str) {
                    resolvers.add(Placeholder.unparsed(key, str.value()));
                } else if (value instanceof PlaceholderValue.ComponentValue comp) {
                    resolvers.add(Placeholder.component(key, comp.value()));
                } else if (value instanceof PlaceholderValue.NumberValue num) {
                    resolvers.add(Placeholder.unparsed(key, String.valueOf(num.value())));
                } else if (value instanceof PlaceholderValue.BooleanValue bool) {
                    resolvers.add(Placeholder.unparsed(key, String.valueOf(bool.value())));
                } else if (value instanceof PlaceholderValue.TemporalValue temp) {
                    resolvers.add(Placeholder.unparsed(key, temp.value().toString()));
                }
            }

            return mm.deserialize(rawContent, TagResolver.resolver(resolvers));
        }
    }
}
