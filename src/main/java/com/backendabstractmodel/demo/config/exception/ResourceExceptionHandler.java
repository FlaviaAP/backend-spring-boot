package com.backendabstractmodel.demo.config.exception;

import com.backendabstractmodel.demo.exceptions.BusinessException;
import com.backendabstractmodel.demo.exceptions.ObjectNotFoundException;
import com.backendabstractmodel.demo.exceptions.UnexpectedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<StandardError> defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }
        log.error(e.getMessage(), e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, req, new UnexpectedException(e));
    }

    @ExceptionHandler({UnexpectedException.class})
    public ResponseEntity<StandardError> unexpectedErrorHandler(HttpServletRequest req, Exception e) {
        log.error(e.getClass().getSimpleName(), e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, req, e);
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<StandardError> businessErrorHandler(HttpServletRequest request, BusinessException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, request, e);
    }

    @ExceptionHandler({ObjectNotFoundException.class})
    public ResponseEntity<StandardError> objectNotFoundErrorHandler(HttpServletRequest request, ObjectNotFoundException e) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, e);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<StandardError> accessDeniedErrorHandler(HttpServletRequest request, AccessDeniedException e) {
        log.error(e.getClass().getSimpleName(), e);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, request, e);
    }

    private ResponseEntity<StandardError> buildErrorResponse(
        HttpStatus httpStatus, HttpServletRequest request, Exception e
    ) {
        StandardError err = new StandardError(
            System.currentTimeMillis(),
            httpStatus.value(),
            e.getClass().getSimpleName().replace("Exception", " Error"),
            e.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(httpStatus).body(err);
    }


    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<StandardError> handleMissingParams(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.error(e.getClass().getSimpleName(), e);
        String str = "O parâmetro " + e.getParameterName() + " é obrigatório";
        StandardError err = new StandardError(System.currentTimeMillis(), HttpStatus.BAD_REQUEST.value(), e.getMessage(), str, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<StandardError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error(e.getClass().getSimpleName(), e);
        ValidationError err = new ValidationError(System.currentTimeMillis(),
            HttpStatus.UNPROCESSABLE_ENTITY.value(), "Erro de validação", e.getMessage(), request.getRequestURI()
        );

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            err.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(err);
    }

}
