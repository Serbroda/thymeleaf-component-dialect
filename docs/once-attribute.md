# The `tc:once` Attribute

## Problem

When a component includes a `<script>` or `<style>` tag and is used multiple times on a page, the script/style block gets duplicated for every instance.

## Solution

The `tc:once` attribute ensures an element is only rendered once per page, regardless of how many times the component is used. Each `tc:once` value acts as a unique identifier.

## Example

### Component Definition

```html
<div th:fragment="my-widget">
    <button onclick="widgetAction()">Click me</button>

    <script type="text/javascript" tc:once="my-widget-script">
        function widgetAction() {
            console.log("Widget clicked");
        }
    </script>

    <style tc:once="my-widget-css">
        .widget { border: 1px solid #ccc; }
    </style>
</div>
```

### Usage

```html
<tc:my-widget/>
<tc:my-widget/>
<tc:my-widget/>
```

### Result

```html
<!-- Button renders 3 times -->
<div>
    <button onclick="widgetAction()">Click me</button>
    <script type="text/javascript">
        function widgetAction() {
            console.log("Widget clicked");
        }
    </script>
    <style>
        .widget { border: 1px solid #ccc; }
    </style>
</div>
<div>
    <button onclick="widgetAction()">Click me</button>
    <!-- script and style NOT duplicated -->
</div>
<div>
    <button onclick="widgetAction()">Click me</button>
    <!-- script and style NOT duplicated -->
</div>
```

## How It Works

The `tc:once` attribute uses Thymeleaf's ID sequence mechanism. The first occurrence with a given value is rendered. All subsequent occurrences with the same value are removed from the output.

Use a unique, descriptive identifier for each `tc:once` value (e.g. `component-name.script`, `component-name.css`).
