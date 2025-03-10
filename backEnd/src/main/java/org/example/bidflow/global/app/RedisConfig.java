package org.example.bidflow.global.app;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// Redis 설정을 담당
@Configuration
public class RedisConfig {
    @Bean
    // 이 메서드는 RedisTemplate을 설정하고 반환하는 메서드
    // RedisTemplate<String, String>은 Redis에서 데이터를 저장하고 조회하는 데 사용 여기서는 String 타입의 키와 값
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {

        // RedisTemplate을 생성. 키와 값 모두 String 타입으로 설정
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

        // Redis 서버와의 연결을 설정
        redisTemplate.setConnectionFactory(connectionFactory);

        // Redis에서 데이터를 직렬화/역직렬화할 때 사용될 직렬화 방법 설정
        // 여기서는 문자열 데이터를 처리하기 위해 StringRedisSerializer 사용
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // 키 직렬화 방법 설정
        redisTemplate.setValueSerializer(new StringRedisSerializer()); // 값 직렬화 방법 설정

        // 설정된 redisTemplate을 반환. 이제 이 Bean은 애플리케이션에서 사용할 수 있음
        return redisTemplate;
    }
}