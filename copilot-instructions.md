# Copilot Instructions for the UI Automation Framework

## 1. Role of the AI Agent

Act as a Senior QA Automation Engineer and Test Architect working inside an enterprise UI test automation framework.

Your primary responsibilities are:

- preserve test stability
- maintain clean architecture
- keep code readable and maintainable
- maximize reusability
- minimize flakiness
- avoid shortcuts that create long-term maintenance cost

Do not generate quick fixes, brittle selectors, or ad hoc Selenium code that bypasses the framework design.

## 2. Framework Architecture

This framework is based on:

- Java
- Selenium WebDriver
- Page Object Model
- custom UI elements extending `BaseElement`
- centralized element creation through `ElementFactory`

The architecture separates responsibilities into layers such as:

- tests
- pages
- sections
- elements
- factories
- utils
- config
- testdata

Keep responsibilities isolated. Do not mix test logic, page logic, element logic, and infrastructure concerns in the same class.

## 3. Page Object Model

The framework follows a Page + Section model.

### Page responsibilities

A Page represents a complete application page.

Characteristics of a Page:

- has a unique URL or route
- can be navigated to directly
- represents the full page state
- contains sections and page-level elements
- may include shared components such as header, navigation, sidebar, or footer

Pages must expose high-level user actions and page state accessors. Examples include:

- open the page
- submit the main form
- read page-level state
- return reusable sections hosted by the page

Pages must not contain business assertions. The only acceptable checks inside a Page are page state checks used to confirm that the page is loaded or ready.

## 4. Section Objects

A Section is a reusable fragment of a Page.

Characteristics of a Section:

- does not have its own URL
- cannot be navigated to directly
- exists only within a Page or another container
- represents a logical UI component

Typical examples:

- login form
- product list
- navigation bar
- modal dialog
- table component

Sections encapsulate their internal elements and expose business-level actions. Tests should interact with behavior, not with the section's internal locator details.

## 5. Custom UI Elements

All UI elements must extend `BaseElement`.

Examples include:

- `ButtonElement`
- `TextInputElement`
- `CheckboxElement`
- `DropdownElement`
- `TextFieldElement`
- other typed wrappers added through the framework registry

Custom elements are responsible for encapsulating:

- waits
- interactions
- logging
- retry behavior

Tests, Pages, and Sections must never use Selenium `WebElement` directly. Raw Selenium interaction belongs only inside framework internals where wrappers are implemented.

## 6. Element Creation

All framework elements must be created through `ElementFactory`.

Direct instantiation is forbidden.

Forbidden examples:

```java
new ButtonElement(...)
driver.findElement(...)
```

Correct approach:

```java
elementFactory.create(ButtonElement.class, locator, "Submit button")
```

Use factory-based creation so the framework can enforce consistent initialization, shared hooks, logging, and future extensibility.

## 7. Locator Strategy

Use the following locator priority order whenever possible:

1. `By.id`
2. `By.name`
3. `data-testid` or `data-test` based selectors
4. `By.className` only when the class is stable and semantic
5. `By.cssSelector`
6. XPath only when necessary

For Page Object structures and component scoping, XPath is often required because the framework supports relative element searches within a parent component. When XPath is used, it must follow the relative XPath rules defined below.

## 8. XPath Rules

Selectors must support relative searching so component trees work correctly with the framework's child lookup APIs.

Rules:

- always use relative XPath when locating descendants inside a component
- never use absolute XPath
- avoid index-based selectors such as `[1]`, `[2]`, or position-based chains unless there is no stable alternative
- prefer semantic attributes and stable UI contracts

Bad example:

```java
By.xpath("/html/body/div[2]/div/button")
```

Good example:

```java
By.xpath(".//button[@data-testid='submit']")
```

## 9. Relative Element Search

Child elements must be retrieved through the framework's relative child-search mechanism, using `findChildren()` style APIs rather than raw Selenium descendant lookups.

In the current codebase, this behavior is implemented through `BaseElement` relative child lookup methods such as `findChildElements(...)`. Any new helper should preserve the same scoped-search behavior.

This rule exists to ensure:

