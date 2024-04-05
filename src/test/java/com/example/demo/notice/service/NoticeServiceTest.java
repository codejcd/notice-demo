package com.example.demo.notice.service;

import static org.mockito.ArgumentMatchers.any;
import com.example.demo.notice.dto.FileDto;
import com.example.demo.notice.dto.NoticeCommonResponse;
import com.example.demo.notice.dto.NoticeDto;
import com.example.demo.notice.dto.NoticeRequest;
import com.example.demo.notice.dto.NoticeResponse;
import com.example.demo.notice.entity.Notice;
import com.example.demo.notice.entity.NoticeFile;
import com.example.demo.notice.repository.NoticeFileRepository;
import com.example.demo.notice.repository.NoticeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.LongSupplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * 공지사항 서비스 단위 ㅔㅌ스트
 */
public class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private NoticeFileRepository noticeFileRepository;

    @InjectMocks
    private NoticeService noticeService;

    public NoticeServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("공지사항 저장")
    public void createNoticeTest() throws Exception {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String year = today.substring(0, 4);
        String month = today.substring(4, 6);
        String day = today.substring(6, 8);

        // given
        NoticeRequest firstNoticeRequest = NoticeRequest.builder()
                .id(1L)
                .title("공지사항 테스트 제목")
                .content("공지사항 테스트 내용")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .regId("testUser1")
                .build();

        Notice firstNotice = Notice.regNoticeBuilder()
                .id(1L)
                .title("공지사항 테스트 제목")
                .content("공지사항 테스트 내용")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .regId("testUser1")
                .regDate(LocalDateTime.now())
                .build();

        List<FileDto> fileDtoList = new ArrayList<>();
        StringBuilder fileUriBuilder = new StringBuilder("/upload").append("/notice")
                .append(year).append(month).append(day);
        String firstRealFileName = "중요 공지사항";
        String firstFileExt = ".pdf";
        StringBuilder firstFileNmBuilder  = new StringBuilder(firstRealFileName)
                .append(today).append(firstFileExt);

        String secondRealFileName = "공지사항";
        String secondFileExt = ".png";
        StringBuilder secondFileNmBuilder  = new StringBuilder(firstRealFileName)
                .append(today).append(secondFileExt);

        FileDto firstFileDto = FileDto.builder().uploadPath(fileUriBuilder.toString())
                .uploadFileName(firstFileNmBuilder.toString())
                .realFileName(firstRealFileName)
                .fileExt(firstFileExt).build();

        FileDto secondFileDto = FileDto.builder().uploadPath(fileUriBuilder.toString())
                .uploadFileName(secondFileNmBuilder.toString())
                .realFileName(secondRealFileName)
                .fileExt(secondFileExt).build();

        fileDtoList.add(firstFileDto);
        fileDtoList.add(secondFileDto);

        NoticeFile firstNoticeFile = NoticeFile.regNoticeFileBuilder()
                .id(1L)
                .fileUri(fileDtoList.get(0).getUploadPath())
                .fileExt(fileDtoList.get(0).getFileExt())
                .fileName(fileDtoList.get(0).getUploadFileName())
                .realFileName(fileDtoList.get(0).getRealFileName())
                .notice(firstNotice)
                .build();

        NoticeFile secondNoticeFile = NoticeFile.regNoticeFileBuilder()
                .id(2L)
                .fileUri(fileDtoList.get(1).getUploadPath())
                .fileExt(fileDtoList.get(1).getFileExt())
                .fileName(fileDtoList.get(1).getUploadFileName())
                .realFileName(fileDtoList.get(1).getRealFileName())
                .notice(firstNotice)
                .build();

        MockMultipartFile firstMockMultipartFile = new MockMultipartFile("중요 공지사항", "notice.pdf"
                , MediaType.APPLICATION_PDF_VALUE, "noticepdf".getBytes());

        MockMultipartFile secondMockMultipartFile = new MockMultipartFile("공지사항", "notice.png"
                , MediaType.IMAGE_PNG_VALUE, "noticepng".getBytes());

        List<MultipartFile> fileList = new ArrayList<>();
        fileList.add(firstMockMultipartFile);
        fileList.add(secondMockMultipartFile);

        when(noticeRepository.save(any(Notice.class))).thenReturn(firstNotice);
        when(noticeFileRepository.save(any(NoticeFile.class))).thenReturn(firstNoticeFile);
        when(noticeFileRepository.save(any(NoticeFile.class))).thenReturn(secondNoticeFile);

        NoticeCommonResponse noticeResponse = noticeService.regNotice(firstNoticeRequest, fileList);

        assertThat(noticeResponse).isNotNull();
        assertThat(noticeResponse.getNoticeId()).isEqualTo(firstNotice.getId());
        assertThat(noticeResponse.getContent()).isEqualTo(firstNotice.getContent());
        assertThat(noticeResponse.getStartDate()).isEqualTo(firstNotice.getStartDate());
        assertThat(noticeResponse.getEndDate()).isEqualTo(firstNotice.getEndDate());
        assertThat(noticeResponse.getRegId()).isEqualTo(firstNotice.getRegId());
        assertThat(noticeResponse.getRegDate()).isEqualTo(firstNotice.getRegDate());
    }

    @Test
    @DisplayName("공지사항 단건 조회")
    public void getNoticeTest() throws Exception {
        // given
        NoticeRequest noticeRequest = NoticeRequest.builder()
                .id(1L)
                .build();

        LocalDate today = LocalDate.now();
        Notice notice = Notice.regNoticeBuilder()
                .id(noticeRequest.getId())
                .title("공지사항 테스트 제목")
                .content("공지사항 테스트 내용")
                .startDate(today)
                .endDate(today.plusDays(7))
                .viewCnt(1L)
                .regId("testUser")
                .regDate(LocalDateTime.now())
                .build();

        when(noticeRepository.findById(noticeRequest.getId())).thenReturn(Optional.of(notice));
        // when
        NoticeResponse response = noticeService.getNotice(noticeRequest.getId());
        // then
        verify(noticeRepository, times(1)).findById(1L);
        assertThat(response).isNotNull();
        assertThat(response.getNoticeId()).isEqualTo(notice.getId());
        assertThat(response.getTitle()).isEqualTo(notice.getTitle());
        assertThat(response.getContent()).isEqualTo(notice.getContent());
        assertThat(response.getViewCnt()).isEqualTo(notice.getViewCnt());
    }

    @Test
    @DisplayName("공지사항 목록 조회")
    public void searchNoticeTest() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        NoticeDto firstNoticeDto = NoticeDto.builder()
                .noticeId(1L)
                .title("공지사항 테스트 제목 1")
                .content("공지사항 테스트 내용 1")
                .viewCnt(1L)
                .regId("testUser1")
                .regDate(LocalDateTime.now())
                .build();

        NoticeDto secondNoticeDto = NoticeDto.builder()
                .noticeId(1L)
                .title("공지사항 테스트 제목 2")
                .content("공지사항 테스트 내용 2")
                .viewCnt(1L)
                .regId("testUser2")
                .regDate(LocalDateTime.now())
                .build();

        List<NoticeDto> noticeDtoList = new ArrayList<>();
        noticeDtoList.add(firstNoticeDto);
        noticeDtoList.add(secondNoticeDto);
        LongSupplier longSupplier = () -> noticeDtoList.size();
        Page<NoticeDto> result =  PageableExecutionUtils.getPage(noticeDtoList, pageRequest, longSupplier);

        when(noticeRepository.searchPage(null, pageRequest)).thenReturn(result);
        List<NoticeResponse> response = noticeService.searchNotice(pageRequest);

        verify(noticeRepository, times(1)).searchPage(null, pageRequest);
        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.get(0).getNoticeId()).isEqualTo(firstNoticeDto.getNoticeId());
        assertThat(response.get(1).getNoticeId()).isEqualTo(secondNoticeDto.getNoticeId());
    }

    @Test
    @DisplayName("공지사항 수정")
    public void modNoticeTest() throws Exception {
        // given
        LocalDate today = LocalDate.now();
        NoticeRequest noticeRequest = NoticeRequest.builder()
                .id(1L)
                .title("공지사항 테스트 제목 변경")
                .content("공지사항 테스트 내용 변경")
                .startDate(today)
                .endDate(today.plusDays(5))
                .build();

        Notice notice = Notice.regNoticeBuilder()
                .id(noticeRequest.getId())
                .title("공지사항 테스트 제목")
                .content("공지사항 테스트 내용")
                .startDate(today.plusDays(1))
                .endDate(today.plusDays(7))
                .viewCnt(0L)
                .regId("testUser")
                .regDate(LocalDateTime.now())
                .build();

        when(noticeRepository.findById(noticeRequest.getId())).thenReturn(Optional.of(notice));

        //when
        NoticeCommonResponse noticeResponse = noticeService.modNotice(noticeRequest);

        // then
        verify(noticeRepository, times(1)).findById(1L);
        assertThat(noticeResponse).isNotNull();
        assertThat(noticeResponse.getNoticeId()).isEqualTo(notice.getId());
        assertThat(noticeResponse.getTitle()).isEqualTo(notice.getTitle());
        assertThat(noticeResponse.getContent()).isEqualTo(notice.getContent());
        assertThat(noticeResponse.getStartDate()).isEqualTo(notice.getStartDate());
        assertThat(noticeResponse.getEndDate()).isEqualTo(notice.getEndDate());

    }

    @Test
    @DisplayName("공지사항 삭제")
    public void delNoticeTest() throws Exception {
        // given
        NoticeRequest noticeRequest = NoticeRequest.builder()
                .id(1L)
                .build();

        LocalDate today = LocalDate.now();
        Notice notice = Notice.regNoticeBuilder()
                .id(noticeRequest.getId())
                .title("공지사항 테스트 제목")
                .content("공지사항 테스트 내용")
                .startDate(today)
                .endDate(today.plusDays(7))
                .viewCnt(0L)
                .regId("testUser")
                .regDate(LocalDateTime.now())
                .build();

        when(noticeRepository.findById(noticeRequest.getId())).thenReturn(Optional.of(notice));

        //when
        NoticeCommonResponse getNoticeResponse = noticeService.delNotice(noticeRequest.getId());

        // then
        verify(noticeRepository, times(1)).findById(noticeRequest.getId());
        verify(noticeRepository, times(1)).delete(notice);
        assertThat(getNoticeResponse).isNotNull();
        assertThat(getNoticeResponse.getNoticeId()).isEqualTo(notice.getId());
        assertThat(getNoticeResponse.getTitle()).isEqualTo(notice.getTitle());
        assertThat(getNoticeResponse.getContent()).isEqualTo(notice.getContent());
    }

}
