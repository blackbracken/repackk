package black.bracken.repackk.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate

class RepackProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("black.bracken.repackk.Repack")

        symbols
            .filter { it.validate() }
            .filterIsInstance<KSClassDeclaration>()
            .forEach {
                val file = codeGenerator
                    .createNewFile(
                        dependencies = Dependencies(
                            false,
                            *resolver.getAllFiles().toList().toTypedArray()
                        ),
                        packageName = it.packageName.asString(),
                        fileName = "${it.simpleName.asString()}Mapper"
                    )

                it.accept(RepackVisitor(logger, file), Unit)

                file.close()
            }

        return symbols.filterNot { it.validate() }.toList()
    }

}