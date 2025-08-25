# User Guide

## Getting Started

### Installation
For installation instructions, please refer to the [Deployment Guide](./07_deployment_guide.md).

### First Launch
When you first launch NEZR2, you'll be presented with the location selection screen. Select the appropriate location for your survey work.

## Main Application Workflow

### 1. Location Selection
- Select your location from the list
- If your location is not in the list, contact your administrator

### 2. Start Screen
- Shows the active questionnaire
- Provides options to:
  - Start a new survey
  - Access admin functions (requires login)

### 3. Admin Functions
To access admin functions, click on "Admin Login":
- For development mode, use the dev login
- For production, enter admin credentials

#### Questionnaire Management
- Create new questionnaires
- Edit existing questionnaires
- Activate/deactivate questionnaires
- Export questionnaire data

#### Question Management
- Add new questions to questionnaires
- Edit existing questions
- Delete questions
- Set question positions
- Assign categories and headlines

### 4. Question Creation

#### Question Types
1. **Multiple Choice Questions**
   - Select "Multiple Choice" as question type
   - Add answer options
   - Set flags (required, list, etc.)

2. **Short Answer Questions**
   - Select "Short Answer" as question type
   - Set validation rules if needed
   - Set flags (required, text area, etc.)

#### Question Flags
- **Required**: Makes the question mandatory
- **Evaluation Question**: Creates a 0-9 rating question
- **List**: Allows multiple selections
- **Text Area**: Provides a larger text input field
- **Yes/No Question**: Creates a yes/no question with "ja"/"nein" options
- **Excel Format**: For yes/no questions, formats the answer for Excel export

#### Categories and Headlines
- **Categories**: Used to group related questions
- **Headlines**: Used to group questions under a common heading

### 5. Survey Administration

#### Starting a Survey
- From the start screen, click "Start"
- Follow the questions in order
- Answer all required questions

#### Saving Surveys
- Surveys are automatically saved when completed
- Survey data includes timestamp and answers

### 6. Data Export

#### Excel Export
- From the admin panel, select a questionnaire
- Choose "Excel Export"
- Select date range for export
- Exported files are saved in the `exportExcel` folder

## User Interface Guide

### Main Navigation
- Use the "Back" button to return to the previous screen
- Use the "Exit" button to return to the start screen

### Question Editing
- Use the question list to view and edit questions
- Click the edit icon to modify a question
- Click the delete icon to remove a question

### Answer Management
- For multiple choice questions, add answer options using the "New Answer" button
- Edit existing answer options using the edit button
- Delete answer options using the delete button

## Best Practices

### Questionnaire Design
1. Plan your questionnaire structure before creating questions
2. Use consistent categories and headlines
3. Set appropriate flags for each question
4. Test your questionnaire before using it in production

### Data Collection
1. Ensure all required questions are answered
2. Provide clear instructions to survey participants
3. Save surveys regularly during long sessions

### Data Export
1. Regularly export data to prevent loss
2. Use appropriate date ranges for exports
3. Store exported files in a secure location

## Troubleshooting

### Common Issues

#### Database Connection Errors
- Check that the database file exists in the `db` folder
- Ensure the application has read/write permissions to the database

#### Export Issues
- Check that the `exportExcel` folder exists and is writable
- Ensure there is sufficient disk space

#### Performance Issues
- For large questionnaires, consider breaking them into smaller sections
- Regularly export and archive old survey data

### Getting Help
For technical support, contact your system administrator or refer to the [Developer Guide](./06_developer_guide.md).