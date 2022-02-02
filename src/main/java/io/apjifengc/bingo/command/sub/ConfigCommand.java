package io.apjifengc.bingo.command.sub;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.command.SubCommand;
import io.apjifengc.bingo.util.Config;
import io.apjifengc.bingo.util.Message;
import io.apjifengc.bingo.util.TaskUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigCommand extends SubCommand {

    private final Bingo plugin = Bingo.getInstance();

    @Override
    public void run(CommandSender sender, String[] args) {
        if (sender.hasPermission("bingo.admin.config")) {
            if (args.length == 1) {
                sender.sendMessage(Message.get("title-text") + Message.get("commands.config.available-configs"));
            } else {
                if ("startkits".equals(args[1])) {
                    if (args.length == 2) {
                        sender.sendMessage(Message.get("title-text") + Message.get("commands.config.startkits.usage"));
                    } else {
                        if ("add".equals(args[2])) {
                            if (sender instanceof Player) {
                                Player player = (Player) sender;
                                ItemStack itemStack = player.getInventory().getItemInMainHand();
                                List<ItemStack> list = Config.getStartkits();
                                list.add(itemStack);
                                Config.getMain().set("game.startkits", list);
                                try {
                                    Config.getMain().save(plugin.saveResource("config.yml"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    sender.spigot().sendMessage(Message.getComponents(
                                            "commands.config.startkits.add",
                                            TaskUtil.getItemComponent(itemStack, new BaseComponent[] {
                                                    TaskUtil.getItemName(itemStack),
                                                    new TextComponent(" x " + itemStack.getAmount())
                                            }),
                                            new TextComponent(String.valueOf(list.size() - 1))
                                    ));
                                }
                            } else {
                                Message.sendToWithPrefix(sender, "commands.no-console");
                            }
                        } else if ("remove".equals(args[2])) {
                            if (args.length == 5) {
                                List<ItemStack> list = Config.getStartkits();
                                if (Integer.parseInt(args[3]) < 0 || Integer.parseInt(args[3]) >= list.size()) {
                                    Message.sendToWithPrefix(sender, "commands.config.startkits.remove.not-exist");
                                } else {
                                    list.remove(Integer.parseInt(args[3]));
                                    Config.getMain().set("game.startkits", list);
                                    try {
                                        Config.getMain().save(plugin.saveResource("config.yml"));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Message.sendToWithPrefix(sender, "commands.config.startkits.remove.success");
                                }
                            }
                        } else if ("list".equals(args[2])) {
                            var list = Config.getMain().getList("game.startkits");
                            if (list == null) list = new ArrayList<>();
                            int index = 0;
                            sender.sendMessage(Message.get("title-text"));
                            for (Object o : list) {
                                if (o instanceof ItemStack) {
                                    ItemStack item = (ItemStack) o;
                                    sender.spigot().sendMessage(Message.getComponents(
                                            "commands.config.startkits.list",
                                            new TextComponent(String.valueOf(index++)),
                                            TaskUtil.getItemComponent(item, new BaseComponent[] {
                                                    TaskUtil.getItemName(item),
                                                    new TextComponent(" x " + item.getAmount())
                                            })
                                    ));
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Message.sendToWithPrefix(sender, "commands.no-permission");
        }
    }
}
