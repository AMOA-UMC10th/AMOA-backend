package com.amoa.server.domain.notice.service.query;

import com.amoa.server.domain.notice.converter.NoticeConverter;
import com.amoa.server.domain.notice.dto.response.NoticeDetailResDTO;
import com.amoa.server.domain.notice.dto.response.NoticeListResDTO;
import com.amoa.server.domain.notice.entity.Notice;
import com.amoa.server.domain.notice.exception.code.NoticeErrorCode;
import com.amoa.server.domain.notice.repository.NoticeRepository;
import com.amoa.server.global.apiPayload.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeQueryService {

    private final NoticeRepository noticeRepository;

    public NoticeDetailResDTO getNoticeDetail(
            Long noticeId
    ) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() ->
                        new GeneralException(
                                NoticeErrorCode.NOTICE_NOT_FOUND
                        )
                );

        return NoticeConverter.toNoticeDetailResDTO(notice);
    }

    public List<NoticeListResDTO> getNoticeList() {

        List<Notice> notices =
                noticeRepository.findAllByOrderByCreatedAtDesc();

        return NoticeConverter.toNoticeListResDTOList(notices);
    }
}