package tech.jorgecastro.jsonapi

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
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
    private lateinit var call: Call<Any>

    @Mock
    private lateinit var callReturnMock: Call<Any>


    private lateinit var jsonApiRetrofitCallAdapter: JsonApiRetrofitCallAdapter<Any>

    @Before
    fun setup() {
        jsonApiRetrofitCallAdapter = JsonApiRetrofitCallAdapter<Any>(call)
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
        `when`(call.clone()).thenReturn(callReturnMock)

        val response = jsonApiRetrofitCallAdapter.clone()
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

        val response = jsonApiRetrofitCallAdapter.isCanceled

        assert(response)

        verify(call).isCanceled
        verifyNoMoreInteractions(call)
    }

    @Test
    fun `isCanceled fail`() {
        `when`(call.isCanceled).thenReturn(false)

        val response = jsonApiRetrofitCallAdapter.isCanceled

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

        jsonApiRetrofitCallAdapter.cancel()
        verify(call).cancel()

        verifyNoMoreInteractions(call)
    }

    /**
     * execute method
     */
    @Test
    fun `execute method success`() {
        val mediaTypeJson = "application/json; charset=utf-8".toMediaType()
        val retrofitResponse = Response.success(200, "")
        `when`(call.execute()).thenReturn(retrofitResponse as Response<Any>)

        val response = jsonApiRetrofitCallAdapter.execute()

        assert(response.code() == 200)

        verify(call).execute()
        verifyNoMoreInteractions(call)
    }

    @Test
    fun `execute server error`() {

        val mediaTypeJson = "application/json; charset=utf-8".toMediaType()
        val retrofitResponse = Response.error<Any>(500, "Error".toResponseBody(mediaTypeJson))
        `when`(call.execute()).thenReturn(retrofitResponse)

        val response = jsonApiRetrofitCallAdapter.execute()

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

        val request = jsonApiRetrofitCallAdapter.request()

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

        val timeout = jsonApiRetrofitCallAdapter.timeout()
        assert(timeout is Timeout)

        verify(call).timeout()
        verifyNoMoreInteractions(call)
    }
}