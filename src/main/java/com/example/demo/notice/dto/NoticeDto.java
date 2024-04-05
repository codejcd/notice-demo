package com.example.demo.notice.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NoticeDto {

    private Long noticeId;

    private String title;

    private String content;

    private Long viewCnt;

    private String regId;

    private LocalDateTime regDate;

    @Builder
    @QueryProjection
    public NoticeDto(Long noticeId, String title, String content, Long viewCnt, String regId
            , LocalDateTime regDate) {

        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.viewCnt = viewCnt;
        this.regId = regId;
        this.regDate = regDate;
    }
}
