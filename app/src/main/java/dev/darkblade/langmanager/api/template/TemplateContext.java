package dev.darkblade.langmanager.api.template;

import dev.darkblade.langmanager.api.template.internal.ContextBuilderImpl;
import net.kyori.adventure.text.Component;

import java.time.temporal.Temporal;
import java.util.Optional;

public interface TemplateContext {

    <T extends PlaceholderValue> Optional<T> get(String key);

    java.util.Map<String, PlaceholderValue> asMap();

    static Builder builder() {
        return new ContextBuilderImpl();
    }

    interface Builder {
        Builder put(String key, String value);
        Builder put(String key, Number value);
        Builder put(String key, Component value);
        Builder put(String key, Boolean value);
        Builder put(String key, Temporal value);
        
        TemplateContext build();
    }
}
