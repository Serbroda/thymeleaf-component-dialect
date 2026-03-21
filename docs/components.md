# Components

## Defining a Component

Components are standard Thymeleaf fragments. Place them in your components directory (default: `templates/components/`). Use `<tc:content>` to mark where child content will be inserted.

```html
<!-- templates/components/panel.html -->
<div th:fragment="panel(title)" class="card">
    <div class="card-header">
        <b th:text="${title}"></b>
    </div>
    <div class="card-body">
        <tc:content></tc:content>
    </div>
</div>
```

## Using a Component

Add the `tc` namespace to your template and use the component by its fragment name:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:tc="https://github.com/Serbroda/thymeleaf-component-dialect">
<body>
    <tc:panel tc:constructor="'My Panel Title'">
        <p>This content replaces the tc:content tag.</p>
    </tc:panel>
</body>
</html>
```

**Result:**

```html
<div class="card">
    <div class="card-header">
        <b>My Panel Title</b>
    </div>
    <div class="card-body">
        <p>This content replaces the tc:content tag.</p>
    </div>
</div>
```

## Constructor Parameters

Use `tc:constructor` to pass parameters to the fragment. The values are Thymeleaf expressions:

```html
<!-- Component definition -->
<div th:fragment="alert(type, message)" class="alert"
     th:classappend="'alert-' + ${type}">
    <strong th:text="${message}"></strong>
    <tc:content></tc:content>
</div>
```

```html
<!-- Usage -->
<tc:alert tc:constructor="'danger', 'Error!'">
    <p>Something went wrong.</p>
</tc:alert>
```

**Result:**

```html
<div class="alert alert-danger">
    <strong>Error!</strong>
    <p>Something went wrong.</p>
</div>
```

## Passing Variables

Attributes on the component tag (that are not `th:*` or `tc:*`) are available as variables inside the component:

```html
<!-- Component definition -->
<button th:fragment="button" class="btn"
        th:classappend="'btn-' + ${type}">
    <tc:content></tc:content>
</button>
```

```html
<!-- Usage: 'type' becomes a variable -->
<tc:button type="'primary'">Click me</tc:button>
```

**Result:**

```html
<button class="btn btn-primary">Click me</button>
```

Boolean attributes (without a value) are set to `true`:

```html
<!-- Component definition -->
<li th:fragment="menu-item" class="nav-item"
    th:classappend="${active != null and active ? 'active' : ''}">
    <a th:text="${text}"></a>
</li>
```

```html
<!-- Usage: 'active' is true -->
<tc:menu-item text="'Home'" active/>
```

**Result:**

```html
<li class="nav-item active">
    <a>Home</a>
</li>
```

## Custom Selectors

By default, the component tag name matches the fragment name. Use `tc:selector` to define a different tag name:

```html
<!-- Fragment name is 'buttongroup', but used as <tc:button-group> -->
<div tc:selector="button-group" th:fragment="buttongroup" class="btn-group">
    <tc:content></tc:content>
</div>
```

```html
<!-- Usage with the custom selector name -->
<tc:button-group>
    <button>A</button>
    <button>B</button>
</tc:button-group>
```

**Result:**

```html
<div class="btn-group">
    <button>A</button>
    <button>B</button>
</div>
```

## Attribute Replacement

Use `tc:repl-*` attributes to replace placeholder values inside the component. In the component definition, use `?[name]` as placeholder:

```html
<!-- Component definition -->
<div th:fragment="form-input">
    <label th:text="${title}"></label>
    <input type="text" data-th-field="*{?[field]}" class="form-control"/>
    <span data-th-if="${#fields.hasErrors('?[field]')}"
          data-th-errors="*{?[field]}" style="color: red"></span>
</div>
```

```html
<!-- Usage -->
<form th:object="${user}">
    <tc:form-input tc:repl-field="name" title="'Full Name'"/>
    <tc:form-input tc:repl-field="email" title="'Email'"/>
</form>
```

**Result** (with a user object having `name` and `email` fields):

```html
<form>
    <div>
        <label>Full Name</label>
        <input type="text" name="name" value="John" class="form-control"/>
    </div>
    <div>
        <label>Email</label>
        <input type="text" name="email" value="john@example.com" class="form-control"/>
    </div>
</form>
```

## Multiple Components per File

A single HTML file can contain multiple fragment definitions:

```html
<!-- templates/components/nav.html -->
<nav th:fragment="menu" class="navbar">
    <ul class="navbar-nav">
        <tc:content></tc:content>
    </ul>
</nav>

<li th:fragment="menu-item" class="nav-item">
    <a class="nav-link" th:href="${href}" th:text="${text}"></a>
</li>
```

```html
<!-- Usage -->
<tc:menu>
    <tc:menu-item href="'/'" text="'Home'"/>
    <tc:menu-item href="'/about'" text="'About'"/>
</tc:menu>
```

**Result:**

```html
<nav class="navbar">
    <ul class="navbar-nav">
        <li class="nav-item">
            <a class="nav-link" href="/">Home</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="/about">About</a>
        </li>
    </ul>
</nav>
```
