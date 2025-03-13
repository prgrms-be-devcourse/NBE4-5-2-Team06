package org.example.bidflow.global.app;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.user.repository.UserRepository;
import org.example.bidflow.global.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final RedisCommon redisCommon;
    private static final long VERICATION_CODE_EXPIRATION = 60 * 3; // 이메일 인증을 완료해야 하는 시간을 3분으로 설정
    private static final long EMAIL_AUTH_EXPIRATION = 60 * 10; // 인증 후 10분간 유효
    private final UserRepository userRepository;

    public void sendVerificationCode(String email) {

        // 이메일이 이미 존재하는 경우 예외 발생
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ServiceException(HttpStatus.CONFLICT.value() + "", "이미 존재하는 이메일입니다.");
        }
        String vertificationCode = generateCode();
        String hashKey = getAuthHashKey(email);

        redisCommon.putInHash(hashKey,"code",vertificationCode);
        redisCommon.setExpireAt(hashKey, LocalDateTime.now().plusSeconds(VERICATION_CODE_EXPIRATION));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verification Code");
        message.setText("인증 코드: " + vertificationCode);

        mailSender.send(message);
    }

    public boolean vertifyCode(String email, String code) {

        String hashKey = getAuthHashKey(email);

        // + 이메일 인증번호 입력시간 3분 설정한 ttl이 만료되면 예외 처리하기

        String storedCode = redisCommon.getFromHash(hashKey, "code", String.class);
        // 3분이라는 TTL만료
        if(storedCode == null){
            throw new ServiceException("400","인증시간이 만료되었습니다.");
        }
        if(storedCode.equals(code)) {
            redisCommon.putInHash(hashKey, "vertify", "true");
            redisCommon.setExpireAt(hashKey, LocalDateTime.now().plusSeconds(EMAIL_AUTH_EXPIRATION));
            return true;
        }

        return false;
    }

    public boolean isVerified(String email) {

        String hashKey = getAuthHashKey(email);
        String checkVertified = redisCommon.getFromHash(hashKey, "vertify", String.class);

        return "true".equals(checkVertified);
    }

    private String generateCode() {
        return String.valueOf((int)(Math.random()* 900000) + 100000);
    }


    public boolean isVerificationExpired(String email) {
        String hashKey = getAuthHashKey(email);
        Long ttl = redisCommon.getTTL(hashKey); // TTL을 초 단위로 가져옴
        return (ttl == null || ttl <= 0);
    }

    private static String getAuthHashKey(String email) {
        return "auth:" + email;
    }
}
