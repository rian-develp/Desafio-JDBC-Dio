--liquibase formatted sql
--changeset junior:202504110907
--comment: cards table create

CREATE TABLE CARDS(

    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(24) NOT NULL,
    description TEXT NOT NULL,
    `order` INT NOT NULL,
    id_board_column BIGINT NOT NULL,
    CONSTRAINT boards_columns__cards_fk
    FOREIGN KEY (id_board_column)
    REFERENCES BOARDS_COLUMNS(id) ON DELETE CASCADE

)ENGINE=InnoDB;
--rollback DROP TABLE CARDS