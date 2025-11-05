package de.vatrascell.nezr.category;

import de.vatrascell.nezr.model.Category;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.question.MultipleChoiceQuestionRepository;
import de.vatrascell.nezr.question.ShortAnswerQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MultipleChoiceQuestionRepository multipleChoiceQuestionRepository;
    private final ShortAnswerQuestionRepository shortAnswerQuestionRepository;

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

    public void setCategoryOnQuestion(long categoryId, long questionId, QuestionType questionType) {

        if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {
            multipleChoiceQuestionRepository.updateCategory(categoryId, questionId);
        } else {
            shortAnswerQuestionRepository.updateCategory(categoryId, questionId);
        }
    }

    private Category convert(de.vatrascell.nezr.category.Category category) {
        return new Category(category.getCategoryId(), category.getName());
    }
}
