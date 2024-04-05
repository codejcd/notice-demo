package com.example.demo.notice.entity;

import lombok.*;

import javax.persistence.*;

/**
 * 공지사항 파일 객체
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_file_id")
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "real_file_name")
    private String realFileName;

    @Column(name = "file_uri")
    private String fileUri;

    @Column(name = "file_ext")
    private String fileExt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @Builder(builderClassName = "regNoticeFileBuilder", builderMethodName = "regNoticeFileBuilder")
    public NoticeFile(Long id, String fileName, String fileUri, String fileExt, String realFileName, Notice notice) {
        this.id = id;
        this.fileName = fileName;
        this.fileUri = fileUri;
        this.fileExt = fileExt;
        this.realFileName = realFileName;
        this.notice = notice;
    }

}
