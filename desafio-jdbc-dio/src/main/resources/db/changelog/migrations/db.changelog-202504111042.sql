--liquibase formatted sql
--changeset junior:202504110907
--comment: blocks table create

CREATE TABLE BLOCKS(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_card BIGINT NOT NULL,
    title VARCHAR(24) NOT NULL,
    blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    block_reason TEXT NOT NULL,
    unblocked_at TIMESTAMP NULL,
    unblock_reason TEXT NOT NULL,
    CONSTRAINT cards__blocks_fk FOREIGN KEY (id_card) REFERENCES CARDS(id) ON DELETE CASCADE
)ENGINE=InnoDB;
--rollback DROP TABLE BLOCKS