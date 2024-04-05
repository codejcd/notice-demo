package com.example.demo.notice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NoticeCommonResponse {
    private Long noticeId;

    private String title;

    private String content;

    private Long viewCnt;

    private LocalDate startDate;

    private LocalDate endDate;

    private String regId;

    private LocalDateTime regDate;

    @Builder
    public NoticeCommonResponse(Long noticeId, String title, String content, Long viewCnt, LocalDate startDate
            , LocalDate endDate, String regId, LocalDateTime regDate) {

        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.viewCnt = viewCnt;
        this.startDate = startDate;
        this.endDate = endDate;
        this.regId = regId;
        this.regDate = regDate;
    }

}
