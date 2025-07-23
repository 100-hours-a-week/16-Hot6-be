ALTER TABLE `scraps`
  MODIFY COLUMN `created_at` TIMESTAMP NOT NULL
    DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN    `last_event_id` VARCHAR(32) NOT NULL
    DEFAULT '0',
  ADD COLUMN    `updated_at` TIMESTAMP    NOT NULL
    DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP;

UPDATE `scraps`
SET
  `last_event_id` = '0',
  `updated_at`    = `created_at`;

ALTER TABLE `scraps`
  ALTER COLUMN `last_event_id` DROP DEFAULT;