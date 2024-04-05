package com.example.demo.notice.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
public class FileDto {

    private String uploadPath;

    private String uploadFileName;

    private String realFileName;

    private String fileExt;

    @Builder
    public FileDto(String uploadPath, String uploadFileName, String realFileName, String fileExt) {
        this.uploadPath = uploadPath;
        this.uploadFileName = uploadFileName;
        this.realFileName = realFileName;
        this.fileExt = fileExt;
    }
}
