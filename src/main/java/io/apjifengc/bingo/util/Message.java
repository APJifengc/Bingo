package io.apjifengc.bingo.util;

import io.apjifengc.bingo.Bingo;
import io.apjifengc.bingo.api.game.BingoPlayer;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
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

    public static final String ERROR_STRING = "§c§l[ERROR]";
    public static final BaseComponent[] ERROR_COMPONENT = TextComponent.fromLegacyText(ERROR_STRING);

    public static void load() {
        extractLanguageFiles();
        var lang = Config.getMain().get("lang");
        config = Config.loadConfig(String.format("language/%s.yml", lang));
    }

    public static String get(String path, Object... args) {
        String src = config.getString(path);
        if (src == null) return ERROR_STRING;
        for (int i = 0; i < args.length; i++) {
            src = src.replace("{" + i + "}", args[i].toString());
        }
        return src.replaceAll("\\\\{0}&(.)", "§$1")
                .replace("\\n", "\n");
    }

    public static List<BaseComponent[]> getWrapComponents(String path, Object... args) {
        var list = new ArrayList<BaseComponent[]>();
        for (String section : config.getString(path).split("(\\n|\\\\n)")) {
            list.add(serveComponents(section, args));
        }
        return list;
    }

    public static BaseComponent[] getComponents(String path, Object... args) {
        return serveComponents(config.getString(path), args);
    }

    // TODO: really needs cleaning up.
    private static BaseComponent[] serveComponents(String src, Object... args) {
        if (src == null) return ERROR_COMPONENT;
        var stream = Stream.<Object>of(translateColor(src));
        for (int i = 0; i < args.length; i++) {
            var arg = args[i];
            var replace = String.format("\\{%d\\}", i);
            stream = stream.map(it -> {
                if (!(it instanceof String)) return it;
                var split = ((String) it).split(replace, -2);
                var list = new ArrayList<>();
                list.add(split[0]);
                for (int j = 1; j < split.length; j++) {
                    list.add(arg);
                    list.add(split[j]);
                }
                return list;
            }).flatMap(it -> {
                if (it instanceof Collection) return ((Collection<?>) it).stream();
                else if (it.getClass().isArray()) return Stream.of((Object[]) it);
                else return Stream.of(it);
            });
        }

        // collect & style process
        var list = stream.map(it -> {
            if (it instanceof BaseComponent) return new BaseComponent[]{(BaseComponent) it};
            else if (it.getClass().isArray() && it.getClass().getComponentType() == BaseComponent.class)
                return (BaseComponent[]) it;
            else return TextComponent.fromLegacyText(it.toString());
        }).flatMap(Stream::of).collect(Collectors.toList());

        list.get(0).setItalic(false);
        for (int i = 0; i < list.size(); i++) {
            var element = list.get(i);
            if (i - 1 >= 0) {
                element.copyFormatting(list.get(i - 1), ComponentBuilder.FormatRetention.FORMATTING, false);
            }
        }
        return list.toArray(new BaseComponent[0]);
    }

    public static String getRaw(String path, Object... args) {
        return ComponentSerializer.toString(getComponents(path, args));
    }

    public static List<String> getWrapRaw(String path, Object... args) {
        return getWrapComponents(path, args).stream().map(
                ComponentSerializer::toString
        ).collect(Collectors.toList());
    }

    private static String translateColor(String str) {
        return str.replaceAll("\\\\{0}&(.)", "§$1");
    }

    public static void extractLanguageFiles() {
        plugin.saveResource("language/en_US.yml");
        plugin.saveResource("language/zh_CN.yml");
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
