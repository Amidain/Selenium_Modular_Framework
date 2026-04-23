package framework.core.wait;

import org.openqa.selenium.By;

import java.util.List;

/**
* Class to define global blockers as loaders, overlays etc. that may be encountered when interacting with UI
 */
public class GlobalBlockerElements {
    List<By> globalBlockerLocators = List.of(
    );

    public List<By> getGlobalBlockerLocators() {
        return globalBlockerLocators;
    }
}


