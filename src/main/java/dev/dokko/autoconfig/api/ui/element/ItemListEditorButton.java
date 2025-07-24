package dev.dokko.autoconfig.api.ui.element;

import dev.dokko.autoconfig.api.AutoConfig;
import dev.dokko.autoconfig.api.config.Config;
import dev.dokko.autoconfig.api.setting.ItemList;
import dev.dokko.autoconfig.api.ui.element.fnc.ItemListEditorScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ItemListEditorButton implements UIElement {
    private final Config config;
    private final String key;
    private final Function<Config, ItemList> getter;
    private final BiConsumer<Config, ItemList> setter;
    private final ItemList defaultValue;

    public ItemListEditorButton(Config config, String key,
                                Function<Config, ItemList> getter,
                                BiConsumer<Config, ItemList> setter) {
        this.config = config;
        this.key = key;
        this.getter = getter;
        this.setter = setter;
        this.defaultValue = cloneList(getter.apply(config));
    }

    @Override
    public ClickableWidget build(int x, int y, int width, int height) {
        return ButtonWidget.builder(getLabel(), btn -> {
                    if (Screen.hasShiftDown()) {
                        setter.accept(config, cloneList(defaultValue));
                        btn.setMessage(getLabel());
                    } else {
                        if(MinecraftClient.getInstance().player == null)return;
                        MinecraftClient.getInstance().setScreen(
                                new ItemListEditorScreen(config, key, getter, setter, AutoConfig.getSupplier(config.getClass()).getScreenIfBuilt())
                        );
                    }
                }).dimensions(x, y, width, height)
                .tooltip(Tooltip.of(Text.translatable(key + ".tooltip")))
                .build();
    }

    private Text getLabel() {
        ItemList list = getter.apply(config);
        return Text.translatable(key);
    }

    private ItemList cloneList(ItemList original) {
        return new ItemList(new java.util.ArrayList<>(original.getItems()), original.isInvert());
    }
}
