package com.dest1ny.irradiated;

import com.dest1ny.irradiated.content.TestEntries;
import com.dest1ny.irradiated.content.blocks.RadSourceBlock;
import com.dest1ny.irradiated.content.items.RadDetectionItem;
import com.dest1ny.irradiated.foundation.RadBehaviour;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;


public class Irradiated implements ModInitializer {
	public static final String ID = "irradiated";
	public static final String NAME = "Irradiated";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	private static final Gson GSON = new Gson();
	private static final Type MAP_TYPE = new TypeToken<Map<String, Double>>(){}.getType();
	public static Map<Block, Double> radShieldingMap = new HashMap<>();


	/*/
	public static final CreativeModeTab IRRADIATED_TAB = FabricItemGroup
			.builder()
			.icon(() -> new ItemStack(RADPROBE))
			.title(Component.translatable("creativetab.irradiated_tab"))
			.build();
	 /**/


	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public void onResourceManagerReload(ResourceManager resourceManager) {
				loadShielding(resourceManager);
			}

			@Override
			public ResourceLocation getFabricId() {
				return asResource( "shielding_data_loader");
			}
		});
		TestEntries.register();
		RadBehaviour.register();
	}

	private void loadShielding(ResourceManager resourceManager) {
		ResourceLocation jsonResource = asResource("shielding/shielding.json");

		try {
			// Attempt to get the resource
			Optional<Resource> optionalResource = resourceManager.getResource(jsonResource);
			if (optionalResource.isEmpty()) {
				// Handle the case where the resource is not found
				System.err.println("Resource not found: " + jsonResource);
				return;
			}

			try (InputStreamReader reader = new InputStreamReader(optionalResource.get().open())) {
				// Parse the JSON file into a Map
				Map<String, Double> shieldingData = GSON.fromJson(reader, MAP_TYPE);

				// Clear and populate the radShieldingMap
				radShieldingMap.clear();
				for (Map.Entry<String, Double> entry : shieldingData.entrySet()) {
					try{
						ResourceLocation blockId = new ResourceLocation(entry.getKey());
						Block block = BuiltInRegistries.BLOCK.get(blockId);
						LOGGER.info(blockId.toString());
						LOGGER.info(block.toString());
						radShieldingMap.put(block, entry.getValue());
					} catch (Exception e) {
						e.printStackTrace();
                    }
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Handle any exceptions such as resource not found or JSON parsing errors
		}
	}

	public static ResourceLocation asResource(String path) {
		return new ResourceLocation(ID, path);
	}
}