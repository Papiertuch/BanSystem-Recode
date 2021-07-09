package de.papiertuch.proxy.commands.mute;

import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.database.interfaces.IDataBase;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.bson.Document;

import java.util.ArrayList;
import java.util.UUID;

public class MuteHistoryCommand extends Command {

    public MuteHistoryCommand() {
        super("muteHistory");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(BanSystem.getInstance().getMessages().getString("messages.console"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission(BanSystem.getInstance().getConfig().getString("permissions.muteHistoryCommand"))) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.noPerms"));
            return;
        }
        IBanPlayer banPlayer = BanSystem.getInstance().getBanPlayer(player.getUniqueId());
        if (!BanSystem.getInstance().getNotify().contains(banPlayer)) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.notLogin"));
            return;
        }
        if (args.length == 1) {
            String name = args[0];
            UUID uuid = BanSystem.getInstance().getUuidFetcher().getUUID(name);
            IDataBase dataBase = BanSystem.getInstance().getMuteHandler().getDataBase();
            ArrayList<Document> documents = dataBase.getHistory(uuid);
            if (documents.isEmpty()) {
                player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.neverMuted"));
                return;
            }
            player.sendMessage(BanSystem.getInstance().getMessages().getListAsString("messages.muteHistory")
                    .replace("%name%", name)
                    .replace("%amount%", String.valueOf(documents.size()))
                    .replace("%duration%", (dataBase.isBanned(uuid) ?
                            BanSystem.getInstance().getDateFormat().format(dataBase.getDuration(uuid)) : "/"))
                    .replace("%muted%", (dataBase.isBanned(uuid) ? "§a✔" : "§c✖"))
                    .replace("%points%", String.valueOf(dataBase.getBanPoints(uuid))));

            for (Document document : documents) {
                player.sendMessage(BanSystem.getInstance().getMessages().getListAsString("messages.historyFormat")
                        .replace("%date%", document.getString("date"))
                        .replace("%operator%", document.getString("user"))
                        .replace("%reason%", document.getString("reason")));

                if (!document.getString("info").equalsIgnoreCase(""))
                    player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.historyInfo")
                            .replace("%info%", document.getString("info").replace("-", " ")));

                if (!document.getString("reduce").equalsIgnoreCase(""))
                    player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.historyReduce")
                            .replace("%reduce%", document.getString("reduce").replace("-", " ")));

                if (!document.getString("unban").equalsIgnoreCase(""))
                    player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.historyUnmute")
                            .replace("%unmute%", document.getString("unban").replace("-", " ")));

                player.sendMessage("");
            }
        } else {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.muteHistorySyntax"));
        }
    }
}
