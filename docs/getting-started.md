# Getting Started

## Requirements

- Java 17+
- Thymeleaf 3.1+

## Installation

Add the JitPack repository and the dependency to your `pom.xml`:

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

For Gradle:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Serbroda:thymeleaf-component-dialect:VERSION'
}
```

## Setup

### Spring Boot 3.x (auto-configured)

No configuration needed. The dialect is auto-configured and scans `templates/components/` for component fragments.

You can customize the defaults in `application.yml`:

```yaml
thymeleaf-component-dialect:
  template-prefix: templates/
  template-suffix: .html
  component-directory: components
```

To fully customize the dialect, define your own bean (the auto-configuration will back off):

```java
@Bean
public ComponentDialect componentDialect() {
    var dialect = new ComponentDialect();
    dialect.addParser(new StandardThymeleafComponentParser(
        "templates/", ".html", "my-components"));
    return dialect;
}
```

### Without Spring Boot

Register the dialect manually with your `TemplateEngine`:

```java
TemplateEngine engine = new TemplateEngine();
engine.setTemplateResolver(templateResolver);

ComponentDialect dialect = new ComponentDialect();
dialect.addParser(new StandardThymeleafComponentParser(
    "templates/", ".html", "components"));
engine.addDialect(dialect.getPrefix(), dialect);
```
