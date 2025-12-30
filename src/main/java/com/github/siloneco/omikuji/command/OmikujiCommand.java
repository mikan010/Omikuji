package com.github.siloneco.omikuji.command;

import com.github.siloneco.omikuji.Omikuji;
import com.github.siloneco.omikuji.OmikujiResult;
import com.github.siloneco.omikuji.utility.Args;
import com.github.siloneco.omikuji.utility.Chat;
import com.github.siloneco.omikuji.utility.MessageBridge;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class OmikujiCommand implements CommandExecutor {

    private final Omikuji plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0) {
            if (plugin.getPluginConfig().isAllowCommand()) {
                if (!sender.hasPermission("omikuji.allowdraw")) {
                    sender.sendMessage(Chat.f("{0} &c権限がありません！", plugin.getPluginConfig().getPrefix()));
                    return true;
                }

                if (sender instanceof Player) {
                    plugin.execute((Player) sender);
                }
                return true;
            }

            if (!sender.hasPermission("omikuji.admin")) {
                sender.sendMessage(Chat.f("{0} &c権限がありません！", plugin.getPluginConfig().getPrefix()));
                return true;
            }

            sendHelpMessage(sender, label);
            return true;
        }

        if (Args.check(args, 0, "openWinningInventory")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Chat.f("{0} &cこのコマンドはプレイヤーのみ実行可能です！", plugin.getPluginConfig().getPrefix()));
                return true;
            }

            if (args.length <= 1) {
                sender.sendMessage(Chat.f("{0} &c/{1} {2} <SecretID>", plugin.getPluginConfig().getPrefix(), label, args[0]));
                return true;
            }

            String secretID = args[1];
            Inventory inv = plugin.getWinningInventoryContainer().getInventory(secretID);

            if (inv == null) {
                ((Player) sender).closeInventory();
                sender.sendMessage(Chat.f("{0} &cインベントリを開けません。アイテムを全て受け取ったと思われます。", plugin.getPluginConfig().getPrefix()));
                return true;
            }

            ((Player) sender).openInventory(inv);
        } else if (!sender.hasPermission("omikuji.admin")) {
            return true;
        } else if (Args.check(args, 0, "help", "?")) {
            sendHelpMessage(sender, label);

        } else if (Args.check(args, 0, "reload")) {
            long start = System.currentTimeMillis();
            plugin.reloadConfig();
            sender.sendMessage(Chat.f("{0} &aConfigをリロードしました！ &7({1}ms)", plugin.getPluginConfig().getPrefix(), System.currentTimeMillis() - start));

        } else if (Args.check(args, 0, "info")) {
            List<OmikujiResult> results = plugin.getPluginConfig().getResultContainer().getResults().values()
                    .stream().sorted((Comparator.comparingInt(OmikujiResult::getPriority))).collect(Collectors.toList());

            if (sender instanceof Player) {
                MessageBridge msg = MessageBridge.create().bar().newline();
                for (OmikujiResult result : results) {
                    msg.then(Chat.f("{0}&7({1}) &a{2}% &7- ", result.getDisplayTitle(), result.getId(), result.getPercentage()));
                    if (!result.getItems().isEmpty()) {
                        msg.then(Chat.f("&e[アイテムを表示]")).runCommand("/" + label + " viewItem " + result.getId());
                        msg.then(" ");
                    }
                    msg.then(Chat.f("&b[アイテムを編集]")).suggestCommand("/" + label + " setItem " + result.getId());
                    msg.newline();
                }
                msg.bar().send((Player) sender);
            } else {
                String name = Chat.r(plugin.getDescription().getName());
                String prefix = "[" + name + "] ";
                sender.sendMessage(prefix + "===========================");
                for (OmikujiResult result : results) {
                    String msg = (Chat.f(prefix + "{0}&7({1}) &a{2}% &7- {3}アイテム",
                        result.getDisplayTitle(),
                        result.getId(),
                        result.getPercentage(),
                        result.getItems().size()
                    ));
                    sender.sendMessage(msg);
                }
                sender.sendMessage(prefix + "===========================");
            }

        } else if (Args.check(args, 0, "viewItem", "viewItems")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Chat.f("{0} &cこのコマンドはプレイヤーのみ実行可能です！", plugin.getPluginConfig().getPrefix()));
                return true;
            }
            if (args.length <= 1) {
                sender.sendMessage(Chat.f("{0} &c/{1} {2} <ID>", plugin.getPluginConfig().getPrefix(), label, args[0]));
                return true;
            }
            OmikujiResult result = plugin.getPluginConfig().getResultContainer().getResult(args[1]);
            if (result == null) {
                sender.sendMessage(Chat.f("{0} &e{1} &cというIDのおみくじ結果は登録されていません。", plugin.getPluginConfig().getPrefix(), args[1]));
                return true;
            }

            if (result.getItems().isEmpty()) {
                MessageBridge.create().then(Chat.f("{0} &r{1} &aのアイテムは登録されていません！ ", plugin.getPluginConfig().getPrefix(), result.getDisplayTitle()))
                        .then(Chat.f("&b[クリックで設定]")).suggestCommand(Chat.f("/{0} setItem {1}", label, result.getId()))
                        .send((Player) sender);
                return true;
            }

            int size = 9 * ((int) ((double) result.getItems().size() / 9d) + 1);
            Inventory inv = Bukkit.createInventory(null, size, Chat.f("&eOmikuji Result Item Viewer"));

            for (ItemStack item : result.getItems()) {
                item = item.clone();
                inv.addItem(item);
            }

            ((Player) sender).openInventory(inv);

        } else if (Args.check(args, 0, "setItem", "setItems")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Chat.f("{0} &cこのコマンドはプレイヤーのみ実行可能です！", plugin.getPluginConfig().getPrefix()));
                return true;
            }

            if (args.length <= 1) {
                sender.sendMessage(Chat.f("{0} &c/{1} {2} <ID>", plugin.getPluginConfig().getPrefix(), label, args[0]));
                return true;
            }
            String id = args[1];

            OmikujiResult result = plugin.getPluginConfig().getResultContainer().getResult(id);
            if (result == null) {
                sender.sendMessage(Chat.f("{0} &e{1} &cというIDのおみくじ結果は登録されていません。", plugin.getPluginConfig().getPrefix(), id));
                return true;
            }

            result.openItemEditInventory((Player) sender);
        } else {
            sender.sendMessage(Chat.f("{0} &c不明な引数: &e{1}", plugin.getPluginConfig().getPrefix(), args[0]));
        }
        return true;
    }

    private void sendHelpMessage(CommandSender sender, String label) {
        if (sender instanceof Player) {
            sendPlayerHelpMessage((Player) sender, label);
        } else {
            sendConsoleHelpMessage(sender, label);
        }
    }

    private void sendPlayerHelpMessage(Player p, String label) {
        String name = plugin.getDescription().getName();
        String version = plugin.getDescription().getVersion();
        MessageBridge.create()
                .bar().newline()
                .then(Chat.f("&a{0} version: {1}", name, version)).newline()
                .newline()
                .then(Chat.f("&a/{0} help &7- &bこのメッセージを表示", label)).newline()
                .then(Chat.f("&a/{0} reload &7- &bConfigをリロード", label)).newline()
                .then(Chat.f("&a/{0} info &7- &bおみくじの結果の設定を表示", label)).newline()
                .then(Chat.f("&a/{0} viewItem <ID> &7- &b貰えるアイテムを表示", label)).newline()
                .then(Chat.f("&a/{0} setitem <ID> &7- &b貰えるアイテムを編集", label)).newline()
                .bar()
                .send(p);
    }

    private void sendConsoleHelpMessage(CommandSender sender, String label) {
        String name = plugin.getDescription().getName();
        String prefix = "[" + name + "] ";
        String version = plugin.getDescription().getVersion();

        sender.sendMessage(prefix + name + " version: " + version);
        sender.sendMessage(prefix + "/" + label + " help - このメッセージを表示");
        sender.sendMessage(prefix + "/" + label + " reload - Configをリロード");
        sender.sendMessage(prefix + "/" + label + " info - おみくじの結果の設定を表示");
        sender.sendMessage(prefix + " ======== 以下プレイヤーのみ =========");
        sender.sendMessage(prefix + "/" + label + " viewItem <ID> - 貰えるアイテムを表示");
        sender.sendMessage(prefix + "/" + label + " setItem <ID> - 貰えるアイテムを編集");
    }
}
