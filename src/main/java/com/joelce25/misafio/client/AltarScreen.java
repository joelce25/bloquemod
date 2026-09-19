package com.joelce25.misafio.client;

import com.joelce25.misafio.block.AltarMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class AltarScreen extends ItemCombinerScreen<AltarMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("misafio", "textures/gui/altar_menu.png");
    private EditBox nameField;

    public AltarScreen(AltarMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, TEXTURE);
        this.titleLabelX = 60;
        this.titleLabelY = 6;
    }

    @Override
    protected void subInit() {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        this.nameField = new EditBox(this.font, i + 62, j + 24, 103, 12, Component.literal("Nombre del Jugador"));
        this.nameField.setCanLoseFocus(false);
        this.nameField.setTextColor(-1);
        this.nameField.setTextColorUneditable(-1);
        this.nameField.setBordered(false);
        this.nameField.setMaxLength(50);
        this.nameField.setResponder(this::onNameChanged);
        this.nameField.setValue("");
        this.addWidget(this.nameField);
        this.setInitialFocus(this.nameField);
        this.nameField.setEditable(true);
    }

    private void onNameChanged(String newName) {
        this.menu.setItemName(newName);
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.connection.send(new ServerboundRenameItemPacket(newName));
        }
    }

    @Override
    protected void renderErrorIcon(GuiGraphics guiGraphics, int x, int y) {
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.closeContainer();
            }
            return true;
        }
        if (this.nameField != null && (this.nameField.keyPressed(keyCode, scanCode, modifiers) || this.nameField.canConsumeInput())) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderFg(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderFg(guiGraphics, mouseX, mouseY, partialTick);
        if (this.nameField != null) {
            this.nameField.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void slotChanged(AbstractContainerMenu container, int slot, ItemStack stack) {
        if (slot == 0 && this.nameField != null) {
            this.nameField.setValue(this.menu.getItemName());
            this.nameField.setEditable(!stack.isEmpty());
            this.setFocused(this.nameField);
        }
    }
}