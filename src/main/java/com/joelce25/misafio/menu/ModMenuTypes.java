package com.joelce25.misafio.menu;

import com.joelce25.misafio.Misafio;
import com.joelce25.misafio.block.AltarMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Misafio.MOD_ID);

    public static final RegistryObject<MenuType<AltarMenu>> ALTAR_MENU =
            MENUS.register("altar_menu", () -> IForgeMenuType.create(AltarMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}