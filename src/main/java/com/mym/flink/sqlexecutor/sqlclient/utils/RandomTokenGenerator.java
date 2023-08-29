package com.mym.flink.sqlexecutor.sqlclient.utils;


import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 随机令牌生成器
 *
 * @author maoym
 * @date 2023/08/24
 */
public class RandomTokenGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    /**
     * 令牌长度
     */
    private final int tokenLength;

    /**
     * 自定义前缀
     */
    private final String definePrefix;
    private final Random random = new Random();
    private final Set<String> issuedTokens = new HashSet<>();

    public RandomTokenGenerator(int tokenLength, String definePrefix) {
        this.tokenLength = tokenLength;
        this.definePrefix = definePrefix;
    }

    public RandomTokenGenerator(int tokenLength) {
        this(tokenLength, "");
    }

    public synchronized String generateToken() {
        String token;
        do {
            token = generateRandomToken();
        } while (issuedTokens.contains(token));

        issuedTokens.add(token);
        return definePrefix + token;
    }

    private String generateRandomToken() {
        StringBuilder token = new StringBuilder(tokenLength);
        for (int i = 0; i < tokenLength; i++) {
            token.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return token.toString();
    }

    public static void main(String[] args) {
        RandomTokenGenerator generator = new RandomTokenGenerator(3, "table-");
        for (int i = 0; i < 10; i++) {
            System.out.println(generator.generateToken());
        }
    }
}

