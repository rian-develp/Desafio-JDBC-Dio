package org.example.persistence.entity.board;

import lombok.Data;
import org.example.persistence.entity.board_column.BoardColumnEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.example.persistence.entity.board_column.BoardColumnKindEnum.CANCEL;

@Data
public class BoardEntity {

    private Long id;
    private String name;
    private List<BoardColumnEntity> boardColumn = new ArrayList<>();

    public BoardColumnEntity getCancelColumn(){
        return getFilteredColumn(bc -> bc.getKind().equals(CANCEL));
    }

    private BoardColumnEntity getFilteredColumn(Predicate<BoardColumnEntity> filter){
        return boardColumn.stream().filter(filter).findFirst().orElseThrow();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BoardColumnEntity> getBoardColumn() {
        return boardColumn;
    }

    public void setBoardColumn(List<BoardColumnEntity> boardColumn) {
        this.boardColumn = boardColumn;
    }
}
