package black.bracken.repackk

import black.bracken.repackk.ext.fatal
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import java.io.OutputStream

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

class RepackVisitor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        if (classDeclaration.classKind !in setOf(ClassKind.CLASS, ClassKind.OBJECT)) {
            logger.fatal(
                "Only classes can be transformed with @Repack (current: ${classDeclaration.classKind})",
                classDeclaration
            )
        }

        val annotation: KSAnnotation = classDeclaration.annotations
            .first { it.shortName.getShortName() == "Repack" }

        val fromFqcn = extractFqcn(annotation, "from")
        val toFqcn = extractFqcn(annotation, "to")

        val fromName = fromFqcn.split('.').last()
        val toName = toFqcn.split('.').last()

        val fromDeclaration = annotation.arguments
            .first { param -> param.name?.getShortName() == "from" }
            .let { it.value as KSType }
            .let { it.declaration as KSClassDeclaration }

        val toDeclaration = annotation.arguments
            .first { param -> param.name?.getShortName() == "to" }
            .let { it.value as KSType }
            .let { it.declaration as KSClassDeclaration }

        val file = codeGenerator
            .createNewFile(
                dependencies = Dependencies(
                    false,
                ),
                packageName = classDeclaration.packageName.asString(),
                fileName = "${classDeclaration.simpleName.asString()}Mapper"
            )

        file += "package ${classDeclaration.packageName.asString()}\n"
        file += "\n"

        file += "import ${classDeclaration.packageName.asString()}.${classDeclaration.simpleName.asString()}\n"
        file += "import $fromFqcn\n"
        file += "import $toFqcn\n"

        file += "\n"

        file += "fun $fromName.to$toName(): $toName = $toName(\n"

        toDeclaration.getAllProperties().forEach { prop ->
            val corresponded = fromDeclaration.getAllProperties()
                .first { prop.simpleName.asString() == it.simpleName.asString() }

            if (prop.type.resolve() == corresponded.type.resolve()
                && prop.typeParameters == corresponded.typeParameters
            ) {
                val propName = prop.simpleName.asString()
                file += "  $propName = $propName,\n"
            } else {
                val mapper = classDeclaration.getAllFunctions()
                    .first { func ->
                        logger.warn(
                            "${func.simpleName.asString()} | annotations = ${
                                func.annotations.map { it.shortName.asString() }.toList()
                            }"
                        )
                        val repackParamAnnotation = func.annotations
                            .first { annotation -> annotation.shortName.asString() == "RepackParam" }

                        repackParamAnnotation.arguments
                            .all { arg ->
                                when (arg.name?.getShortName()) {
                                    "from" -> corresponded.simpleName.asString() == arg.value
                                    "to" -> prop.simpleName.asString() == arg.value
                                    else -> true
                                }
                            }
                    }

                file += "  ${prop.simpleName.asString()} = ${classDeclaration.simpleName.asString()}.${mapper.simpleName.asString()}(${corresponded.simpleName.asString()}),\n"
            }
        }

        file += ")\n"

        file.close()
    }

    private operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    private fun extractFqcn(annotation: KSAnnotation, parameterName: String): String {
        val param = annotation.arguments
            .firstOrNull { param -> param.name?.getShortName() == parameterName }
            ?: logger.fatal("$parameterName parameter is not found", annotation)

        val declaration = (param.value as? KSType)
            ?.declaration
            ?: logger.fatal("$parameterName parameter must be KSType", annotation)

        return "${declaration.packageName.asString()}.${declaration.simpleName.asString()}"
    }

}

class RepackProcessorProvider : SymbolProcessorProvider {

    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor = RepackProcessor(
        codeGenerator = environment.codeGenerator,
        logger = environment.logger,
    )

}