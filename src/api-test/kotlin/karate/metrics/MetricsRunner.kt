package karate.metrics

import com.intuit.karate.junit5.Karate

class MetricsRunner {
    @Karate.Test
    fun testAll(): Karate? {
        return Karate.run().tags("~@MLT").relativeTo(javaClass)
    }
}