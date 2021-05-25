package metrik.infrastructure.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.math.BigDecimal
import java.math.BigInteger

class NumberSerializer : JsonSerializer<Number>() {
    override fun serialize(value: Number?, g: JsonGenerator?, serializers: SerializerProvider?) {
        when {
            value is BigDecimal -> g!!.writeNumber(value)
            value is BigInteger -> g!!.writeNumber(value)
            value is Long -> g!!.writeNumber(value)
            value is Double -> if (value.isNaN()) g!!.writeNumber(value) else g!!.writeNumber("%.2f".format(value))
            value is Float -> if (value.isNaN()) g!!.writeNumber(value) else g!!.writeNumber("%.2f".format(value))
            value !is Int && value !is Byte && value !is Short -> g!!.writeNumber(value.toString())
            else -> g!!.writeNumber(value.toInt())
        }
    }
}