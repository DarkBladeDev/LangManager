package dev.darkblade.langmanager.gradle;

import org.gradle.api.provider.Property;

public interface LangManagerExtension {
    /** The namespace of the plugin (e.g. "mbe") */
    Property<String> getNamespace();
    
    /** The path to the source properties file (e.g. "src/main/resources/messages_en.properties") */
    Property<String> getSourceFile();
    
    /** The package name for the generated enum (e.g. "dev.darkblade.mbe.core.i18n") */
    Property<String> getTargetPackage();
    
    /** The name of the generated enum class (e.g. "Messages") */
    Property<String> getClassName();
}
