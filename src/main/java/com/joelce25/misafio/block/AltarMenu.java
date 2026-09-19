package com.joelce25.misafio.block;

import com.joelce25.misafio.menu.ModMenuTypes;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class AltarMenu extends AnvilMenu {
    private String nombreJugador = "";

    public AltarMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, ContainerLevelAccess.NULL);
    }

    public AltarMenu(int containerId, Inventory inv, ContainerLevelAccess access) {
        super(containerId, inv, access);
    }

    @Override
    public MenuType<?> getType() {
        return ModMenuTypes.ALTAR_MENU.get();
    }

    @Override
    protected boolean isValidBlock(BlockState state) {
        return state.is(ModBlocks.ALTAR_RESURRECCION.get());
    }

    @Override
    public boolean setItemName(String newName) {
        boolean result = super.setItemName(newName);
        this.nombreJugador = newName;
        this.createResult();
        return result;
    }

    public String getItemName() {
        return this.nombreJugador == null ? "" : this.nombreJugador;
    }

    @Override
    public void createResult() {
        ItemStack slotCuchara = this.inputSlots.getItem(0);

        ResourceLocation idCuchara = new ResourceLocation("dedsafio3", "cuchara_r");
        Item itemCuchara = ForgeRegistries.ITEMS.getValue(idCuchara);

        boolean esCucharaValida = (!slotCuchara.isEmpty() && itemCuchara != null && slotCuchara.is(itemCuchara))
                || (!slotCuchara.isEmpty() && slotCuchara.getItem().toString().contains("cuchara"));

        if (esCucharaValida && this.nombreJugador != null && !this.nombreJugador.isBlank()) {
            ItemStack output = new ItemStack(Items.TOTEM_OF_UNDYING);
            output.setHoverName(Component.literal("§aClic para revivir a: " + this.nombreJugador));
            this.resultSlots.setItem(0, output);
        } else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        }
        this.broadcastChanges();
    }

    @Override
    protected void onTake(Player player, ItemStack stack) {
        // 1. Consumir la cuchara de la ranura de entrada
        this.inputSlots.getItem(0).shrink(1);

        // 2. Ejecutar el desbaneo en el servidor
        if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer && this.nombreJugador != null && !this.nombreJugador.isBlank()) {
            UserBanList banList = serverPlayer.server.getPlayerList().getBans();
            Optional<GameProfile> perfil = serverPlayer.server.getProfileCache().get(this.nombreJugador);

            if (perfil.isPresent() && banList.isBanned(perfil.get())) {
                banList.remove(perfil.get());
                serverPlayer.server.getPlayerList().broadcastSystemMessage(
                        Component.literal("§d¡" + serverPlayer.getScoreboardName() + " ha revivido a " + this.nombreJugador + " usando el Altar!"),
                        false
                );
            } else {
                serverPlayer.sendSystemMessage(Component.literal("§cEl jugador " + this.nombreJugador + " no existe o no está baneado."));
            }
        }

        // 3. Romper el ítem de resultado inmediatamente para que el jugador no se quede con el tótem
        stack.setCount(0);
        this.setCarried(ItemStack.EMPTY);

        // 4. Efecto de sonido de rotura / uso del altar
        this.access.execute((level, pos) -> level.levelEvent(1030, pos, 0));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            int resultSlotIndex = this.getResultSlot();

            // Si el jugador intenta usar Shift+Clic en la ranura del resultado
            if (index == resultSlotIndex) {
                ItemStack itemstack = slot.getItem();
                this.onTake(player, itemstack);
                slot.setByPlayer(ItemStack.EMPTY);
                slot.setChanged();
                this.broadcastChanges();
                return ItemStack.EMPTY;
            }

            // Mover objetos normales del inventario
            ItemStack itemstack1 = slot.getItem();
            ItemStack itemstack = itemstack1.copy();
            if (index != 0 && index != 1) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
            return itemstack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected boolean mayPickup(Player player, boolean hasStack) {
        return hasStack;
    }
}