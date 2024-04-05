package com.example.demo.notice.controller;

import com.example.demo.notice.dto.NoticeCommonResponse;
import com.example.demo.notice.dto.NoticeDto;
import com.example.demo.notice.dto.NoticeRequest;
import com.example.demo.notice.dto.NoticeResponse;
import com.example.demo.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

/**
 * 공지사항 컨트롤러
 */
@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 공지사항 목록 조회
     * @param pageable
     * @return
     * @throws Exception
     */
    @GetMapping("/v1/notice")
    public ResponseEntity<?> searchNotice(Pageable pageable) throws Exception {
        List<NoticeResponse> response = noticeService.searchNotice(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 공지사항 조회
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("/v1/notice/{id}")
    public ResponseEntity<?> getNotice(@PathVariable Long id) throws Exception {
        NoticeDto noticeDto = noticeService.updateNoticeViewCnt(id);
        NoticeResponse response = noticeService.getNotice(noticeDto.getNoticeId());

        return ResponseEntity.ok(response);
    }

    /**
     * 공지사항 등록
     * @param noticeRequest
     * @param multipartFile
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/v1/notice", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> regNotice(@RequestPart(value="notice", required = true) NoticeRequest noticeRequest
            , @RequestPart(value="files", required = false) List<MultipartFile> multipartFile) throws Exception {

        NoticeCommonResponse response = noticeService.regNotice(noticeRequest, multipartFile);
        String uri = "/v1/notice/" + response.getNoticeId();
        return ResponseEntity.created(URI.create(uri)).build();
    }

    /**
     * 공지사항 수정
     * @param noticeRequest
     * @return
     * @throws Exception
     */
    @PatchMapping("/v1/notice")
    public ResponseEntity<?> modNotice(@RequestBody NoticeRequest noticeRequest) throws Exception {
        NoticeCommonResponse response = noticeService.modNotice(noticeRequest);
        noticeService.clearCache("getNoticeCache" , noticeRequest.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * 공지사항 삭제
     * @param id
     * @return
     * @throws Exception
     */
    @DeleteMapping("/v1/notice/{id}")
    public ResponseEntity<?> delNotice(@PathVariable Long id) throws Exception {
        NoticeCommonResponse response = noticeService.delNotice(id);
        noticeService.clearCache("getNoticeCache", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
