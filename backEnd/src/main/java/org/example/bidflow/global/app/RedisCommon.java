package org.example.bidflow.global.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j // 설명: 로깅을 위한 어노테이션
@RequiredArgsConstructor
@Service
// explain: Redis에 데이터를  주입하고 가져고 하는 등의 처리 메서드 모음
public class RedisCommon {
    private final RedisTemplate<String, String> template;
    private final Gson gson;
    private final Duration timeUnit = Duration.ofSeconds(5);

    /*@Value("${spring.data.redis.timeout}")
    private Duration defaultExpireTime;*/

    /*
    opsForValue는 Redis의 문자열(String) 값을 다루기 위한 여러 가지 작업을 제공하는 인터페이스입니다.
    이 인터페이스를 통해 Redis에서 문자열 값을 설정(set), 가져오기(get), 삭제(delete) 등의 작업을 수행할 수 있습니다.
     */

    // explain: redis 에서 데이터 받아오기 - 캐시된 데이터를 객체로 조회
    public <T> T getData(String key, Class<T> clazz) {
        String jsonValue = template.opsForValue().get(key);  // 키에 저장된 값을 문자열(JSON) 형태로 가져온다.
        if (jsonValue == null) {
            return null;
        }

        return gson.fromJson(jsonValue, clazz); // 역질렬화 왜하는 거야? : JSON 형태의 문자열을 객체로 변환하기 위해
    }

    public Set<String> getAllKeys() {
        return template.keys("*");
    }

    /*public List<String> getAllKeys() {
        return new ArrayList<>(template.keys("*"));
    }*/

    // explain: redis 에 데이터 저장하기 - 단일 데이터 저장
    public <T> void setData(String key, T value /*Duration expiredTime: 데이터 만료시간*/) {
        String jsonValue = gson.toJson(value); // 객체를 JSON 형태의 문자열로 변환 = 직렬화
        template.opsForValue().set(key, jsonValue);
        template.expire(key, /*defaultExpireTime*/ timeUnit);
    }

    // explain: redis 에 여러 데이터를 한번에 저장하기 - 대량 데이터 저장
    public <T> void multiSetdata(Map<String, T> datas) {
        Map<String, String> jsonMap = new HashMap<>();

        for (Map.Entry<String, T> entry : datas.entrySet()) {
            jsonMap.put(entry.getKey(), gson.toJson(entry.getValue()));
        }

        template.opsForValue().multiSet(jsonMap);  // 다중 키-값 쌍을 한 번의 명령으로 저장.
//        template.opsForValue().setIfAbsent() // 키가 존재하지 않을 때만 값을 설정
//        template.opsForValue().setIfPresent() // 키가 존재할 때만 값을 설정
    }

    // explain: redis 에 순위 및 랭킹을 매기기 위해 값을 저장
    public <T> void addToSortedSet(String key, T value, Float score) {
        String jsonValue = gson.toJson(value);
        template.opsForZSet().add(key, jsonValue, score);
    }

    // explain: redis 에서 원하는 범위의 데이터를 가져오기
    public <T> Set<T> rangeByScore(String key, Float minScore, Float maxScore, Class<T> clazz /*어떤 클래스로 직렬화 할지 정하기 위함*/) {
        Set<String> jsonValues = template.opsForZSet().rangeByScore(key, minScore, maxScore); // score가 minScore와 maxScore 사이에 있는 모든 멤버를 반환
        Set<T> resultSet = new HashSet<T>();

        if (jsonValues != null) {
            for (String jsonValue : jsonValues) {
                T v = gson.fromJson(jsonValue, clazz);
                resultSet.add(v);
            }
        }

        return resultSet;
    }

    // explain: redis 에서 상위 N개의 데이터를 가져오기
    public <T> List<T> getTopNFromSortedSet(String key, int n, Class<T> clazz) {
//        template.opsForZSet().range(key, 0, -1); //  key에 저장된 값들을 0부터 -1까지 가져온다. 즉, 전체 데이터를 가져온다.
//        template.opsForZSet().range(key, 0, -1);

        Set<String> jsonValues = template.opsForZSet().reverseRange(key, 0, n - 1);
        List<T> resultSet = new ArrayList<T>();

        if (jsonValues != null) {
            for (String jsonValue : jsonValues) {
                T v = gson.fromJson(jsonValue, clazz);
                resultSet.add(v);
            }
        }

        return resultSet;
    }

    // explain: Redis List의 왼쪽(앞)에 값을 추가
    public <T> void addToListLeft(String key, T value) {
        String jsonValue = gson.toJson(value);
        template.opsForList().leftPush(key, jsonValue);
//        template.expire(key, defaultExpireTime); // 만료시간을 넣지 않으면 기본적으로 0으로 설정된다.
    }

    // explain: Redis List의 오른쪽(앞)에 값을 추가
    public <T> void addToListRight(String key, T value) {
        String jsonValue = gson.toJson(value);
        template.opsForList().rightPush(key, jsonValue);
    }

