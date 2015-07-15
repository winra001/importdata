-- speaker
CREATE TABLE `database`.`speaker` (
  `id` INT NOT NULL,
  `first_name` VARCHAR(50) NULL,
  `last_name` VARCHAR(50) NULL,
  `position` VARCHAR(100) NULL,
  `company` VARCHAR(100) NULL,
  `description` VARCHAR(5000) NULL,
  `photo` VARCHAR(100) NULL,
  `abstracts` VARCHAR(100) NULL,
PRIMARY KEY (`id`));

-- program
CREATE TABLE `database`.`program` (
  `id` INT NOT NULL,
  `start_date` DATETIME NULL,
  `end_date` DATETIME NULL,
  `category1` VARCHAR(100) NULL,
  `category2` VARCHAR(200) NULL,
  `title` VARCHAR(1000) NULL,
  `description` VARCHAR(1000) NULL,
  `venue` VARCHAR(150) NULL,
  `speaker` VARCHAR(100) NULL,
PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- venue
CREATE TABLE `database`.`venue` (
  `id` INT NOT NULL,
  `name` VARCHAR(200) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE `database`.`category` (
  `id` INT NOT NULL,
  `code` INT(3) NOT NULL,
  `category` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- program_speaker
CREATE TABLE `database`.`program_speaker` (
  `program_id` INT(11) NOT NULL,
  `speaker_id` INT(11) NOT NULL,
  PRIMARY KEY (`program_id`, `speaker_id`),
  INDEX `fk_speaker_id_idx` (`speaker_id` ASC),
  CONSTRAINT `fk_program_id`
    FOREIGN KEY (`program_id`)
    REFERENCES `database`.`program` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_speaker_id`
    FOREIGN KEY (`speaker_id`)
    REFERENCES `database`.`speaker` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- abstracts
CREATE TABLE `database`.`abstracts` (
  `id` INT(11) NOT NULL,
  `filename` VARCHAR(300) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- speaker_abstracts
CREATE TABLE `database`.`speaker_abstracts` (
  `speaker_id` INT(11) NOT NULL,
  `abstracts_id` INT(11) NOT NULL,
  PRIMARY KEY (`speaker_id`, `abstracts_id`),
  INDEX `fk_abstracts_id_idx` (`abstracts_id` ASC),
  CONSTRAINT `fk_speaker_id_2`
    FOREIGN KEY (`speaker_id`)
    REFERENCES `database`.`speaker` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_abstracts_id`
    FOREIGN KEY (`abstracts_id`)
    REFERENCES `database`.`abstracts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;
