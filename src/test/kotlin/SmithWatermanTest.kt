import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SmithWatermanTest {
    @Test
    fun basic() {
        val aligner = SmithWaterman(2.0, 3.0, 3.0)
        // Test case from https://en.wikipedia.org/wiki/Smith%E2%80%93Waterman_algorithm#Example
        assertEquals(aligner.align("TGTTACGG", "GGTTGACTA"),"GTT-AC\nGTTGAC")
    }

    @Test
    fun aBitHarder() {
        val aligner = SmithWaterman(1.0, 5.0, 4.0)
        // Test case from https://en.wikipedia.org/wiki/Smith%E2%80%93Waterman_algorithm#Example
        assertEquals(aligner.align("TACGGGCCCGCTAC", "TAGCCCTATCGGTCA"),"TACGGGCCCGCTA-C\nTA-G--CCC--TATC")
    }

    @Test
    fun covid() {
        val aligner = SmithWaterman(1.0, 5.0, 4.0)
        // Test case from https://en.wikipedia.org/wiki/Smith%E2%80%93Waterman_algorithm#Example
        assertEquals(aligner.align(targetGenome, targetGenome),"$targetGenome\n$targetGenome")
    }
}