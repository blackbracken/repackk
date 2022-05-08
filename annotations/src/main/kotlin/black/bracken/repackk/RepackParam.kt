package black.bracken.repackk

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RepackParam(
    val from: String,
    val to: String,
)
