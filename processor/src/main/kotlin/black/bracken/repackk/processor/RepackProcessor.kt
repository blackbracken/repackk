package black.bracken.repackk.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
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
            .filter { it is KSClassDeclaration && it.validate() }
            .forEach {
                it.accept(RepackVisitor(logger, codeGenerator), Unit)
            }

        return symbols.filterNot { it.validate() }.toList()
    }

}