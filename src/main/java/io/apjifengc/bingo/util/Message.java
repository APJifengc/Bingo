package io.apjifengc.bingo.util;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.game.BingoPlayer;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Bingo message constants.
 *
 * @author Milkory
 */
public class Message {

    private static final Bingo plugin = Bingo.getInstance();
    private static final Logger logger = plugin.getLogger();

    @Getter private static YamlConfiguration config;

    public static void load() {
        extractLanguageFiles();
        var lang = Config.getMain().get("lang");
        config = Config.loadConfig(String.format("language/%s.yml", lang));
    }

    public static String get(String path, Object... args) {
        String src = config.getString(path);
        if (src == null) {
            return "§c§l[Unknown Message]";
        }
        for (int i = 0; i < args.length; i++) {
            src = src.replace("{" + i + "}", args[i].toString());
        }
        return src.replaceAll("\\\\{0}&(.)", "§$1");
    }

    // TODO: Needs cleaning up.
    public static BaseComponent[] getRaw(String path, Object... args) {
        String src = config.getString(path);
        if (src == null) {
            return TextComponent.fromLegacyText("§c§l[Unknown Message]");
        }
        var stream = Stream.<Object>of(src);
        for (int i = 0; i < args.length; i++) {
            var arg = args[i];
            var replace = String.format("{%d}", i);
            stream = stream.map(it -> {
                if (!(it instanceof String)) return it;
                var split = ((String) it).split(replace);
                var list = new ArrayList<>();
                list.add(split[0]);
                for (int j = 1; j < split.length; j++) {
                    list.add(arg);
                    list.add(split[j]);
                }
                return list;
            }).flatMap(it -> {
                if (it instanceof List) return ((List<?>) it).stream();
                else return Stream.of(it);
            });
        }
        var list = stream.map(it -> {
            if (!(it instanceof BaseComponent)) return new TextComponent(it.toString());
            else return (BaseComponent) it;
        }).collect(Collectors.toList());
        return translateColor(list).toArray(new BaseComponent[0]);
    }

    private static String translateColor(String str) {
        return str.replaceAll("\\\\{0}&(.)", "§$1");
    }

    public static void extractLanguageFiles() {
        plugin.saveResource("language/en_US.yml");
        plugin.saveResource("language/zh_CN.yml");
    }

    private static List<BaseComponent> translateColor(List<BaseComponent> list) {
        list.forEach(it -> {
            if (it instanceof TextComponent) {
                var iti = (TextComponent) it;
                iti.setText(translateColor(iti.getText()));
            }
        });
        return list;
    }

    /** Convert a unicode to chinese characters. */
    public static String unicodeToCn(String src) {
        StringBuilder sb = new StringBuilder(src);
        while (true) {
            Matcher m = Pattern.compile("\\\\u[a-z,A-Z,0-9]{4}").matcher(sb.toString());
            if (!m.find()) {
                return sb.toString();
            }
            sb.replace(m.start(), m.end(), String
                    .valueOf((char) Integer.valueOf(sb.substring(m.start() + 2, m.end()), 16).intValue()));
        }
    }

    public static void sendTo(Collection<BingoPlayer> bingoPlayer, String msg) {
        bingoPlayer.forEach(it -> it.getPlayer().sendMessage(msg));
    }

    public static void warning(String path, Object... args) {
        logger.warning(get(path, args));
    }

}
