package com.dest1ny.irradiated.foundation;

import com.dest1ny.irradiated.Irradiated;
import com.dest1ny.irradiated.content.blocks.RadSourceBlock;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;

import static com.dest1ny.irradiated.Irradiated.asResource;
import static com.dest1ny.irradiated.Irradiated.radShieldingMap;
import static net.minecraft.resources.ResourceKey.createRegistryKey;

public class RadBehaviour {

    private static final Map<BlockPos, RadComponent> radiationMap = new HashMap<>();

    // Calculation of radiation paths
    public static void addRadiationSource(Level world, BlockPos center) {
        Irradiated.LOGGER.info("Source at: " + center);
        BlockState blockState = world.getBlockState(center);
        // Check if the block is an instance of RadSourceBlock
        if (blockState.getBlock() instanceof RadSourceBlock radSourceBlock) {
            // Safely cast the block to RadSourceBlock
            int radius = radSourceBlock.radRange;

            // Iterate over the outer shell of the sphere
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos targetPos = center.offset(x, y, z);
                        double distanceSquared = center.distSqr(targetPos);

                        // If the block is on the outer shell of the sphere calculate the radiation values for all the blocks in between
                        if (distanceSquared >= (radius - 1) * (radius - 1) && distanceSquared <= radius * radius) {
                            radLine(world, center, radSourceBlock, targetPos);
                        }
                    }
                }
            }
        }
    }

    public static List<BlockPos> getLine(BlockPos start, BlockPos end) {
        List<BlockPos> line = new ArrayList<>();
        line.add(start);
        int x1 = start.getX(),
                y1 = start.getY(),
                z1 = start.getZ(),
                x2 = end.getX(),
                y2 = end.getY(),
                z2 = end.getZ(),
                dx = Math.abs(x2 - x1),
                dy = Math.abs(y2 - y1),
                dz = Math.abs(z2 - z1),
                xs = (x2 > x1) ? 1 : -1,
                ys = (y2 > y1) ? 1 : -1,
                zs = (z2 > z1) ? 1 : -1;

        if (dx >= dy && dx >= dz){
            int p1 = 2 * dy - dx;
            int p2 = 2 * dz - dx;
            while (x1 != x2) {
                x1 += xs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dx;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dx;
                }
                p1 += 2 * dy;
                p2 += 2 * dz;
                line.add(new BlockPos(x1,y1,z1));
            }
        } else if (dy >= dx && dy >= dz){
            int p1 = 2 * dx - dy;
            int p2 = 2 * dz - dy;
            while (y1 != y2) {
                y1 += ys;
                if (p1 >= 0) {
                    x1 += xs;
                    p1 -= 2 * dy;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dy;
                }
                p1 += 2 * dx;
                p2 += 2 * dz;
                line.add(new BlockPos(x1,y1,z1));
            }
        } else if (dz >= dy && dz >= dx){

            int p1 = 2 * dy - dz;
            int p2 = 2 * dx - dz;
            while (z1 != z2) {
                z1 += zs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dz;
                }
                if (p2 >= 0) {
                    x1 += xs;
                    p2 -= 2 * dz;
                }
                p1 += 2 * dy;
                p2 += 2 * dx;
                line.add(new BlockPos(x1,y1,z1));
            }
        }
        return line;
    }

    public static void radLine(Level world, BlockPos center, RadSourceBlock source, BlockPos targetPos){

        // Use Bresenham's line algorithm or another method to trace the line
        Irradiated.LOGGER.info("Target:" + targetPos);
        List<BlockPos> path = getLine(center, targetPos);
        RadLevel level = source.getRadiationLevel();

        // Iterate along the path and apply radiation/shielding logic
        for (BlockPos pos : path) {
            world.setBlock(pos,Blocks.DIRT.defaultBlockState(),3);
            BlockState posState = world.getBlockState(pos);
            double shielding = radShieldingMap.getOrDefault(posState.getBlock(), 0.00);

            // Apply the shielding to the radiation level
            level.multiply(1.0 - shielding);

            // Calculate distance-based attenuation
            double distance = Math.sqrt(getSquaredDistance(center, pos));
            RadLevel attenuatedLevel = new RadLevel(
                    level.getAlpha() / Math.pow(distance + 1,1),
                    level.getBeta() / Math.pow(distance + 1,1),
                    level.getGamma() / Math.pow(distance + 1,1)
            );

            // Check if the radiation level is below a threshold (e.g., 1) to stop propagation
            if (attenuatedLevel.getAlpha() < 0.1 && attenuatedLevel.getBeta() < 0.1 && attenuatedLevel.getGamma() < 0.1) {
                break;
            }

            // Check if the block already has a RadComponent
            if (!radiationMap.containsKey(pos)) {
                // If not, create a new RadComponent with initial level 0
                radiationMap.put(pos, new RadComponent());
            }

            // Get the RadComponent for the current position
            RadComponent component = radiationMap.get(pos);
            // then add the radiation from the source
            component.addRadiation(attenuatedLevel, center, targetPos);
            Irradiated.LOGGER.info("    Component at: " + pos);
            Irradiated.LOGGER.info("    Shielding:" + shielding);
            Irradiated.LOGGER.info("    Rad level: " + Arrays.toString(component.getRadiationLevels()));
            Irradiated.LOGGER.info("");
        }
        world.setBlock(targetPos,Blocks.GOLD_BLOCK.defaultBlockState(),3);
    }

    // Method to remove a radiation source
    public static void removeRadiationSource(Level world, BlockPos sourcePos) {
        // Iterate over the blocks within the range
        Collection<RadComponent> values = radiationMap.values();
        for (RadComponent target : values) {
            target.removeRadiation(sourcePos);
        }
    }

    // Method to get radiation level at a specific block position
    public static double getRadiationLevel(BlockPos pos) {
        RadComponent component = radiationMap.get(pos);
        return component != null ? component.getRadiationLevel() : 0;
    }


    // Method to get radiation level at a specific block position
    public static double[] getRadiationLevels(BlockPos pos) {
        RadComponent component = radiationMap.get(pos);
        return component != null ? component.getRadiationLevels(): new double[]{0,0,0};
    }


    // Method to compute squared distance between two BlockPos
    public static int getSquaredDistance(BlockPos pos1, BlockPos pos2) {
        int dx = pos1.getX() - pos2.getX();
        int dy = pos1.getY() - pos2.getY();
        int dz = pos1.getZ() - pos2.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    public static RadComponent getComponent(BlockPos pos){
        return radiationMap.get(pos);
    }

    public static void register(){}
}
