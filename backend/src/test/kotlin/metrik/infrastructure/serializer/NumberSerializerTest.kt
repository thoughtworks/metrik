package metrik.infrastructure.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.math.BigInteger

@ExtendWith(MockitoExtension::class)
internal class NumberSerializerTest {

    private lateinit var numberSerializer: NumberSerializer

    @Mock
    private lateinit var jsonGenerator: JsonGenerator

    @Mock
    private lateinit var serializerProvider: SerializerProvider

    @BeforeEach
    fun setUp() {
        numberSerializer = NumberSerializer()
    }

    @Test
    fun should_write_value_when_serialize_big_decimal() {
        numberSerializer.serialize(BigDecimal.ONE, jsonGenerator, serializerProvider)
        verify(jsonGenerator).writeNumber(eq(BigDecimal.ONE))
    }

    @Test
    fun should_write_value_when_serialize_big_integer() {
        numberSerializer.serialize(BigInteger.ONE, jsonGenerator, serializerProvider)
        verify(jsonGenerator).writeNumber(eq(BigInteger.ONE))
    }

    @Test
    fun should_write_value_when_serialize_long() {
        numberSerializer.serialize(Long.MIN_VALUE, jsonGenerator, serializerProvider)
        verify(jsonGenerator).writeNumber(eq(Long.MIN_VALUE))
    }

    @Test
    fun should_write_value_when_serialize_double() {
        numberSerializer.serialize(1.0, jsonGenerator, serializerProvider)
        verify(jsonGenerator).writeNumber(eq("1.00"))
    }

    @Test
    fun should_write_value_when_serialize_float() {
        numberSerializer.serialize(1.0f, jsonGenerator, serializerProvider)
        verify(jsonGenerator).writeNumber(eq("1.00"))
    }

    @Test
    fun should_write_value_when_serialize_double_nan() {
        numberSerializer.serialize(Double.NaN, jsonGenerator, serializerProvider)
        verify(jsonGenerator).writeNumber(eq(Double.NaN))
    }

    @Test
    fun should_write_value_when_serialize_float_nan() {
        numberSerializer.serialize(Float.NaN, jsonGenerator, serializerProvider)
        verify(jsonGenerator).writeNumber(eq(Float.NaN))
    }

    @Test
    fun should_write_value_when_serialize_value_not_int_byte_short() {
        val mockNumber = mock(Number::class.java)
        `when`(mockNumber.toString()).thenReturn("mockNumber")
        numberSerializer.serialize(mockNumber, jsonGenerator, serializerProvider)
        verify(jsonGenerator).writeNumber(eq("mockNumber"))
    }

    @Test
    fun should_write_value_when_serialize_int() {
        numberSerializer.serialize(Int.MIN_VALUE, jsonGenerator, serializerProvider)
        verify(jsonGenerator).writeNumber(eq(Int.MIN_VALUE))
    }
}
