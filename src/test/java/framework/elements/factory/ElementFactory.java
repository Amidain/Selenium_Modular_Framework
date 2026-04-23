package framework.elements.factory;

import java.util.Objects;
import java.util.function.Consumer;

import org.openqa.selenium.By;

import framework.elements.ButtonElement;
import framework.elements.CheckboxElement;
import framework.elements.DropdownElement;
import framework.elements.ImageElement;
import framework.elements.LinkElement;
import framework.elements.TextFieldElement;
import framework.elements.TextInputElement;
import framework.elements.base.BaseElement;
import framework.elements.widgets.FormElement;

/**
 * Central entry point for building typed elements without PageFactory.
 * <p>The factory looks up a {@link FactoryRegistry.ElementBuilder}, hydrates the element, applies the
 * {@link ElementFactoryContext} initializer (shared hooks), and finally executes optional
 * per-call customizers. This makes the element creation pipeline explicit and traceable for testers.</p>
 */
public class ElementFactory {

    private final FactoryRegistry registry;
    private final ElementFactoryContext context;

    /**
     * Builds a factory that reuses the default context (DriverManager + no-op initializer).
     */
    public ElementFactory(FactoryRegistry registry) {
        this(registry, ElementFactoryContext.defaultContext());
    }

    /**
     * Builds a factory with a custom context so teams can plug in their own driver providers or initializers.
     */
    public ElementFactory(FactoryRegistry registry, ElementFactoryContext context) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.context = Objects.requireNonNull(context, "context");
    }

    /**
     * @return fully configured factory with all built-in elements registered.
     */
    public static ElementFactory defaultFactory() {
        FactoryRegistry registry = new FactoryRegistry();
        registerDefaults(registry);
        return new ElementFactory(registry);
    }

    /**
     * Creates an element with an explicit display name.
     */
    public <T extends BaseElement> T create(Class<T> type, By locator, String name) {
        return create(type, locator, name, element -> {});
    }
    /**
     * Full creation pipeline: resolve builder, instantiate element, run context initializer, then run customizer.
     */
    public <T extends BaseElement> T create(Class<T> type, By locator, String name, Consumer<T> customizer) {
        Objects.requireNonNull(locator, "locator");
        Objects.requireNonNull(customizer, "customizer");

        FactoryRegistry.ElementBuilder<T> builder = registry.get(type);
        T element = builder.build(locator, name, context);
        context.getElementInitializer().accept(element);
        customizer.accept(element);
        return element;
    }

    /**
     * Convenience methods for common element types. These just call through to {@link #create} with the appropriate class.
     */
    public ButtonElement button(By locator, String name) {
        return create(ButtonElement.class, locator, name);
    }
    public TextInputElement textInput(By locator, String name) {
        return create(TextInputElement.class, locator, name);
    }
    public CheckboxElement checkbox(By locator, String name) {
        return create(CheckboxElement.class, locator, name);
    }
    public DropdownElement dropdown(By locator, String name) {
        return create(DropdownElement.class, locator, name);
    }
    public LinkElement link(By locator, String name) {
        return create(LinkElement.class, locator, name);
    }
    public ImageElement image(By locator, String name) {
        return create(ImageElement.class, locator, name);
    }
    public TextFieldElement textField(By locator, String name) {
        return create(TextFieldElement.class, locator, name);
    }
    public FormElement form(By locator, String name) {
        return create(FormElement.class, locator, name);
    }

    /**
     * Registers the built-in element/widget types. Called by {@link #defaultFactory()} but also available for custom registries.
     */
    private static void registerDefaults(FactoryRegistry registry) {
        registry.register(ButtonElement.class, (locator, name, ctx) -> new ButtonElement(locator, name, ctx.getDriverProvider()));
        registry.register(TextInputElement.class, (locator, name, ctx) -> new TextInputElement(locator, name, ctx.getDriverProvider()));
        registry.register(CheckboxElement.class, (locator, name, ctx) -> new CheckboxElement(locator, name, ctx.getDriverProvider()));
        registry.register(DropdownElement.class, (locator, name, ctx) -> new DropdownElement(locator, name, ctx.getDriverProvider()));
        registry.register(LinkElement.class, (locator, name, ctx) -> new LinkElement(locator, name, ctx.getDriverProvider()));
        registry.register(ImageElement.class, (locator, name, ctx) -> new ImageElement(locator, name, ctx.getDriverProvider()));
        registry.register(TextFieldElement.class, (locator, name, ctx) -> new TextFieldElement(locator, name, ctx.getDriverProvider()));
        registry.register(FormElement.class, (locator, name, ctx) -> new FormElement(locator, name, ctx.getDriverProvider()));
    }
}
