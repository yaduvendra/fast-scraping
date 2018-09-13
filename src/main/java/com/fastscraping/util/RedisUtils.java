package com.fastscraping.util;

import java.util.HashMap;
import java.util.Map;

public class RedisUtils {

    private static final String staticEncodeDecodeLock = "staticEncodeDecodeLock";

    private static final Map<Character, String> encodeMap = new HashMap<>();
    private static final Map<String, Character> decodeMap = new HashMap<>();

    static {
        encodeMap.put('0', "zero");
        encodeMap.put('1', "one");
        encodeMap.put('2', "two");
        encodeMap.put('3', "three");
        encodeMap.put('4', "four");
        encodeMap.put('5', "five");
        encodeMap.put('6', "six");
        encodeMap.put('7', "seven");
        encodeMap.put('8', "eight");
        encodeMap.put('9', "nine");

        decodeMap.put("zero", '0');
        decodeMap.put("one", '1');
        decodeMap.put("two", '2');
        decodeMap.put("three", '3');
        decodeMap.put("four", '4');
        decodeMap.put("five", '5');
        decodeMap.put("six", '6');
        decodeMap.put("seven", '7');
        decodeMap.put("eight", '8');
        decodeMap.put("nine", '9');
    }

    public static String encodeRedisKey(final String key) {
        synchronized (staticEncodeDecodeLock) {

            final char[] charactersInKey = key.toCharArray();
            int keyLength = charactersInKey.length;

            StringBuffer encodedString = new StringBuffer();
            int index = 0;

            for (; index < keyLength; index++) {
                if (encodeMap.containsKey(charactersInKey[index])) {
                    encodedString.append("~" + encodeMap.get(charactersInKey[index]));
                } else {
                    encodedString.append("~" + charactersInKey[index]);
                }
            }

            return encodedString.toString();
        }
    }

    static String decodeRedisKey(final String key) {
        synchronized (staticEncodeDecodeLock) {
            String[] splittedKey = key.split("~");
            int keyLength = key.length();

            StringBuffer decodedString = new StringBuffer();
            int index = 0;

            for (; index < keyLength; index++) {
                if (decodeMap.containsKey(splittedKey[index])) {
                    decodedString.append(decodeMap.get(splittedKey[index]));
                } else {
                    decodedString.append(splittedKey[index]);
                }
            }

            return decodedString.toString();
        }
    }

}
