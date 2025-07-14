local cursor = KEYS[1]
local pattern = ARGV[1]
local count = tonumber(ARGV[2])

-- SCAN 명령 실행
local result = redis.call('SCAN', cursor, 'MATCH', pattern, 'COUNT', count)

-- 다음 커서 위치
local next_cursor = result[1]

-- 찾은 키 목록
local keys = result[2]

-- 찾은 키가 있으면 UNLINK 명령으로 삭제 (비동기 백그라운드 삭제)
if #keys > 0 then
    redis.call('UNLINK', unpack(keys))
end

-- 다음 커서 위치를 반환. '0'이면 스캔이 끝났음을 의미.
return next_cursor