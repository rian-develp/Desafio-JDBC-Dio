package org.example.persistence.entity.card;

import lombok.Data;
import org.example.persistence.entity.board_column.BoardColumnEntity;

@Data
public class CardEntity {
    private Long id;
    private String title;
    private String description;
    private BoardColumnEntity boardColumnEntity = new BoardColumnEntity();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BoardColumnEntity getBoardColumnEntity() {
        return boardColumnEntity;
    }

    public void setBoardColumnEntity(BoardColumnEntity boardColumnEntity) {
        this.boardColumnEntity = boardColumnEntity;
    }
}