-- 외래 키 제약 조건 일시 비활성화
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE desk_products;
TRUNCATE TABLE product_sub_category;
TRUNCATE TABLE product_main_category;
TRUNCATE TABLE likes;
TRUNCATE TABLE scraps;

SET FOREIGN_KEY_CHECKS = 1;