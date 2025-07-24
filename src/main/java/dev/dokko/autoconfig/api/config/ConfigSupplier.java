package dev.dokko.autoconfig.api.config;

import dev.dokko.autoconfig.AutoConfigAPI;
import dev.dokko.autoconfig.api.config.annot.BooleanSetting;
import dev.dokko.autoconfig.api.config.annot.Setting;
import dev.dokko.autoconfig.api.setting.ItemList;
import dev.dokko.autoconfig.api.ui.GeneratedConfigScreen;
import dev.dokko.autoconfig.api.ui.element.BooleanButton;
import dev.dokko.autoconfig.api.ui.element.EnumCyclerButton;
import dev.dokko.autoconfig.api.ui.element.ItemListEditorButton;
import dev.dokko.autoconfig.api.ui.element.UIElement;
import lombok.Getter;
import net.minecraft.client.gui.screen.Screen;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ConfigSupplier<T extends Config> {
    @Getter
    private final T config;
    private Screen screen;

    public ConfigSupplier(T config) {
        this.config = config;
    }

    public Screen build(Screen parent) {
        List<UIElement> elements = new ArrayList<>();
        for(Field field : getFields()){
            processBoolean(field, elements);
            processEnum(field, elements);
            processItemList(field, elements);
        }
        this.screen = new GeneratedConfigScreen(this.config, elements, parent);
        return screen;
    }
    private void processItemList(Field field, List<UIElement> elements) {
        if (!ItemList.class.isAssignableFrom(field.getType())) return;

        String key = "setting." + config.getModId() + "." + field.getName();
        field.setAccessible(true);

        elements.add(new ItemListEditorButton(config, key,
                (cfg) -> {
                    try {
                        return (ItemList) field.get(cfg);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return new ItemList();
                    }
                },
                (cfg, value) -> {
                    try {
                        field.set(cfg, value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        ));
    }


    @SuppressWarnings({"unchecked","rawtypes"})
    private void processEnum(Field field, List<UIElement> elements) {
        Class<? extends Enum> enumClass = (Class<? extends Enum>) field.getType();
        if (!field.getType().isEnum()) return;

        String key = "setting." + config.getModId() + "." + field.getName();

        field.setAccessible(true);

        EnumCyclerButton button = new EnumCyclerButton(
                config,
                key,
                enumClass,
                (c) -> {
                    try {
                        return field.get(c);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                },
                (c, v) -> {
                    try {
                        field.set(c, v);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        );

        elements.add(button);
    }


    private void processBoolean(Field field, List<UIElement> elements) {
        if(!field.getType().equals(boolean.class) && !field.getType().equals(Boolean.class))return;
        int type = BooleanSetting.YESNO;
        if(field.isAnnotationPresent(BooleanSetting.class)){
            type = field.getAnnotation(BooleanSetting.class).type();
        }
        field.setAccessible(true);
        String key = "setting."+config.getModId()+"."+field.getName();
        BooleanButton booleanButton = new BooleanButton(config, key, type, (i) -> {
            try {
                return (Boolean) field.get(i);
            } catch (IllegalAccessException e) {
                AutoConfigAPI.LOGGER.error("Could not get field {}: {}", field.getName(), e);
            }
            return null;
        }, (i, v) -> {
            try {
                field.set(i, v);
            } catch (IllegalAccessException e) {
                AutoConfigAPI.LOGGER.error("Could not set field {}: {}", field.getName(), e);
            }
        });
        elements.add(booleanButton);
    }

    public List<Field> getFields() {
        List<Field> elements = new ArrayList<>();
        for(Field field : config.getClass().getFields()){
            if(!field.isAnnotationPresent(Setting.class))continue;
            if((field.getModifiers() & Modifier.FINAL) != 0)continue;
            elements.add(field);
        }
        return elements;
    }

    public Screen getScreenIfBuilt() {
        return screen;
    }
}
