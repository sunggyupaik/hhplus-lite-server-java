package kr.hhplus.be.server.support.util;

import java.util.UUID;

public class WaitingTokenGenerator {
    private static final int TOKEN_LENGTH = 32;

    public static String randomCharacter() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
