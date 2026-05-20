package com.dest1ny.irradiated.foundation;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RadComponent{

    public Map<BlockPos, RadLevel> radMap = new HashMap<>();
    public List<BlockPos> endPoints = new ArrayList<>();

    public RadComponent() {
    }


    public double getRadiationLevel() {
        double total = 0;
        Collection<RadLevel> values = radMap.values();
        for (RadLevel level: values){
            total += level.getTotalRad();
        }
        return total;
    }

    public double[] getRadiationLevels() {
        double[] total = {0,0,0};
        Collection<RadLevel> values = radMap.values();
        for (RadLevel level: values){
            total[0] += level.getAlpha();
            total[1] += level.getBeta();
            total[2] += level.getGamma();
        }
        return total;
    }

    public void addRadiation(RadLevel radLevel, BlockPos source, BlockPos endPoint) {
        radMap.put(source,radLevel);
        if (!endPoints.contains(endPoint)){
            endPoints.add(endPoint);
        }
    }

    public void removeRadiation(BlockPos source) {
        radMap.remove(source);
    }
}
