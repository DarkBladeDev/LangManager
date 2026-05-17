package dev.darkblade.langmanager.api.template.internal;

import dev.darkblade.langmanager.api.template.PlaceholderValue;
import dev.darkblade.langmanager.api.template.TemplateContext;
import net.kyori.adventure.text.Component;

import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ContextBuilderImpl implements TemplateContext.Builder {

    private final Map<String, PlaceholderValue> placeholders = new HashMap<>();

    @Override
    public TemplateContext.Builder put(String key, String value) {
        placeholders.put(key, new PlaceholderValue.StringValue(value));
        return this;
    }

    @Override
    public TemplateContext.Builder put(String key, Number value) {
        placeholders.put(key, new PlaceholderValue.NumberValue(value));
        return this;
    }

    @Override
    public TemplateContext.Builder put(String key, Component value) {
        placeholders.put(key, new PlaceholderValue.ComponentValue(value));
        return this;
    }

    @Override
    public TemplateContext.Builder put(String key, Boolean value) {
        placeholders.put(key, new PlaceholderValue.BooleanValue(value));
        return this;
    }

    @Override
    public TemplateContext.Builder put(String key, Temporal value) {
        placeholders.put(key, new PlaceholderValue.TemporalValue(value));
        return this;
    }

    @Override
    public TemplateContext build() {
        // Create an unmodifiable snapshot for the context
        final Map<String, PlaceholderValue> snapshot = Map.copyOf(placeholders);
        
        return new TemplateContext() {
            @Override
            @SuppressWarnings("unchecked")
            public <T extends PlaceholderValue> Optional<T> get(String key) {
                return Optional.ofNullable((T) snapshot.get(key));
            }

            @Override
            public Map<String, PlaceholderValue> asMap() {
                return snapshot;
            }
        };
    }
}
