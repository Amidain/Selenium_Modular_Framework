# Selenium Modular Framework

This project is a modular UI automation framework built on top of Selenium WebDriver, TestNG, and strongly typed custom element wrappers. The core design goal is to move responsibility away from raw WebDriver calls and into framework abstractions that own synchronization, retries, diagnostics, selector strategy, and driver lifecycle.

Instead of interacting with Selenium WebElement directly in tests, the framework exposes typed UI components such as buttons, text inputs, dropdowns, links, images, forms, and other reusable wrappers. Each element is created centrally, receives the same driver access pattern, and automatically uses the same waiting and retry rules.

The result is a framework where:

- page objects stay focused on business intent,
- tests remain readable and deterministic,
- synchronization is embedded into the interaction layer,
- driver lifecycle is thread-safe,
- configuration is typed and immutable at runtime,
- locator strategy can evolve without rewriting the whole suite.

## Menu

1. [Framework overview](#1-framework-overview)
	1. [Tools](#11-tools)
2. [Framework structure overview](#2-framework-structure-overview)
	1. [Custom elements](#21-custom-elements)
	2. [ElementFactory](#22-elementfactory)
	3. [Waits](#23-waits)
	4. [Configuration](#24-configuration)
	5. [Drivers and thread safety](#25-drivers-and-thread-safety)
3. [Tests](#3-tests)

## 1. Framework overview

The framework is organized around a layered interaction model.

At the top, tests describe user flows using page objects. Below the page layer, custom element wrappers encapsulate UI behavior. A page object does not click a raw Selenium locator directly. Instead, it uses a typed component created by the factory, for example a button, text input, or form wrapper. That wrapper is responsible for finding the element, validating state, waiting for readiness, handling retries, and reporting failures.

The execution flow of a typical interaction looks like this:

1. A test creates a page object with a shared ElementFactory.
2. The page object creates typed elements from locators.
3. An element action such as click or type is called.
4. The element delegates execution to ActionExecutor.
5. ActionExecutor delegates retry handling to ActionRetrier.
6. Before the real Selenium call, the element uses ConditionalWait to ensure the UI is ready.
7. If the action fails with a handled transient exception, the framework retries it.
8. If the action still fails, diagnostics such as screenshots are captured.

This makes the framework opinionated in a useful way: tests do not need to remember when to wait, when to retry, or how to capture evidence. Those concerns are centralized in reusable framework code.

### 1.1 Tools

The current stack is intentionally lightweight and focused on UI automation:

- Java
- Selenium 
- TestNG (test runner)
- Allure (reporting)
- JavaFaker (dynamic test data generation)

## 2. Framework structure overview

The framework is split into a few clear architectural areas:

tests
└── business scenarios (test cases)

pages
└── page objects (UI domain layer) 

framework
├── elements
│   ├── base        (BaseElement abstractions)
│   ├── factory     (ElementFactory, registry)
│   └── widgets     (typed UI components, as FormElement)
│
├── core
│   ├── controllers (actions, retry, relative finders, selectors)
│   ├── wait        (wait strategies, conditions)
│   ├── driver      (WebDriver management)
│   ├── js          (JsActions utilized within BaseElement)
│   └── configs     (configuration, environment setup)

- `framework.elements` contains strongly typed UI wrappers.
- `framework.elements.base` contains the base abstraction shared by all elements.
- `framework.elements.factory` contains the element construction pipeline.
- `framework.core.controllers` contains action execution, retries, relative lookup, and custom selectors.
- `framework.core.wait` contains waiting primitives and application readiness checks.
- `framework.core.driver` contains browser creation and thread-safe driver management.
- `framework.core.configs` contains typed immutable configuration objects.
- `pages` contains page objects built on top of typed elements.
- `tests` contains business-level test scenarios.

### 2.1 Custom elements

Custom elements are the foundation of the framework.

The central abstraction is `BaseElement`. It wraps three pieces of information:

- a Selenium `By` locator,
- an element name used in logs and diagnostics,
- a `WebDriver` provider instead of a hard reference to a concrete driver instance.

`BaseElement` provides common interaction methods such as:

- `click()`
- `clickWithJs()`
- `getText()`
- `getAttribute()`
- `isDisplayed()`
- `isEnabled()`
- `exists()`
- `scrollIntoView()`
- `findChildElements()`

The important architectural decision is that these methods do not call Selenium directly without protection. Each interaction is funneled through `ActionExecutor`, and before the physical Selenium operation happens, the wrapper applies preconditions using `ConditionalWait`.

For example, a standard click from `BaseElement` first waits for:

- UI readiness,
- element stability,
- clickability.

Only then does it perform `WebElement.click()`.

This is the framework's built-in auto-waiting model. Waiting is not a separate concern that test authors must remember. It is part of the element contract.

#### Built-in element types

The default factory registers a set of reusable, typed wrappers:

- `ButtonElement`
- `TextInputElement`
- `CheckboxElement`
- `DropdownElement`
- `LinkElement`
- `ImageElement`
- `TextFieldElement`
- `FormElement`

Each concrete element extends `BaseElement` and adds behavior appropriate to its role.

Examples:

- `TextInputElement` supports `type`, `typeJs`, `setValue`, `appendValue`, `clear`, and `clearWithKeyboard`.
- `ButtonElement` adds `getLabel()` and `clickAndWaitToDisappear()`.
- `FormElement` is used both as a reusable container abstraction and as a page-level readiness anchor.

This gives the framework stronger semantics than raw Selenium. A page object can declare that something is a button or input and receive behavior aligned with that intent.

#### ActionExecutor

`ActionExecutor` is the execution facade used by elements to run actions safely and consistently.

Its responsibilities are:

- log the start and end of each action,
- delegate transient-failure handling to `ActionRetrier`,
- capture failure artifacts when an action ultimately fails.

This class prevents repeated boilerplate across element methods. Without it, every wrapper method would need to duplicate logging, retrying, and screenshot handling.

#### ActionRetrier

`ActionRetrier` is responsible for re-running transiently failing actions.

Based on retry parameters specified in Configuration, it retries only selected exception types defined in `IActionRetrier`. The current handled set includes common UI timing and DOM volatility issues such as:

- `StaleElementReferenceException`
- `InvalidElementStateException`
- `ElementClickInterceptedException`

The retrier calculates:

- retry count,
- maximum attempts,
- delay between attempts.

If the thrown exception is not in the handled list, the error is rethrown immediately. If it is handled and attempts remain, the framework waits for the configured delay and tries again.

#### JsActions

`JsActions` exposes controlled JavaScript-based fallbacks for cases where standard WebDriver interaction is not sufficient.

This utility is intentionally tied to the same locator and driver provider model used by `BaseElement`. That keeps JavaScript interactions aligned with the same element instance.

#### Relative child lookup

`RelativeElementFinder` supports finding typed child components inside a parent component using relative XPath locators.

This matters because complex UI structures are often easier to model as container components with children rather than flat global locators. The flow is:

1. Start from a parent typed element.
2. Find children with a relative XPath.
3. Compose an indexed `ByChained` locator.
4. Recreate each child as a typed element through the factory.

That means even nested components preserve the same framework behavior: waits, retries, logging, and naming.

Example of usage:

```java
private static final By SLIDER = By.cssSelector(".slider");

FormElement sliderForm = elementFactory.form(SLIDER, "Home Page Slider");

List<ButtonElement> addToCartButtons = sliderForm.findChildElements(
	"Add To Cart Button",
	By.xpath(".//*[contains(@class, 'btn-add-to-cart')]"),
	ButtonElement.class,
	elementFactory
);
```

In this example the parent element is a `FormElement` created for the container with `class="slider"`. The framework first finds that container, and then searches only inside it for child elements that have class: `btn-add-to-cart`.

Required relative xpath locator (starts with `.`):

```java
By.xpath(".//*[contains(@class, 'btn') and contains(@class, 'btn-add-to-cart')]")
```

Incorrect xpath locator:

```java
By.xpath("//*[contains(@class, 'btn-add-to-cart')]")
```

The incorrect version starts from the whole document instead of the parent element, so it breaks the idea of scoped child lookup.

### 2.2 ElementFactory

`ElementFactory` is the central creation point for every typed UI element and alternative to PageFactory. In practice, this means that a page object does not instantiate elements manually and does not work directly with raw `WebElement` references. Instead, it asks the factory for a ready-to-use wrapper.

This makes page objects simpler. They only define locators and choose what type of element should be created for a given locator. The factory takes care of building an element that is already connected to the rest of the framework, including driver access, waits, retries, and action logging.

`ElementFactory` also exposes convenience methods that simplify object creation for the most common element types. Because of that, a page object can use either the generic creation method or a shorter type-specific helper.

Both approaches are valid:

```java
ButtonElement loginButtonA = elementFactory.create(ButtonElement.class, LOGIN_BUTTON, "Login Button");
ButtonElement loginButtonB = elementFactory.button(LOGIN_BUTTON, "Login Button");
```

The first version is more explicit and works for every registered type. The second one is shorter and usually easier to read in page objects.

Example of usage:

```java
public class BaseTest {
	protected ElementFactory elementFactory;

	@BeforeMethod
	public void beforeMethod() {
		elementFactory = ElementFactory.defaultFactory();
	}
}

public class SignUpLoginPage extends BasePage {
	private static final By LOGIN_BUTTON = ByTestId.byTestId("login-button");
	private static final By EMAIL_INPUT_LOGIN = ByTestId.byTestId("login-email");

	private final ButtonElement loginButton;
	private final TextInputElement loginInput;

	public SignUpLoginPage(ElementFactory elementFactory) {
		super("Sign Up/Login Page", By.className("login-form"), elementFactory);
		this.loginButton = elementFactory.button(LOGIN_BUTTON, "Login Button");
		this.loginInput = elementFactory.textInput(EMAIL_INPUT_LOGIN, "Login Input");
	}
}

@Test
public void exampleTest() {
	SignUpLoginPage signUpLoginPage = new SignUpLoginPage(elementFactory);
	signUpLoginPage.waitForPage();
}
```

`ElementFactory` standardizes how elements are created across the whole framework.

#### FactoryRegistry

`FactoryRegistry` is a thread-safe map from element class to builder function.

Its purpose is to make element construction declarative and replaceable. Builders can be registered, replaced, unregistered, or cleared. This allows the framework to define a default set of components while still enabling test-level or project-level customization.

#### ElementFactoryContext

`ElementFactoryContext` carries shared dependencies used by all elements created by the factory.

### 2.3 Waits

The main class is `ConditionalWait`, returned from `BaseElement.await()`.

It is a fluent wait wrapper built around Selenium `WebDriverWait`, but the framework adds its own policy on top:

- default timeout and polling are loaded from typed configuration,
- short timeout and short polling are supported as a separate fast profile,
- `StaleElementReferenceException` and `NoSuchElementException` are ignored during polling,
- waiting methods operate on element state rather than generic lambdas only.

Supported wait conditions include:

- `untilPresent()`
- `untilNotPresent()`
- `untilVisible()`
- `untilNotVisible()`
- `untilEnabled()`
- `untilDisabled()`
- `untilClickable()`
- `untilStable()`
- `untilValueEquals()`
- `untilValueNotEquals()`
- `untilTextContains()`
- `untilTextDoesNotContain()`
- `untilAttributeEquals()`
- generic `until(...)`

#### Auto-waits

The most important point is that waits are not only available, they are already used by element methods.

Examples:

- `click()` waits for UI readiness, stability, and clickability.
- `getText()` waits for UI readiness, stability, and visibility.
- `getAttribute()` waits for UI readiness and presence.
- `type()` waits for UI readiness, stability, and clickability.

This makes the framework auto-waiting by design.

#### UI readiness

One of the stronger architectural ideas here is `untilUiReady()`.

This method does not only look at a target element. It also verifies application-wide readiness through `AppStateWaits`:

- `document.readyState === "complete"`
- absence of any currently configured global blockers.

The global blockers are defined in `GlobalBlockerElements`. This is where loaders, overlays, spinners, or blocking modals can be registered as locators that should disappear before user interaction continues.

That creates a two-level synchronization model:

1. page-level or application-level readiness,
2. element-level readiness.

### 2.4 Configuration

The framework configuration model is strongly typed, centralized, and immutable at runtime.

The root entry point is `Configuration`, which builds a single immutable object. It exposes typed sub-configurations instead of raw string lookups.

The current sub-configurations are:

- `RetryConfiguration`
- `WaitConfiguration`
- `DriverConfiguration`
- `ReportingConfiguration`
- `LocatorConfiguration`

#### PropertyReader

`PropertyReader` is responsible for loading raw configuration values. It supports two input channels:

- a properties file from test resources,
- environment variables.

There is also a runtime override for the config file name:

- system property `config.file`
- environment variable `CONFIG_FILE`

If no override is supplied, the framework falls back to `config.properties`.

For individual configuration keys, environment variables take precedence over the properties file. The conversion strategy maps keys such as `retry.count` to environment variable names such as `RETRY_COUNT`.

This gives the framework a clean local-to-CI configuration story.

#### Typed configuration groups

Configuration values are grouped by responsibility:

- retry settings: retry count and retry delay,
- wait settings: default and short timeout and polling values,
- driver settings: browser, base URL, grid URL, headless mode, remote mode, Docker flag, DB insert flag, auto-scroll flag,
- reporting settings: screenshot behavior and verbose UI logging,
- locator settings: custom test-id attribute definition.

This avoids a common anti-pattern where raw strings are fetched all over the project and parsed ad hoc.

#### Custom selector layer: byTestId

`ByTestId` is a configuration-driven abstraction that centralizes how test identifiers are resolved.
Instead of hardcoding a specific attribute (e.g. data-qa), it uses the attribute list defined in `LocatorConfiguration` (`test.id.attribute`).

Calling ByTestId.byTestId("login-email") dynamically builds a CSS selector based on the configured attributes, ensuring flexibility and consistency across the framework.

The sample configuration might be:

`data-qa`

but the implementation supports a comma-separated list of attributes. That means the framework can generate a selector template matching one or more supported test-id attributes without rewriting page objects.

This is valuable for projects with inconsistent locator conventions or when gradually migrating to a dedicated test attribute strategy.

### 2.5 Drivers and thread safety

Browser lifecycle is handled centrally so that tests never own raw driver construction or disposal logic.

#### DriverManager

`DriverManager` is the thread-safe access point for WebDriver instances.

It uses:

- `ThreadLocal<WebDriver>`
- `ThreadLocal<BrowserType>`

The driver is created lazily on first access for a given thread. If the cached session has been closed, the manager creates a fresh one. This is especially important for parallel execution because each test thread must operate on its own isolated browser session.

The manager also exposes `setDriverProvider(...)`, which means driver creation can be replaced if needed, for example for custom capabilities, cloud providers, or alternative environments.

#### BrowserFactory

`BrowserFactory` builds actual browser instances based on the typed driver configuration.

The current implementation supports Chrome in two modes:

- local execution,
- remote execution through Selenium Grid.

For local runs it:

- resolves ChromeDriver through WebDriverManager,
- creates a dedicated `ChromeDriverService`,
- applies a consistent set of Chrome options,
- optionally runs in headless mode,
- enables browser and driver logging,
- creates per-thread and per-session artifact directories.

For remote runs it creates a `RemoteWebDriver` using the configured Grid URL and the same Chrome options.

#### Driver artifacts and isolation

An important implementation detail is the artifact directory strategy. BrowserFactory creates output under `target/driver-artifacts` and isolates it by:

- browser,
- thread id,
- generated session id.

This keeps logs, profile data, and cache folders separated between parallel sessions. `ServiceFactory` further wires ChromeDriver logs into the session-specific directory.

## 3. Tests

The test layer is intentionally thin. Most implementation complexity lives below it in reusable abstractions.

#### Page objects

Page objects extend `BasePage`, which provides two important concepts:

- a page name used in logs and diagnostics,
- a unique page locator used as a readiness anchor.

`BasePage.waitForPage()` creates a `FormElement` for the page's unique locator and waits for:

- UI readiness,
- visibility of the unique element.

Concrete page objects then focus on three things only:

- defining locators,
- creating typed elements via the factory,
- exposing business-level actions.

The test never manages retry logic, never creates waits manually for each field interaction, and never performs direct low-level Selenium operations on raw locators. That is exactly the architectural payoff of the framework.


