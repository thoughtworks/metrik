package metrik.infrastructure.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.math.BigInteger

@ExtendWith(MockKExtension::class)
internal class NumberSerializerTest {
    @MockK(relaxed = true)
    private lateinit var jsonGenerator: JsonGenerator

    @MockK(relaxed = true)
    private lateinit var serializerProvider: SerializerProvider

    private lateinit var numberSerializer: NumberSerializer

    @BeforeEach
    fun setUp() {
        numberSerializer = NumberSerializer()
    }

    @Test
    fun should_write_value_when_serialize_big_decimal() {
        numberSerializer.serialize(BigDecimal.ONE, jsonGenerator, serializerProvider)
        verify { jsonGenerator.writeNumber(BigDecimal.ONE) }
    }

    @Test
    fun should_write_value_when_serialize_big_integer() {
        numberSerializer.serialize(BigInteger.ONE, jsonGenerator, serializerProvider)
        verify { jsonGenerator.writeNumber(BigInteger.ONE) }
    }

    @Test
    fun should_write_value_when_serialize_long() {
        numberSerializer.serialize(Long.MIN_VALUE, jsonGenerator, serializerProvider)
        verify { jsonGenerator.writeNumber(Long.MIN_VALUE) }
    }

    @Test
    fun should_write_value_when_serialize_double() {
        numberSerializer.serialize(1.0, jsonGenerator, serializerProvider)
        verify { jsonGenerator.writeNumber("1.00") }
    }

    @Test
    fun should_write_value_when_serialize_float() {
        numberSerializer.serialize(1.0f, jsonGenerator, serializerProvider)
        verify { jsonGenerator.writeNumber("1.00") }
    }

    @Test
    fun should_write_value_when_serialize_double_nan() {
        numberSerializer.serialize(Double.NaN, jsonGenerator, serializerProvider)
        verify { jsonGenerator.writeNumber(Double.NaN) }
    }

    @Test
    fun should_write_value_when_serialize_float_nan() {
        numberSerializer.serialize(Float.NaN, jsonGenerator, serializerProvider)
        verify { jsonGenerator.writeNumber(Float.NaN) }
    }

    @Test
    fun should_write_value_when_serialize_value_not_int_byte_short() {
        val mockNumber = mockk<Number>()
        every { mockNumber.toString() } returns "mockNumber"
        numberSerializer.serialize(mockNumber, jsonGenerator, serializerProvider)
        verify { jsonGenerator.writeNumber("mockNumber") }
    }

    @Test
    fun should_write_value_when_serialize_int() {
        numberSerializer.serialize(Int.MIN_VALUE, jsonGenerator, serializerProvider)
        verify { jsonGenerator.writeNumber(Int.MIN_VALUE) }
    }
}