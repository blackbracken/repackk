package black.bracken.repackk

import black.bracken.repackk.processor.RepackProcessor
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class RepackProcessorProvider : SymbolProcessorProvider {

    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor = RepackProcessor(
        codeGenerator = environment.codeGenerator,
        logger = environment.logger,
    )

}