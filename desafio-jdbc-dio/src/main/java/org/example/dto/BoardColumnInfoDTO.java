package org.example.dto;

import org.example.persistence.entity.board_column.BoardColumnKindEnum;

public record BoardColumnInfoDTO(Long id, int order, BoardColumnKindEnum kind){
}
