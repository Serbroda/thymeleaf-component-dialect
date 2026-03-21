# Named Slots

Named slots allow components to define multiple content areas that can be filled independently, similar to Vue's `<slot>` or React's `props.children` with render props.

## Basic Concept

- **`<tc:content>`** in the component definition marks a content insertion point (slot)
- **`<tc:content name="...">`** defines a named slot
- **`<tc:slot name="...">`** in the usage fills a named slot
- Content not wrapped in `<tc:slot>` goes into the default (unnamed) slot

## Example

### Component Definition

```html
<!-- templates/components/layout.html -->
<div th:fragment="page-layout" class="layout">
    <header>
        <tc:content name="header"></tc:content>
    </header>
    <main>
        <tc:content></tc:content>
    </main>
    <footer>
        <tc:content name="footer"></tc:content>
    </footer>
</div>
```

### Usage

```html
<tc:page-layout>
    <tc:slot name="header">
        <h1>My Page Title</h1>
    </tc:slot>
    <tc:slot name="footer">
        <span>Copyright 2026</span>
    </tc:slot>

    <p>This goes into the default slot (main).</p>
</tc:page-layout>
```

### Result

```html
<div class="layout">
    <header>
        <h1>My Page Title</h1>
    </header>
    <main>
        <p>This goes into the default slot (main).</p>
    </main>
    <footer>
        <span>Copyright 2026</span>
    </footer>
</div>
```

## Fallback Content

Slots can define fallback content that is rendered when no matching `<tc:slot>` is provided:

```html
<!-- Component definition with fallback -->
<div th:fragment="card" class="card">
    <div class="card-header">
        <tc:content name="header">Default Header</tc:content>
    </div>
    <div class="card-body">
        <tc:content>Default Body</tc:content>
    </div>
</div>
```

```html
<!-- Only override the header, body uses fallback -->
<tc:card>
    <tc:slot name="header"><b>Custom Header</b></tc:slot>
</tc:card>
```

Result:

```html
<div class="card">
    <div class="card-header">
        <b>Custom Header</b>
    </div>
    <div class="card-body">
        Default Body
    </div>
</div>
```

## Empty Slots

An empty component (or empty slots) will use the fallback content:

```html
<!-- Uses all fallback content -->
<tc:card></tc:card>
```

**Result:**

```html
<div class="card">
    <div class="card-header">
        Default Header
    </div>
    <div class="card-body">
        Default Body
    </div>
</div>
```

## Combining with Constructor Parameters

Named slots work alongside `tc:constructor`:

```html
<!-- Component definition -->
<div th:fragment="panel(title)" class="panel">
    <div class="panel-header">
        <tc:content name="header"><b th:text="${title}"></b></tc:content>
    </div>
    <div class="panel-body">
        <tc:content></tc:content>
    </div>
    <div class="panel-footer">
        <tc:content name="footer"></tc:content>
    </div>
</div>
```

```html
<!-- Use the title parameter for header (fallback), override footer -->
<tc:panel tc:constructor="'Countries'">
    <tc:slot name="footer">
        <span>4 countries registered</span>
    </tc:slot>

    <p>Body content here</p>
</tc:panel>
```

**Result:**

```html
<div class="panel">
    <div class="panel-header">
        <b>Countries</b>
    </div>
    <div class="panel-body">
        <p>Body content here</p>
    </div>
    <div class="panel-footer">
        <span>4 countries registered</span>
    </div>
</div>
```

## Unmatched Slots

If a `<tc:slot name="...">` references a name that doesn't exist in the component, the slot content is silently ignored. The default content still renders normally.

```html
<tc:card>
    <tc:slot name="nonexistent"><span>This will be ignored</span></tc:slot>
    <p>This goes into the default slot</p>
</tc:card>
```

**Result:**

```html
<div class="card">
    <div class="card-header">
        Default Header
    </div>
    <div class="card-body">
        <p>This goes into the default slot</p>
    </div>
</div>
```
