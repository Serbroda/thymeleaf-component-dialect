Thymeleaf Component Dialect
===========================

[![Build Status](https://travis-ci.org/Serbroda/thymeleaf-component-dialect.svg?branch=develop)](https://travis-ci.org/Serbroda/thymeleaf-component-dialect)
[![jitpack](https://jitpack.io/v/Serbroda/thymeleaf-component-dialect.svg)](https://jitpack.io/#Serbroda/thymeleaf-component-dialect)
[![license](https://img.shields.io/github/license/Serbroda/thymeleaf-component-dialect.svg)](https://github.com/Serbroda/thymeleaf-component-dialect/blob/master/LICENSE.txt)


A dialect for creating reusable thymeleaf components.

Installation
------

Add the jitpack repository.

```html
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the dependency (for all available versions see [https://jitpack.io/#Serbroda/thymeleaf-component-dialect](https://jitpack.io/#Serbroda/thymeleaf-component-dialect)).

```html
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

For detailed configurations have a look at the [demo project](https://github.com/Serbroda/thymeleaf-component-dialect-demo).

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

Add the namespace `xmlns:tc="http://www.morphbit.com/thymeleaf/component"` and use the component in your application.

```html
<!DOCTYPE html>
<html 
    xmlns:th="http://www.thymeleaf.org" 
    xmlns:tc="http://www.morphbit.com/thymeleaf/component">
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