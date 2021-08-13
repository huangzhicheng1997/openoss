package io.oss.protocol.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.oss.util.exception.BadRequestException;
import io.oss.util.exception.FileNotFindException;
import io.oss.util.exception.PullMsgTooLongException;

/**
 * 定义所有的异常，对应的协议编码。
 * <p>
 * 对于基于tcp的私有协议，客户端接受的错误响应元数据为 [code,msg,exception.getMessage]<br>
 * 对于http协议，http客户端在此枚举下定义了java异常与http协议的异常对应关系
 * </p>
 *
 * @Author zhicheng
 * @Date 2021/5/25 3:15 下午
 * @Version 1.0
 */
public enum ExceptionCode {

    //未处理的异常
    UN_CATCH_EXCEPTION("00", "unCatchException error", Throwable.class, HttpResponseStatus.INTERNAL_SERVER_ERROR),
    NOT_FIND_FILE("02", "not find file", FileNotFindException.class, HttpResponseStatus.NOT_FOUND),
    AUTH_FAILED("01", "auth error", AuthenticationException.class, HttpResponseStatus.FORBIDDEN),
    URL_PROCESSOR_NOT_FIND("03", "404 not find", NotFindProcessorException.class, HttpResponseStatus.NOT_FOUND),
    BAD_REQUEST("04", "bad request", BadRequestException.class, HttpResponseStatus.BAD_REQUEST),
    PULL_TOO_LONG("05", "too long", PullMsgTooLongException.class, HttpResponseStatus.FORBIDDEN),
    ;


    String code;
    String msg;
    Class<? extends Throwable> exception;
    HttpResponseStatus status;

    ExceptionCode(String code, String msg, Class<? extends Throwable> exception, HttpResponseStatus status) {
        assert exception != null;
        this.code = code;
        this.msg = msg;
        this.exception = exception;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Class<? extends Throwable> getException() {
        return exception;
    }

    public HttpResponseStatus getResponseStatus() {
        return status;
    }

    public static ExceptionCode match(Class<? extends Throwable> clazz) {
        ExceptionCode[] values = ExceptionCode.values();
        for (ExceptionCode exceptionCode : values) {
            if (exceptionCode.exception.equals(clazz)) {
                return exceptionCode;
            }
        }
        return null;
    }

    public static ExceptionCode matchByCode(String code) {
        ExceptionCode[] values = ExceptionCode.values();
        for (ExceptionCode exceptionCode : values) {
            if (exceptionCode.code.equals(code)) {
                return exceptionCode;
            }
        }
        return null;
    }

}
