package org.example.service.cards;

import org.example.dto.CardDetailsDTO;
import org.example.persistence.dao.card.CardDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class CardQueryService {

    private Connection connection;

    public CardQueryService(Connection connection) {
        this.connection = connection;
    }

    public Optional<CardDetailsDTO> findById(Long id) throws SQLException {
        var dao = new CardDao(connection);
        return dao.findById(id);
    }
}