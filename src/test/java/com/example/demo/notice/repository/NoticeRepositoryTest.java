package com.example.demo.notice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.notice.dto.NoticeDto;
import com.example.demo.notice.entity.Notice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * 공지사항 저장소 단위 테스트
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class NoticeRepositoryTest {

    @Autowired
    private NoticeRepository noticeRepository;

    @Test
    @DisplayName("공지사항 저장")
    public void createNoticeTest() {
        Notice notice = Notice.regNoticeBuilder()
                .title("공지사항 테스트 제목")
                .content("공지사항 테스트 내용")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .regId("testUser1")
                .regDate(LocalDateTime.now())
                .build();
        Notice savedNotice = noticeRepository.save(notice);

        assertThat(savedNotice.getId()).isEqualTo(notice.getId());
        assertThat(savedNotice.getContent()).isEqualTo(notice.getContent());
        assertThat(savedNotice.getStartDate()).isEqualTo(notice.getStartDate());
        assertThat(savedNotice.getEndDate()).isEqualTo(notice.getEndDate());
        assertThat(savedNotice.getRegId()).isEqualTo(notice.getRegId());
        assertThat(savedNotice.getRegDate()).isEqualTo(notice.getRegDate());
    }

    @Test
    @DisplayName("공지사항 단건 조회")
    public void getNoticeTest() {
        Notice notice = Notice.regNoticeBuilder()
                .title("공지사항 테스트 제목")
                .content("공지사항 테스트 내용")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .regId("testUser1")
                .regDate(LocalDateTime.now())
                .build();
        noticeRepository.save(notice);

        Optional<Notice> result = noticeRepository.findById(notice.getId());
        assertThat(result.orElseThrow(NoSuchElementException::new)).isEqualTo(notice);
    }

    @Test
    @DisplayName("공지사항 목록 조회")
    public void searchNoticeTest() {
        Notice firstNotice = Notice.regNoticeBuilder()
                .title("공지사항 테스트 제목1")
                .content("공지사항 테스트 내용1")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .regId("testUser1")
                .regDate(LocalDateTime.now())
                .build();

        Notice secondNotice = Notice.regNoticeBuilder()
                .title("공지사항 테스트 제목2")
                .content("공지사항 테스트 내용2")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(8))
                .regId("testUser2")
                .regDate(LocalDateTime.now())
                .build();

        Notice firstSavedNotice = noticeRepository.save(firstNotice);
        Notice secondSavedNotice = noticeRepository.save(secondNotice);

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<NoticeDto> result = noticeRepository.searchPage(null, pageRequest);

        assertThat(result.get().count()).isEqualTo(2);
        assertThat(result.getContent()).extracting("noticeId").containsExactly(firstSavedNotice.getId()
                , secondSavedNotice.getId());
    }

    @Test
    @DisplayName("공지사항 수정")
    public void modNoticeTest() {
        String originalTitle = "공지사항 테스트 제목";
        String originalContent = "공지사항 테스트 내용";
        LocalDate originalStartDate = LocalDate.now();
        LocalDate originalEndDate = LocalDate.now().plusDays(7);

        Notice notice = Notice.regNoticeBuilder()
                .title(originalTitle)
                .content(originalContent)
                .startDate(originalStartDate)
                .endDate(originalEndDate)
                .regId("testUser1")
                .regDate(LocalDateTime.now())
                .build();

        Notice savedNotice = noticeRepository.save(notice);

        String otherTitle = "공지사항 다른 테스트 제목";
        String otherContent = "공지사항 다른 테스트 내용";
        LocalDate otherStartDate = LocalDate.now().plusDays(3);
        LocalDate otherEndDate = LocalDate.now().plusDays(9);
        savedNotice.updateNotice(otherTitle, otherContent,otherStartDate, otherEndDate);

        assertThat(savedNotice.getTitle()).isNotEqualTo(originalTitle);
        assertThat(savedNotice.getContent()).isNotEqualTo(originalContent);
        assertThat(savedNotice.getStartDate()).isNotEqualTo(originalStartDate);
        assertThat(savedNotice.getEndDate()).isNotEqualTo(originalEndDate);

        assertThat(savedNotice.getId()).isEqualTo(notice.getId());
        assertThat(savedNotice.getContent()).isEqualTo(notice.getContent());
        assertThat(savedNotice.getStartDate()).isEqualTo(notice.getStartDate());
        assertThat(savedNotice.getEndDate()).isEqualTo(notice.getEndDate());
        assertThat(savedNotice.getRegId()).isEqualTo(notice.getRegId());
        assertThat(savedNotice.getRegDate()).isEqualTo(notice.getRegDate());
    }

    @Test
    @DisplayName("공지사항 삭제")
    public void delNoticeTest() {
        Notice notice = Notice.regNoticeBuilder()
                .title("공지사항 테스트 제목")
                .content("공지사항 테스트 내용")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .regId("testUser1")
                .regDate(LocalDateTime.now())
                .build();

        Notice savedNotice = noticeRepository.save(notice);
        noticeRepository.delete(savedNotice);
        Optional<Notice> result = noticeRepository.findById(notice.getId());

        assertThat(result).isEqualTo(Optional.empty());
    }
}
