package de.vatrascell.nezr.category;

import de.vatrascell.nezr.model.Category;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.question.MultipleChoiceQuestionRepository;
import de.vatrascell.nezr.question.ShortAnswerQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final MultipleChoiceQuestionRepository multipleChoiceQuestionRepository;
    private final ShortAnswerQuestionRepository shortAnswerQuestionRepository;

    public List<Category> getCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::mapCategory)
                .toList();
    }

    public Category getCategory(String name) {

        return categoryMapper.mapCategory(
                categoryRepository.findByName(name)
                        .orElseThrow());
    }

    public Category createCategory(String name) {
        var existingCategory = categoryRepository.findByName(name);
        if (existingCategory.isPresent()) {
            return categoryMapper.mapCategory(existingCategory.get());
        }
        try {
            var newCategory = categoryRepository.save(new de.vatrascell.nezr.category.Category(name));
            return categoryMapper.mapCategory(newCategory);
        } catch (DataIntegrityViolationException e) {
            // Duplicate name, fetch existing
            return categoryMapper.mapCategory(categoryRepository.findByName(name).orElseThrow());
        }
    }

    public void setCategoryOnQuestion(long categoryId, long questionId, QuestionType questionType) {

        if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {
            multipleChoiceQuestionRepository.updateCategory(categoryId, questionId);
        } else {
            shortAnswerQuestionRepository.updateCategory(categoryId, questionId);
        }
    }
}
