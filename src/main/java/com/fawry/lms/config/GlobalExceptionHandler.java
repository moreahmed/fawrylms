package com.fawry.lms.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fawry.lms.shared.errors.BadRequestError;
import com.fawry.lms.shared.errors.ConflictError;
import com.fawry.lms.shared.errors.ErrorResponse;
import com.fawry.lms.shared.errors.ForbiddenError;
import com.fawry.lms.shared.errors.NotFoundError;
import com.fawry.lms.shared.errors.UnauthorizedError;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler (NotFoundError.class)
    public ResponseEntity<ErrorResponse> handleNotFoundError(NotFoundError error) {
        ErrorResponse response = new ErrorResponse(
            error.getClass().getSimpleName(),
            error.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler (BadRequestError.class)
    public ResponseEntity<ErrorResponse> handleBadRequestError(BadRequestError error) {
        ErrorResponse response = new ErrorResponse(
            error.getClass().getSimpleName(),
            error.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (UnauthorizedError.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedError(UnauthorizedError error) {
        ErrorResponse response = new ErrorResponse(
            error.getClass().getSimpleName(),
            error.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler (ForbiddenError.class)
    public ResponseEntity<ErrorResponse> handleForbiddenError(ForbiddenError error) {
        ErrorResponse response = new ErrorResponse(
            error.getClass().getSimpleName(),
            error.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler (ConflictError.class)
    public ResponseEntity<ErrorResponse> handleConflictError(ConflictError error) {
        ErrorResponse response = new ErrorResponse(
            error.getClass().getSimpleName(),
            error.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

}
