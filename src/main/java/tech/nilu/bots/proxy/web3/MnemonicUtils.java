//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package tech.nilu.bots.proxy.web3;

import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.web3j.crypto.Hash;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MnemonicUtils {
    private static final int SEED_ITERATIONS = 2048;
    private static final int SEED_KEY_SIZE = 512;
    private static final Map<Bip39Locale, List<String>> WORD_LIST = new HashMap<>();

    static {
        for (Bip39Locale l : Bip39Locale.values())
            WORD_LIST.put(l, populateWordList(l.getLocale()));
    }

    public MnemonicUtils() {
    }

    public static String generateMnemonic(byte[] initialEntropy) {
        return generateMnemonic(Bip39Locale.ENGLISH, initialEntropy);
    }

    public static String generateMnemonic(Bip39Locale lang, byte[] initialEntropy) {
        validateInitialEntropy(initialEntropy);
        int ent = initialEntropy.length * 8;
        int checksumLength = ent / 32;
        byte checksum = calculateChecksum(initialEntropy);
        boolean[] bits = convertToBits(initialEntropy, checksum);
        int iterations = (ent + checksumLength) / 11;
        StringBuilder mnemonicBuilder = new StringBuilder();

        for (int i = 0; i < iterations; ++i) {
            int index = toInt(nextElevenBits(bits, i));
            mnemonicBuilder.append(WORD_LIST.get(lang).get(index));
            boolean notLastIteration = i < iterations - 1;
            if (notLastIteration) {
                mnemonicBuilder.append(" ");
            }
        }

        return mnemonicBuilder.toString();
    }

    public static byte[] generateSeed(String mnemonic, String passphrase) {
        validateMnemonic(mnemonic);
        passphrase = passphrase == null ? "" : passphrase;
        String salt = String.format("mnemonic%s", passphrase);
        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA512Digest());
        gen.init(mnemonic.getBytes(StandardCharsets.UTF_8), salt.getBytes(StandardCharsets.UTF_8), 2048);
        return ((KeyParameter) gen.generateDerivedParameters(512)).getKey();
    }

    private static void validateMnemonic(String mnemonic) {
        if (mnemonic == null || mnemonic.trim().isEmpty()) {
            throw new IllegalArgumentException("Mnemonic is required to generate a seed");
        }
    }

    private static boolean[] nextElevenBits(boolean[] bits, int i) {
        int from = i * 11;
        int to = from + 11;
        return Arrays.copyOfRange(bits, from, to);
    }

    private static void validateInitialEntropy(byte[] initialEntropy) {
        if (initialEntropy == null) {
            throw new IllegalArgumentException("Initial entropy is required");
        } else {
            int ent = initialEntropy.length * 8;
            if (ent < 128 || ent > 256 || ent % 32 != 0) {
                throw new IllegalArgumentException("The allowed size of ENT is 128-256 bits of multiples of 32");
            }
        }
    }

    private static boolean[] convertToBits(byte[] initialEntropy, byte checksum) {
        int ent = initialEntropy.length * 8;
        int checksumLength = ent / 32;
        int totalLength = ent + checksumLength;
        boolean[] bits = new boolean[totalLength];

        int i;
        for (i = 0; i < initialEntropy.length; ++i) {
            for (int j = 0; j < 8; ++j) {
                byte b = initialEntropy[i];
                bits[8 * i + j] = toBit(b, j);
            }
        }

        for (i = 0; i < checksumLength; ++i) {
            bits[ent + i] = toBit(checksum, i);
        }

        return bits;
    }

    private static boolean toBit(byte value, int index) {
        return ((value >>> (7 - index)) & 1) > 0;
    }

    private static int toInt(boolean[] bits) {
        int value = 0;

        for (int i = 0; i < bits.length; ++i) {
            boolean isSet = bits[i];
            if (isSet) {
                value += 1 << bits.length - i - 1;
            }
        }

        return value;
    }

    private static byte calculateChecksum(byte[] initialEntropy) {
        int ent = initialEntropy.length * 8;
        byte mask = (byte) (255 << 8 - ent / 32);
        byte[] bytes = Hash.sha256(initialEntropy);
        return (byte) (bytes[0] & mask);
    }

    private static List<String> populateWordList(String lang) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(lang + "-mnemonic-word-list.txt");

        try {
            return Files.readAllLines(Paths.get(url.toURI()));
        } catch (Exception var2) {
            return Collections.emptyList();
        }
    }
}
