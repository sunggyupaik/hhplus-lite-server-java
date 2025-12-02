package kr.hhplus.be.server.support.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    ;
    private final String errorMessage;

    public String getErrorMessage(Object... arg) {
        return String.format(errorMessage, arg);
    }
}
