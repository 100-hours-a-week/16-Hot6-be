CREATE TABLE users (
  id bigint NOT NULL AUTO_INCREMENT,
  email varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  role varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'USER',
  nickname_kakao varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  nickname_community varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  point int unsigned NOT NULL DEFAULT '0',
  image_path varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  is_active tinyint(1) NOT NULL DEFAULT '1',
  is_verified tinyint(1) NOT NULL DEFAULT '0',
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at timestamp NULL DEFAULT NULL,
  ai_image_generated_date datetime DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE oauth_tokens (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  provider varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  provider_id varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  access_token text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY fk_oauth_tokens_user (user_id),
  CONSTRAINT fk_oauth_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;




CREATE TABLE refresh_tokens (
  user_id bigint NOT NULL,
  refresh_token text,
  refresh_token_expiration timestamp NULL DEFAULT NULL,
  created_at timestamp NULL DEFAULT NULL,
  updated_at timestamp NULL DEFAULT NULL,
  PRIMARY KEY (user_id),
  CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



CREATE TABLE posts (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  type varchar(90) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  title varchar(78) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  content text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  comment_count int unsigned NOT NULL DEFAULT '0',
  like_count int unsigned NOT NULL DEFAULT '0',
  view_count bigint unsigned NOT NULL DEFAULT '0',
  scrap_count int unsigned NOT NULL DEFAULT '0',
  weight double NOT NULL DEFAULT '0',
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_type_created (type,created_at DESC),
  KEY idx_user_id_created (user_id,created_at DESC),
  KEY idx_type_view_count (type,view_count DESC,created_at DESC),
  KEY idx_type_like_count (type,like_count DESC,created_at DESC),
  KEY idx_created_at (created_at DESC,id DESC),
  CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT posts_chk_1 CHECK ((weight >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=194 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE post_images (
  id bigint NOT NULL AUTO_INCREMENT,
  post_id bigint NOT NULL,
  sequence int NOT NULL,
  image_uuid varchar(255) NOT NULL,
  PRIMARY KEY (id),
  KEY post_id_idx (post_id),
  CONSTRAINT post_id FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE ai_images (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  post_id bigint DEFAULT NULL,
  state varchar(30) NOT NULL DEFAULT 'PENDING',
  before_image_path varchar(255) NOT NULL,
  after_image_path varchar(255) DEFAULT NULL,
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY post_id_UNIQUE (post_id),
  KEY idx_user_id_created (user_id,created_at DESC),
  CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=247 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE comments (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  post_id bigint NOT NULL,
  content text NOT NULL,
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_post_id_created (post_id,created_at DESC),
  KEY fk_comments_user_idx (user_id),
  CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
  CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=385 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE likes (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  type varchar(30) NOT NULL,
  target_id bigint NOT NULL,
  is_active tinyint NOT NULL DEFAULT '1',
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY user_id_idx (user_id),
  CONSTRAINT user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=265 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE replies (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  comment_id bigint NOT NULL,
  content text NOT NULL,
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY fk_replies_user (user_id),
  KEY idx_comment_id_created (comment_id,created_at DESC),
  CONSTRAINT fk_replies_comment FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE CASCADE,
  CONSTRAINT fk_replies_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE scraps (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  type varchar(30) NOT NULL,
  target_id bigint NOT NULL,
  is_active tinyint(1) NOT NULL DEFAULT '1',
  created_at timestamp NOT NULL,
  PRIMARY KEY (id),
  KEY idx_user_id_created (user_id,created_at DESC),
  KEY idx_user_id_type_created (user_id,type,created_at DESC),
  CONSTRAINT fk_scraps_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE product_main_category (
  id bigint NOT NULL AUTO_INCREMENT,
  name varchar(30) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE product_sub_category (
  id bigint NOT NULL AUTO_INCREMENT,
  main_category_id bigint NOT NULL,
  name varchar(30) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY name_UNIQUE (name),
  KEY fk_main_category (main_category_id),
  CONSTRAINT fk_main_category FOREIGN KEY (main_category_id) REFERENCES product_main_category (id) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=134 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE desk_products (
  id bigint NOT NULL AUTO_INCREMENT,
  sub_category_id bigint NOT NULL,
  ai_image_id bigint DEFAULT NULL,
  name varchar(255) NOT NULL,
  price int NOT NULL,
  purchase_place varchar(255) NOT NULL,
  scrap_count int NOT NULL DEFAULT '0',
  click_count int NOT NULL DEFAULT '0',
  weight double NOT NULL DEFAULT '0',
  purchase_url varchar(255) NOT NULL,
  center_x int NOT NULL,
  center_y int NOT NULL,
  image_path varchar(255) NOT NULL,
  PRIMARY KEY (id),
  KEY fk_ai_image (ai_image_id),
  KEY idx_sub_category_weight (sub_category_id,weight DESC),
  KEY idx_sub_category_scrap (sub_category_id,scrap_count DESC),
  CONSTRAINT fk_ai_image FOREIGN KEY (ai_image_id) REFERENCES ai_images (id) ON DELETE CASCADE,
  CONSTRAINT fk_sub_category FOREIGN KEY (sub_category_id) REFERENCES product_sub_category (id) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3241 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
