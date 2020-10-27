package tech.jorgecastro.jsonapi

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import tech.jorgecastro.jsonapi.adapter.JsonApiCallAdapter
import tech.jorgecastro.jsonapi.adapter.JsonApiRetrofitCallAdapter
import java.lang.reflect.Type

@RunWith(MockitoJUnitRunner::class)
class JsonApiCallAdapterTest {

    private val responseTypeMock = mock(Type::class.java)

    private lateinit var jsonApiCallAdapter: JsonApiCallAdapter<Any>

    @Mock
    private lateinit var retrofitCallMock: Call<Any>

    @Before
    fun setupTest() {
        jsonApiCallAdapter = JsonApiCallAdapter<Any>(responseTypeMock)
    }

    @Test
    fun `call adapt method success`() {
        val responseType = jsonApiCallAdapter.adapt(retrofitCallMock)
        assert(responseType is JsonApiRetrofitCallAdapter<Any>)
    }

    @Test
    fun `call responseType method success`() {
        val callAdapter = JsonApiCallAdapter<Any>(responseTypeMock)
        val responseType = callAdapter.responseType()
        assertEquals(responseType, responseTypeMock)
    }
}
