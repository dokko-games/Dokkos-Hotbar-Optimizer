package dev.dokko.autoconfig.api.ui;

import dev.dokko.autoconfig.api.AutoConfig;
import dev.dokko.autoconfig.api.config.Config;
import dev.dokko.autoconfig.api.ui.element.UIElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class GeneratedConfigScreen extends Screen {
    private final Screen parent;
    private final List<UIElement> elements;
    private final Config config;
    private final List<ClickableWidget> renderedWidgets = new ArrayList<>();
    private int scrollOffset = 0;
    private static final int SPACING = 35;
    private static final int TOP_MARGIN = 22;
    private static final int BOTTOM_MARGIN = 25; // Enough space above the done button

    public GeneratedConfigScreen(Config config, List<UIElement> elements, Screen parent) {
        super(Text.translatable("text." + config.getModId() + ".config.screen"));
        this.config = config;
        this.parent = parent;
        this.elements = elements;
    }

    @Override
    protected void init() {
        super.init();

        renderedWidgets.clear();
        int y = TOP_MARGIN - scrollOffset;

        for (UIElement element : elements) {
            ClickableWidget widget = element.build(width / 2 - 150, y, 300, 30);
            renderedWidgets.add(widget);
            addDrawableChild(widget);
            y += SPACING;
        }

        // Done button at bottom
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), btn -> close())
                .dimensions(width / 2 - 100, height - 25, 200, 20)
                .build());
    }

    @Override
    public void close() {
        AutoConfig.saveConfig(config.getClass());
        MinecraftClient.getInstance().setScreen(parent);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int maxScroll = Math.max(0, elements.size() * SPACING - (height - TOP_MARGIN - BOTTOM_MARGIN));
        scrollOffset -= (int) (verticalAmount * SPACING);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        updateWidgetPositions();
        return true;
    }

    private void updateWidgetPositions() {
        int y = TOP_MARGIN - scrollOffset;
        for (ClickableWidget widget : renderedWidgets) {
            widget.setY(y);
            y += SPACING;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        for (Element element : this.children()) {
            Drawable drawable = (Drawable) element;
            if (renderedWidgets.contains(drawable)) continue;
            drawable.render(context, mouseX, mouseY, delta);
        }

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);

        int renderBottom = height - BOTTOM_MARGIN;

        for (ClickableWidget widget : renderedWidgets) {
            int widgetBottom = widget.getY() + widget.getHeight();
            if (widget.getY() >= TOP_MARGIN && widgetBottom <= renderBottom) {
                widget.render(context, mouseX, mouseY, delta);
            }
        }

    }
}
