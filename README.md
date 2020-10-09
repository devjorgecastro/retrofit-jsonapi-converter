# Retrofit JsonApi Converter: Android Library for Retrofit

[website](https://devjorgecastro.github.io/retrofit-jsonapi-converter/)

[ ![Download](https://api.bintray.com/packages/devjorgecastro/RetrofitJsonApiConverter/tech.jorgecastro.retrofit-jsonapi-converter/images/download.svg?version=1.0.0-beta2) ](https://bintray.com/devjorgecastro/RetrofitJsonApiConverter/tech.jorgecastro.retrofit-jsonapi-converter/1.0.0-beta2/link)

Written purely in kotlin :heart_eyes::heart:

retrofit-jsonapi-converter is a Kotlin library for Android that adapts with retrofit and moshi to be able to map http responses with jsonapi.  
[see jsonapi specification](https://jsonapi.org/)

__Support__
+ RxJava Observables
  - Observable
  - Single
+ Coroutine Flow

# Introduction
In recent years, the Rest architecture has been widely adopted for the exchange of information between web services and clients (client-server). JsonApi is a standard that works over HTTP; it was created to help define a more consistent response standard within the development team to increase productivity and efficiency; Thus reducing the number of requests and the size of the data packets transported between the client and the server.

# Differences between Restful and JsonApi
Let's look at the difference between a list of articles with their respective authors

__RESTful__
```js
[
  {
    "id": "1",
    "title": "JSON:API paints my bikeshed!",
    "body": "The shortest article. Ever.",
    "created": "2015-05-22T14:56:29.000Z",
    "updated": "2015-05-22T14:56:28.000Z",
    "author": {
      "id": "42",
      "name": "John",
      "age": 80,
      "gender": "male"
    }
  }
]
```

__JsonApi__ ([see Json Api specification](https://jsonapi.org/))
```json
{
  "data": [{
    "type": "articles",
    "id": "1",
    "attributes": {
      "title": "JSON:API paints my bikeshed!",
      "body": "The shortest article. Ever.",
      "created_date": "2015-05-22T14:56:29.000Z",
      "updated_date": "2015-05-22T14:56:28.000Z"
    },
    "relationships": {
      "author": {
        "data": {"id": "42", "type": "people"}
      }
    }
  }],
  "included": [
    {
      "type": "people",
      "id": "42",
      "attributes": {
        "name": "John",
        "age": 80,
        "gender": "male"
      }
    }
  ]
}
```

:relaxed:**retrofit-jsonapi-converter** helps you to work with jsonapi responses in a simpler way, as you were used to with restful.

# Getting started
### Setting up the dependency
Add retrofit-jsonapi-converter as Gradle build dependency.
```gradle
repositories {
  mavenCentral()
  google()
}

dependencies {
  implementation 'tech.jorgecastro:retrofit-jsonapi-converter:LAST_VERSION'
}
```

# Basic setup
add the following lines when creating the retrofit instance:
* **addConverterFactory(JsonApiConverterFactory())**
* **addCallAdapterFactory(JsonApiCallAdapterFactory.create())**
```kotlin
Retrofit.Builder()
  .baseUrl(baseUrl)
  .client(httpClient)
  .addConverterFactory(JsonApiConverterFactory())
  .addCallAdapterFactory(JsonApiCallAdapterFactory.create())
  .build()
```

# Retrofit setup with Moshi and Rxjava
Adding **MoshiConverterFactory** and **RxJava2CallAdapterFactory**. </br>
:warning:Note: In the case of RxJava, it is not necessary to add **RxJava2CallAdapterFactory** when the responses are of the JsonApi type (annotated with **@JsonApiMethod**). Retrofit JsonApi Converter allows working with Observable and Single responses.
```kotlin
val moshi = 
  Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
            
Retrofit.Builder()
  .baseUrl(baseUrl)
  .client(httpClient)
  .addConverterFactory(JsonApiConverterFactory())
  .addConverterFactory(MoshiConverterFactory.create(moshi))
  .addCallAdapterFactory(JsonApiCallAdapterFactory.create())
  .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
  .build()
```

# Usage example
:warning:Note: add the `@JsonApiMethod` annotation to each method that responds with jsonapi format
```kotlin
interface TestApi {

    @JsonApiMethod
    @GET("PATH_URL")
    fun getArticles(): Single<List<Article>>
    
    @JsonApiMethod
    @GET("PATH_URL/{id}")
    fun getArticle(@Path("id") id: Int): Single<Article>
}
```


### Setting the response object
```kotlin

@JsonApiResource(type = "people")
data class People(
    @field:Json(name = "id") var id: String = "", // required
    var name: String = "",
    var age: String = "",
    var gender: String = ""
)

@JsonApiResource(type = "article")
data class Article(
    @field:Json(name = "id") val id: String = "", // required
    val title: String = "",
    val body: String = "",
    @Json(name = "created_date") val createdDate: String = "",
    @Json(name = "updated_date") val updatedDate: String = "",
    @JsonApiRelationship(jsonApiResourceName = "people", jsonAttrName = "author")
    var authors: List<People>? = listOf() // name is JsonApiResource of people
)
```
### Make the request
```kotlin
val dataApi = getRetrofitApi()
dataApi.getArticles()
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe({
        // List<Article>
    }, {
        // Code for Error
    })
    
dataApi.getArticle(1)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe({
        // Article
    }, {
        // Code for Error
    })
```

# Error Objects
When you work with JsonApi you can find multiple problems, these are represented in an array of errors. JsonApi returns a JsonApiException when http **4xx** codes are processed
### Example
```json
{
  "jsonapi": { "version": "1.0" },
  "errors": [
    {
      "code":   "400",
      "source": { "pointer": "/data/attributes/firstName" },
      "title":  "Value is too short",
      "detail": "First name must contain at least three characters."
    },
    {
      "code":   "400",
      "source": { "pointer": "/data/attributes/password" },
      "title": "Passwords must contain a letter, number, and punctuation character.",
      "detail": "The password provided is missing a punctuation character."
    },
    {
      "code":   "400",
      "source": { "pointer": "/data/attributes/password" },
      "title": "Password and password confirmation do not match."
    }
  ]
}
```
#### Kotlin
```kotlin
val dataApi = getRetrofitApi()
dataApi.getDataWithObservableError()
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe({
        // Success
    }, {
        if (it is JsonApiResponseException) { // Exception type for JsonApi errors
            val errorData = it.data // List of errors with data attribute.
        }
    })
```

# Author
Jorge Castro - [@devjorgecastro on GitHub](https://github.com/devjorgecastro), [@devjcastro on Twitter](https://twitter.com/devjcastro)

# Disclaimer
This is not an official [Square product](https://square.github.io/).

## LICENSE

    Copyright (c) 2020, Jorge Castro.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
