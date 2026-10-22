
package com.alibou.ecommerce.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private final String msg;

    public BusinessException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
