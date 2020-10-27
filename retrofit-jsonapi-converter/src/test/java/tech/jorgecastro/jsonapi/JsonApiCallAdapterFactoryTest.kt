package tech.jorgecastro.jsonapi

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.reactivex.Observable
import io.reactivex.Single
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import tech.jorgecastro.jsonapi.adapter.JsonApiCallAdapterFactory
import tech.jorgecastro.jsonapi.adapter.JsonApiFlowCallAdapter
import tech.jorgecastro.jsonapi.adapter.JsonApiRxJava2CallAdapter
import tech.jorgecastro.jsonapi.examples.api.TestFlowApi
import tech.jorgecastro.jsonapi.examples.api.TestObservableApi
import tech.jorgecastro.jsonapi.examples.api.TestSingleApi
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class JsonApiCallAdapterFactoryTest {

    @MockK
    private lateinit var retrofit: Retrofit

    @Before
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    fun `test when JsonApiCallAdapterFactory array annotation is empty`() {

        //Given
        val returnType = mockk<ParameterizedType>()
        val annotations = emptyArray<Annotation>()

        val jsonApiCallAdapterFactory = JsonApiCallAdapterFactory.create()

        every { returnType.actualTypeArguments } returns arrayOf()


        //When
        val response = jsonApiCallAdapterFactory.get(returnType, annotations, retrofit)


        //Then
        assertNull(response)
        verify { returnType wasNot called }
    }

    @Test
    fun `test JsonApiCallAdapterFactory when ParametizedType return null`() {
        //Given

        /**
         * you could also call the [getReturnTypeFromRxObservable] function to get a returnType
         */
        val returnType = String::class.java.genericSuperclass
        val jsonApiMethod = object : TestSingleApi {
            @JsonApiMethod
            override fun test() = Single.just(listOf(""))
        }
        val annotations = jsonApiMethod.javaClass.declaredMethods.first().annotations
        val retrofit = mockk<Retrofit>()

        val jsonApiCallAdapterFactory = JsonApiCallAdapterFactory.create()


        //When
        val response = jsonApiCallAdapterFactory.get(returnType!!, annotations, retrofit)


        //Then
        assertNull(response)
    }

    @Test
    fun `test JsonApiCallAdapterFactory when returnType is JsonApiRxJava2CallAdapter by Single`() {
        //Given
        /**
         * you could also call the [getReturnTypeFromRxObservable] function to get a returnType
         */
        val returnType = mockk<ParameterizedType>()


        val jsonApiMethod = object : TestSingleApi {
            @JsonApiMethod
            override fun test() = Single.just(listOf(""))
        }
        val annotations = jsonApiMethod.javaClass.declaredMethods.first().annotations

        val jsonApiCallAdapterFactory = JsonApiCallAdapterFactory.create()

        every { returnType.actualTypeArguments } returns arrayOf(Single::class.java.genericSuperclass)
        every { returnType.rawType } returns Single::class.java


        //When
        val response = jsonApiCallAdapterFactory.get(returnType!!, annotations, retrofit)


        //Then
        assert(response is JsonApiRxJava2CallAdapter)
        verifyOrder {
            returnType.actualTypeArguments
            returnType.rawType
        }
    }

    @Test
    fun `test JsonApiCallAdapterFactory when returnType is JsonApiRxJava2CallAdapter by Observable`() {
        //Given
        /**
         * you could also call the [getReturnTypeFromRxObservable] function to get a returnType
         */
        val returnType = mockk<ParameterizedType>()


        val jsonApiMethod = object : TestObservableApi {
            @JsonApiMethod
            override fun test() = Observable.just(listOf(""))
        }
        val annotations = jsonApiMethod.javaClass.declaredMethods.first().annotations

        val jsonApiCallAdapterFactory = JsonApiCallAdapterFactory.create()
        every { returnType.actualTypeArguments } returns arrayOf(Single::class.java.genericSuperclass)
        every { returnType.rawType } returns Observable::class.java


        //When
        val response = jsonApiCallAdapterFactory.get(returnType!!, annotations, retrofit)


        //Then
        assert(response is JsonApiRxJava2CallAdapter)
        verifyOrder {
            returnType.actualTypeArguments
            returnType.rawType
        }
    }

    @Test
    fun `test JsonApiCallAdapterFactory when returnType is JsonApiFlowCallAdapter`() {
        //Given
        /**
         * you could also call the [getReturnTypeFromFlow] function to get a returnType
         */
        val returnType = mockk<ParameterizedType>()


        val jsonApiMethod = object : TestFlowApi {
            @JsonApiMethod
            override fun test() = flowOf(listOf(""))
        }
        val annotations = jsonApiMethod.javaClass.declaredMethods.first().annotations

        val jsonApiCallAdapterFactory = JsonApiCallAdapterFactory.create()
        every { returnType.actualTypeArguments } returns arrayOf(Single::class.java.genericSuperclass)
        every { returnType.rawType } returns Flow::class.java


        //When
        val response = jsonApiCallAdapterFactory.get(returnType!!, annotations, retrofit)


        //Then
        assert(response is JsonApiFlowCallAdapter)
        verifyOrder {
            returnType.actualTypeArguments
            returnType.rawType
        }
    }

    private fun getReturnTypeFromRxObservable(): Type? {
        val dataResponse = Single.just(listOf<String>())
        return dataResponse::class.java.genericSuperclass
    }

    private fun getReturnTypeFromFlow(): Type? {
        val dataResponse = flowOf(listOf(""))
        return dataResponse::class.java.genericInterfaces.first()
    }
}