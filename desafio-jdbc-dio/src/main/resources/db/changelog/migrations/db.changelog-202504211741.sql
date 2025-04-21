--liquibase formatted sql
--changeset junior:202504211743
--comment: set blocks table


ALTER TABLE BLOCKS
MODIFY COLUMN unblock_reason TEXT NULL;

--rollback ALTER TABLE BLOCKS MODIFY COLUMN unblock_reason TEXT NOT NULL;