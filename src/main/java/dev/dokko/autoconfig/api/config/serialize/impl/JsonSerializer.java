package dev.dokko.autoconfig.api.config.serialize.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dev.dokko.autoconfig.AutoConfigAPI;
import dev.dokko.autoconfig.api.AutoConfig;
import dev.dokko.autoconfig.api.config.Config;
import dev.dokko.autoconfig.api.config.serialize.ConfigSerializer;
import dev.dokko.autoconfig.api.setting.ItemList;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.lang.reflect.*;
import java.nio.file.Path;
import java.util.*;

public class JsonSerializer<T extends Config> implements ConfigSerializer<T> {
    private final T config;

    public JsonSerializer(T config) {
        this.config = config;
    }

    @Override
    public String serialize() throws Exception {
        getFile().toFile().getParentFile().mkdirs();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject element = new JsonObject();
        List<Field> fields = AutoConfig.getSupplier(config.getClass()).getFields();

        for (Field field : fields) {
            String name = field.getName();
            field.setAccessible(true);
            Object value = field.get(config);
            if (value == null) continue;

            if (value instanceof String) {
                element.addProperty(name, (String) value);
            } else if (value instanceof Boolean) {
                element.addProperty(name, (Boolean) value);
            } else if (value instanceof Number) {
                element.addProperty(name, (Number) value);
            } else if (value instanceof Enum<?>) {
                element.addProperty(name, ((Enum<?>) value).name().toLowerCase());
            } else if (value instanceof ItemList) {
                JsonObject jsonObj = new JsonObject();
                addItemList(jsonObj, (ItemList) value);
                element.add(name, jsonObj);
            } else if (value instanceof Identifier) {
                element.addProperty(name, value.toString());
            } else if (value instanceof Item) {
                element.addProperty(name, Registries.ITEM.getId((Item) value).toString());
            } else if (value instanceof Collection<?>) {
                JsonArray array = new JsonArray();
                for (Object obj : (Collection<?>) value) {
                    array.add(gson.toJsonTree(obj));
                }
                element.add(name, array);
            } else if (value instanceof Map<?, ?>) {
                JsonObject obj = new JsonObject();
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                    obj.add(entry.getKey().toString(), gson.toJsonTree(entry.getValue()));
                }
                element.add(name, obj);
            } else {
                // Try nested object
                element.add(name, gson.toJsonTree(value));
            }
        }

        return gson.toJson(element);
    }

    private void addItemList(JsonObject jsonObj, ItemList list) {
        jsonObj.addProperty("type", "ItemList");
        JsonArray items = new JsonArray();
        for (Item item : list.items) {
            items.add(Registries.ITEM.getId(item).toString());
        }
        jsonObj.add("items", items);
        jsonObj.addProperty("invert", list.invert);
    }

    @Override
    public void deserialize(String contents) {
        if (contents == null || contents.trim().isEmpty()) return;

        Path filePath = getFile();
        filePath.toFile().getParentFile().mkdirs();
        Gson gson = new Gson();

        try {
            JsonObject jsonObject = JsonParser.parseString(contents).getAsJsonObject();

            for (String key : jsonObject.keySet()) {
                try {
                    Field field = config.getClass().getDeclaredField(key);
                    field.setAccessible(true);
                    Class<?> type = field.getType();
                    JsonElement element = jsonObject.get(key);

                    if (element.isJsonObject() && element.getAsJsonObject().has("type") &&
                            element.getAsJsonObject().get("type").getAsString().equals("ItemList")) {
                        List<Item> items = new ArrayList<>();
                        for (JsonElement itemEl : element.getAsJsonObject().getAsJsonArray("items")) {
                            items.add(Registries.ITEM.get(Identifier.of(itemEl.getAsString())));
                        }
                        boolean invert = element.getAsJsonObject().get("invert").getAsBoolean();
                        field.set(config, new ItemList(items, invert));
                        continue;
                    }

                    if (Enum.class.isAssignableFrom(type)) {
                        @SuppressWarnings("unchecked")
                        Class<? extends Enum> enumType = (Class<? extends Enum>) type;
                        Enum<?> value = Enum.valueOf(enumType, element.getAsString().toUpperCase());
                        field.set(config, value);
                    } else if (type.equals(Item.class)) {
                        Item item = Registries.ITEM.get(Identifier.of(element.getAsString()));
                        field.set(config, item);
                    } else if (type.equals(Identifier.class)) {
                        field.set(config, Identifier.of(element.getAsString()));
                    } else if (Collection.class.isAssignableFrom(type)) {
                        Type genericType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        Type listType = TypeToken.getParameterized(List.class, (Class<?>) genericType).getType();
                        Object list = gson.fromJson(element, listType);
                        field.set(config, list);
                    } else if (Map.class.isAssignableFrom(type)) {
                        Type[] types = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                        Type mapType = TypeToken.getParameterized(Map.class, types).getType();
                        Object map = gson.fromJson(element, mapType);
                        field.set(config, map);
                    } else {
                        Object value = gson.fromJson(element, type);
                        field.set(config, value);
                    }

                } catch (NoSuchFieldException e) {
                    AutoConfigAPI.LOGGER.warn("Unknown config field: {}", key);
                } catch (Exception e) {
                    AutoConfigAPI.LOGGER.warn("Could not set field \"{}\":", key);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            AutoConfigAPI.LOGGER.error("Failed to deserialize config JSON:");
            e.printStackTrace();
        }
    }

    @Override
    public String getFileName() {
        return config.getModId() + ".json";
    }
}
