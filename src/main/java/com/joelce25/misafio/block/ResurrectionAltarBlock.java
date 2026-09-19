package com.joelce25.misafio.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class ResurrectionAltarBlock extends Block {
    public ResurrectionAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            MenuProvider containerProvider = new SimpleMenuProvider(
                    (id, inventory, p) -> new AltarMenu(id, inventory, ContainerLevelAccess.create(level, pos)),
                    Component.literal("Altar de Resurrección")
            );
            NetworkHooks.openScreen(serverPlayer, containerProvider, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}