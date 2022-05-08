package black.bracken.repackk

import com.google.common.truth.Truth.assertThat
import org.junit.Test

data class DifferentSrc(
    val a: Int,
    val b: Int,
    val c: Int,
    val d: Int,
    val e: Int,
    val f: Int,
)

data class DifferentDest(
    val a: Int,
    val b: Int,
    val c: String,
    val d: Int,
    val e: Int,
    val f: Double,
)

@Repack(
    from = DifferentSrc::class,
    to = DifferentDest::class,
)
object DifferentSrcToDest {

    @RepackParam(from = "c", to = "c")
    fun repackC(from: Int): String = "$from"

    @RepackParam(from = "f", to = "f")
    fun repackF(from: Int): Double = from.toDouble()

}



class DifferentParametersTest {

    @Test
    fun testSrcToDest() {
        val src = DifferentSrc(10, 20, 30, 40, 50, 60)
        val dest = src.toDifferentDest()

        assertThat(dest.a).isEqualTo(10)
        assertThat(dest.b).isEqualTo(20)
        assertThat(dest.c).isEqualTo("30")
        assertThat(dest.d).isEqualTo(40)
        assertThat(dest.e).isEqualTo(50)
        assertThat(dest.f).isEqualTo(60.0)
    }

}