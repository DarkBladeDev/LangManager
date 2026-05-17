package dev.darkblade.langmanager.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

public class LangManagerPlugin implements Plugin<Project> {
    
    @Override
    public void apply(Project project) {
        LangManagerExtension extension = project.getExtensions()
            .create("langManager", LangManagerExtension.class);

        // Definimos un default para el targetClassName
        extension.getClassName().convention("Messages");

        TaskProvider<GenerateMessageKeysTask> generateTask = project.getTasks().register(
            "generateMessageKeys", 
            GenerateMessageKeysTask.class, 
            task -> {
                task.getNamespace().set(extension.getNamespace());
                task.getSourceFile().set(extension.getSourceFile());
                task.getTargetPackage().set(extension.getTargetPackage());
                task.getClassName().set(extension.getClassName());
                
                // Definir el directorio de salida en build/generated/sources/langmanager/java
                task.getOutputDirectory().set(
                    project.getLayout().getBuildDirectory().dir("generated/sources/langmanager/java/main")
                );
            }
        );

        // Aseguramos que compileJava dependa de esta tarea y añadimos el directorio generado al source set
        project.getPluginManager().withPlugin("java", plugin -> {
            project.getTasks().named("compileJava").configure(task -> {
                task.dependsOn(generateTask);
            });
            
            // Añadir el código generado al source set "main"
            org.gradle.api.plugins.JavaPluginExtension javaExt = 
                project.getExtensions().getByType(org.gradle.api.plugins.JavaPluginExtension.class);
            javaExt.getSourceSets().getByName("main").getJava()
                .srcDir(generateTask.flatMap(GenerateMessageKeysTask::getOutputDirectory));
        });
    }
}
