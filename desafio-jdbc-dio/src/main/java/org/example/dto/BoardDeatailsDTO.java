package org.example.dto;

import java.util.List;

public record BoardDeatailsDTO(Long id, String name, List<BoardColumnDTO> columns){}