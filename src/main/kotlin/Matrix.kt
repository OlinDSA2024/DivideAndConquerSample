import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.measureTime

class Matrix(size: Int) {
    private val n = size
    private val data: Array<DoubleArray> = Array(n) { DoubleArray(n) }

    operator fun set(i: Int, j: Int, value: Double) {
        data[i][j] = value
    }

    operator fun get(i: Int, j: Int): Double {
        return data[i][j]
    }

    fun printMatrix() {
        println("[")
        data.forEach {
            print("  [")
            it.indices.forEach { ind ->
                print("${it[ind]}")
                if (ind != it.indices.last) {
                    print(", ")
                }
            }
            println("]")
        }
        println("]")
    }

    fun multiply(other: Matrix):Matrix? {
        if (other.n != n) {
            return null
        }
        val result = Matrix(n)
        for (i in 0 until n) {
            for (j in 0 until n) {
                for (k in 0 until n) {
                    result[i, j] += this[i,k] * other[k,j]
                }
            }
        }
        return result
    }

    fun add(other: Matrix):Matrix? {
        if (other.n != n) {
            return null
        }
        val result = Matrix(n)
        for (i in 0 until n) {
            for (j in 0 until n) {
                result[i, j] = this[i,j] + other[i,j]
            }
        }
        return result
    }

    fun subtract(other: Matrix):Matrix? {
        if (other.n != n) {
            return null
        }
        val result = Matrix(n)
        for (i in 0 until n) {
            for (j in 0 until n) {
                result[i, j] = this[i,j] - other[i,j]
            }
        }
        return result
    }

    fun strassenMultiply(other: Matrix):Matrix? {
        if (n == 1) {
            val result = Matrix(1)
            result[0, 0] = this[0, 0] * other[0, 0]
            return result
        }
        val (A11, A12, A21, A22) = Matrix.splitIntoBlocks(this)
        val (B11, B12, B21, B22) = Matrix.splitIntoBlocks(other)

        val M1 = (A11 + A22)*(B11 + B22)
        val M2 = (A21 + A22)*B11
        val M3 = A11*(B12 - B22)
        val M4 = A22*(B21 - B11)
        val M5 = (A11 + A12)*B22
        val M6 = (A21 - A11)*(B11 + B12)
        val M7 = (A12 - A22)*(B21 + B22)

        return Matrix.assembleFromBlocks(M1 + M4 - M5 + M7, M3 + M5, M2 + M4, M1 - M2 + M3 + M6)
    }

    companion object {
        data class BlockMatrix(val m11: Matrix, val m12: Matrix, val m21: Matrix, val m22: Matrix)
        fun assembleFromBlocks(
            m11: Matrix,
            m12: Matrix,
            m21: Matrix,
            m22: Matrix
        ): Matrix? {
            if (m11.n != m12.n || m11.n != m21.n || m11.n != m22.n) {
                return null
            }
            val result = Matrix(m11.n * 2)
            for (i in 0 until m11.n) {
                for (j in 0 until m11.n) {
                    result[i, j] = m11[i, j]
                    result[i + m11.n, j] = m21[i, j]
                    result[i, j + m11.n] = m12[i, j]
                    result[i + m11.n, j + m11.n] = m22[i, j]
                }
            }
            return result
        }

        fun splitIntoBlocks(M: Matrix):BlockMatrix {
            if (M.n % 2 != 0) {
                throw Exception("matrix size is not even")
            }
            val m11 = Matrix(M.n/2)
            val m12 = Matrix(M.n/2)
            val m21 = Matrix(M.n/2)
            val m22 = Matrix(M.n/2)

            for (i in 0 until M.n/2) {
                for (j in 0 until M.n/2) {
                    m11[i, j] = M[i, j]
                    m12[i, j] = M[i, M.n/2 + j]
                    m21[i, j] = M[M.n/2 + i, j]
                    m22[i, j] = M[M.n/2 + i, M.n/2 + j]
                }
            }

            return BlockMatrix(m11, m12, m21, m22)
        }
    }

    operator fun times(other: Matrix): Matrix {
        return strassenMultiply(other)!!
    }

    operator fun minus(other: Matrix): Matrix {
        return subtract(other)!!
    }

    operator fun plus(other: Matrix): Matrix {
        return add(other)!!
    }
}

fun main() {
    for (i in 10 until 15) {
        val base = 2.0
        val n = base.pow(i).toInt()
        val m = Matrix(n)
        for (j in 0 until n) {
            for (k in 0 until n) {
                m[j, k] = Random.nextDouble()
            }
        }
        val timeTaken = measureTime {
            val result = m.multiply(m)
        }
        val timeTakenStrassen = measureTime {
            val result = m * m
        }
        println("$n $timeTaken $timeTakenStrassen")
    }
}