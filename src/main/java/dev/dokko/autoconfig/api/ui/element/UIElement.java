package dev.dokko.autoconfig.api.ui.element;

import net.minecraft.client.gui.widget.ClickableWidget;

public interface UIElement {
    ClickableWidget build(int x, int y, int width, int height);
}
