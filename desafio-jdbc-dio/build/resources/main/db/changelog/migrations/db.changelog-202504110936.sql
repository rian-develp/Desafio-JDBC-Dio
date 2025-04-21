--liquibase formatted sql
--changeset junior:202504110907
--comment: boards_columns table create

CREATE TABLE BOARDS_COLUMNS(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(42) NOT NULL,
    `order` INT NOT NULL,
    kind VARCHAR(7) NOT NULL,
    id_board INT NOT NULL,
    CONSTRAINT boards__boards_column_fk FOREIGN KEY (id_board) REFERENCES BOARDS(id) ON DELETE CASCADE,
    CONSTRAINT id_order_uk UNIQUE KEY unique_board_id_order (id_board, `order`)
)ENGINE=InnoDB;
--rollback DROP TABLE BOARDS_COLUMNS