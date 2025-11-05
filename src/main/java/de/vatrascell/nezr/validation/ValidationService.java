package de.vatrascell.nezr.validation;

import de.vatrascell.nezr.model.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final ValidationRepository validationRepository;
    private final ValidationMapper validationMapper;

    public Validation getValidation(int questionRelationId) {
        return validationMapper.mapValidation(
                validationRepository.findByShortAnswerRelationId(questionRelationId)
                        .orElse(null));
    }

    public void createValidation(Validation validation) {
        validationRepository.save(validationMapper.mapValidation(validation));
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
