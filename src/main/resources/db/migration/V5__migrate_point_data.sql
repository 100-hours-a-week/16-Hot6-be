-- ⚠️ 운영 환경에서만 사용. dev/test에서는 절대 중복 실행 금지

INSERT INTO point_history (user_id, amount, balance_after, type, description, created_at)
SELECT id, point, point, 'EARN', 'CHANGE_DATA', NOW()
FROM users
WHERE point > 0;