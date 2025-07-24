package dev.dokko.autoconfig.api.ui.element.fnc;

import dev.dokko.autoconfig.api.config.Config;
import dev.dokko.autoconfig.api.setting.ItemList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ItemListEditorScreen extends Screen {
    private final Config config;
    private final BiConsumer<Config, ItemList> setter;
    private final Screen parent;

    private final ItemList list;

    public ItemListEditorScreen(Config config, String key,
                                Function<Config, ItemList> getter,
                                BiConsumer<Config, ItemList> setter,
                                Screen parent) {
        super(Text.translatable(key + ".editor"));
        this.config = config;
        this.setter = setter;
        this.parent = parent;
        this.list = new ItemList(new ArrayList<>(getter.apply(config).getItems()), getter.apply(config).isInvert());
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int y = 40;

        addDrawableChild(ButtonWidget.builder(Text.literal("Toggle Mode (" + (list.invert ? "Inverted" : "Normal") + ")"), btn -> {
            list.setInvert(!list.isInvert());
            btn.setMessage(Text.literal("Toggle Mode (" + (list.invert ? "Inverted" : "Normal") + ")"));
        }).dimensions(centerX - 100, y, 200, 20).build());
        y += 30;

        addDrawableChild(ButtonWidget.builder(Text.literal("Add Item in Hand"), btn -> {
            ItemStack held = MinecraftClient.getInstance().player.getMainHandStack();
            if (!list.getItems().contains(held.getItem())) {
                list.getItems().add(held.getItem());
            }
        }).dimensions(centerX - 100, y, 200, 20).build());
        y += 30;

        addDrawableChild(ButtonWidget.builder(Text.literal("Add Hotbar Items"), btn -> {
            var player = MinecraftClient.getInstance().player;
            if (player != null) {
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = player.getInventory().getStack(i);
                    if (!stack.isEmpty() && !list.getItems().contains(stack.getItem())) {
                        list.getItems().add(stack.getItem());
                    }
                }
            }
        }).dimensions(centerX - 100, y, 200, 20).build());
        y += 30;

        addDrawableChild(ButtonWidget.builder(Text.literal("Remove Last"), btn -> {
            if (!list.getItems().isEmpty()) {
                list.getItems().remove(list.getItems().size() - 1);
            }
        }).dimensions(centerX - 100, y, 200, 20).build());
        y += 30;

        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), btn -> {
            list.getItems().clear();
            list.setInvert(false);
        }).dimensions(centerX - 100, y, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), btn -> {
            setter.accept(config, list);
            MinecraftClient.getInstance().setScreen(parent);
        }).dimensions(centerX - 100, height - 30, 200, 20).build());
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 10, 0xFFFFFF);
        context.drawText(textRenderer, Text.literal("Items:"), 20, 160, 0xFFFFFF, true);

        int y = 185;
        for (int i = 0; i < list.getItems().size(); i++) {
            String id = Registries.ITEM.getId(list.getItems().get(i)).toString();
            context.drawText(textRenderer, Text.literal("- " + id), 30, y, 0xAAAAAA, false);
            y += 10;
        }

    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }
}
