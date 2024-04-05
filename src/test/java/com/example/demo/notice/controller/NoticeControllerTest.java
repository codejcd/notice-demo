package com.example.demo.notice.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.notice.dto.NoticeCommonResponse;
import com.example.demo.notice.dto.NoticeDto;
import com.example.demo.notice.dto.NoticeRequest;
import com.example.demo.notice.dto.NoticeResponse;
import com.example.demo.notice.service.NoticeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 공지사항 컨트롤러 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
public class NoticeControllerTest {

    @Mock
    private NoticeService noticeService;

    @InjectMocks
    private NoticeController noticeController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(noticeController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()) // pageable init
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @DisplayName("공지사항 목록 조회")
    @Test
    public void searchNotice() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);

        mockMvc.perform(
                    get("/v1/notice")
                            .queryParam("page", "0")
                            .queryParam("size", "10")
                )
                .andExpect(status().isOk());

        verify(noticeService).searchNotice(pageRequest);
    }

    @DisplayName("공지사항 상세 조회")
    @Test
    public void getNotice() throws Exception {
        Long id = 1L;

        NoticeResponse response = NoticeResponse.builder().noticeId(id).build();
        NoticeDto noticeDto = NoticeDto.builder().noticeId(id).build();
        when(noticeService.updateNoticeViewCnt(id)).thenReturn(noticeDto);
        when(noticeService.getNotice(id)).thenReturn(response);

        this.mockMvc.perform(
                        get("/v1/notice/{id}", id)
                )
                .andExpect(status().isOk());

        verify(noticeService).getNotice(id);
    }

    @DisplayName("공지사항 등록")
    @Test
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

        NoticeCommonResponse response = NoticeCommonResponse.builder().noticeId(1L).build();
        when(noticeService.regNotice(any(), any())).thenReturn(response);

        this.mockMvc.perform(
                        multipart("/v1/notice")
                                //.part(new MockPart("files", firstMockMultipartFile.getBytes()))
                                .file(firstMockMultipartFile)
                                .file(secondMockMultipartFile)
                                .file(notice)
                                //.part(new MockPart("notice", requestBody.getBytes(StandardCharsets.UTF_8)))
                )
                .andExpect(status().isCreated());
                // .andDo(MockMvcResultHandlers.print());

    }

    @DisplayName("공지사항 수정")
    @Test
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
                .andExpect(status().isOk());
    }

    @DisplayName("공지사항 삭제")
    @Test
    public void delNotice() throws Exception {
        Long id = 1L;

        mockMvc.perform(
                        delete("/v1/notice/{id}", id)
                )
                .andExpect(status().is2xxSuccessful());

        verify(noticeService).delNotice(id);
    }
}

