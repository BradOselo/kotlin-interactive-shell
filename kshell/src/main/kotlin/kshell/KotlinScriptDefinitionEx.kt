package kshell

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import org.jetbrains.kotlin.script.KotlinScriptDefinition
import kotlin.reflect.KClass
import kotlin.script.dependencies.KotlinScriptExternalDependencies

/**
 * Eventually adopted from https://github.com/kohesive/keplin
 */
open class KotlinScriptDefinitionEx(template: KClass<out Any>,
                                    override val defaultEmptyArgs: ScriptArgsWithTypes?,
                                    val defaultImports: List<String> = emptyList())
    : KotlinScriptDefinition(template), ScriptTemplateEmptyArgsProvider {
    class EmptyDependencies : KotlinScriptExternalDependencies
    class DefaultImports(val defaultImports: List<String>, val base: KotlinScriptExternalDependencies) : KotlinScriptExternalDependencies by base {
        override val imports: List<String> get() = (defaultImports + base.imports).distinct()
    }

    override fun <TF : Any> getDependenciesFor(file: TF, project: Project, previousDependencies: KotlinScriptExternalDependencies?): KotlinScriptExternalDependencies? {
        val base = super.getDependenciesFor(file, project, previousDependencies)
        return if (previousDependencies == null && defaultImports.isNotEmpty()) DefaultImports(defaultImports, base ?: EmptyDependencies())
        else base
    }
}

interface ScriptTemplateEmptyArgsProvider {
    val defaultEmptyArgs: org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes?
}