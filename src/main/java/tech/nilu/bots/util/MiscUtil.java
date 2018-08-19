package tech.nilu.bots.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiscUtil {
    private final static Pattern ADDRESS_PATTERN = Pattern.compile("0x[a-fA-F0-9]{40}");
    public static String lookupNiluId(String text) {
        Matcher matcher = ADDRESS_PATTERN.matcher(text);
        String ret = matcher.find() ? matcher.group() : null;
        return ret;
    }

}
