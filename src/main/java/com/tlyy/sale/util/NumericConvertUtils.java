package com.tlyy.sale.util;

/**
 * @author LeiDongxing
 * created on 2021/6/21
 */
public class NumericConvertUtils {
    /**
     * 在进制表示中的字符集合，0-Z分别用于表示最大为62进制的符号表示
     */
    private static final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
            'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
            't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
            'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private static final char[] digits26 = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    private static final char[] digits32 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
            'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W',
            'X', 'Y', 'Z'};

    private static final char[] digits52 = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C',
            'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
            'V', 'W', 'X', 'Y', 'Z'};

    private static final char[] digits62 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
            'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};


    //62个打乱的字符元素
    private final static char[] randomDigits62 = new char[]{'J', 'P', 'O', 'n', 'i', 'm', '6', 'M', 'o',
            'N', 'g', 'e', 'j', '8', 'U', 'x', 'w', 'C', 'S', 'G', '7', 'Y', 'Z', 'L', 'd', 'a', 'A', 'z',
            'I', 'B', '4', 'l', 's', 'K', 'W', 'X', 'v', '1', 'k', 'H', 'E', 'r', '3', 'Q', 'R', 'b', 'h',
            '0', 'p', 'q', '5', 'V', 'u', 'c', 'f', 'F', 't', 'y', '9', '2', 'T', 'D'};

    public static String toOtherNumberSystem(long number, int seed) {
        if (number < 0) {
            number = ((long) 2 * 0x7fffffff) + number + 2;
        }
        char[] buf = new char[32];
        int charPos = 32;
        while ((number / seed) > 0) {
            buf[--charPos] = digits[(int) (number % seed)];
            number /= seed;
        }
        buf[--charPos] = digits[(int) (number % seed)];
        return new String(buf, charPos, (32 - charPos));
    }

    public static long toDecimalNumber(String number, int seed) {
        char[] charBuf = number.toCharArray();
        if (seed == 10) {
            return Long.parseLong(number);
        }

        long result = 0, base = 1;

        for (int i = charBuf.length - 1; i >= 0; i--) {
            int index = 0;
            for (int j = 0, length = digits.length; j < length; j++) {
                // 找到对应字符的下标，对应的下标才是具体的数值
                if (digits[j] == charBuf[i]) {
                    index = j;
                }
            }
            result += index * base;
            base *= seed;
        }
        return result;
    }

    public static String toOtherNumberSystem26(long number, int seed) {
        if (number < 0) {
            number = ((long) 2 * 0x7fffffff) + number + 2;
        }
        char[] buf = new char[32];
        int charPos = 32;
        while ((number / seed) > 0) {
            buf[--charPos] = digits26[(int) (number % seed)];
            number /= seed;
        }
        buf[--charPos] = digits26[(int) (number % seed)];
        return new String(buf, charPos, (32 - charPos));
    }

    public static long toDecimalNumber26(String number, int seed) {
        char[] charBuf = number.toCharArray();
        if (seed == 10) {
            return Long.parseLong(number);
        }

        long result = 0, base = 1;

        for (int i = charBuf.length - 1; i >= 0; i--) {
            int index = 0;
            for (int j = 0, length = digits26.length; j < length; j++) {
                // 找到对应字符的下标，对应的下标才是具体的数值
                if (digits26[j] == charBuf[i]) {
                    index = j;
                }
            }
            result += index * base;
            base *= seed;
        }
        return result;
    }

    public static String toOtherNumberSystem52(long number, int seed) {
        if (number < 0) {
            number = ((long) 2 * 0x7fffffff) + number + 2;
        }
        char[] buf = new char[32];
        int charPos = 32;
        while ((number / seed) > 0) {
            buf[--charPos] = digits52[(int) (number % seed)];
            number /= seed;
        }
        buf[--charPos] = digits52[(int) (number % seed)];
        return new String(buf, charPos, (32 - charPos));
    }

    public static long toDecimalNumber52(String number, int seed) {
        char[] charBuf = number.toCharArray();
        if (seed == 10) {
            return Long.parseLong(number);
        }

        long result = 0, base = 1;

        for (int i = charBuf.length - 1; i >= 0; i--) {
            int index = 0;
            for (int j = 0, length = digits52.length; j < length; j++) {
                // 找到对应字符的下标，对应的下标才是具体的数值
                if (digits52[j] == charBuf[i]) {
                    index = j;
                }
            }
            result += index * base;
            base *= seed;
        }
        return result;
    }

    public static String toOtherNumberSystem62(long number, int seed) {
        if (number < 0) {
            number = ((long) 2 * 0x7fffffff) + number + 2;
        }
        char[] buf = new char[32];
        int charPos = 32;
        while ((number / seed) > 0) {
            buf[--charPos] = digits62[(int) (number % seed)];
            number /= seed;
        }
        buf[--charPos] = digits62[(int) (number % seed)];
        return new String(buf, charPos, (32 - charPos));
    }

    public static long toDecimalNumber62(String number, int seed) {
        char[] charBuf = number.toCharArray();
        if (seed == 10) {
            return Long.parseLong(number);
        }

        long result = 0, base = 1;

        for (int i = charBuf.length - 1; i >= 0; i--) {
            int index = 0;
            for (int j = 0, length = digits62.length; j < length; j++) {
                // 找到对应字符的下标，对应的下标才是具体的数值
                if (digits62[j] == charBuf[i]) {
                    index = j;
                }
            }
            result += index * base;
            base *= seed;
        }
        return result;
    }

    public static String toOtherNumberSystem32(long number, int seed) {
        if (number < 0) {
            number = ((long) 2 * 0x7fffffff) + number + 2;
        }
        char[] buf = new char[32];
        int charPos = 32;
        while ((number / seed) > 0) {
            buf[--charPos] = digits32[(int) (number % seed)];
            number /= seed;
        }
        buf[--charPos] = digits32[(int) (number % seed)];
        return new String(buf, charPos, (32 - charPos));
    }

    public static long toDecimalNumber32(String number, int seed) {
        char[] charBuf = number.toCharArray();
        if (seed == 10) {
            return Long.parseLong(number);
        }

        long result = 0, base = 1;

        for (int i = charBuf.length - 1; i >= 0; i--) {
            int index = 0;
            for (int j = 0, length = digits32.length; j < length; j++) {
                // 找到对应字符的下标，对应的下标才是具体的数值
                if (digits32[j] == charBuf[i]) {
                    index = j;
                }
            }
            result += index * base;
            base *= seed;
        }
        return result;
    }


    public static String toRandomNumberSystem62(long number) {
        if (number < 0) {
            number = ((long) 2 * 0x7fffffff) + number + 2;
        }
        char[] buf = new char[32];
        int charPos = 32;
        while ((number / 62) > 0) {
            buf[--charPos] = randomDigits62[(int) (number % 62)];
            number /= 62;
        }
        buf[--charPos] = randomDigits62[(int) (number % 62)];
        return new String(buf, charPos, (32 - charPos));
    }

    public static long toRandomDecimalNumber62(String number) {
        char[] charBuf = number.toCharArray();
        long result = 0, base = 1;

        for (int i = charBuf.length - 1; i >= 0; i--) {
            int index = 0;
            for (int j = 0, length = randomDigits62.length; j < length; j++) {
                // 找到对应字符的下标，对应的下标才是具体的数值
                if (randomDigits62[j] == charBuf[i]) {
                    index = j;
                }
            }
            result += index * base;
            base *= 62;
        }
        return result;
    }
}

