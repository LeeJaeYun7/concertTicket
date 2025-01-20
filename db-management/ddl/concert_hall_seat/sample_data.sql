-- 올림픽공원(id=1), 좌석 수 3152석
SET @rownum := 0;

INSERT INTO concert_hall_seat (concert_hall_id, number, created_at, updated_at)
SELECT 1, n, NOW(), NOW()
FROM (SELECT @rownum := @rownum + 1 AS n
      FROM information_schema.columns a, information_schema.columns b
      LIMIT 3152) AS numbers;

-- 블루스퀘어(id=2), 좌석 수 3389석

SET @rownum := 0;

INSERT INTO concert_hall_seat (concert_hall_id, number, created_at, updated_at)
SELECT 2, n, NOW(), NOW()
FROM (SELECT @rownum := @rownum + 1 AS n
      FROM information_schema.columns a, information_schema.columns b
      LIMIT 3389) AS numbers;


-- 경희대학교평화의전당(id=3), 좌석 수 4400석

SET @rownum := 0;

INSERT INTO concert_hall_seat (concert_hall_id, number, created_at, updated_at)
SELECT 3, n, NOW(), NOW()
FROM (SELECT @rownum := @rownum + 1 AS n
      FROM information_schema.columns a, information_schema.columns b
      LIMIT 4400) AS numbers;


