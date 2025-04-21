package org.example.dto;

import org.example.persistence.entity.board_column.BoardColumnKindEnum;

public record BoardColumnDTO(Long id, String name, int order, BoardColumnKindEnum kind, int cardsAmount) {}