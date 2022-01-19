package io.apjifengc.bingo.command;

import org.bukkit.command.CommandSender;

/**
 * @author Milkory
 */
public abstract class SubCommand {
    public abstract void run(CommandSender sender, String[] args);
}
