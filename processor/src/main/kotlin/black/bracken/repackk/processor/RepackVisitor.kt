package black.bracken.repackk.processor

import black.bracken.repackk.ext.fatal
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import java.io.OutputStream

class RepackVisitor(
    private val logger: KSPLogger,
    private val file: OutputStream,
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        if (classDeclaration.classKind !in setOf(ClassKind.CLASS, ClassKind.OBJECT)) {
            logger.fatal(
                "Only classes can be transformed with @Repack (current: ${classDeclaration.classKind})",
                classDeclaration
            )
        }

        val annotation: KSAnnotation = classDeclaration.annotations.first { it.shortName.getShortName() == "Repack" }

        val packageName = classDeclaration.packageName.asString()
        val simpleName = classDeclaration.simpleName.asString()

        val fromDeclaration = annotation.arguments.extractParamDeclaration("from")
        val toDeclaration = annotation.arguments.extractParamDeclaration("to")

        val fromName = fromDeclaration.simpleName.asString()
        val toName = toDeclaration.simpleName.asString()

        val fromFqcn = "${fromDeclaration.packageName.asString()}.${fromName}"
        val toFqcn = "${toDeclaration.packageName.asString()}.${toName}"

        file += "package $packageName\n"
        file += "\n"

        file += "import $packageName.$simpleName\n"
        file += "import $fromFqcn\n"
        file += "import $toFqcn\n"

        file += "\n"

        file += "fun $fromName.to$toName(): $toName = $toName(\n"

        toDeclaration.getAllProperties()
            .map { toProp ->
                val toPropName = toProp.simpleName.asString()

                val fromProp = fromDeclaration.getAllProperties()
                    .firstOrNull { toPropName == it.simpleName.asString() }
                    ?: logger.fatal("Failed to find parameter corresponding to $toPropName", toProp)

                fromProp to toProp
            }.forEach { (fromProp, toProp) ->
                val fromPropName = fromProp.simpleName.asString()
                val toPropName = toProp.simpleName.asString()

                val matchesType = fromProp.type.resolve() == toProp.type.resolve()
                        && fromProp.typeParameters == toProp.typeParameters

                if (matchesType) {
                    file += "  $fromPropName = $toPropName,\n"
                } else {
                    val mapper = classDeclaration.getAllFunctions()
                        .firstOrNull { func ->
                            val repackParamAnnotation = func.annotations
                                .firstOrNull { annotation ->
                                    annotation.shortName.asString() == "RepackParam"
                                }
                                ?: return@firstOrNull false

                            repackParamAnnotation.arguments
                                .all { arg ->
                                    when (arg.name?.getShortName()) {
                                        "from" -> fromPropName == arg.value
                                        "to" -> toPropName == arg.value
                                        else -> true
                                    }
                                }
                        } ?: logger.fatal("Failed to find mapper for ${toName}.${toPropName}}", classDeclaration)

                    file += "  $toPropName = $simpleName.${mapper.simpleName.asString()}($fromPropName),\n"
                }
            }

        file += ")\n"
    }

    private operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    private fun List<KSValueArgument>.extractParamDeclaration(name: String): KSClassDeclaration =
        first { param -> param.name?.getShortName() == name }
            .let { it.value as KSType }
            .let { it.declaration as KSClassDeclaration }

}