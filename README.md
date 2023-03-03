
[![Ci](https://github.com/json-mockr/json-mockr/actions/workflows/ci.yml/badge.svg)](https://github.com/json-mockr/json-mockr/actions/workflows/ci.yml)


![](logo.png)


A customizable web server for mocking HTTP JSON API responses for REST webservices.

**json-mockr** is written in Kotlin using [Springboot](https://spring.io/) as the underlying server.

## Motivation

The motivation behind this software is to provide an easy way to mock external APIs during development phase without much configuration but also flexible enough.

## Features

* Support for **nested resources**
* **GET, POST, PUT, DELETE** with **NOT_FOUND**, **METHOD_NOT_ALLOWED**, **OK**, **CREATED** and **NO_CONTENT** responses
* **List responses with arrays or wrapped in an envelope**
* **Resource validation with BAD_REQUEST response**

## Limitations

* **PATCH NOT IMPLEMENTED**
* **In Memory database** (by design)

## ⌨️ Usage

Check the [Documentation](https://json-mockr.github.io)
## Contributing

To get started, please fork the repo and checkout a new branch. You can then build the library with the Maven wrapper

```shell script
./mnvw clean package
```

See more info in [CONTRIBUTING.md](CONTRIBUTING.md)

## ⚖️ License
This software is licensed under the [MIT License](LICENSE)
