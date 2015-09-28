package util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

import models.Server;

import org.ocpsoft.prettytime.PrettyTime;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import play.Configuration;

public class Util {
    public static void configure(Configuration configuration) {
        DEFAULT_SUPPORT = configuration.getString("supporturl");
        DOMAIN_NAME = configuration.getString("domainname");
        HOST_DOMAIN_NAME = configuration.getString("hostdomainname");
    }

    public static String formatAsRelativeTime(Date input) {
        return PRETTY_TIME.format(input);
    }

    public static String generateRandomPassword() {
        StringBuilder builder = new StringBuilder();
        SECURE_RANDOM.ints(0, PASSWORD_CHARS.length).limit(12).map((i) -> PASSWORD_CHARS[i])
        .forEach((c) -> builder.append(c));
        return builder.toString();
    }

    public static String getFormFieldId(String label) {
        return PUNCT.matcher(label.toLowerCase()).replaceAll("") + "_id";
    }

    public static String getSupportURL(Server server) {
        return "http://" + server.name + "." + DOMAIN_NAME;
    }

    public static boolean isValidPassword(String password) {
        return password.length() > 6 && PASSWORD_MATCHER.matcher(password).matches();
    }

    public static String renderMarkdown(String input) {
        return PEGDOWN.markdownToHtml(input);
    }

    public static String DEFAULT_SUPPORT = "http://global.ticketeer.net";
    public static String DOMAIN_NAME = "ticketeer.net";
    public static String HOST_DOMAIN_NAME = "http://ticketeer.net";
    public static final char[] PASSWORD_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
            .toCharArray();
    private static Pattern PASSWORD_MATCHER = Pattern
            .compile("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890']+");
    private static PegDownProcessor PEGDOWN = new PegDownProcessor(Extensions.SUPPRESS_ALL_HTML | Extensions.AUTOLINKS
            | Extensions.FENCED_CODE_BLOCKS | Extensions.TABLES | Extensions.STRIKETHROUGH);
    private static PrettyTime PRETTY_TIME = new PrettyTime(Locale.getDefault());
    public static String PROFECTUS_BLUE_HEX = "#2AC3F0";
    public static String PROFECTUS_RED_HEX = "#ED4C26";
    public static String PROFECTUS_YELLOW_HEX = "#E5C401";
    private static Pattern PUNCT = Pattern.compile("\\p{Punct}");
    private static Random SECURE_RANDOM;
    static {
        try {
            SECURE_RANDOM = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            try {
                SECURE_RANDOM = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException e1) {
                SECURE_RANDOM = new Random();
                e1.printStackTrace();
            }
        }
    }
}
