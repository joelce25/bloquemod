package com.joelce25.misafio;

import com.joelce25.misafio.block.AltarMenu;
import com.joelce25.misafio.block.ModBlocks;
import com.joelce25.misafio.client.AltarScreen;
import com.joelce25.misafio.command.RevivirCommand;
import com.joelce25.misafio.menu.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Misafio.MOD_ID)
public class Misafio {
    public static final String MOD_ID = "misafio";

    public Misafio() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        modEventBus.addListener(this::onClientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.<AltarMenu, AltarScreen>register(
                    ModMenuTypes.ALTAR_MENU.get(),
                    AltarScreen::new
            );
        });
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        RevivirCommand.register(event.getDispatcher());
    }
}