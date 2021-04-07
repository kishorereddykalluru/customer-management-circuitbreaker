package com.customermanagement.circuitbreaker.errorhandler;

import com.customermanagement.circuitbreaker.exception.NotFoundException;
import com.customermanagement.circuitbreaker.exception.domain.ApiError;
import com.customermanagement.circuitbreaker.exception.domain.ApiErrors;
import com.customermanagement.circuitbreaker.interceptors.RequestInterceptor;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.MDC;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.MimeType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.Resource;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomerExceptionHandler extends ResponseEntityExceptionHandler {

    @Resource(name = "messageSource")
    private MessageSource messageSource;

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest webRequest){
        int code = 1004;
        return handleAllExceptions(ex, HttpStatus.NOT_FOUND, webRequest,
                ApiError.builder().code(code).message(resolveMessageSource(code, ex.getMessage())).build());
    }

    @ExceptionHandler({ValidationException.class, ConstraintViolationException.class})
    public final ResponseEntity<Object> validationExceptionHandler(Exception e, WebRequest request){
        int code = 1001;
        if(e instanceof ConstraintViolationException){
            // Added message template to pass custom message for constraint violation
            return handleAllExceptions(e, HttpStatus.BAD_REQUEST, request,
                    ((ConstraintViolationException) e).getConstraintViolations().stream().map(ex -> {
                        PathImpl path = (PathImpl) ex.getPropertyPath();
                        return ApiError.builder().code(code).message(resolveMessageSource(code,
                                ex.getInvalidValue() != null ? ex.getInvalidValue().toString(): " ",
                                path.getLeafNode().asString()))
                                .build();
                    }).collect(Collectors.toList()));
        }
        return handleAllExceptions(e, HttpStatus.BAD_REQUEST, request, ApiError.builder().code(code).message(e.getMessage()).build());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        int invalidCode = 1001;
        int missingCode = 1003;

        return handleAllExceptions(ex, HttpStatus.BAD_REQUEST, request, ex.getBindingResult().getFieldErrors().stream()
            .map(e -> ApiError.builder().code(Objects.nonNull(e.getRejectedValue())? invalidCode: missingCode)
                    .message(Objects.nonNull(e.getRejectedValue())?resolveMessageSource(invalidCode, (String) e.getRejectedValue(),e.getField()):
                            resolveMessageSource(missingCode, e.getField())).build()).collect(Collectors.toList()));
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        int code = 1002;
        return handleAllExceptions(ex, HttpStatus.BAD_REQUEST, request,
                ApiError.builder().code(code).message(resolveMessageSource(code, ex.getVariableName())).build());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        int code = 1005;
        return handleAllExceptions(ex, HttpStatus.BAD_REQUEST, request,
                ApiError.builder().code(code).message(resolveMessageSource(code, ex.getMessage())).build());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        int code = 1002;
        return handleAllExceptions(ex, HttpStatus.BAD_REQUEST, request,
                ApiError.builder().code(code).message(resolveMessageSource(code, ex.getParameterName())).build());
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        int code = 1001;
        String message = null;
        if(ex instanceof MethodArgumentTypeMismatchException){
            message = resolveMessageSource(code, ex.getValue().toString(),
                    ((MethodArgumentTypeMismatchException) ex).getName());
        } else {
            message = resolveMessageSource(code, ex.getValue().toString(), ex.getPropertyName());
        }

        var apiError = ApiError.builder().code(code).message(message).build();
        return handleAllExceptions(ex, HttpStatus.BAD_REQUEST, request, apiError);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        int code = 1006;
        return handleAllExceptions(ex, HttpStatus.NOT_ACCEPTABLE, request, ApiError.builder().code(code)
        .message(resolveMessageSource(code, request.getHeader(HttpHeaders.ACCEPT),
                ex.getSupportedMediaTypes().stream().map(MimeType::toString).collect(Collectors.toList()).toString())).build());
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(RequestInterceptor.HEADER_X_CORRELATION_ID, MDC.get(RequestInterceptor.HEADER_X_CORRELATION_ID));
        return super.handleExceptionInternal(ex,body, headers, status, request);
    }

    private ResponseEntity<Object> handleAllExceptions(Exception e, HttpStatus status, WebRequest request, List<ApiError> apiErrors) {
        return handleExceptionInternal(e, ApiErrors.builder().apiErrors(apiErrors).build(), new HttpHeaders(), status, request);
    }

    private ResponseEntity<Object> handleAllExceptions(Exception e, HttpStatus status, WebRequest request, ApiError apiError) {
        return handleAllExceptions(e, status, request, List.of(apiError));
    }

    protected String resolveMessageSource(int prop, String... args) {
        return resolveMessageSource(String.valueOf(prop), args);
    }

    private String resolveMessageSource(String prop, String... args) {
        return messageSource.getMessage(prop, args, LocaleContextHolder.getLocale());
    }

}
