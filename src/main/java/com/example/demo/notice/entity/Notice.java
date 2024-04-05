package com.example.demo.notice.entity;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 공지사항 객체
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;
    private String title;

    private String content;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "view_cnt")
    private Long viewCnt;

    @Column(name = "reg_id")
    private String regId;

    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
    private List<NoticeFile> noticeFileList = new ArrayList<>();

    @Builder(builderClassName = "regNoticeBuilder", builderMethodName = "regNoticeBuilder")
    public Notice(Long id, String title, String content, LocalDate startDate,LocalDate endDate, Long viewCnt
    , String regId, LocalDateTime regDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.viewCnt = viewCnt;
        this.regId = regId;
        this.regDate = regDate;
    }

    public void updateNotice(String title, String content, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateNoticeViewCnt(Long viewCnt) {
        this.viewCnt = viewCnt;
    }

}
