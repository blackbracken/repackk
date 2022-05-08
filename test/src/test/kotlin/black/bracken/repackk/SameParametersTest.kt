package black.bracken.repackk

import com.google.common.truth.Truth.assertThat
import org.junit.Test

data class Src(
    val a: Int,
    val b: Int,
    val c: Int,
)

data class Dest(
    val a: Int,
    val b: Int,
    val c: Int,
)

@Repack(
    from = Src::class,
    to = Dest::class,
)
object SrcToDest

class RepackTest {

    @Test
    fun testSrcToDest() {
        val src = Src(10, 20, 30)
        val dest = src.toDest()

        assertThat(dest.a).isEqualTo(src.a)
        assertThat(dest.b).isEqualTo(src.b)
        assertThat(dest.c).isEqualTo(src.c)
    }

}

