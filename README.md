# Retrofit JsonApi Converter: Android Library for Retrofit

[ ![Download](https://api.bintray.com/packages/devjorgecastro/RetrofitJsonApiConverter/tech.jorgecastro.retrofit-jsonapi-converter/images/download.svg?version=1.0.0-alpha1) ](https://bintray.com/devjorgecastro/RetrofitJsonApiConverter/tech.jorgecastro.retrofit-jsonapi-converter/1.0.0-alpha1/link)

retrofit-jsonapi-converter is a Kotlin library for Android that adapts  
with retrofit to be able to map http responses with jsonapi.  
[see jsonapi specification](https://jsonapi.org/)

# Introduction
In recent years, the Rest architecture has been widely adopted for the  
exchange of information between web services and clients  
(client-server). JsonApi is a standard that works over HTTP; it was  
created to help define a more consistent response standard within the  
development team to increase productivity and efficiency; Thus reducing  
the number of requests and the size of the data packets transported  
between the client and the server.

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

# Author
Jorge Castro - [@devjorgecastro on GitHub](https://github.com/devjorgecastro), [@devjcastro on Twitter](https://twitter.com/devjcastro)

# Disclaimer
This is not an official [Square product](https://square.github.io/).
