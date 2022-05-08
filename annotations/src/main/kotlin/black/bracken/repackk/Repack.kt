package black.bracken.repackk

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Repack(
    val from: KClass<*>,
    val to: KClass<*>,
)