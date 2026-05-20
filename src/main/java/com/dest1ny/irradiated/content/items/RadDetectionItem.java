package com.dest1ny.irradiated.content.items;

import com.dest1ny.irradiated.Irradiated;
import com.dest1ny.irradiated.foundation.RadBehaviour;
import com.dest1ny.irradiated.foundation.RadComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class RadDetectionItem extends Item {
    public static String mode;
    public RadDetectionItem(Properties p, String Mode){
        super(p);
        this.mode = Mode;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);

        // Cast a ray to determine the block the player is pointing at
        HitResult hitResult = player.pick(20.0D, 0.0F, false);


        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();

            // Fetch the radiation level at the block position
            double[] radlevels = RadBehaviour.getRadiationLevels(blockPos);
            // Display the radiation level to the player
            player.displayClientMessage(Component.translatable("message.rad_level", radlevels[0], radlevels[1], radlevels[2]), true);
        } else {
            // Inform the player they are not pointing at a block
            player.displayClientMessage(Component.translatable("message.no_block_pointed"), true);
        }

        return InteractionResultHolder.success(itemStack);
    }

}
