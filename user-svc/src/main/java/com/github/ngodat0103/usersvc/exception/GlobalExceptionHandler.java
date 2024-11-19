package com.github.ngodat0103.usersvc.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @Getter
  static class Error {
    private final String detail;
    private final String pointer;
    @JsonIgnore private final String jsonPathSchema = "#/";

    public Error(String detail, String pointer) {
      this.detail = detail;
      this.pointer = jsonPathSchema + pointer;
    }
  }

  @ExceptionHandler({ConflictException.class})
  @ResponseStatus(HttpStatus.CONFLICT)
  public ProblemDetail handleApiException(RuntimeException e, ServerHttpRequest request) {
    ProblemDetail problemDetails = ProblemDetail.forStatus(HttpStatus.CONFLICT);
    problemDetails.setDetail(e.getMessage());
    problemDetails.setType(URI.create("https://problems-registry.smartbear.com/already-exists"));
    problemDetails.setTitle("Already exists");
    problemDetails.setDetail(e.getMessage());
    problemDetails.setInstance(URI.create(request.getPath().toString()));
    return problemDetails;
  }

  @ExceptionHandler({WebExchangeBindException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ProblemDetail handleMethodArgumentNotValidException(
      Exception e, ServerHttpRequest request) {
    ProblemDetail problemDetails = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetails.setType(
        URI.create("https://problems-registry.smartbear.com/invalid-body-property-value/"));
    problemDetails.setDetail("The request body contains an invalid body property value.");
    problemDetails.setTitle("Validation Error.");
    problemDetails.setInstance(URI.create(request.getPath().toString()));
    Set<Error> errors = new HashSet<>();
    if (e instanceof WebExchangeBindException exception) {
      exception
          .getFieldErrors()
          .forEach(
              fieldError -> {
                Error error = new Error(fieldError.getDefaultMessage(), fieldError.getField());
                errors.add(error);
              });
    }
    problemDetails.setProperties(Collections.singletonMap("errors", errors));
    return problemDetails;
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ProblemDetail handleBadCredentialsException(
      BadCredentialsException e, ServerHttpRequest request) {
    ProblemDetail problemDetails = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
    problemDetails.setType(URI.create("https://problems-registry.smartbear.com/unauthorized"));
    problemDetails.setTitle("Unauthorized");
    problemDetails.setDetail(e.getMessage());
    problemDetails.setInstance(URI.create(request.getPath().toString()));
    return problemDetails;
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ProblemDetail handleNotFoundException(NotFoundException e, ServerHttpRequest request) {
    ProblemDetail problemDetails = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    problemDetails.setType(URI.create("https://problems-registry.smartbear.com/not-found"));
    problemDetails.setTitle("Not Found");
    problemDetails.setDetail(e.getMessage());
    problemDetails.setInstance(URI.create(request.getPath().toString()));
    return problemDetails;
  }
}