package com.nightandir.primitiveignition;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrimitiveIgnition implements ModInitializer {
    public static final String MOD_ID = "primitive_ignition";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (hand != Hand.MAIN_HAND || player.isSpectator()) return ActionResult.PASS;

            var stack = player.getStackInHand(hand);
            if (!stack.isOf(Items.FLINT_AND_STEEL)) return ActionResult.PASS;

            BlockPos pos = hit.getBlockPos();
            BlockState state = world.getBlockState(pos);
            if (!(state.getBlock() instanceof AbstractFurnaceBlock)) return ActionResult.PASS;
            if (!(world.getBlockEntity(pos) instanceof AbstractFurnaceBlockEntity furnace)) return ActionResult.PASS;
            if (!(furnace instanceof ManualIgnitionAccess access)) return ActionResult.PASS;
            if (state.get(AbstractFurnaceBlock.LIT) || access.primitiveIgnition$isManuallyIgnited()) return ActionResult.PASS;

            // Only allow ignition when the furnace has an input and fuel.
            if (furnace.getStack(0).isEmpty() || furnace.getStack(1).isEmpty()) return ActionResult.PASS;
            if (world.isClient()) return ActionResult.SUCCESS;

            access.primitiveIgnition$setManuallyIgnited(true);
            world.setBlockState(pos, state.with(AbstractFurnaceBlock.LIT, true), 3);
            world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
            stack.damage(1, player, EquipmentSlot.MAINHAND);
            furnace.markDirty();
            return ActionResult.SUCCESS;
        });

        LOGGER.info("Primitive Ignition initialized: vanilla furnaces require manual ignition.");
    }
}
