package de.vatrascell.nezr.category;

import de.vatrascell.nezr.application.Database;
import de.vatrascell.nezr.model.Category;
import de.vatrascell.nezr.model.QuestionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static de.vatrascell.nezr.application.SqlStatement.SQL_SET_CATEGORY_ON_MULTIPLE_CHOICE;
import static de.vatrascell.nezr.application.SqlStatement.SQL_SET_CATEGORY_ON_SHORT_ANSWER;

@Service
@RequiredArgsConstructor
public class CategoryService extends Database {

    private CategoryRepository categoryRepository;

    public List<Category> getCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convert)
                .toList();
    }

    public Category getCategory(String name) {

        return convert(categoryRepository.findByName(name)
                .orElseThrow());
    }

    public Category createCategory(String name) {
        return convert(categoryRepository.findByName(name)
                .orElse(categoryRepository.save(new de.vatrascell.nezr.category.Category(name))));
    }

    public void setCategoryOnQuestion(Connection connection, int categoryId, int questionId, QuestionType questionType) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                    SQL_SET_CATEGORY_ON_MULTIPLE_CHOICE : SQL_SET_CATEGORY_ON_SHORT_ANSWER);
            psSql.setInt(1, categoryId);
            psSql.setInt(2, questionId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private Category convert(de.vatrascell.nezr.category.Category category) {
        return new Category(category.getId(), category.getName());
    }
}
