package com.example.demo.notice.repository;


import com.example.demo.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long>, CustomNoticeRepository {

}
