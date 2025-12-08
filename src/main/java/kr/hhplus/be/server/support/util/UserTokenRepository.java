package kr.hhplus.be.server.support.util;

import kr.hhplus.be.server.support.exception.InvalidParamException;

public class UserTokenRepository {
    public static Long findUserId(String userToken) {
        if (userToken == null) throw new InvalidParamException("userToken");
        return Long.parseLong(userToken);
    }
}
