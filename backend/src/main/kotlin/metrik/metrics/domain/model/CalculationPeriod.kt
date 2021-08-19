package metrik.metrics.domain.model

enum class CalculationPeriod(val timeInDays: Long) {
    Fortnightly(14L), Monthly(30L)
}