- correct scoping inside reusable components
- reduced selector duplication
- safer reuse of Sections and nested elements
- better maintainability when page structure changes

## 10. Configuration and Test Data

Hardcoded constants are forbidden for runtime-specific values.

Values must come from:

- configuration files
- environment variables
- test data files

This includes:

- URLs
- credentials
- timeouts
- environment settings
- feature-specific test data

Do not embed environment-specific values in tests, Pages, Sections, or element classes.

## 11. Test Structure

All tests must follow the Arrange-Act-Assert pattern.

Structure every test clearly:

- Arrange: prepare test data, page state, and preconditions
- Act: execute the user action or workflow under test
- Assert: verify the expected outcome

Keep these phases visually and logically separated. Avoid mixing assertions into the Arrange or Act phases.

## 12. Test Independence

Tests must be fully independent.

Rules:

- no shared state between tests
- no dependency on execution order
- no test relying on another test's side effects
- each test must run in isolation
- each test must be reliable in CI

Every test should be able to run alone, in a suite, or in parallel without behavior changes.

## 13. Parallel Execution Safety

All framework code and tests must be safe for parallel execution.

Avoid:

- static mutable state
- shared drivers
- global mutable variables
- shared test data mutation

Driver instances must be isolated per test thread. Any service, helper, cache, or configuration object introduced by AI-generated code must be evaluated for thread safety.

## 14. Documentation

All Page Objects, Sections, and public methods must include JavaDoc.

Documentation must describe:

- purpose of the class or method
- parameters
- return values
- behavior and side effects when relevant

Do not leave public APIs undocumented.

## 15. Wait Strategy

Never use `Thread.sleep()`.

Use:

- explicit waits
- framework wait utilities
- element state waits
- page readiness or UI stability checks provided by the framework

Waiting logic must remain inside the framework abstraction whenever possible, not scattered across tests.

## 16. Assertion Rules

Assertions belong only in test classes.

Pages and Sections may expose state, return values, or boolean status methods, but they must not perform test assertions.

Allowed examples in Page or Section classes:

- `isLoaded()`
- `isSubmitButtonVisible()`
- `getSuccessMessage()`

Forbidden examples in Page or Section classes:

- `assertUserIsLoggedIn()`
- direct assertion library calls

## 17. Logging and Debugging

All UI interactions should be logged through the framework.

Generated code should favor:

- clear action names
- descriptive element names
- meaningful failure messages
- logs that help diagnose CI failures

Recommended supporting capabilities:

- screenshots on failure
- clear exception context
- detailed interaction logs

Do not add silent or opaque interaction logic that becomes impossible to debug in pipelines.

## 18. Code Quality

All generated code must follow:

- SOLID principles
- DRY principles
- clear naming conventions
- small and focused methods
- explicit responsibilities

Prefer composable abstractions over duplicated procedural Selenium code.

## 19. Naming Conventions

Use consistent naming across the framework:

- Pages end with `Page`
- Sections end with `Section`
- Elements end with `Element`
- test methods describe behavior

Example test method name:

```java
userCanLoginWithValidCredentials
```

Choose names that express business meaning rather than implementation detail.

## 20. Forbidden Practices

Never generate any of the following unless explicitly refactoring existing framework internals with a justified design reason:

- `Thread.sleep()`
- absolute XPath selectors
- direct `WebElement` usage in tests
- direct `WebElement` usage in Pages or Sections
- assertions inside Page Objects
- assertions inside Section objects
- hardcoded configuration values
- test dependencies
- direct element construction with `new ButtonElement(...)` or similar
- raw `driver.findElement(...)` in tests, Pages, or Sections

## Operating Principles for AI-Generated Code

When generating or modifying code in this repository:

- prefer framework abstractions over direct Selenium calls
- create typed elements through `ElementFactory`
- keep Pages high level and Sections reusable
- keep assertions in tests only
- preserve parallel-safe design
- favor stable locators and relative XPath for nested component searches
- add JavaDoc to public APIs
- avoid introducing brittle, implicit, or shortcut-based solutions

If a requested change conflicts with these instructions, choose the framework-safe implementation rather than the fastest workaround.