    // explain: redis 에서 리스트의 모든 데이터를 가져오기
    public <T> List<T> getAllList(String key, Class<T> clazz) {
        List<String> jsonValues = template.opsForList().range(key, 0, -1);
        List<T> reusltSet = new ArrayList<>();


        if (jsonValues != null) {
            for (String jsonValue : jsonValues) {
                T value = gson.fromJson(jsonValue, clazz);
                reusltSet.add(value);
            }
        }

        return reusltSet;
    }

    // explain:  Redis List에서 특정 값 제거
    public <T> void removeFromList(String key, T value) {
        String jsonValue = gson.toJson(value);
        template.opsForList().remove(key, 1, jsonValue); // count: 제거할 개수, 첫번째 매칭만 제거
    }

    // explain: Redis Hash에 필드와 값을 추가. - 객체 속성 저장 (예: 사용자 프로필).
    public <T> void putInHash(String key, String field, T value) {
        String jsonValue = gson.toJson(value);
        template.opsForHash().put(key, field, jsonValue);
    }

    // explain: Redis Hash에서 특정 필드의 값을 조회. - 특정 속성 조회.
    public <T> T getFromHash(String key, String field, Class<T> clazz) {
        Object result = template.opsForHash().get(key, field);

        if (result != null) {
            return gson.fromJson(result.toString(), clazz);
//            return clazz.cast(result);
        }

        return null;
    }

    // explain: Redis에 복수개의 데이터를 한 번에 저장
    public <T> void putAllInHash(String key, Map<String, T> entries) {
        Map<Object, Object> mappedEntries = new HashMap<>();

        // Map의 각 항목을 Object 타입으로 변환
        for (Map.Entry<String, T> entry : entries.entrySet()) {
            mappedEntries.put(entry.getKey(), entry.getValue());
        }

        // Redis에 한 번에 저장
        template.opsForHash().putAll(key, mappedEntries);
    }

    public <T> T getHashAsObject(String key, Class<T> clazz) {
        Map<Object, Object> entries = template.opsForHash().entries(key);

        if (entries == null || entries.isEmpty()) {
            return null;
        }

        // ObjectMapper를 사용하여 Map을 객체로 변환
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(entries, clazz);
    }

    public <T> void putObjectAsHash(String key, T object) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.convertValue(object, new TypeReference<Map<String, Object>>() {});

        template.opsForHash().putAll(key, map);
    }

    // explain: Redis Hash에서 특정 필드를 삭제.
    public void removeFromhash(String key, String field) {
        template.opsForHash().delete(key, field);
    }

    /*// explain: 키의 값과 남은 TTL을 함께 조회. - 캐시 유효 기간 확인.
    public <T> ValueWithTTL<T> GetValueWithTTl(String key, Class<T> clazz) { // 값과 TTL을 함께 반환
        T value = null;                                                       // 반환할 값 초기화
        Long ttl = null;                                                      // TTL 초기화

        try {
            List<Object> results = template.executePipelined(new RedisCallback<Object>() { // 파이프라인으로 다중 명령 실행
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisConnection conn = (StringRedisConnection) connection; // 문자열 기반 연결로 캐스팅
                    conn.get(key);                                           // 키의 값 조회
                    conn.ttl(key);                                           // 키의 남은 TTL(초 단위) 조회
                    return null;                                             // 파이프라인에서는 null 반환
                }
            });

            value = (T) gson.fromJson((String) results.get(0), clazz);       // 첫 번째 결과(값)를 JSON 역직렬화
            ttl = (Long) results.get(1);                                     // 두 번째 결과(TTL) 할당
        } catch (Exception e) {
            e.printStackTrace();                                             // 예외 발생 시 스택 출력
        }

        return new ValueWithTTL<T>(value, ttl);                              // 값과 TTL을 담은 객체 반환
    }*/

    public void setExpireAt(String key, LocalDateTime expireTime) {
        LocalDateTime now = LocalDateTime.now();
        long secondsUntilExpire = Duration.between(now, expireTime).getSeconds();

        if (secondsUntilExpire > 0) {
            template.expire(key, secondsUntilExpire, TimeUnit.SECONDS);
        }
    }

    // TTL 반환1
    public Long getTTL(String key) {
        return template.getExpire(key, TimeUnit.SECONDS);
    }
    // TTL 반환2
    public Duration getRemainingTTL(String key) {
        Long seconds = template.getExpire(key, TimeUnit.SECONDS);
        if (seconds == null || seconds < 0) {
            return null; // 키가 존재하지 않거나 만료 시간이 설정되지 않은 경우
        }
        return Duration.ofSeconds(seconds);
    }
    // TTL 반환3
    public LocalDateTime getExpireTime(String key) {
        Long seconds = template.getExpire(key, TimeUnit.SECONDS);
        if (seconds == null || seconds < 0) {
            return null; // 키가 존재하지 않거나 만료 시간이 설정되지 않은 경우
        }
        return LocalDateTime.now().plusSeconds(seconds);
    }
}
