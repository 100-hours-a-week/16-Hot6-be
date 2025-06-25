CREATE TABLE `service_products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(30) NOT NULL,
  `status` varchar(30) NOT NULL DEFAULT 'ACTIVE',
  `name` varchar(50) NOT NULL,
  `description` text NOT NULL,
  `specification` json NOT NULL,
  `sales_count` int unsigned NOT NULL DEFAULT '0',
  `scrap_count` int unsigned NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `service_product_variants` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `status` varchar(30) NOT NULL DEFAULT 'ACTIVE',
  `name` varchar(50) NOT NULL,
  `price` int unsigned NOT NULL,
  `total_quantity` int unsigned NOT NULL,
  `reserved_quantity` int unsigned NOT NULL DEFAULT '0',
  `sold_quantity` int unsigned NOT NULL DEFAULT '0',
  `is_on_promotion` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_prod_variant` (`product_id`,`name`),
  KEY `fk_variant_to_product` (`product_id`),
  CONSTRAINT `fk_variant_to_product` FOREIGN KEY (`product_id`) REFERENCES `service_products` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `service_product_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `variant_id` bigint NOT NULL,
  `sequence` int NOT NULL,
  `image_uuid` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_image_to_product_idx` (`variant_id`),
  CONSTRAINT `fk_image_to_product` FOREIGN KEY (`variant_id`) REFERENCES `service_product_variants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `service_product_promotions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `variant_id` bigint NOT NULL,
  `status` varchar(30) NOT NULL DEFAULT 'ACTIVE',
  `type` varchar(30) NOT NULL,
  `name` varchar(50) NOT NULL,
  `original_price` int unsigned NOT NULL,
  `discount_price` int unsigned NOT NULL,
  `rate` decimal(3,0) NOT NULL,
  `total_quantity` int unsigned NOT NULL,
  `reserved_quantity` int unsigned NOT NULL DEFAULT '0',
  `sold_quantity` int unsigned NOT NULL DEFAULT '0',
  `start_at` timestamp NOT NULL,
  `end_at` timestamp NOT NULL,
  `max_per_customer` int unsigned NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_promotion_to_variant_idx` (`variant_id`),
  CONSTRAINT `fk_promotion_to_variant` FOREIGN KEY (`variant_id`) REFERENCES `service_product_variants` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `product_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `order_number` char(26) NOT NULL,
  `status` varchar(30) NOT NULL,
  `subtotal_amount` int NOT NULL,
  `discount_amount` int NOT NULL DEFAULT '0',
  `order_fingerprint` varchar(100) NOT NULL,
  `ordered_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `delivered_at` timestamp NULL DEFAULT NULL,
  `confirmed_at` timestamp NULL DEFAULT NULL,
  `canceled_at` timestamp NULL DEFAULT NULL,
  `refunded_at` timestamp NULL DEFAULT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_number` (`order_number`),
  KEY `fk_product_orders_user` (`user_id`),
  CONSTRAINT `fk_product_orders_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `variants_id` bigint NOT NULL,
  `promotion_id` bigint DEFAULT NULL,
  `status` varchar(30) NOT NULL DEFAULT 'PENDING',
  `original_price` int unsigned NOT NULL,
  `quantity` int unsigned NOT NULL,
  `discount_amount` int unsigned NOT NULL DEFAULT '0',
  `final_price` int unsigned NOT NULL,
  `refund_amount` int unsigned DEFAULT NULL,
  `refund_reason` varchar(30) DEFAULT NULL,
  `cancel_reason` varchar(30) DEFAULT NULL,
  `canceled_at` timestamp NULL DEFAULT NULL,
  `refunded_at` timestamp NULL DEFAULT NULL,
  `refund_requested_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_product_orders_order_idx` (`variants_id`),
  KEY `fk_product_orders_order_idx1` (`order_id`),
  KEY `fk_service_product_promotions_idx` (`promotion_id`),
  KEY `fk_product_orders_order_items_idx` (`order_id`,`variants_id`),
  CONSTRAINT `fk_product_orders_order_items` FOREIGN KEY (`order_id`) REFERENCES `product_orders` (`id`),
  CONSTRAINT `fk_service_product_promotions_order_items` FOREIGN KEY (`promotion_id`) REFERENCES `service_product_promotions` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_service_product_variants_order_items` FOREIGN KEY (`variants_id`) REFERENCES `service_product_variants` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `payment_method` varchar(30) NOT NULL DEFAULT 'POINT',
  `payment_status` varchar(30) NOT NULL DEFAULT 'SUCCESS',
  `payment_amount` int NOT NULL,
  `refunded_amount` int NOT NULL DEFAULT '0',
  `approve_number` varchar(50) NOT NULL,
  `paid_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_refunded_at` timestamp NULL DEFAULT NULL,
  `canceled_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_id` (`order_id`),
  UNIQUE KEY `approve_number` (`approve_number`),
  CONSTRAINT `fk_product_order_payments` FOREIGN KEY (`order_id`) REFERENCES `product_orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `point_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `amount` int unsigned NOT NULL,
  `balance_after` int unsigned NOT NULL,
  `type` varchar(20) NOT NULL,
  `description` varchar(30) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_point_hisotry_user` (`user_id`),
  CONSTRAINT `fk_point_hisotry_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `image_generation_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `date_key` date NOT NULL,
  `prompt_summary` text,
  `generated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_image_generation_history_user` (`user_id`),
  CONSTRAINT `fk_image_generation_history_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `ai_image_recommended_products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ai_image_id` bigint NOT NULL,
  `desk_product_id` bigint NOT NULL,
  `center_x` int DEFAULT NULL,
  `center_y` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_ai_image_desk_product` (`ai_image_id`,`desk_product_id`),
  KEY `fk_ai_image_recommended_products_desk_product` (`desk_product_id`),
  CONSTRAINT `fk_ai_image_recommended_products_ai_image` FOREIGN KEY (`ai_image_id`) REFERENCES `ai_images` (`id`),
  CONSTRAINT `fk_ai_image_recommended_products_desk_product` FOREIGN KEY (`desk_product_id`) REFERENCES `desk_products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;