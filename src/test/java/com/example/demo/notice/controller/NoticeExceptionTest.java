package com.example.demo.notice.controller;
import static org.assertj.core.api.Assertions.assertThat;
import com.example.demo.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 전역 익셉션 핸들러 테스트
 */
@ExtendWith(MockitoExtension.class)
public class NoticeExceptionTest {

    @Test
    @DisplayName("익셉션 핸들러 테스트")
    public void noSuchHandleException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResponseEntity<?> responseEntity = handler.handleException();
        assertThat(HttpStatus.NOT_FOUND).isEqualTo(responseEntity.getStatusCode());
    }
}
