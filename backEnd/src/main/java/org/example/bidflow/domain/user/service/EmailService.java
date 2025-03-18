package org.example.bidflow.domain.user.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bidflow.domain.user.repository.UserRepository;
import org.example.bidflow.global.app.RedisCommon;
import org.example.bidflow.global.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final RedisCommon redisCommon;
    private static final long VERIFICATION_CODE_EXPIRATION = 60 * 4; // 이메일 인증을 완료해야 하는 시간을 3분 + 여유으로 설정
    private static final long EMAIL_AUTH_EXPIRATION = 60 * 10; // 인증 후 10분간 유효
    private final UserRepository userRepository;

    public void sendVerificationCode(String email) {

        // 이메일이 이미 존재하는 경우 예외 발생
        if (userRepository.findByEmail(email).isPresent()) {
            log.error("Email {} already exists", email);
            throw new ServiceException(HttpStatus.CONFLICT.value() + "", "이미 존재하는 이메일입니다.");
        }

        String verificationCode = generateCode();  // 변수명 오타 수정
        String hashKey = getAuthHashKey(email);

        // Redis에 인증 코드 저장 및 만료 시간 설정
        redisCommon.putInHash(hashKey, "code", verificationCode);
        redisCommon.setExpireAt(hashKey, LocalDateTime.now().plusSeconds(VERIFICATION_CODE_EXPIRATION));

        try {
            // HTML 이메일을 전송하기 위해 MimeMessage 사용
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Verification Code");

            String text = "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #F1F1F1; padding: 20px;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 30px; background-color: #FFFFFF; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);'>" +
                    "<h2 style='color: #4CAF50; font-size: 24px; text-align: center;'>인증을 위한 이메일 인증번호</h2>" +
                    "<p style='font-size: 16px; color: #333;'>안녕하세요, <strong>회원님</strong>.</p>" +
                    "<p style='font-size: 16px; color: #555;'>요청하신 인증 번호는 아래와 같습니다:</p>" +
                    "<div style='text-align: center; padding: 20px; background-color: #F9F9F9; border-radius: 8px; margin: 20px 0;'>" +
                    "<h1 style='font-size: 36px; color: #4CAF50; font-weight: bold;'>" + verificationCode + "</h1>" +
                    "<p style='font-size: 16px; color: #555;'>이 코드를 입력하여 이메일 인증을 완료하세요.</p>" +
                    "</div>" +
                    "<p style='font-size: 14px; color: #777;'>감사합니다!</p>" +
                    "<footer style='font-size: 12px; color: #aaa; text-align: center;'>" +
                    "<p>&copy; 2025 Your Company</p>" +
                    "</footer>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(text, true); // HTML 이메일 전송 설정
            mailSender.send(message);
            log.error("Send verification code failed: {}", email);

        } catch (Exception e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value() + "", "이메일 전송 중 오류가 발생했습니다.");
        }
    }

    public boolean vertifyCode(String email, String code) {

        String hashKey = getAuthHashKey(email);

        // + 이메일 인증번호 입력시간 3분 설정한 ttl이 만료되면 예외 처리하기

        String storedCode = redisCommon.getFromHash(hashKey, "code", String.class);
        // 3분이라는 TTL만료
        if (storedCode == null) {
            log.error("Verification time expired for email(인증시간 만료): {}", email);
            throw new ServiceException("400", "인증시간이 만료되었습니다.");
        }

        if (storedCode.equals(code)) {
            redisCommon.putInHash(hashKey, "vertify", "true");
            redisCommon.setExpireAt(hashKey, LocalDateTime.now().plusSeconds(EMAIL_AUTH_EXPIRATION));

            log.error("Verification code matched for email(인증 코드 불일치): {}", email);

            return true;
        }

        log.error("Verification code does not match for email(뭔가 문제가 생김(인증은 통과)): {}", email);

        return false;
    }

    public boolean isVerified(String email) {

        String hashKey = getAuthHashKey(email);
        String checkVertified = redisCommon.getFromHash(hashKey, "vertify", String.class);

        return "true".equals(checkVertified);
    }

    public void deleteVerificationCode(String key) {

        String authHashKey = getAuthHashKey(key);
        redisCommon.setExpireAt(authHashKey, LocalDateTime.now().plusSeconds(10));
    }

    public boolean isVerificationExpired(String email) {
        String hashKey = getAuthHashKey(email);
        Long ttl = redisCommon.getTTL(hashKey); // TTL을 초 단위로 가져옴
        return (ttl == null || ttl <= 0);
    }

    private String generateCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    private static String getAuthHashKey(String email) {
        return "auth:" + email;
    }


}
