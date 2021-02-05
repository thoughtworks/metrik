package karate

import com.intuit.karate.junit5.Karate

class KarateTests {
    @Karate.Test
    fun testAll(): Karate? {
        return Karate.run().relativeTo(javaClass)
    }
}