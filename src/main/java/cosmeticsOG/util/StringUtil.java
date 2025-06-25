package cosmeticsOG.util;

import cosmeticsOG.Utils;
import cosmeticsOG.managers.SettingsManager;
import cosmeticsOG.particles.Hat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;

public class StringUtil {

    private static final String[] chars = {
        "\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|", "<", ">", "-", "&", "%"
    };
    private static final TreeMap<Integer, String> romanNumerals = new TreeMap<>();
    private static final String sanitizeRegex = "[\\@\\^\\$\\{\\}\\[\\]\\(\\)\\.\\,\\*\\+\\?\\|\\<\\>\\-\\&\\%\\#]";

    private static final String newLineCharacter = "/n";
    private static final Map<String, Pattern> patternCache = new HashMap<>();

    static {
        romanNumerals.put(40, "XL");
        romanNumerals.put(10, "X");
        romanNumerals.put(9, "IX");
        romanNumerals.put(5, "V");
        romanNumerals.put(4, "IV");
        romanNumerals.put(1, "I");
    }

    public static String getColorValue(String string) {
        return string.replaceAll("&", "&");
    }

    /**
     * Returns a list of translated strings
     * @param list
     * @return
     */
    public static List<String> getColorValue(List<String> list) {
        List<String> result = new ArrayList<>();
        for (String s : list) {
            result.add(s.replaceAll("&", "&"));
        }
        return result;
    }

    /**
     * Trims out any characters that aren't numbers
     * @param s
     * @return
     */
    public static String getInteger(String s) {
        int i = 0;
        while (i < s.length() && !Character.isDigit(s.charAt(i))) i++;
        int j = i;
        while (j < s.length() && Character.isDigit(s.charAt(j))) j++;
        return s.substring(i, j).trim();
    }

    /**
     * Returns the integer value of the given string, or a default value if the given string is not an integer
     * @param s
     * @param defaultValue
     * @return
     */
    public static int toInt(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Returns a string with every special character escaped
     * @param s
     * @return
     */
    public static String escapeSpecialCharacters(String s) {
        for (String c : chars) {
            if (s.contains(c)) {
                s = s.replace(c, "\\" + c);
            }
        }
        return s;
    }

    /**
     * Returns a string with the first letter capitalized
     * @param s
     * @return
     */
    public static String capitalizeFirstLetter(String s) {
        String original = s.replaceAll("_", " ");
        String[] words = original.split(" ");
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            builder.append(Character.toUpperCase(words[i].charAt(0)));
            builder.append(words[i].substring(1));

            if (i < words.length - 1) {
                builder.append(' ');
            }
        }

        return builder.toString();
    }

    public static String getMaterialName(Material material) {
        return capitalizeFirstLetter(material.toString().toLowerCase());
    }

    /**
     * Replaces any &'s with their respective TextComponent using MiniMessage
     * @param list
     * @return
     */
    public static List<TextComponent> colorize(List<String> list) {
        List<TextComponent> result = new ArrayList<>();
        for (String s : list) {
            result.add(Utils.legacySerializerAnyCase(s));
        }
        return result;
    }

    /**
     * Compares 2 server versions
     * @param version1
     * @param version2
     * @return
     */
    public static int compareServerVersions(String version1, String version2) {
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");
        int length = Math.max(v1.length, v2.length);

        for (int i = 0; i < length; i++) {
            Integer ver1 = i < v1.length ? Integer.parseInt(v1[i]) : 0;
            Integer ver2 = i < v2.length ? Integer.parseInt(v2[i]) : 0;
            int result = ver1.compareTo(ver2);

            if (result != 0) {
                return result;
            }
        }

        return 0;
    }

    /**
     * Returns the Roman Numeral value of this number
     * @param number
     * @return
     */
    public static final String toRomanNumeral(int number) {
        if (number <= 0) {
            return "";
        }

        int n = romanNumerals.floorKey(number);
        if (number == n) {
            return romanNumerals.get(number);
        }
        return romanNumerals.get(n) + toRomanNumeral(number - n);
    }

