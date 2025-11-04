package de.vatrascell.nezr.validation;

import de.vatrascell.nezr.application.Database;
import de.vatrascell.nezr.model.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static de.vatrascell.nezr.application.SqlStatement.SQL_CREATE_VALIDATION;

@Service
@RequiredArgsConstructor
public class ValidationService extends Database {

    private final ValidationRepository validationRepository;
    private final ValidationMapper validationMapper;

    public Validation getValidation(int questionRelationId) {
        return validationMapper.mapValidation(
                validationRepository.findByShortAnswerRelationId(questionRelationId)
                        .orElse(null));
    }

    public void createValidation(Connection connection, Validation validation) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_CREATE_VALIDATION);
            psSql.setBoolean(1, validation.isNumbers());
            psSql.setBoolean(2, validation.isLetters());
            psSql.setBoolean(3, validation.isAlphanumeric());
            psSql.setBoolean(4, validation.isAllChars());
            psSql.setBoolean(5, validation.isRegex());
            psSql.setBoolean(6, validation.isHasLength());
            psSql.setString(7, validation.getRegex());
            psSql.setInt(8, validation.getMinLength());
            psSql.setInt(9, validation.getMaxLength());
            psSql.setInt(10, validation.getLength());
            psSql.execute();

        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    public Validation save(Validation validation) {
        return validationMapper.mapValidation(validationRepository.save(validationMapper.mapValidation(validation)));
    }

    public Long getLastValidationId() {
        return validationRepository.findAll().stream()
                .mapToLong(de.vatrascell.nezr.validation.Validation::getValidationId)
                .max()
                .orElse(0L);
    }
}
