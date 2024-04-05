package com.example.demo.notice.repository;

import com.example.demo.notice.dto.NoticeDto;
import com.example.demo.notice.dto.NoticeSearchCondition;
import com.example.demo.notice.dto.QNoticeDto;
import com.example.demo.notice.entity.Notice;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static com.example.demo.notice.entity.QNotice.notice;

/**
 * 공지사항 목록 조회 Repository 구현체
 */
public class NoticeRepositoryImpl implements CustomNoticeRepository {

    private final JPAQueryFactory queryFactory;

    public NoticeRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<NoticeDto> searchPage(NoticeSearchCondition condition, Pageable pageable) {
        List<NoticeDto> content = queryFactory.select(new QNoticeDto(
                        notice.id.as("noticeId"),
                        notice.title,
                        notice.content,
                        notice.viewCnt,
                        notice.regId,
                        notice.regDate
                        ))
                .from(notice)
                .where(
                        startDateLoe(),
                        endDateGoe()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Notice> countQuery = queryFactory.select(notice)
                .from(notice)
                .where(
                       startDateLoe(),
                        endDateGoe()
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    private BooleanExpression startDateLoe() {
        LocalDate now = LocalDate.now();
        return notice.startDate.loe(now);
    }

    private BooleanExpression endDateGoe() {
        LocalDate now = LocalDate.now();
        return notice.endDate.goe(now);
    }

}
