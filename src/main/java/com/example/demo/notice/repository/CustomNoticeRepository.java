package com.example.demo.notice.repository;

import com.example.demo.notice.dto.NoticeDto;
import com.example.demo.notice.dto.NoticeSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomNoticeRepository {

    Page<NoticeDto> searchPage(NoticeSearchCondition condition, Pageable pageable);
}
