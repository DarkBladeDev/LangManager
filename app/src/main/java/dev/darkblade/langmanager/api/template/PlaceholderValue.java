package dev.darkblade.langmanager.api.template;

import net.kyori.adventure.text.Component;
import java.time.temporal.Temporal;

public sealed interface PlaceholderValue {

    Object raw();

    record StringValue(String value) implements PlaceholderValue {
        @Override
        public Object raw() {
            return value;
        }
    }

    record ComponentValue(Component value) implements PlaceholderValue {
        @Override
        public Object raw() {
            return value;
        }
    }

    record NumberValue(Number value) implements PlaceholderValue {
        @Override
        public Object raw() {
            return value;
        }
    }

    record BooleanValue(Boolean value) implements PlaceholderValue {
        @Override
        public Object raw() {
            return value;
        }
    }

    record TemporalValue(Temporal value) implements PlaceholderValue {
        @Override
        public Object raw() {
            return value;
        }
    }
}
