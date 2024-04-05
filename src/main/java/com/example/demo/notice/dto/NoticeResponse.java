package com.example.demo.notice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
public class NoticeResponse {

    private Long noticeId;

    private String title;

    private String content;

    private Long viewCnt;

    private String regId;

    private LocalDateTime regDate;

    @Builder
    public NoticeResponse(Long noticeId, String title, String content, Long viewCnt, String regId
            , LocalDateTime regDate) {

        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.viewCnt = viewCnt;
        this.regId = regId;
        this.regDate = regDate;
    }

}
