package black.bracken.repackk

import com.google.common.truth.Truth.assertThat
import org.junit.Test

data class SameSrc(
    val a: Int,
    val b: Int,
    val c: Int,
)

data class SameDest(
    val a: Int,
    val b: Int,
    val c: Int,
)

@Repack(
    from = SameSrc::class,
    to = SameDest::class,
)
object SameSrcToDest

class SameParametersTest {

    @Test
    fun testSrcToDest() {
        val src = SameSrc(10, 20, 30)
        val dest = src.toSameDest()

        assertThat(dest.a).isEqualTo(src.a)
        assertThat(dest.b).isEqualTo(src.b)
        assertThat(dest.c).isEqualTo(src.c)
    }

}

