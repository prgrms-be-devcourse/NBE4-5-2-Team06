package org.example.bidflow.global.app;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// Redis 설정을 담당
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        configuration.setPassword(RedisPassword.of(password));
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>(); // 키와 타입을 지정
        // Redis는 데이터를 바이트 배열(byte[])로 저장하므로, Java 객체를 Redis에 저장하려면 직렬화가 필요하다.
        // 직렬화: 저장된 데이터를 사람이 읽기 쉬운 형태로  유지할 수 있다.
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // new StringRedisSerializer() 때문에 값도 문자열로 처리된다.
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }

    /*@Bean
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
    }*/
}