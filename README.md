Thymeleaf Component Dialect
===========================

[![CI](https://github.com/Serbroda/thymeleaf-component-dialect/actions/workflows/ci.yml/badge.svg)](https://github.com/Serbroda/thymeleaf-component-dialect/actions/workflows/ci.yml)
[![jitpack](https://jitpack.io/v/Serbroda/thymeleaf-component-dialect.svg)](https://jitpack.io/#Serbroda/thymeleaf-component-dialect)
[![license](https://img.shields.io/github/license/Serbroda/thymeleaf-component-dialect.svg)](https://github.com/Serbroda/thymeleaf-component-dialect/blob/develop/LICENSE.txt)

A dialect for creating reusable Thymeleaf components, similar to React or Vue components.

Requirements
------

- Java 17+
- Thymeleaf 3.1+

Installation
------

Add the jitpack repository.

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the dependency (for all available versions see [https://jitpack.io/#Serbroda/thymeleaf-component-dialect](https://jitpack.io/#Serbroda/thymeleaf-component-dialect)).

```xml
<dependency>
    <groupId>com.github.Serbroda</groupId>
    <artifactId>thymeleaf-component-dialect</artifactId>
    <version>VERSION</version>
</dependency>
```

Create a bean of `ComponentDialect`. You can also register parsers to add components automatically. The `StandardThymeleafComponentParser` searches for all `th:fragment` attributes and registers them as components.

```java
@Bean
public ComponentDialect componentDialect() {
    ComponentDialect dialect = new ComponentDialect();
    dialect.addParser(new StandardThymeleafComponentParser("templates/", ".html", "components"));
    return dialect;
}
```


Usage
-----

### Create a thymeleaf component

Thymeleaf components uses the standard `th:fragment` attribute to register components. Just create a fragment with a `<tc:content/>` tag which will be replaced with specific contents.

```html
<div th:fragment="panel" class="panel panel-primary">
    <div class="panel-heading">
        <b th:text="${title != null ? title : ''}"></b>
    </div>
    <div class="panel-body">
        <tc:content></tc:content>
    </div>
</div>
```

### Use the component

Add the namespace `xmlns:tc="https://github.com/Serbroda/thymeleaf-component-dialect"` and use the component in your application.

```html
<!DOCTYPE html>
<html 
    xmlns:th="http://www.thymeleaf.org" 
    xmlns:tc="https://github.com/Serbroda/thymeleaf-component-dialect">
<head>
</head>
<body>
    <tc:panel title="'A title'">
        <h1>Hello world</h1>
        <p>This is my first thymeleaf component</p>
    </tc:panel>
</body>
</html>
```

### The result will be

```html
<div class="panel panel-primary">
    <div class="panel-heading">
        <b>A title</b>
    </div>
    <div class="panel-body">
        <h1>Hello world</h1>
        <p>This is my first thymeleaf component</p>
    </div>
</div>
```

### The `tc:once` Attribute

Use `tc:once` to ensure an element (e.g. a script tag) is only rendered once, even if the component is used multiple times on the same page:

```html
<div th:fragment="my-widget">
    <button>Click me</button>
    <script tc:once="my-widget-script">
        // This script will only be included once
    </script>
</div>
```

Contributing
------

Contributions are welcome! Feel free to open an [issue](https://github.com/Serbroda/thymeleaf-component-dialect/issues) or submit a [pull request](https://github.com/Serbroda/thymeleaf-component-dialect/pulls).

Before submitting a PR, please make sure:
- All tests pass: `./mvnw clean verify`
- Code is formatted: `./mvnw spotless:apply`

License
------

This project is licensed under the [Apache License, Version 2.0](LICENSE.txt).