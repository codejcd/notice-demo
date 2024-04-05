package com.example.demo.notice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 공지사항 조회 트래픽 테스트
 */
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NoticeTrafficTest {
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @DisplayName("공지사항 등록")
    @Test
    @Order(1)
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

    @DisplayName("공지사항 대용량 트래픽 테스트")
    @Test
    @Order(2)
    public void testGetNoticeApiEndPoint() throws Exception {
        int numThreads = 5;
        int numRequestsPerThread = 1000;
        List<String> result = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {

            executorService.execute(() -> {
                long startTime = System.currentTimeMillis();
                for (int j = 0; j < numRequestsPerThread; j++) {
                    try {
                        String url =  "/v1/notice/1";
                        final ResultActions resultActions = mockMvc.perform(get(url))
                                .andExpect(status().isOk());
                        final MvcResult mvcResult = resultActions.andReturn();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                result.add(numRequestsPerThread + " 건당 실행 시간 : " + duration);
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);
        result.stream().forEach(System.out::println);
    }
}
