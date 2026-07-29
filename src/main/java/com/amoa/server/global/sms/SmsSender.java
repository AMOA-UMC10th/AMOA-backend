package com.amoa.server.global.sms;

import com.amoa.server.domain.user.exception.UserException;
import com.amoa.server.domain.user.exception.code.UserErrorCode;
import com.solapi.sdk.SolapiClient;
import com.solapi.sdk.message.dto.request.SendRequestConfig;
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsSender {

    @Value("${solapi.api-key}")
    private String apiKey;

    @Value("${solapi.api-secret}")
    private String apiSecret;

    @Value("${solapi.sender-phone-number}")
    private String senderPhoneNumber;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init() {
        this.messageService = SolapiClient.INSTANCE.createInstance(apiKey, apiSecret);
    }

    public void send(String toPhoneNumber, String text) {
        Message message = new Message();
        message.setFrom(senderPhoneNumber);
        message.setTo(toPhoneNumber);
        message.setText(text);

        try {
            messageService.send(message, null);
        } catch (SolapiMessageNotReceivedException e) {
            log.error("SMS 발송 실패: {}", e.getFailedMessageList(), e);
            throw new UserException(UserErrorCode.SMS_SEND_FAILED);
        } catch (Exception e) {
            log.error("SMS 발송 중 알 수 없는 오류가 발생했습니다.", e);
            throw new UserException(UserErrorCode.SMS_SEND_FAILED);
        }
    }

    public String sendScheduled(String toPhoneNumber, String text, LocalDateTime scheduledAt) {
        Message message = new Message();
        message.setFrom(senderPhoneNumber);
        message.setTo(toPhoneNumber);
        message.setText(text);

        // Kotlin data class라 기본값 포함 전체 생성자로 호출
        SendRequestConfig config = new SendRequestConfig(null, false, false, null);
        config.setScheduledDateFromLocalDateTime(scheduledAt);

        try {
            return messageService.send(message, config)
                    .getGroupInfo()
                    .getGroupId();
        } catch (SolapiMessageNotReceivedException e) {
            log.error("SMS 예약발송 실패: {}", e.getFailedMessageList(), e);
            throw new UserException(UserErrorCode.SMS_SEND_FAILED);
        } catch (Exception e) {
            log.error("SMS 예약발송 중 알 수 없는 오류가 발생했습니다.", e);
            throw new UserException(UserErrorCode.SMS_SEND_FAILED);
        }
    }
}