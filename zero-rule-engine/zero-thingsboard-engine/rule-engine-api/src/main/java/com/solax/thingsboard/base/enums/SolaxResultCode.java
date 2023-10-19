package com.solax.thingsboard.base.enums;

import lombok.Getter;
import lombok.Setter;

public enum SolaxResultCode {
    SUCCESS(200, "SUCCESS")
    ;


    @Getter
    private int code;

    @Setter
    private String message;

    SolaxResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
