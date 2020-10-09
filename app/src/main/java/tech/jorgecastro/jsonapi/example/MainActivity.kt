package tech.jorgecastro.jsonapi.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import tech.jorgecastro.jsonapi.converter.JsonApiConverterFactory
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import tech.jorgecastro.jsonapi.adapter.JsonApiCallAdapterFactory
import tech.jorgecastro.jsonapi.exception.JsonApiResponseException
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch {
            //getErrorDataWithRxJava()
            //getErrorDataWithFlow()
            //testOrderApiSingleRxJava()
            //testGetDataWithFlow()
            //testGetOrderDetailWithFlow()


            //testGetArticlesWithRxJava()

            //testGetArticlesWithCoroutine()



            compositeDisposable.add(
                getRetrofitInstance()
                    .create(TestApi::class.java)
                    .test()
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        val response = it
                    }, {
                        val err = it
                    })
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun testGetArticlesWithRxJava() {
        compositeDisposable.add(
            getRetrofitInstance()
                .create(TestApi::class.java)
                .getArticlesWithMultipleAuthors()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val response = it
                }, {
                    val err = it
                })
        )
    }

    private suspend fun testOneData() {
        try {
            val response = getRetrofitInstance()
                .create(TestApi::class.java)
                .getOneData()
            val cityName = response.cityName
        }
        catch (e: Exception) {
            val error = e
        }
    }

    private suspend fun testOneDataWithFlow() {
        withContext(Dispatchers.IO) {
            getRetrofitInstance()
                .create(TestApi::class.java)
                .getOneDataWithFlow()
                .catch {
                    val error = it
                }
                .collect {
                    val result = it
                }
        }
    }

    private suspend fun testApiData1(){
        try {
            val response = getRetrofitInstance()
                .create(TestApi::class.java)
                .getData1()
            val cityName = response.first().cityName
        }
        catch (e: Exception) {
            val error = e
        }
    }

    private suspend fun testGetDataWithFlow(){
        withContext(Dispatchers.IO) {
            getRetrofitInstance()
                .create(TestApi::class.java)
                .getDataWithFlow()
                .collect {
                    val result = it
                }
        }
    }

    private fun testApiDataRxSingleList() {
        compositeDisposable.add(
            getRetrofitInstance()
                .create(TestApi::class.java)
                .getDataSingleList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val response = it
                }, {
                    val err = it
                })
        )
    }

    private fun testApiDataRxObservableList() {
        compositeDisposable.add(
            getRetrofitInstance()
                .create(TestApi::class.java)
                .getDataSingleList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val response = it
                }, {
                    val err = it
                })
        )
    }

    private fun getArticles() {

        val time = measureTimeMillis {
            compositeDisposable.add(
                getRetrofitInstance()
                    .create(TestApi::class.java)
                    .getArticles()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val response = it
                    }, {
                        val err = it
                    })
            )
        }
        Log.d("Total", "$time")
    }

    private fun getErrorDataWithRxJava() {
        compositeDisposable.add(
            getRetrofitInstance()
                .create(TestApi::class.java)
                .getErrorDataWithRxJava()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val response = it
                }, {
                    if (it is JsonApiResponseException) {
                        val errorData = it.data
                    }
                })
        )
    }

    private suspend fun getErrorDataWithFlow(){
        try {
            val response = getRetrofitInstance()
                .create(TestApi::class.java)
                .getErrorDataWithFlow()
                .collect {
                    val cityName = it.first().cityName
                }
        }
        catch (e: JsonApiResponseException) {
            val errorData = e.data
        }
        catch (e: Exception) {
            val error = e
        }
    }

    private suspend fun testCountryData() {
        withContext(Dispatchers.IO) {
            getRetrofitInstance()
                .create(CountryApi::class.java)
                .getCountryCodes()
                .catch {
                    val error = it
                }
                .collect {
                    val data = it
                }
        }
    }

    private fun testOrderApiSingleRxJava() {
        compositeDisposable.add(
            getRetrofitInstance()
                .create(OrderApi::class.java)
                .getOrderDetailWithRxJava()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val response = it
                }, {
                    val err = it
                })
        )
    }

    private suspend fun testGetOrderDetailWithFlow(){
        getRetrofitInstance()
            .create(OrderApi::class.java)
            .getOrderDetailWithFlow()
            .collect {
                val data = it
            }
    }

    private suspend fun testGetArticlesWithCoroutine(){
        getRetrofitInstance()
            .create(ArticlesApi::class.java)
            .getArticlesWithCoroutine()
            .catch {
                val err = it
            }
            .collect {
                val data = it
            }
    }

    private fun getRetrofitInstance(): Retrofit {

        val baseUrl = "http://www.mocky.io/"

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        val httpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addConverterFactory(JsonApiConverterFactory())
                .addConverterFactory(MoshiConverterFactory.create(moshi))/*Add for catch HttpException*/
                .addCallAdapterFactory(JsonApiCallAdapterFactory.create()) /*Add for catch HttpException*/
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }
}
