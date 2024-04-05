package com.example.demo.notice.dto;

import com.example.demo.notice.entity.Notice;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDate;


@Getter
@ToString
@NoArgsConstructor
public class NoticeRequest {

    @JsonProperty("noticeId")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @JsonProperty("startDate")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @JsonProperty("endDate")
    private LocalDate endDate;

    @JsonProperty("viewCnt")
    private Long viewCnt;

    @JsonProperty("regId")
    private String regId;

    @Builder
    public NoticeRequest(Long id, String title, String content, LocalDate startDate, LocalDate endDate, Long viewCnt
    , String regId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.viewCnt = viewCnt;
        this.regId = regId;
    }

    public Notice toEntity() {
        Notice notice = Notice.regNoticeBuilder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .viewCnt(this.viewCnt)
                .regId(this.regId)
                .build();

        return notice;
    }
}
