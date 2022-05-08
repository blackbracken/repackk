package black.bracken.repackk

import com.google.common.truth.Truth.assertThat
import org.junit.Test

data class DifferentSrc(
    val a: Int,
    val b: Int,
    val c: Int,
)

data class DifferentDest(
    val a: Int,
    val b: Int,
    val c: String,
)

@Repack(
    from = DifferentSrc::class,
    to = DifferentDest::class,
)
object DifferentSrcToDest {

    @RepackParam(from = "c", to = "c")
    fun repackC(from: Int): String = "$from"

}

class DifferentParametersTest {

    @Test
    fun testSrcToDest() {
        val src = DifferentSrc(10, 20, 30)
        val dest = src.toDifferentDest()

        assertThat(dest.a).isEqualTo(10)
        assertThat(dest.b).isEqualTo(20)
        assertThat(dest.c).isEqualTo("30")
    }

}