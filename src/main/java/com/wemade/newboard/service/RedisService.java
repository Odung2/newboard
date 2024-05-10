package com.wemade.newboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * setValues()
     * key와 data를 Redis에 저장. 만료시간 설정 시 세 번째 파라미터로 Duration 객체 전달.
     * @param key
     * @param data
     */
    public void setValues(String key, String data){
        ValueOperations<String, Object> values=redisTemplate.opsForValue();
        values.set(key, data);
    }

    public void setValues(String key, String data, Duration duration) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    /**
     * getValues()
     * key 파라미터를 받아 key 기반으로 데이터 조회
     * @param key
     * @return
     */
    @Transactional(readOnly = true)
    public String getValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if (values.get(key) == null) {
            return "false";
        }
        return (String) values.get(key);
    }

    /**
     * deleteValues()
     * key 기반 데이터 삭제
     * @param key
     */
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    /**
     *
     * @param key
     * @param timeout
     */
    public void expireValues(String key, int timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
    }

    public void setHashOps(String key, Map<String, String> data) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        values.putAll(key, data);
    }

    @Transactional(readOnly = true)
    public String getHashOps(String key, String hashKey) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        return Boolean.TRUE.equals(values.hasKey(key, hashKey)) ? (String) redisTemplate.opsForHash().get(key, hashKey) : "";
    }

    public void deleteHashOps(String key, String hashKey) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        values.delete(key, hashKey);
    }

    /**
     * checkExistValue()
     * 조회 데이터가 없다면 false를 반환.
     * @param value
     * @return
     */
    public boolean checkExistsValue(String value) {
        return !value.equals("false");
    }


}
