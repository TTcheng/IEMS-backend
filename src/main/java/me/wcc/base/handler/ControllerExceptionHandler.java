package me.wcc.base.handler;

import me.wcc.base.exception.CommonException;
import me.wcc.base.exception.ExceptionResponse;
import me.wcc.base.exception.NotFoundException;
import me.wcc.base.exception.OptimisticLockException;
import me.wcc.base.infra.constant.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.choerodon.mybatis.helper.LanguageHelper;

/**
 * 捕获控制器的异常，并返回异常信息
 *
 * @author wuguokai
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);
    private static final Map<Locale, String> map = new HashMap<>();
    private static final Map<Locale, String> exceptionMap = new HashMap<>();

    static {
        map.put(Locale.SIMPLIFIED_CHINESE, "实体字段重复");
        map.put(Locale.US, "Entity field is repeated");
    }


    private MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }



    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ExceptionResponse> process(CommonException exception) {
        LOGGER.info("exception info {}", exception.getTrace());
        String message = null;
        try {
            message = messageSource.getMessage(exception.getCode(), exception.getParameters(), locale());
        } catch (Exception e) {
            LOGGER.trace("exception message {}", exception);
        }
        return new ResponseEntity<>(
                new ExceptionResponse(true, exception.getCode(), message != null ? message : exception.getMessage()),
                HttpStatus.OK);
    }

    /**
     * 拦截NotFound异常信息，将信息ID换为messages信息。
     *
     * @param exception 异常
     * @return ExceptionResponse
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> process(NotFoundException exception) {
        LOGGER.info("exception info", exception);
        String message = null;
        exceptionMap.put(Locale.SIMPLIFIED_CHINESE, "资源不存在");
        exceptionMap.put(Locale.US, "error.resource.notExist");
        try {
            message = messageSource.getMessage(exception.getMessage(), null, locale());
        } catch (Exception e) {
            LOGGER.trace("exception message", exception);
        }
        return new ResponseEntity<>(
                new ExceptionResponse(true, "error.resource.notExist" , message != null ? message : exceptionMap.get(Locale.US)),
                HttpStatus.OK);
    }

    /**
     * 拦截处理 Valid 异常
     *
     * @param exception 异常
     * @return ExceptionResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> process(MethodArgumentNotValidException exception) {
        LOGGER.info("exception process {}", exception);
        String message = null;
        String code = "error.methodArgument.notValid";
        try {
            code = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
            message = messageSource.getMessage(code, null, locale());
        } catch (Exception e) {
            LOGGER.trace("exception process get massage exception {}", exception);
        }
        return new ResponseEntity<>(
                new ExceptionResponse(true, code, message != null ? message : code),
                HttpStatus.OK);
    }

    /**
     * 拦截处理 DuplicateKeyException 异常
     *
     * @param exception DuplicateKeyException
     * @return ExceptionResponse
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ExceptionResponse> process(DuplicateKeyException exception) {
        LOGGER.info("exception process {}", exception);
        String message = null;
        try {
            message = map.get(locale());
        } catch (Exception e) {
            LOGGER.trace("exception process get massage exception {}", exception);
        }
        return new ResponseEntity<>(
                new ExceptionResponse(true, "error.db.duplicateKey", message != null ? message : "error.key.duplicate"),
                HttpStatus.OK);
    }

    /**
     * 拦截 {@link OptimisticLockException} 异常信息，返回 “记录不存在或版本不一致” 信息
     *
     * @param exception OptimisticLockException
     * @return ExceptionResponse
     */
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ExceptionResponse> process(OptimisticLockException exception) {
        LOGGER.warn(exception.getMessage(), exception);
        ExceptionResponse er = new ExceptionResponse(BaseConstants.ErrorCode.OPTIMISTIC_LOCK);
        return new ResponseEntity<>(er, HttpStatus.OK);
    }

    /**
     * 拦截处理 MultipartException 异常
     *
     * @param exception MultipartException
     * @return ExceptionResponse
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ExceptionResponse> process(MultipartException exception) {
        LOGGER.info("exception process", exception);
        exceptionMap.put(Locale.SIMPLIFIED_CHINESE, "文件上传不得大于1M");
        exceptionMap.put(Locale.US, "The size of file can not exceed 1M");
        String message = null;
        try {
            message = exceptionMap.get(locale());
        } catch (Exception e) {
            LOGGER.trace("exception process get massage exception", exception);
        }
        return new ResponseEntity<>(
                new ExceptionResponse(true, "error.upload.multipartSize", message != null ? message : exceptionMap.get(Locale.US)),
                HttpStatus.OK);
    }

    /**
     * 拦截处理 BadSqlGrammarException 异常
     * 搜索接口排序字段错误，在这里拦截异常，并友好返回前端
     *
     * @param exception BadSqlGrammarException
     * @return ExceptionResponse
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    public ResponseEntity<ExceptionResponse> process(BadSqlGrammarException exception) {
        LOGGER.info("exception process", exception);
        exceptionMap.put(Locale.SIMPLIFIED_CHINESE, "数据库sql语句错误,请联系开发人员");
        exceptionMap.put(Locale.US, "There is someting wrong with sql, please connect with the developer");
        String message = null;
        try {
            message = exceptionMap.get(locale());
        } catch (Exception e) {
            LOGGER.trace("exception process get massage exception", exception);
        }
        return new ResponseEntity<>(
                new ExceptionResponse(true, "error.db.badSql", message != null ? message : exceptionMap.get(Locale.US)),
                HttpStatus.OK);
    }

    /**
     * 返回用户的语言类型
     *
     * @return Simplified Chinese or US english
     */
    private Locale locale() {
        if ("zh_CN".equals(LanguageHelper.language())) {
            return Locale.SIMPLIFIED_CHINESE;
        }
        return Locale.US;
    }
}
