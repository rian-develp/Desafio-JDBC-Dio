--liquibase formatted sql
--changeset junior:202504110907
--comment: board table create

CREATE TABLE BOARDS(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(42) NOT NULL
)ENGINE=InnoDB;
--rollback DROP TABLE BOARDS