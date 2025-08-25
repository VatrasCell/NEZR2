# API Documentation

## Overview

NEZR2 is primarily a desktop application built with JavaFX, so it doesn't expose traditional REST APIs. However, the application does have internal APIs in the form of service methods that are used by controllers to interact with the database and perform business logic.

## Service Layer APIs

### QuestionService

#### Methods

##### `getMaxQuestionPosition(int questionnaireId)`
Returns the maximum position of questions in a questionnaire.

##### `saveShortAnswerQuestion(int questionnaireId, Question question)`
Saves a short answer question to the database.

##### `saveMultipleChoice(int questionnaireId, Question question)`
Saves a multiple choice question to the database.

##### `doQuestionExistsInQuestionnaire(String question, int questionnaireId, QuestionType questionType)`
Checks if a question already exists in a questionnaire.

##### `getMultipleChoiceAnswerOptionsRelationIds(int multipleChoiceId)`
Gets the relation IDs for multiple choice answer options.

##### `getMultipleChoiceAnswersRelationId(int multipleChoiceId, int answerId)`
Gets the relation ID for a specific multiple choice answer option.

##### `getQuestionQuestionnaireRelationId(int questionnaireId, int questionId, QuestionType questionType)`
Gets the relation ID between a question and questionnaire.

##### `getQuestionId(String question, QuestionType questionType)`
Gets the ID of a question by its text and type.

### QuestionListService

#### Methods

##### `getQuestions(int questionnaireId)`
Retrieves all questions for a specific questionnaire.

##### `deleteQuestion(int questionnaireId, int questionId, QuestionType questionType)`
Deletes a question from a questionnaire.

### SurveyService

#### Methods

##### `getSurveys(int questionnaireId, String fromDate, String toDate)`
Retrieves surveys for a questionnaire within a date range.

##### `getAnswer(int surveyId, Question question)`
Gets the answer for a specific question in a survey.

### ExportService

#### Methods

##### `getSurveyCount()`
Returns the total count of surveys.

##### `getAnswerPositions(Question question, String fromDate, String toDate)`
Gets answer positions for a question within a date range.

##### `getAnswerPositions(Question question, AnswerOption answerOption, String fromDate, String toDate)`
Gets answer positions for a specific answer option within a date range.

### CategoryService

#### Methods

##### `getCategories()`
Retrieves all categories.

##### `createCategory(String name)`
Creates a new category.

### HeadlineService

#### Methods

##### `getHeadlines()`
Retrieves all headlines.

##### `createHeadline(String name)`
Creates a new headline.

## Controller APIs

### QuestionController

#### Methods

##### `save()`
Saves a question.

##### `createAnswer()`
Creates a new answer option.

##### `createCategory()`
Creates a new category.

##### `createHeadline()`
Creates a new headline.

##### `react()`
Handles react/conditional question functionality.

##### `validation()`
Handles validation functionality.

### QuestionListController

#### Methods

##### `deleteQuestion()`
Deletes a question.

##### `editQuestion()`
Edits a question.

### SurveyController

#### Methods

##### `saveSurvey()`
Saves a survey response.

### ExportController

#### Methods

##### `createExcelFile(Questionnaire questionnaire, String fromDate, String toDate)`
Creates an Excel file export of survey data.

## Database Access APIs

The application uses direct JDBC calls for database access. The SQL statements are defined in the `SqlStatement` class and used by service classes.

### Key SQL Operations

1. **Question Operations**
   - Create multiple choice questions
   - Create short answer questions
   - Update question positions
   - Delete questions

2. **Survey Operations**
   - Create survey records
   - Save survey answers
   - Retrieve survey data

3. **Export Operations**
   - Retrieve survey counts
   - Get answer positions
   - Aggregate survey data for export

## Internal Data Models

### Question
Represents a survey question with properties:
- question (String)
- questionId (Integer)
- questionType (QuestionType)
- category (Category)
- position (int)
- headline (Headline)
- flags (FlagList)
- answerOptions (List<AnswerOption>)

### Questionnaire
Represents a questionnaire with properties:
- id (long)
- creationDate (LocalDateTime)
- name (String)
- location (String)
- isActive (BooleanProperty)
- isFinal (BooleanProperty)

### Survey
Represents a survey response with properties:
- surveyId (int)
- creationDate (String)
- questionnaireId (int)

### Category
Represents a question category with properties:
- id (int)
- name (String)

### Headline
Represents a question headline with properties:
- id (int)
- name (String)

### AnswerOption
Represents a multiple choice answer option with properties:
- id (Integer)
- value (String)

### FlagList
Represents question flags with properties:
- required (boolean)
- evaluationQuestion (boolean)
- multipleChoice (boolean)
- list (boolean)
- textArea (boolean)
- yesNoQuestion (boolean)
- singleLine (boolean)
- reacts (List<React>)

## Error Handling

The application uses try-catch blocks for SQL operations and displays error messages to the user through the NotificationController. Database errors are logged to the console.