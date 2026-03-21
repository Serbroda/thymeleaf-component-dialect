<p align="center">
  <img src="assets/logo.png" alt="Thymeleaf Component Dialect" width="120">
</p>

<h1 align="center">Thymeleaf Component Dialect</h1>

<p align="center">
  <a href="https://github.com/Serbroda/thymeleaf-component-dialect/actions/workflows/ci.yml"><img src="https://github.com/Serbroda/thymeleaf-component-dialect/actions/workflows/ci.yml/badge.svg" alt="CI"></a>
  <a href="https://jitpack.io/#Serbroda/thymeleaf-component-dialect"><img src="https://jitpack.io/v/Serbroda/thymeleaf-component-dialect.svg" alt="jitpack"></a>
  <a href="https://github.com/Serbroda/thymeleaf-component-dialect/blob/main/LICENSE.txt"><img src="https://img.shields.io/github/license/Serbroda/thymeleaf-component-dialect.svg" alt="license"></a>
</p>

<p align="center">A dialect for creating reusable Thymeleaf components with named slots, similar to React or Vue components.</p>

## Quick Example

**Define** a component (`templates/components/panel.html`):

```html
<div th:fragment="panel(title)" class="card">
    <div class="card-header"><b th:text="${title}"></b></div>
    <div class="card-body"><tc:content></tc:content></div>
</div>
```

**Use** it in your template:

```html
<tc:panel tc:constructor="'My Title'">
    <p>Hello world</p>
</tc:panel>
```

**Result:**

```html
<div class="card">
    <div class="card-header"><b>My Title</b></div>
    <div class="card-body"><p>Hello world</p></div>
</div>
```

## Features

- **Reusable Components** - Define with `th:fragment`, use with `<tc:*>` tags
- **[Named Slots](docs/named-slots.md)** - Multiple content areas per component
- **[Fallback Content](docs/named-slots.md#fallback-content)** - Default content when no slot is provided
- **[tc:once](docs/once-attribute.md)** - Deduplicate scripts and styles
- **[Attribute Replacement](docs/components.md#attribute-replacement)** - Dynamic placeholder values
- **Spring Boot Auto-Configuration** - Just add the dependency, no setup needed

## Installation

Requires **Java 17+** and **Thymeleaf 3.1+**.

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.Serbroda</groupId>
    <artifactId>thymeleaf-component-dialect</artifactId>
    <version>VERSION</version>
</dependency>
```

**Spring Boot 3.x:** No configuration needed - auto-configured out of the box.

See [Getting Started](docs/getting-started.md) for Gradle setup, custom configuration, and non-Spring Boot usage.

## Documentation

- [Getting Started](docs/getting-started.md) - Installation and setup
- [Components](docs/components.md) - Defining and using components
- [Named Slots](docs/named-slots.md) - Multi-slot content projection
- [tc:once Attribute](docs/once-attribute.md) - Deduplicate scripts and styles

## Contributing

Contributions are welcome! Feel free to open an [issue](https://github.com/Serbroda/thymeleaf-component-dialect/issues) or submit a [pull request](https://github.com/Serbroda/thymeleaf-component-dialect/pulls).

Before submitting a PR, please make sure:
- All tests pass: `./mvnw clean verify`
- Code is formatted: `./mvnw spotless:apply`

## License

This project is licensed under the [Apache License, Version 2.0](LICENSE.txt).
