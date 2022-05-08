# repackk
an attempt to use ksp

```kotlin
fun main() {
    val src = Src(
        10, 20, 30, 40, 50, 60
    )
    
    val dest = src.toDest() // <-- auto-generated
    
    println(dest.f) // 60.0
}

data class Src(
    val a: Int,
    val b: Int,
    val c: Int,
    val d: Int,
    val e: Int,
    val f: Int,
)

data class Dest(
    val a: Int,
    val b: Int,
    val c: String,
    val d: Int,
    val e: Int,
    val f: Double,
)

@Repack(
    from = Src::class,
    to = Dest::class,
)
object DifferentSrcToDest {

    @RepackParam(from = "c", to = "c")
    fun repackC(from: Int): String = "$from"

    @RepackParam(from = "f", to = "f")
    fun repackF(from: Int): Double = from.toDouble()

}
```