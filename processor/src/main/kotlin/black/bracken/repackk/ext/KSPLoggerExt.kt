package black.bracken.repackk.ext

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode

@Suppress("NOTHING_TO_INLINE")
internal inline fun KSPLogger.fatal(message: String, symbol: KSNode? = null): Nothing {
    error(message, symbol)

    throw IllegalStateException()
}