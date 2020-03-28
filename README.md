# Retrofit JsonApi Converter: Android Library for Retrofit

[ ![Download](https://api.bintray.com/packages/devjorgecastro/RetrofitJsonApiConverter/tech.jorgecastro.retrofit-jsonapi-converter/images/download.svg?version=1.0.0-alpha1) ](https://bintray.com/devjorgecastro/RetrofitJsonApiConverter/tech.jorgecastro.retrofit-jsonapi-converter/1.0.0-alpha1/link)

Written purely in kotlin :heart_eyes::heart:

retrofit-jsonapi-converter is a Kotlin library for Android that adapts with retrofit to be able to map http responses with jsonapi.  
[see jsonapi specification](https://jsonapi.org/)

__Support__
+ RxJava Observables
  - Single
+ Coroutine Flow

# Introduction
In recent years, the Rest architecture has been widely adopted for the exchange of information between web services and clients (client-server). JsonApi is a standard that works over HTTP; it was created to help define a more consistent response standard within the development team to increase productivity and efficiency; Thus reducing the number of requests and the size of the data packets transported between the client and the server.

# Differences between Restful and JsonApi
Let's look at the difference between a list of articles with their respective authors

__Restful__
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

__JsonApi__
```json
{
  "data": [{
    "type": "articles",
    "id": "1",
    "attributes": {
      "title": "JSON:API paints my bikeshed!",
      "body": "The shortest article. Ever.",
      "created": "2015-05-22T14:56:29.000Z",
      "updated": "2015-05-22T14:56:28.000Z"
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
add the following line when creating the retrofit instance:
**addConverterFactory(JsonApiConverterFactory())**
```
Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(httpClient)
    .addConverterFactory(JsonApiConverterFactory())
    .build()
```

# Basic example
:warning:Note: add the `@JsonApiMethod` annotation to each method that responds with jsonapi format
```kotlin
interface TestApi {

    @JsonApiMethod
    @GET("PATH_URL")
    fun getMyDataList(): Single<List<MyDto>>
}
```

```kotlin
dataApi.getMyDataList()
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe({
        // List<MyDto>
    }, {
        // Code for Error
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
