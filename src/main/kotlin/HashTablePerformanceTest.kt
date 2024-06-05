import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*


class HashTablePerformanceTest {
    private lateinit var hashTable: HashTable<Int, Vector>

    @BeforeEach
    fun setUp() {
        hashTable = HashTable()
    }

    @Test
    fun testRehashingPerformance() {
        val startTime = System.currentTimeMillis()
        for (i in 1..150000) {
            hashTable.put(i, PolarVector(i.toDouble(), i.toDouble()))
                // println("I value - ${i}")
        }
        val endTime = System.currentTimeMillis()
        println("Rehashing performance test took ${endTime - startTime} milliseconds")
    }

}
