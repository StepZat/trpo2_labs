import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class HashTableTest {
    private lateinit var hashTable: HashTable<Int, Vector>

    @BeforeEach
    fun setUp() {
        hashTable = HashTable()
    }

    @Test
    fun testPutAndGet() {
        println("TEST1: Проверка добавления элемента в хэш-таблицу")
        val vector = PolarVector(5.0, 45.0)
        val vector2 = PolarVector(6.0, 45.0)
        hashTable.put(1, vector)
        val retrieved = hashTable.get(2)
        println("Expected: $vector, but got: $retrieved")
        assertNotNull(retrieved)
        assertEquals(vector, retrieved)
        println()
    }

    @Test
    fun testRemove() {
        println("TEST2: Проверка удаления элемента из хэш-таблицы")
        val vector = CartesianVector(3.0, 4.0)
        hashTable.put(2, vector)
        val removed = hashTable.remove(2)
        println("Expected: $vector, but got: $removed")
        assertNotNull(removed)
        assertEquals(vector, removed)
        val retrieved = hashTable.get(2)
        println("Expected: null, but got: $retrieved")
        assertNull(retrieved)
        println()
    }

    @Test
    fun testClear() {
        println("TEST3: Проверка очистки хэш-таблицы")
        hashTable.put(1, PolarVector(5.0, 45.0))
        hashTable.put(2, CartesianVector(3.0, 4.0))
        hashTable.clear()
        hashTable.put(1, PolarVector(5.0, 45.0))
        val retrieved1 = hashTable.get(1)
        val retrieved2 = hashTable.get(2)
        println("Expected: null for key 1, but got: $retrieved1")
        println("Expected: null for key 2, but got: $retrieved2")
        assertNull(retrieved1)
        assertNull(retrieved2)
        println()
    }

    @Test
    fun testSameValues() {
        println("TEST4: Проверка добавления одинаковых элементов в хэш-таблицу")
        val vector = PolarVector(5.0, 45.0)
        hashTable.put(1, vector)
        hashTable.put(2, vector)
        val retrieved1 = hashTable.get(1)
        val retrieved2 = hashTable.get(2)
        println("Expected: $vector for key 1, but got: $retrieved1")
        println("Expected: $vector for key 2, but got: $retrieved2")
        assertEquals(vector, retrieved1)
        assertEquals(vector, retrieved2)
        println()
    }

    @Test
    fun testUnorderedValues() {
        println("TEST5: Проверка добавления неотсортированных элементов в хэш-таблицу")
        hashTable.put(3, PolarVector(3.0, 30.0))
        hashTable.put(1, PolarVector(1.0, 10.0))
        hashTable.put(2, PolarVector(2.0, 20.0))
        val retrieved1 = hashTable.get(1)
        val retrieved2 = hashTable.get(2)
        val retrieved3 = hashTable.get(3)
        println("Expected: PolarVector(3.0, 30.0) for key 3, but got: $retrieved3")
        println("Expected: PolarVector(1.0, 10.0) for key 1, but got: $retrieved1")
        println("Expected: PolarVector(2.0, 20.0) for key 2, but got: $retrieved2")
        assertEquals(PolarVector(3.0, 30.0), retrieved3)
        assertEquals(PolarVector(1.0, 10.0), retrieved1)
        assertEquals(PolarVector(2.0, 20.0), retrieved2)
        println()
    }

    @Test
    fun testExtremeValues() {
        println("TEST6: Проверка добавления пограничных значений в хэш-таблицу")
        hashTable.put(1, PolarVector(Double.MIN_VALUE, 45.0))
        hashTable.put(2, PolarVector(Double.MIN_VALUE, 45.0))
        val retrieved1 = hashTable.get(1)
        val retrieved2 = hashTable.get(2)
        println("Expected: PolarVector(Double.MAX_VALUE, 45.0) for key 1, but got: $retrieved1")
        println("Expected: PolarVector(Double.MIN_VALUE, 45.0) for key 2, but got: $retrieved2")
        assertEquals(PolarVector(Double.MAX_VALUE, 45.0), retrieved1)
        assertEquals(PolarVector(Double.MIN_VALUE, 45.0), retrieved2)
        println()
    }

    @Test
    fun testRehashing() {
        println("TEST7: Проверка перехэширования хэш-таблицы")
        for (i in 1..1500) {
            hashTable.put(i, PolarVector(i.toDouble(), i.toDouble()))
        }
        val size = hashTable.size
        println("Expected size: 1000, but got: $size")
        assertEquals(1000, size)
        println()
    }

    @Test
    fun testCollisionResolution() {
        println("TEST8: Проверка разрешения коллизий хэш-таблицы")
        hashTable.put(1, PolarVector(1.0, 1.0))
        hashTable.put(1, PolarVector(2.0, 2.0))
        val retrieved = hashTable.get(1)
        println("Expected: PolarVector(2.0, 2.0), but got: $retrieved")
        assertEquals(PolarVector(2.0, 2.0), retrieved)
        println()
    }

}
