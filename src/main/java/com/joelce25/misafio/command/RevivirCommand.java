package com.joelce25.misafio.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanList;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class RevivirCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("misafio")
                .then(Commands.literal("revivir")
                        .then(Commands.argument("jugador", StringArgumentType.word())
                                .executes(RevivirCommand::ejecutarResurreccion))));
    }

    private static int ejecutarResurreccion(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer jugadorEjecutor = source.getPlayerOrException();
        String nombreAmigo = StringArgumentType.getString(context, "jugador");

        ResourceLocation idCuchara = new ResourceLocation("dedsafio3", "cuchara_r");
        Item itemCuchara = ForgeRegistries.ITEMS.getValue(idCuchara);

        if (itemCuchara != null && jugadorEjecutor.getMainHandItem().getItem() == itemCuchara) {
            UserBanList banList = source.getServer().getPlayerList().getBans();
            Optional<GameProfile> perfilBaneado = source.getServer().getProfileCache().get(nombreAmigo);

            if (perfilBaneado.isPresent() && banList.isBanned(perfilBaneado.get())) {
                banList.remove(perfilBaneado.get());
                jugadorEjecutor.getMainHandItem().shrink(1);

                source.getServer().getPlayerList().broadcastSystemMessage(
                        Component.literal("§d¡" + jugadorEjecutor.getScoreboardName() + " ha usado una Cuchara de Resurrección para revivir a " + nombreAmigo + "!"),
                        false
                );
                return 1;
            } else {
                source.sendFailure(Component.literal("El jugador '" + nombreAmigo + "' no está baneado o no existe."));
                return 0;
            }
        } else {
            source.sendFailure(Component.literal("¡Debes sostener la Cuchara de Resurrección en la mano principal para revivir a alguien!"));
            return 0;
        }
    }
}