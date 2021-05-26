-- -----------------------------------------------------
-- Table `location`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `location` ;

CREATE TABLE IF NOT EXISTS `location` (
  `location_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`location_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `questionnaire`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `questionnaire` ;

CREATE TABLE IF NOT EXISTS `questionnaire` (
  `questionnaire_id` INT NOT NULL AUTO_INCREMENT,
  `creation_date` DATE NOT NULL,
  `name` TINYTEXT NOT NULL,
  `is_active` TINYINT(1) NOT NULL,
  `location_id` INT NOT NULL,
  `is_final` TINYINT(1) NOT NULL,
  PRIMARY KEY (`questionnaire_id`),
  INDEX `fk_Fragebogen_Ort1` (`location_id` ASC),
  CONSTRAINT `fk_Fragebogen_Ort1`
    FOREIGN KEY (`location_id`)
    REFERENCES `location` (`location_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `category`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `category` ;

CREATE TABLE IF NOT EXISTS `category` (
  `category_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`category_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `headline`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `headline` ;

CREATE TABLE IF NOT EXISTS `headline` (
  `headline_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`headline_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `multiple_choice`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `multiple_choice` ;

CREATE TABLE IF NOT EXISTS `multiple_choice` (
  `multiple_choice_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `question` TINYTEXT NOT NULL,
  `category_id` INT NOT NULL,
  `headline_id` INT NULL,
  PRIMARY KEY (`multiple_choice_id`),
  INDEX `fk_MultipleChoice_Kategorie1` (`category_id` ASC),
  INDEX `fk_multiple_choice_headline1_idx` (`headline_id` ASC),
  CONSTRAINT `fk_MultipleChoice_Kategorie1`
    FOREIGN KEY (`category_id`)
    REFERENCES `category` (`category_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_multiple_choice_headline1`
    FOREIGN KEY (`headline_id`)
    REFERENCES `headline` (`headline_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `q_has_mc`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `q_has_mc` ;

CREATE TABLE IF NOT EXISTS `q_has_mc` (
  `q_mc_relation_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `questionnaire_id` INT NOT NULL,
  `multiple_choice_id` INT UNSIGNED NOT NULL,
  `position` TINYINT NOT NULL,
  PRIMARY KEY (`q_mc_relation_id`),
  INDEX `fk_MultipleChoice_has_Fragebogen_MultipleChoice` (`multiple_choice_id` ASC),
  INDEX `fk_MultipleChoice_has_Fragebogen_Fragebogen1` (`questionnaire_id` ASC),
  CONSTRAINT `fk_MultipleChoice_has_Fragebogen_MultipleChoice`
    FOREIGN KEY (`multiple_choice_id`)
    REFERENCES `multiple_choice` (`multiple_choice_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_MultipleChoice_has_Fragebogen_Fragebogen1`
    FOREIGN KEY (`questionnaire_id`)
    REFERENCES `questionnaire` (`questionnaire_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `short_answer`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `short_answer` ;

CREATE TABLE IF NOT EXISTS `short_answer` (
  `short_answer_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `question` TINYTEXT NOT NULL,
  `category_id` INT NOT NULL,
  `headline_headline_id` INT NULL,
  PRIMARY KEY (`short_answer_id`),
  INDEX `fk_FreieFragen_Kategorie1` (`category_id` ASC),
  INDEX `fk_FreieFragen_headline1_idx` (`headline_headline_id` ASC),
  CONSTRAINT `fk_FreieFragen_Kategorie1`
    FOREIGN KEY (`category_id`)
    REFERENCES `category` (`category_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_FreieFragen_headline1`
    FOREIGN KEY (`headline_headline_id`)
    REFERENCES `headline` (`headline_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `validation` ;

CREATE TABLE IF NOT EXISTS `validation` (
  `validation_id` INT NOT NULL AUTO_INCREMENT,
  `is_numbers` TINYINT NOT NULL DEFAULT 0,
  `is_letters` TINYINT NOT NULL DEFAULT 0,
  `is_alphanumeric` TINYINT NOT NULL DEFAULT 0,
  `is_all_chars` TINYINT NOT NULL DEFAULT 0,
  `is_regex` TINYINT NOT NULL DEFAULT 0,
  `has_length` TINYINT NOT NULL DEFAULT 0,
  `regex` VARCHAR(255) NOT NULL,
  `min_length` INT NULL,
  `max_length` INT NULL,
  `length` INT NULL,
  PRIMARY KEY (`validation_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `q_has_sa`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `q_has_sa` ;

CREATE TABLE IF NOT EXISTS `q_has_sa` (
  `q_sa_relation_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `questionnaire_id` INT NOT NULL,
  `short_answer_id` INT UNSIGNED NOT NULL,
  `position` TINYINT NOT NULL,
  `validation_id` INT NULL,
  PRIMARY KEY (`q_sa_relation_id`),
  INDEX `fk_FreieFragen_has_Fragebogen_FreieFragen1` (`short_answer_id` ASC),
  INDEX `fk_FreieFragen_has_Fragebogen_Fragebogen1` (`questionnaire_id` ASC),
  INDEX `fk_q_has_sa_validation1_idx` (`validation_id` ASC),
  CONSTRAINT `fk_FreieFragen_has_Fragebogen_FreieFragen1`
    FOREIGN KEY (`short_answer_id`)
    REFERENCES `short_answer` (`short_answer_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_FreieFragen_has_Fragebogen_Fragebogen1`
    FOREIGN KEY (`questionnaire_id`)
    REFERENCES `questionnaire` (`questionnaire_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_q_has_sa_validation1`
    FOREIGN KEY (`validation_id`)
    REFERENCES `validation` (`validation_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `survey`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `survey` ;

CREATE TABLE IF NOT EXISTS `survey` (
  `survey_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `creation_date` DATE NOT NULL,
  `questionnaire_id` INT NOT NULL,
  PRIMARY KEY (`survey_id`),
  INDEX `fk_Befragung_Fragebogen1` (`questionnaire_id` ASC),
  CONSTRAINT `fk_Befragung_Fragebogen1`
    FOREIGN KEY (`questionnaire_id`)
    REFERENCES `questionnaire` (`questionnaire_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `answer`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `answer` ;

CREATE TABLE IF NOT EXISTS `answer` (
  `answer_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(2000) NULL DEFAULT NULL,
  PRIMARY KEY (`answer_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `sa_has_a`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sa_has_a` ;

CREATE TABLE IF NOT EXISTS `sa_has_a` (
  `sa_a_relation_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `short_answer_id` INT UNSIGNED NOT NULL,
  `answer_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`sa_a_relation_id`),
  INDEX `fk_Antworten_has_FreieFragen_Antworten1` (`answer_id` ASC),
  INDEX `fk_Antworten_has_FreieFragen_FreieFragen1` (`short_answer_id` ASC),
  CONSTRAINT `fk_Antworten_has_FreieFragen_Antworten1`
    FOREIGN KEY (`answer_id`)
    REFERENCES `answer` (`answer_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Antworten_has_FreieFragen_FreieFragen1`
    FOREIGN KEY (`short_answer_id`)
    REFERENCES `short_answer` (`short_answer_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mc_has_a`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mc_has_a` ;

CREATE TABLE IF NOT EXISTS `mc_has_a` (
  `mc_a_relation_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `multiple_choice_id` INT UNSIGNED NOT NULL,
  `answer_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`mc_a_relation_id`),
  INDEX `fk_Antworten_has_MultipleChoice_Antworten1` (`answer_id` ASC),
  INDEX `fk_Antworten_has_MultipleChoice_MultipleChoice1` (`multiple_choice_id` ASC),
  CONSTRAINT `fk_Antworten_has_MultipleChoice_Antworten1`
    FOREIGN KEY (`answer_id`)
    REFERENCES `answer` (`answer_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Antworten_has_MultipleChoice_MultipleChoice1`
    FOREIGN KEY (`multiple_choice_id`)
    REFERENCES `multiple_choice` (`multiple_choice_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `s_has_mc`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `s_has_mc` ;

CREATE TABLE IF NOT EXISTS `s_has_mc` (
  `s_mc_relation_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `survey_id` INT UNSIGNED NOT NULL,
  `multiple_choice_id` INT UNSIGNED NOT NULL,
  `answer_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`s_mc_relation_id`),
  INDEX `fk_B_has_MC_Antworten1_idx` (`answer_id` ASC),
  INDEX `fk_Befragung_has_MultipleChoice_Befragung1` (`survey_id` ASC),
  INDEX `fk_Befragung_has_MultipleChoice_MultipleChoice1` (`multiple_choice_id` ASC),
  CONSTRAINT `fk_Befragung_has_MultipleChoice_Befragung1`
    FOREIGN KEY (`survey_id`)
    REFERENCES `survey` (`survey_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Befragung_has_MultipleChoice_MultipleChoice1`
    FOREIGN KEY (`multiple_choice_id`)
    REFERENCES `multiple_choice` (`multiple_choice_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_B_has_MC_Antworten1`
    FOREIGN KEY (`answer_id`)
    REFERENCES `answer` (`answer_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `s_has_sa`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `s_has_sa` ;

CREATE TABLE IF NOT EXISTS `s_has_sa` (
  `s_sa_relation_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `survey_id` INT UNSIGNED NOT NULL,
  `short_answer_id` INT UNSIGNED NOT NULL,
  `answer_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`s_sa_relation_id`),
  INDEX `fk_B_has_FF_Antworten1_idx` (`answer_id` ASC),
  INDEX `fk_Befragung_has_FreieFragen_Befragung1` (`survey_id` ASC),
  INDEX `fk_Befragung_has_FreieFragen_FreieFragen1` (`short_answer_id` ASC),
  CONSTRAINT `fk_Befragung_has_FreieFragen_Befragung1`
    FOREIGN KEY (`survey_id`)
    REFERENCES `survey` (`survey_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Befragung_has_FreieFragen_FreieFragen1`
    FOREIGN KEY (`short_answer_id`)
    REFERENCES `short_answer` (`short_answer_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_B_has_FF_Antworten1`
    FOREIGN KEY (`answer_id`)
    REFERENCES `answer` (`answer_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `react`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `react` ;

CREATE TABLE IF NOT EXISTS `react` (
  `react_id` INT NOT NULL AUTO_INCREMENT,
  `short_answer_id` INT NULL,
  `multiple_choice_id` INT NULL,
  `answer_position` INT NOT NULL,
  PRIMARY KEY (`react_id`),
  INDEX `fk_react_short_answer1_idx` (`short_answer_id` ASC),
  INDEX `fk_react_multiple_choice1_idx` (`multiple_choice_id` ASC),
  CONSTRAINT `fk_react_short_answer1`
    FOREIGN KEY (`short_answer_id`)
    REFERENCES `short_answer` (`short_answer_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_react_multiple_choice1`
    FOREIGN KEY (`multiple_choice_id`)
    REFERENCES `multiple_choice` (`multiple_choice_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flag_list_mc`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flag_list_mc` ;

CREATE TABLE IF NOT EXISTS `flag_list_mc` (
  `flag_list_mc_id` INT UNSIGNED NOT NULL,
  `is_evaluation_question` TINYINT NOT NULL DEFAULT 0,
  `is_required` TINYINT NOT NULL DEFAULT 0,
  `is_multiple_choice` TINYINT NOT NULL DEFAULT 0,
  `is_list` TINYINT NOT NULL DEFAULT 0,
  `is_yes_no_question` TINYINT NOT NULL DEFAULT 0,
  `is_single_line` TINYINT NOT NULL,
  INDEX `fk_flag_list_mc_q_has_mc1_idx` (`flag_list_mc_id` ASC),
  PRIMARY KEY (`flag_list_mc_id`),
  CONSTRAINT `fk_flag_list_mc_q_has_mc1`
    FOREIGN KEY (`flag_list_mc_id`)
    REFERENCES `q_has_mc` (`q_mc_relation_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `sa_has_react`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sa_has_react` ;

CREATE TABLE IF NOT EXISTS `sa_has_react` (
  `q_has_react_id` INT NOT NULL AUTO_INCREMENT,
  `q_has_sa_id` INT NOT NULL,
  `react_id` INT NOT NULL,
  PRIMARY KEY (`q_has_react_id`),
  INDEX `fk_q_has_react_q_has_sa1_idx` (`q_has_sa_id` ASC),
  INDEX `fk_q_has_react_react1_idx` (`react_id` ASC),
  CONSTRAINT `fk_q_has_react_q_has_sa1`
    FOREIGN KEY (`q_has_sa_id`)
    REFERENCES `q_has_sa` (`q_sa_relation_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_q_has_react_react1`
    FOREIGN KEY (`react_id`)
    REFERENCES `react` (`react_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mc_has_react`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mc_has_react` ;

CREATE TABLE IF NOT EXISTS `mc_has_react` (
  `mc_has_react_id` INT NOT NULL AUTO_INCREMENT,
  `react_id` INT NOT NULL,
  `q_has_mc_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`mc_has_react_id`),
  INDEX `fk_mc_has_react_react1_idx` (`react_id` ASC),
  INDEX `fk_mc_has_react_q_has_mc1_idx` (`q_has_mc_id` ASC),
  CONSTRAINT `fk_mc_has_react_react1`
    FOREIGN KEY (`react_id`)
    REFERENCES `react` (`react_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_mc_has_react_q_has_mc1`
    FOREIGN KEY (`q_has_mc_id`)
    REFERENCES `q_has_mc` (`q_mc_relation_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flag_list_sa`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flag_list_sa` ;

CREATE TABLE IF NOT EXISTS `flag_list_sa` (
  `flag_list_sa_id` INT UNSIGNED NOT NULL,
  `is_required` TINYINT NOT NULL DEFAULT 0,
  `is_text_area` TINYINT NOT NULL DEFAULT 0,
  INDEX `fk_flag_list_sa_q_has_sa1_idx` (`flag_list_sa_id` ASC),
  PRIMARY KEY (`flag_list_sa_id`),
  CONSTRAINT `fk_flag_list_sa_q_has_sa1`
    FOREIGN KEY (`flag_list_sa_id`)
    REFERENCES `q_has_sa` (`q_sa_relation_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;