package dev.darkblade.langmanager.core.provider;

import dev.darkblade.langmanager.api.provider.TranslationProvider;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class YamlDirectoryProvider implements TranslationProvider {

    private final File directory;
    private final String baseName;

    public YamlDirectoryProvider(File directory, String baseName) {
        this.directory = directory;
        this.baseName = baseName;
    }

    @Override
    public CompletableFuture<Map<Locale, Map<String, String>>> load() {
        return CompletableFuture.supplyAsync(() -> {
            Map<Locale, Map<String, String>> result = new HashMap<>();

            if (!directory.exists() || !directory.isDirectory()) {
                return result;
            }

            File[] files = directory.listFiles((dir, name) -> name.startsWith(baseName + "_") && (name.endsWith(".yml") || name.endsWith(".yaml")));
            
            if (files == null) {
                return result;
            }

            Yaml yaml = new Yaml();

            for (File file : files) {
                // Parse locale from filename, e.g., "messages_es_ES.yml"
                String name = file.getName();
                String localePart = name.substring(baseName.length() + 1, name.lastIndexOf('.'));
                
                String[] parts = localePart.split("_");
                Locale locale;
                if (parts.length == 1) {
                    locale = Locale.of(parts[0]);
                } else if (parts.length == 2) {
                    locale = Locale.of(parts[0], parts[1]);
                } else {
                    locale = Locale.of(parts[0], parts[1], parts[2]);
                }

                try (FileInputStream fis = new FileInputStream(file)) {
                    Map<String, Object> yamlMap = yaml.load(fis);
                    if (yamlMap != null) {
                        Map<String, String> flatMap = new HashMap<>();
                        flattenYaml(yamlMap, "", flatMap);
                        result.put(locale, flatMap);
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // En un entorno real se usaría el logger
                }
            }

            return result;
        });
    }

    @SuppressWarnings("unchecked")
    private void flattenYaml(Map<String, Object> source, String path, Map<String, String> dest) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            String newPath = path.isEmpty() ? key : path + "." + key;
            Object value = entry.getValue();
            if (value instanceof Map) {
                flattenYaml((Map<String, Object>) value, newPath, dest);
            } else if (value != null) {
                dest.put(newPath, String.valueOf(value));
            }
        }
    }
}
