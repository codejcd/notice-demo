package com.example.demo.notice.service;

import com.example.demo.notice.dto.FileDto;
import com.example.demo.notice.dto.NoticeCommonResponse;
import com.example.demo.notice.dto.NoticeDto;
import com.example.demo.notice.dto.NoticeRequest;
import com.example.demo.notice.dto.NoticeResponse;
import com.example.demo.notice.entity.Notice;
import com.example.demo.notice.entity.NoticeFile;
import com.example.demo.notice.repository.NoticeFileRepository;
import com.example.demo.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 공지사항 서비스
 */
@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    private final NoticeFileRepository noticeFileRepository;

    private final org.springframework.cache.CacheManager cacheManager;

    @Value("${file.upload.path}")
    private String uploadPath;

    /**
     * 공지사항 목록 조회
     * @param pageble
     * @return
     */
    public List<NoticeResponse> searchNotice(Pageable pageble) {
        Page<NoticeDto> list = noticeRepository.searchPage(null, pageble);
        List<NoticeResponse> resultList = new ArrayList<>();
        if (null != list && !list.getContent().isEmpty()) {
            list.stream().forEach(noticeDto -> {
                NoticeResponse noticeResponse = NoticeResponse.builder()
                        .noticeId(noticeDto.getNoticeId())
                        .title(noticeDto.getTitle())
                        .content(noticeDto.getContent())
                        .viewCnt(noticeDto.getViewCnt())
                        .regId(noticeDto.getRegId())
                        .regDate(noticeDto.getRegDate())
                        .build();
                resultList.add(noticeResponse);
            });
        } else {
            throw new NoSuchElementException();
        }
        return resultList;
    }

    /**
     * 공지사항 조회
     * @param noticeId
     * @return
     * @throws Exception
     */
    @Cacheable(cacheNames="getNoticeCache", key = "#noticeId")
    public NoticeResponse getNotice(Long noticeId) throws Exception {
        // Thread.sleep(1000);
        Optional<Notice> optionalNotice = noticeRepository.findById(noticeId);
        Notice savedNotice = optionalNotice.orElseThrow(() -> new NoSuchElementException());

        NoticeResponse response = NoticeResponse.builder()
                .noticeId(savedNotice.getId())
                .title(savedNotice.getTitle())
                .content(savedNotice.getContent())
                .viewCnt(savedNotice.getViewCnt())
                .regId(savedNotice.getRegId())
                .regDate(savedNotice.getRegDate())
                .build();


        return response;
    }

    /**
     * 공지사항 조회수 업데이트
     * @param noticeId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public NoticeDto updateNoticeViewCnt(Long noticeId) {
        Optional<Notice> optionalNotice = noticeRepository.findById(noticeId);
        Notice savedNotice = optionalNotice.orElseThrow(() -> new NoSuchElementException());
        Long viewCnt = savedNotice.getViewCnt();
        savedNotice.updateNoticeViewCnt(++viewCnt);
        // noticeRepository.save(savedNotice);

        NoticeDto noticeDto = NoticeDto.builder()
                .noticeId(savedNotice.getId())
                .title(savedNotice.getTitle())
                .content(savedNotice.getContent())
                .viewCnt(viewCnt)
                .regId(savedNotice.getRegId())
                .regDate(savedNotice.getRegDate())
                .build();

        return noticeDto;
    }

    /**
     * 공지사항 등록
     * @param noticeRequest
     * @param fileList
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public NoticeCommonResponse regNotice(NoticeRequest noticeRequest, List<MultipartFile> fileList) throws Exception {
        Notice notice = Notice.regNoticeBuilder()
                .title(noticeRequest.getTitle())
                .content(noticeRequest.getContent())
                .startDate(noticeRequest.getStartDate())
                .endDate(noticeRequest.getEndDate())
                .viewCnt(0L)
                .regDate(LocalDateTime.now())
                .regId(noticeRequest.getRegId())
                .build();

        Notice savedNotice = noticeRepository.save(notice);

        if (null != fileList && fileList.size() > 0) {
            List<FileDto> fileDtoList = this.uploadFile(fileList);
            fileDtoList.stream().forEach(item -> {
                NoticeFile noticeFile = NoticeFile.regNoticeFileBuilder()
                        .fileUri(item.getUploadPath())
                        .fileName(item.getUploadFileName())
                        .realFileName(item.getRealFileName())
                        .fileExt(item.getFileExt())
                        .notice(savedNotice)
                        .build();

                noticeFileRepository.save(noticeFile);
            });
        }

        NoticeCommonResponse noticeResponse = NoticeCommonResponse.builder()
                .noticeId(savedNotice.getId())
                .title(savedNotice.getTitle())
                .content(savedNotice.getContent())
                .viewCnt(savedNotice.getViewCnt())
                .startDate(savedNotice.getStartDate())
                .endDate(savedNotice.getEndDate())
                .regDate(savedNotice.getRegDate())
                .regId(savedNotice.getRegId())
                .build();

        return noticeResponse;
    }

    /**
     * 공지사항 수정
     * @param noticeRequest
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public NoticeCommonResponse modNotice(NoticeRequest noticeRequest) throws Exception {
        Long id = noticeRequest.getId();
        Optional<Notice> optionalNotice = noticeRepository.findById(id);
        Notice savedNotice = optionalNotice.orElseThrow(() -> new NoSuchElementException());
        savedNotice.updateNotice(noticeRequest.getTitle(), noticeRequest.getContent()
                , noticeRequest.getStartDate(), noticeRequest.getEndDate());

        NoticeCommonResponse noticeResponse = NoticeCommonResponse.builder()
                .noticeId(savedNotice.getId())
                .title(savedNotice.getTitle())
                .content(savedNotice.getContent())
                .viewCnt(savedNotice.getViewCnt())
                .startDate(savedNotice.getStartDate())
                .endDate(savedNotice.getEndDate())
                .regDate(savedNotice.getRegDate())
                .regId(savedNotice.getRegId())
                .build();

        return noticeResponse;
    }

    /**
     * 공지사항 삭제
     * @param noticeId
     * @return
     * @throws Exception
     */
    @CacheEvict(cacheNames = "getNoticeCache", key = "#noticeId")
    @Transactional(rollbackFor = Exception.class)
    public NoticeCommonResponse delNotice(Long noticeId) throws Exception {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoSuchElementException());

        List<NoticeFile> fileList = notice.getNoticeFileList();
        for (NoticeFile file : fileList) {
            if (null != fileList && fileList.size() > 0) {
                 if (!this.removeFile(file.getFileUri())) {
                     throw new FileNotFoundException();
                 }
            }
        }

        noticeRepository.delete(notice);
        NoticeCommonResponse noticeResponse = NoticeCommonResponse.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .viewCnt(notice.getViewCnt())
                .startDate(notice.getStartDate())
                .endDate(notice.getEndDate())
                .regDate(notice.getRegDate())
                .regId(notice.getRegId())
                .build();

        return noticeResponse;
    }

    /**
     * 파일 업로드
     * @param files
     * @return
     * @throws Exception
     */
    private List<FileDto> uploadFile(List<MultipartFile> files) throws Exception {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String year = today.substring(0, 4);
        String month = today.substring(4, 6);
        String day = today.substring(6, 8);
        List<FileDto> resultMapList = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileFullName = file.getOriginalFilename();
            int pos = fileFullName.lastIndexOf(".");
            String fileName = fileFullName.substring(0, pos);
            String fileNameExt = fileFullName.substring(pos + 1);
            String uploadFileName = String.format("%s_%s.%s", fileName, today, fileNameExt);
            String baseFilePath = uploadPath;

            String uploadDir = String.format("%s/notice/%s/%s/%s", baseFilePath, year, month, day);
            File folder = new File(uploadDir);

            if (!folder.exists()) {    // 해당 폴더가 없으면 폴더 생성
                folder.mkdirs();
            }

            String uploadPath = uploadDir + File.separator + uploadFileName;
            BufferedOutputStream stream = new BufferedOutputStream(Files.newOutputStream(Paths.get(uploadPath)));
            stream.write(file.getBytes());
            stream.close();

            FileDto fileDto = FileDto.builder()
                    .uploadPath(uploadPath)
                    .uploadFileName(uploadFileName)
                    .realFileName(fileName)
                    .fileExt(fileNameExt)
                    .build();

            resultMapList.add(fileDto);
        }

        return resultMapList;
    }

    /**
     * 파일 제거
     * @param filePath
     * @return
     * @throws Exception
     */
    private boolean removeFile(String filePath) throws Exception {
        boolean result = false;
        File file = new File(filePath);

        if (file.exists()) {
            result = file.delete();
        }
        return result;
    }

    /**
     * 캐시 제거
     * @param cacheName
     * @param key
     */
    public void clearCache(String cacheName, Long key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (null != cache) {
            cache.evict(key);
        }
    }
}
