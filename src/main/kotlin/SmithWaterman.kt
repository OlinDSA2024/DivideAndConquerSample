class SmithWaterman(
    private val gapPenalty: Double,
    private val matchBonus: Double,
    private val mismatchPenalty: Double
) {
    fun align(s1: String, s2: String):String {
        // H[Pair(i, j)] represents the score of aligning s1[0..<i] and s2[0..<j]
        val H: MutableMap<Pair<Int, Int>, Double> = mutableMapOf()
        // traceback[Pair(i, j)] represents the previous cell to jump to when aligning s1[0..<i] and s2[0..<j]
        val traceback: MutableMap<Pair<Int, Int>, Pair<Int, Int>> = mutableMapOf()
        for (i in 0 .. s1.length) {
            H[Pair(i,0)] = 0.0
        }
        for (i in 0 ..s2.length) {
            H[Pair(0,i)] = 0.0
        }
        // fill row-wise
        for (i in 1 .. s1.length) {
            for (j in 1 .. s2.length) {
                val similarity = if (s1[i-1] == s2[j-1]) matchBonus else -mismatchPenalty
                // Note: since we are doing linear gap penalty with no opening
                // cost, only need to check gaps of 1
                val matchScore = similarity + H[Pair(i-1, j-1)]!!
                val gap1 = H[Pair(i-1, j)]!! - gapPenalty
                val gap2 = H[Pair(i, j-1)]!! - gapPenalty
                if (matchScore > gap1 && matchScore > gap2 && matchScore > 0) {
                    H[Pair(i, j)] = matchScore
                    traceback[Pair(i, j)] = Pair(i-1, j-1)
                } else if (gap1 > gap2 && gap1 > 0) {
                    H[Pair(i, j)] = gap1
                    traceback[Pair(i, j)] = Pair(i-1, j)
                } else if (gap2 > 0) {
                    H[Pair(i, j)] = gap2
                    traceback[Pair(i, j)] = Pair(i, j-1)
                } else {
                    H[Pair(i, j)] = 0.0
                }
            }
        }
        var bestStart = Pair(0, 0)
        var bestValue = 0.0
        for (i in 0 .. s1.length) {
            for (j in 0 .. s2.length) {
                if (H[Pair(i, j)]!! > bestValue) {
                    bestValue = H[Pair(i,j)]!!
                    bestStart = Pair(i, j)
                }
            }
        }
        // execute traceback
        val s1Alignment: MutableList<Char> = mutableListOf()
        val s2Alignment: MutableList<Char> = mutableListOf()

        while (true) {
            traceback[bestStart]?.also {
                if (it.first < bestStart.first && it.second < bestStart.second) {
                    // we have a correspondence
                    s1Alignment.add(s1[bestStart.first-1])
                    s2Alignment.add(s2[bestStart.second-1])
                } else if (it.first < bestStart.first) {
                    // we have a gap on the second string
                    s1Alignment.add(s1[bestStart.first-1])
                    s2Alignment.add('-')
                } else {
                    // we have a gap on the first string
                    s1Alignment.add('-')
                    s2Alignment.add(s2[bestStart.second-1])
                }
                bestStart = it
            } ?: run {
                return formatAlignment(s1Alignment, s2Alignment)
            }
        }
    }

    fun formatAlignment(s1Alignment: MutableList<Char>,
                        s2Alignment: MutableList<Char>): String {
        val alignment = "${s1Alignment.reversed().joinToString("")}\n${s2Alignment.reversed().joinToString("")}"
        print(alignment)
        return alignment
    }
}