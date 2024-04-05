package com.example.demo.notice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.notice.dto.NoticeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * 공지사항 통합 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NoticeIntegrationTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    }

    @DisplayName("공지사항 목록 조회")
    @Test
    @Order(50)
    public void searchNotice() throws Exception {
        this.mockMvc.perform(
                        get("/v1/notice")
                                .queryParam("page", "0")
                                .queryParam("size", "10")
                )
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("공지사항 상세 조회")
    @Test
    @Order(20)
    public void getNotice() throws Exception {
        Long id = 1L;

        this.mockMvc.perform(
                        get("/v1/notice/{id}", id)
                )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("공지사항 등록")
    @Test
    @Order(10)
    public void regNotice() throws Exception {
        LocalDate today = LocalDate.now();
        NoticeRequest noticeRequest = NoticeRequest.builder()
                .id(1L)
                .title("공지사항 테스트 제목 변경")
                .content("공지사항 테스트 내용 변경")
                .startDate(today)
                .endDate(today.plusDays(5))
                .regId("testUser")
                .build();

        String requestBody = objectMapper.writeValueAsString(noticeRequest);
        MockMultipartFile firstMockMultipartFile = new MockMultipartFile("files", "notice.pdf"
                , MediaType.APPLICATION_PDF_VALUE, "pdf".getBytes());

        MockMultipartFile secondMockMultipartFile = new MockMultipartFile("files", "notice.txt"
                , MediaType.TEXT_PLAIN_VALUE, "text".getBytes());

        MockMultipartFile notice = new MockMultipartFile("notice", "notice"
                , MediaType.APPLICATION_JSON_VALUE, requestBody.getBytes(StandardCharsets.UTF_8));

        List<MultipartFile> fileList = new ArrayList<>();
        fileList.add(firstMockMultipartFile);
        fileList.add(secondMockMultipartFile);

        this.mockMvc.perform(
                        multipart("/v1/notice")
                                //.part(new MockPart("files", firstMockMultipartFile.getBytes()))
                                .file(firstMockMultipartFile)
                                .file(secondMockMultipartFile)
                                .file(notice)
                        //.part(new MockPart("notice", requestBody.getBytes(StandardCharsets.UTF_8)))
                )
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());

    }

    @DisplayName("공지사항 수정")
    @Test
    @Order(30)
    public void modNotice() throws Exception {
        LocalDate today = LocalDate.now();
        NoticeRequest noticeRequest = NoticeRequest.builder()
                .id(1L)
                .title("공지사항 테스트 제목 변경")
                .content("공지사항 테스트 내용 변경")
                .startDate(today)
                .endDate(today.plusDays(5))
                .build();

        String requestBody = objectMapper.writeValueAsString(noticeRequest);

        this.mockMvc.perform(
                        patch("/v1/notice")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)

                )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("공지사항 삭제")
    @Test
    @Order(40)
    public void delNotice() throws Exception {
        Long id = 1L;

        mockMvc.perform(
                        delete("/v1/notice/{id}", id)
                )
                .andExpect(status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print());
    }

}
