ALTER TABLE `likes`
  ADD COLUMN `last_event_id` VARCHAR(32) NOT NULL
    DEFAULT '0',
  ADD COLUMN `updated_at`   TIMESTAMP    NOT NULL
    DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP;

UPDATE `likes`
SET
  `last_event_id` = '0',
  `updated_at`    = `created_at`;

ALTER TABLE `likes`
  ALTER COLUMN `last_event_id` DROP DEFAULT;