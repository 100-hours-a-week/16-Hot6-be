ALTER TABLE `ai_images`
  ADD COLUMN `concept` varchar(45) NOT NULL DEFAULT 'BASIC' AFTER `post_id`;

ALTER TABLE `desk_products`
  DROP FOREIGN KEY `fk_ai_image`,
  DROP KEY `fk_ai_image`,
  DROP COLUMN `ai_image_id`,
  DROP COLUMN `center_x`,
  DROP COLUMN `center_y`,
  ADD COLUMN `product_code` varchar(45) NOT NULL AFTER `sub_category_id`,
  ADD UNIQUE KEY `product_code_UNIQUE` (`product_code`);

ALTER TABLE `likes`
  DROP FOREIGN KEY `user_id`,
  DROP KEY `user_id_idx`,
  DROP COLUMN `type`,
  CHANGE COLUMN `target_id` `post_id` bigint NOT NULL,
  ADD KEY `post_id_idx` (`post_id`),
  ADD UNIQUE KEY `uq_user_post_like` (`user_id`,`post_id`),
  ADD CONSTRAINT `fk_posts_likes` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_users_likes` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT;

ALTER TABLE `product_main_category`
  ADD UNIQUE KEY `name_UNIQUE` (`name`);