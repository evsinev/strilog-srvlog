package com.payneteasy.strilog.sender.util;

public class FixDbString {

    public static String fixDbString(String aText, int aMaxBytesLength) {
        if (aText == null || aText.isEmpty()) {
            return aText;
        }

        boolean noSurrogateAndLessThenMaxLength = !hasSurrogate(aText) && aText.length() < (aMaxBytesLength / 2);
        if (noSurrogateAndLessThenMaxLength) {
            return aText;
        }

        return replaceSurrogateAndTrim(aText, aMaxBytesLength);
    }

    private static boolean hasSurrogate(String aText) {
        for (char c : aText.toCharArray()) {
            if (Character.isSurrogate(c)) {
                return true;
            }
        }
        return false;
    }

    private static String replaceSurrogateAndTrim(String aText, int aMaxBytesLength) {
        char[] original = aText.toCharArray();
        char[] trimmed = new char[Math.min(original.length, aMaxBytesLength)];

        int bytesCount = 0;
        int position = 0;

        for (int i = 0; i < original.length; i++) {
            char c           = original[i];
            int  bytesInChar = getBytesInChar(c);

            if (bytesInChar > 3) {
                continue;
            }

            bytesCount += bytesInChar;
            if (bytesCount > aMaxBytesLength) {
                break;
            }

            trimmed[position] = c;
            position++;

        }
        return new String(trimmed, 0, position);
    }

    /* Legal UTF-8 Byte Sequences
     *
     * #    Code Points      Bits   Bit/Byte pattern
     * 1                     7      0xxxxxxx
     *      U+0000..U+007F          00..7F
     *
     * 2                     11     110xxxxx    10xxxxxx
     *      U+0080..U+07FF          C2..DF      80..BF
     *
     * 3                     16     1110xxxx    10xxxxxx    10xxxxxx
     *      U+0800..U+0FFF          E0          A0..BF      80..BF
     *      U+1000..U+FFFF          E1..EF      80..BF      80..BF
     *
     * 4                     21     11110xxx    10xxxxxx    10xxxxxx    10xxxxxx
     *     U+10000..U+3FFFF         F0          90..BF      80..BF      80..BF
     *     U+40000..U+FFFFF         F1..F3      80..BF      80..BF      80..BF
     *    U+100000..U10FFFF         F4          80..8F      80..BF      80..BF
     *
     */
    private static int getBytesInChar(char c) {
        if (Character.isSurrogate(c)) {
            return 4;
        }

        if (c <= '\u007F') {
            return 1;
        }

        if (c <= '\u07FF') {
            return 2;
        }

        if (c <= '\uFFFF') {
            return 3;
        }

        return 4;
    }
}
