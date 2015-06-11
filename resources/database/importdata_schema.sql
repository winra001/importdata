CREATE TABLE `database_name`.`speaker` (
  `id` INT NOT NULL,
  `first_name` VARCHAR(50) NULL,
  `last_name` VARCHAR(50) NULL,
  `position` VARCHAR(100) NULL,
  `company` VARCHAR(100) NULL,
  `description` VARCHAR(5000) NULL,
PRIMARY KEY (`id`));
  
CREATE TABLE `database_name`.`program` (
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
  
CREATE TABLE `test4`.`venue` (
  `id` INT NOT NULL,
  `name` VARCHAR(200) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;