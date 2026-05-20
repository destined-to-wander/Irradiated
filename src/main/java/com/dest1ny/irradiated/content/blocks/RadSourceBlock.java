package com.dest1ny.irradiated.content.blocks;

import com.dest1ny.irradiated.Irradiated;
import com.dest1ny.irradiated.foundation.RadBehaviour;
import com.dest1ny.irradiated.foundation.RadComponent;
import com.dest1ny.irradiated.foundation.RadLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.dest1ny.irradiated.foundation.RadBehaviour.addRadiationSource;
import static com.dest1ny.irradiated.foundation.RadBehaviour.removeRadiationSource;

public class RadSourceBlock extends Block {
    public RadLevel radMap;
    public int radRange;
    public List<RadComponent> RadComponents;
    public RadSourceBlock(Properties p, RadLevel radLevel, int Range){
        super(p);
        this.radMap = radLevel;
        this.radRange = Range;
    }
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        // Ensure this only runs on the server side
        if (!level.isClientSide) {
            addRadiationSource(level, pos);
        }
    }
    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        Level actualLevel = (Level) level;
        if (!actualLevel.isClientSide) {
            removeRadiationSource(actualLevel, pos);
        }
    }

    public RadLevel getRadiationLevel() {
        return radMap;
    }
}
