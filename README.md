# ktor-client-karoo

This Android library provides a Ktor HTTP client engine that integrates with the Karoo System Service, introduced by Karoo Extensions. 
It enables seamless HTTP requests within applications using the Karoo APIs.

## Features
- Provides a Ktor `HttpClientEngine` that works with Karoo System Service.
- Supports custom timeouts for HTTP requests.
- Works even if the Karoo itself has no wifi connection. (Karoo Companion required)

## Limitations
- Only works with the new Hammerhead Karoo. Karoo 2 is not supported.
- Http Requests will always be sent through the Karoo System Service even though a network connection
on the device it self might be available.
- Websockets and SSE are not supported.
- Requests and Responses are limited to 100KB in size

## Installation
To include this library in your project, add the following dependency to your `build.gradle`:

```gradle
dependencies {
    implementation "de.jonasfranz.ktor-client-karoo:1.0.0"
}
```

The library is released on the GitHub package repository. In order to add the repository you need to add this to your gradle buildscript:
```kotlin
dependencyResolutionManagement {
    // ...
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/jonasfranz/ktor-client-karoo")
            credentials {
                username = providers.gradleProperty("gpruser").getOrElse(System.getenv("USERNAME"))
                password = providers.gradleProperty("gprkey").getOrElse(System.getenv("TOKEN"))
            }
        }
    }
}
```

In order to use the repository authentication is required as described [here](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package).

## Usage
### Using the HTTP Client
Below is an example of how to use the Karoo Ktor Client:

```kotlin
val karooSystem: KarooSystemService = TODO()
val client = HttpClient(Karoo(karooSystem))

fun sendRequest() = runBlocking {
    val response = client.get("https://api.sampleapis.com/wines/sparkling/${Random.nextInt(10)}")
    print(response)
}
```
Please also  have a look on the [complete example](app/src/main/java/de/jonasfranz/ktorclientkarooexample/MainViewModel.kt) in the `app` directory.

## License
This project is licensed under the Apache 2 License.

