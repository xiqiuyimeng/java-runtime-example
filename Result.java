package com.example.demo.jython;

import lombok.Data;

import java.io.Serializable;

/**
 * @author luwt
 * @date 2019/9/5.
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -8437681105209139746L;

    private String code;

    private String message;

    private T json;

}
