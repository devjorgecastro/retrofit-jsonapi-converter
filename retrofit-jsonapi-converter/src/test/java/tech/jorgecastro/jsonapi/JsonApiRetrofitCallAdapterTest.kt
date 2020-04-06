package tech.jorgecastro.jsonapi

import okhttp3.MediaType
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Response
import tech.jorgecastro.jsonapi.adapter.JsonApiRetrofitCallAdapter

@RunWith(MockitoJUnitRunner::class)
class JsonApiRetrofitCallAdapterTest {

    @Mock
    private lateinit var call: Call<*>

    private lateinit var jsonApiRetrofitCallAdapter: JsonApiRetrofitCallAdapter<*>

    @Before
    fun setup() {
        jsonApiRetrofitCallAdapter = JsonApiRetrofitCallAdapter(call)
    }

    /**
     * isExecute Method
     */
    @Test
    fun `isExecuted success`() {
        `when`(call.isExecuted).thenReturn(true)
        val response = jsonApiRetrofitCallAdapter.isExecuted
        assert(response)

        verify(call).isExecuted
        verifyNoMoreInteractions(call)
    }

    @Test
    fun `isExecuted fail`() {
        `when`(call.isExecuted).thenReturn(false)
        val response = jsonApiRetrofitCallAdapter.isExecuted
        assert(!response)

        verify(call).isExecuted
        verifyNoMoreInteractions(call)
    }

    /**
     * clone method
     */
    @Test
    fun `clone`() {
        val callReturnMock = mock(Call::class.java)
        `when`(call.clone()).thenReturn(callReturnMock)

        val response = call.clone()
        assert(call::class.java == response::class.java)

        verify(call).clone()
        verifyNoMoreInteractions(call, callReturnMock)
    }

    /**
     * isCanceled method
     */
    @Test
    fun `isCanceled success`() {
        `when`(call.isCanceled).thenReturn(true)

        val response = call.isCanceled

        assert(response)

        verify(call).isCanceled
        verifyNoMoreInteractions(call)
    }

    @Test
    fun `isCanceled fail`() {
        `when`(call.isCanceled).thenReturn(false)

        val response = call.isCanceled

        assert(!response)

        verify(call).isCanceled
        verifyNoMoreInteractions(call)
    }

    /**
     * cancel method
     */
    @Test
    fun `cancel method`() {
        doNothing().`when`(call).cancel()

        call.cancel()
        verify(call).cancel()

        verifyNoMoreInteractions(call)
    }

    /**
     * execute method
     */
    @Test
    fun `execute method success`() {

        val retrofitResponse = Response.success(200, "")
        `when`(call.execute()).thenReturn(retrofitResponse)

        val response = call.execute()

        assert(response.code() == 200)

        verify(call).execute()
        verifyNoMoreInteractions(call)
    }

    @Test
    fun `execute server error`() {

        val mediaTypeJson = MediaType.get("application/json; charset=utf-8")
        val retrofitResponse = Response.error<Throwable>(500, ResponseBody.create(mediaTypeJson, "Error"))
        `when`(call.execute()).thenReturn(retrofitResponse)

        val response = call.execute()

        assert(response.code() == 500)

        verify(call).execute()
        verifyNoMoreInteractions(call)
    }

    /**
     * request method
     */
    @Test
    fun `request method success`() {

        val requestReturn = Request
            .Builder()
            .url("https://github.com/devjorgecastro/retrofit-jsonapi-converter")
            .addHeader("test", "test")
            .build()

        `when`(call.request()).thenReturn(requestReturn)

        val request = call.request()

        assertEquals(request.header("test"), requestReturn.header("test"))

        verify(call).request()
        verifyNoMoreInteractions(call)
    }

    /**
     * timeout method
     */
    @Test
    fun `timeout method`() {
        val timeoutReturn = mock(Timeout::class.java)
        `when`(call.timeout()).thenReturn(timeoutReturn)

        val timeout = call.timeout()
        assert(timeout is Timeout)

        verify(call).timeout()
        verifyNoMoreInteractions(call)
    }
}