package com.dest1ny.irradiated.content;

import com.dest1ny.irradiated.content.blocks.RadSourceBlock;
import com.dest1ny.irradiated.content.items.RadDetectionItem;
import com.dest1ny.irradiated.foundation.RadLevel;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static com.dest1ny.irradiated.Irradiated.asResource;

public class TestEntries {
    public static final ResourceKey<CreativeModeTab> IRRADIATED = ResourceKey
            .create(Registries.CREATIVE_MODE_TAB, asResource("irradiated"));

    public static <T extends Item> T registerItem(String name, T item) {
        Registry.register(BuiltInRegistries.ITEM, asResource(name), item);
        return item;
    }

    public static <T extends Block> T registerBlock(String name, T block) {
        Registry.register(BuiltInRegistries.BLOCK, asResource(name), block);
        // Register the BlockItem for the block
        BlockItem blockItem = new BlockItem(block, new Item.Properties());
        Registry.register(BuiltInRegistries.ITEM, asResource(name), blockItem);
        return block;
    }

    public static RadDetectionItem RADPROBE = registerItem("radprobe", new RadDetectionItem((new Item.Properties()).stacksTo(1),"use"));;

    public static RadSourceBlock TESTSOURCE = registerBlock("radsource",new RadSourceBlock(Block.Properties.copy(Blocks.STONE), new RadLevel(50,50,50), 10));


    public static final CreativeModeTab IRRADIATED_TAB = FabricItemGroup.builder()
                .icon(() -> new ItemStack(RADPROBE))
            .title(Component.translatable("creativetab.irradiated_tab"))
            .build();

    public static void register() {
        ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, asResource("irradiated"));
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, IRRADIATED_TAB);
        ItemGroupEvents.modifyEntriesEvent(IRRADIATED).register((content) -> {
            content.accept(RADPROBE.getDefaultInstance());
            content.accept(TESTSOURCE.asItem());
        });
    }
}
