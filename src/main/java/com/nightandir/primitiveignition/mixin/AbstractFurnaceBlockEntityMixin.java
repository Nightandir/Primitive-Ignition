package com.nightandir.primitiveignition.mixin;

import com.nightandir.primitiveignition.ManualIgnitionAccess;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin implements ManualIgnitionAccess {
    @Unique private boolean primitiveIgnition$manuallyIgnited = false;

    @Override
    public boolean primitiveIgnition$isManuallyIgnited() {
        return primitiveIgnition$manuallyIgnited;
    }

    @Override
    public void primitiveIgnition$setManuallyIgnited(boolean ignited) {
        primitiveIgnition$manuallyIgnited = ignited;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private static void primitiveIgnition$requireManualIgnition(ServerWorld world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        if (!(blockEntity instanceof ManualIgnitionAccess access)) return;
        if (!access.primitiveIgnition$isManuallyIgnited()) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private static void primitiveIgnition$clearWhenFireGoesOut(ServerWorld world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        if (!(blockEntity instanceof ManualIgnitionAccess access)) return;
        if (!world.getBlockState(pos).get(AbstractFurnaceBlock.LIT)) {
            access.primitiveIgnition$setManuallyIgnited(false);
            blockEntity.markDirty();
        }
    }

    @Inject(method = "readData", at = @At("TAIL"))
    private void primitiveIgnition$readManualIgnition(ReadView view, CallbackInfo ci) {
        primitiveIgnition$manuallyIgnited = view.getBoolean("PrimitiveIgnitionManuallyIgnited", false);
    }

    @Inject(method = "writeData", at = @At("TAIL"))
    private void primitiveIgnition$writeManualIgnition(WriteView view, CallbackInfo ci) {
        view.putBoolean("PrimitiveIgnitionManuallyIgnited", primitiveIgnition$manuallyIgnited);
    }
}
