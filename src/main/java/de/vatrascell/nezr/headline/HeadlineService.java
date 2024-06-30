package de.vatrascell.nezr.headline;

import de.vatrascell.nezr.application.Database;
import de.vatrascell.nezr.model.Headline;
import de.vatrascell.nezr.model.QuestionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static de.vatrascell.nezr.application.SqlStatement.SQL_SET_HEADLINE_ON_MULTIPLE_CHOICE;
import static de.vatrascell.nezr.application.SqlStatement.SQL_SET_HEADLINE_ON_SHORT_ANSWER;

@Service
@RequiredArgsConstructor
public class HeadlineService extends Database {

    private final HeadlineRepository headlineRepository;

    public List<Headline> getHeadlines() {
        return headlineRepository.findAll()
                .stream()
                .map(this::convert)
                .toList();
    }

    public Headline getHeadline(long headlineId) {
        return convert(headlineRepository.findById(headlineId)
                .orElseThrow());
    }

    public Headline getHeadlineByName(String name) {
        return convert(headlineRepository.findByName(name)
                .orElseThrow());
    }

    public Headline createHeadline(String name) {
        return convert(headlineRepository.findByName(name)
                .orElse(headlineRepository.save(new de.vatrascell.nezr.headline.Headline(name))));
    }

    public void setHeadlineOnQuestion(Connection connection, int headline, int multipleChoiceId, QuestionType questionType) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                    SQL_SET_HEADLINE_ON_MULTIPLE_CHOICE : SQL_SET_HEADLINE_ON_SHORT_ANSWER);
            psSql.setInt(1, headline);
            psSql.setInt(2, multipleChoiceId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private Headline convert(de.vatrascell.nezr.headline.Headline headline) {
        return new Headline(headline.getHeadlineId(), headline.getName());
    }
}
