package dev.dokko.autoconfig.api.setting;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ItemList {
    public List<Item> items = new ArrayList<>();
    public boolean invert;

    public ItemList(List<Item> items) {
        this.items = items;
    }

    public ItemList(boolean invert) {
        this.invert = invert;
    }

    public ItemList(List<Item> items, boolean invert) {
        this.items = items;
        this.invert = invert;
    }

    public ItemList() {

    }

    public boolean isIn(Item item){
        for(Item item1 : items){
            if(item1.getTranslationKey().equals(item.getTranslationKey()))return !invert;
        }
        return invert;
    }

    @Override
    public String toString() {
        return "ItemList{" +
                "items=" + items +
                ", invert=" + invert +
                '}';
    }
}
