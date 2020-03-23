package github.apjifengc.bingo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {

	public static String get(String path, Object... args) {

		String src = Configs.getMsgCfg().getString(path);
		if (src == null) {
			return "§c§l[Unknown Message]";
		}
		for (int i = 0; i < args.length; i++) {
			src = src.replace("%" + i + "$s", args[i].toString());
		}
		return src.replaceAll("\\\\{0}&(.)", "§$1");
	}

	/**
	 * 转换一串字符串中的 Unicode 字符。
	 * 
	 * @param src 源字符串
	 * @return 转换后的字符串
	 */
	public static String unicodeToCn(String src) {
		StringBuilder sb = new StringBuilder(src);
		while (true) {
			Matcher m = Pattern.compile("\\\\u[a-z,A-Z,0-9]{4}").matcher(sb.toString());
			if (!m.find()) {
				return sb.toString();
			}
			sb.replace(m.start(), m.end(), String
					.valueOf((char) Integer.valueOf(sb.toString().substring(m.start() + 2, m.end()), 16).intValue()));
		}
	}

}