    /**
     * Checks to see if the given String exists in this list
     * @param string
     * @param list
     * @return
     */
    public static boolean stringContainsFromList(String string, List<String> list) {
        for (String s : list) {
            if (string.contains(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Splits a String at '/n'
     * @param description
     * @return A List of Components with color code translation.
     */
    public static List<Component> parseDescription(String description) {

        List<Component> desc = new ArrayList<>();
        String[] values = description.split(newLineCharacter);

        for (String s : values) {
            desc.add(Utils.legacySerializerAnyCase(s));
        }

        return desc;
    }

    public static List<TextComponent> parseDescription(Hat hat, List<String> description) {
        List<TextComponent> desc = new ArrayList<>();
        for (String line : description) {
            desc.add(Utils.legacySerializerAnyCase(parseString(line, hat)));
        }
        return desc;
    }

    public static String parseString(String string, Hat hat) {
        return string.replace("{TYPE}", hat.getType().getStrippedName())
                .replace("{LOCATION}", hat.getLocation().getStrippedName())
                .replace("{MODE}", hat.getMode().getStrippedName())
                .replace("{PRICE}", Integer.toString(hat.getPrice()))
                .replace("{CURRENCY}", SettingsManager.CURRENCY.getString())
                .replace("<locked>", "")
                .replace("</locked>", "");
    }

    public static String parseRegex(String string, String regex) {
        String begin = "<%>".replace("%", regex);
        String end = "</%>".replace("%", regex);

        if (string.contains(begin) && string.contains(end)) {
            String r = "(?<=<%>).*?(?=</%>)".replaceAll("%", regex);
            return string.replaceAll(r, "");
        }
        return string;
    }

    public static String getParseValue(String description, String regex) {
        return parseValue(description, regex)[1];
    }

    /**
     * Parses a string for any {#=}'s and returns {#=} and its contents in an array<br>
     * <b>parseValue("Hello {1=World}", "1") -> [0]{1=World} [1]World</b>
     * @param string
     * @param regex
     * @return
     */
    public static String[] parseValue(String string, String regex) {
        Pattern pattern;
        if (patternCache.containsKey(regex)) {
            pattern = patternCache.get(regex);
        } else {
            pattern = Pattern.compile("\\{" + regex + "=(.*?)\\}");
            patternCache.put(regex, pattern);
        }

        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            return new String[] {matcher.group(0), matcher.group(1)};
        }
        return new String[] {"", ""};
    }

    /**
     * Returns a compiled pattern from this regex
     * @param regex
     * @return
     */
    public static Pattern getPattern(String regex) {
        if (patternCache.containsKey(regex)) {
            return patternCache.get(regex);
        }

        Pattern pattern = Pattern.compile(regex);
        patternCache.put(regex, pattern);

        return pattern;
    }

    public static List<TextComponent> translateFormatting(List<String> list, String regex) {
        List<TextComponent> formattedList = new ArrayList<>();
        for (String s : list) {
            String formatted = s.replaceAll("(?<=<" + regex + ">).*?(?=</" + regex + ">)", "");
            if (Utils.stripColors(formatted).length() == 0) {
                continue;
            }
            formattedList.add(Utils.legacySerializerAnyCase(formatted));
        }
        return formattedList;
    }

    /**
     * Get the time formatted as mm:ss from an int
     * @param time
     * @return
     */
    public static String getTimeFormat(int time) {
        int remainder = time % 3600; // get the rest in seconds
        int minutes = remainder / 60; // get the amount of minutes from the rest
        int seconds = remainder % 60; // get the new rest
        String disMinu = (minutes < 10 ? "0" : "") + minutes; // get minutes and add "0" before if lower than 10
        String disSec = (seconds < 10 ? "0" : "") + seconds; // get seconds and add "0" before if lower than 10

        return disMinu + ":" + disSec; // get the whole time
    }

    public static String sanitizeString(String s) {
        return s.replaceAll(sanitizeRegex, "");
    }

    /**
     * Attempts to interpret the boolean value of the argument
     * Acceptable strings include... (yes, no, on, off, 1, 0, true, false)
     * @param arg
     * @return
     */
    public static boolean getToggleValue(String arg) {
        switch (arg.toLowerCase()) {
            case "yes":
            case "on":
            case "1":
            case "true":
                return true;
            case "no":
            case "off":
            case "0":
            case "false":
                return false;
            default:
                return false;
        }
    }
}
