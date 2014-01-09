SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `jiffy` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `jiffy` ;

-- -----------------------------------------------------
-- Table `jiffy`.`User`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `jiffy`.`User` (
  `user_id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(200) NULL ,
  `username` VARCHAR(45) NULL ,
  `pass_hash` VARCHAR(64) NULL ,
  `type` ENUM('TEACHER', 'STUDENT', 'GUARDIAN') NULL ,
  `gender` ENUM('FEMALE', 'MALE') NULL ,
  PRIMARY KEY (`user_id`) ,
  UNIQUE INDEX `benutzername_UNIQUE` (`username` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `jiffy`.`Teacher`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `jiffy`.`Teacher` (
  `user_id` INT NOT NULL ,
  PRIMARY KEY (`user_id`) ,
  INDEX `fk_Teacher_User` (`user_id` ASC) ,
  CONSTRAINT `fk_Teacher_User`
    FOREIGN KEY (`user_id` )
    REFERENCES `jiffy`.`User` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
PACK_KEYS = DEFAULT;


-- -----------------------------------------------------
-- Table `jiffy`.`Guardian`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `jiffy`.`Guardian` (
  `user_id` INT NOT NULL ,
  `language` VARCHAR(10) NULL ,
  `is_contactperson` TINYINT(1) NULL DEFAULT 0 ,
  PRIMARY KEY (`user_id`) ,
  INDEX `fk_Guardian_User` (`user_id` ASC) ,
  CONSTRAINT `fk_Guardian_User`
    FOREIGN KEY (`user_id` )
    REFERENCES `jiffy`.`User` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `jiffy`.`Student`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `jiffy`.`Student` (
  `user_id` INT NOT NULL ,
  `guardian_user_id` INT NULL ,
  PRIMARY KEY (`user_id`) ,
  INDEX `fk_Student_User` (`user_id` ASC) ,
  INDEX `fk_Student_Guardian` (`guardian_user_id` ASC) ,
  CONSTRAINT `fk_Student_User`
    FOREIGN KEY (`user_id` )
    REFERENCES `jiffy`.`User` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Student_Guardian`
    FOREIGN KEY (`guardian_user_id` )
    REFERENCES `jiffy`.`Guardian` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `jiffy`.`Class`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `jiffy`.`Class` (
  `class_id` INT NOT NULL AUTO_INCREMENT ,
  `year` SMALLINT NULL ,
  `description` VARCHAR(50) NULL ,
  `teacher_user_id` INT NOT NULL ,
  PRIMARY KEY (`class_id`) ,
  INDEX `fk_Class_Teacher` (`teacher_user_id` ASC) ,
  CONSTRAINT `fk_Class_Teacher`
    FOREIGN KEY (`teacher_user_id` )
    REFERENCES `jiffy`.`Teacher` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `jiffy`.`Schoolsubject`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `jiffy`.`Schoolsubject` (
  `schoolsubject_id` INT NOT NULL AUTO_INCREMENT ,
  `description` VARCHAR(45) NULL ,
  `teacher_user_id` INT NOT NULL ,
  PRIMARY KEY (`schoolsubject_id`) ,
  INDEX `fk_Subject_Teacher` (`teacher_user_id` ASC) ,
  CONSTRAINT `fk_Subject_Teacher`
    FOREIGN KEY (`teacher_user_id` )
    REFERENCES `jiffy`.`Teacher` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `jiffy`.`Grade`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `jiffy`.`Grade` (
  `grade_id` INT NOT NULL AUTO_INCREMENT ,
  `grade` DECIMAL(3,2) NULL ,
  `zeitpunkt` DATETIME NULL ,
  `grade_weight` TINYINT NULL ,
  `comment` TEXT NULL ,
  `schoolsubject_id` INT NOT NULL ,
  `student_user_id` INT NOT NULL ,
  PRIMARY KEY (`grade_id`) ,
  INDEX `fk_Grade_Subject` (`schoolsubject_id` ASC) ,
  INDEX `fk_Grade_Student` (`student_user_id` ASC) ,
  CONSTRAINT `fk_Grade_Subject`
    FOREIGN KEY (`schoolsubject_id` )
    REFERENCES `jiffy`.`Schoolsubject` (`schoolsubject_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Grade_Student`
    FOREIGN KEY (`student_user_id` )
    REFERENCES `jiffy`.`Student` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `jiffy`.`Schoolmessage`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `jiffy`.`Schoolmessage` (
  `school_message_id` INT NOT NULL AUTO_INCREMENT ,
  `subject` VARCHAR(100) NULL ,
  `message` TEXT NULL ,
  `send_date` DATETIME NULL ,
  `type` ENUM('GRADEMSG', 'INFOMSG') NULL ,
  PRIMARY KEY (`school_message_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `jiffy`.`Grademessage`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `jiffy`.`Grademessage` (
  `school_message_id` INT NOT NULL ,
  `approved` TINYINT(1) NULL DEFAULT 0 ,
  `grade_id` INT NOT NULL ,
  PRIMARY KEY (`school_message_id`, `grade_id`) ,
  INDEX `fk_Grademessage_Schoolmessage` (`school_message_id` ASC) ,
  INDEX `fk_Grademessage_Grade` (`grade_id` ASC) ,
  CONSTRAINT `fk_Grademessage_Schoolmessage`
    FOREIGN KEY (`school_message_id` )
    REFERENCES `jiffy`.`Schoolmessage` (`school_message_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Grademessage_Grade`
    FOREIGN KEY (`grade_id` )
    REFERENCES `jiffy`.`Grade` (`grade_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `jiffy`.`Informationnote`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `jiffy`.`Informationnote` (
  `school_message_id` INT NOT NULL ,
  `description_date` DATETIME NULL ,
  PRIMARY KEY (`school_message_id`) ,
  INDEX `fk_Informationnote_Schoolmessage` (`school_message_id` ASC) ,
  CONSTRAINT `fk_Informationnote_Schoolmessage`
    FOREIGN KEY (`school_message_id` )
    REFERENCES `jiffy`.`Schoolmessage` (`school_message_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `jiffy`.`Guardianmessage`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `jiffy`.`Guardianmessage` (
  `guardian_message_id` INT NOT NULL AUTO_INCREMENT ,
  `subject` VARCHAR(100) NULL ,
  `message` TEXT NULL ,
  `send_date` DATETIME NULL ,
  `type` ENUM('QUESTIONMSG', 'EXCUSEMSG') NULL ,
  `to_teacher_user_id` INT NOT NULL ,
  `from_guardian_user_id` INT NOT NULL ,
  PRIMARY KEY (`guardian_message_id`) ,
  INDEX `fk_Guardianmessage_Teacher` (`to_teacher_user_id` ASC) ,
  INDEX `fk_Guardianmessage_Guardian` (`from_guardian_user_id` ASC) ,
  CONSTRAINT `fk_Guardianmessage_Teacher`
    FOREIGN KEY (`to_teacher_user_id` )
    REFERENCES `jiffy`.`Teacher` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Guardianmessage_Guardian`
    FOREIGN KEY (`from_guardian_user_id` )
    REFERENCES `jiffy`.`Guardian` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `jiffy`.`Excusemessage`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `jiffy`.`Excusemessage` (
  `guardian_message_id` INT NOT NULL ,
  `date_from` DATETIME NULL ,
  `date_to` DATETIME NULL ,
  `approved` TINYINT(1) NULL DEFAULT 0 ,
  PRIMARY KEY (`guardian_message_id`) ,
  INDEX `fk_Excusemessage_Guardianmessage` (`guardian_message_id` ASC) ,
  CONSTRAINT `fk_Excusemessage_Guardianmessage`
    FOREIGN KEY (`guardian_message_id` )
    REFERENCES `jiffy`.`Guardianmessage` (`guardian_message_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `jiffy`.`Student_visits_Class`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `jiffy`.`Student_visits_Class` (
  `student_user_id` INT NOT NULL ,
  `class_id` INT NOT NULL ,
  PRIMARY KEY (`student_user_id`, `class_id`) ,
  INDEX `fk_Student_visits_Class_Class` (`class_id` ASC) ,
  INDEX `fk_Student_visits_Class_Student` (`student_user_id` ASC) ,
  CONSTRAINT `fk_Student_visits_Class_Student`
    FOREIGN KEY (`student_user_id` )
    REFERENCES `jiffy`.`Student` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Student_visits_Class_Class`
    FOREIGN KEY (`class_id` )
    REFERENCES `jiffy`.`Class` (`class_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `jiffy`.`Teacher_to_Guardian`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `jiffy`.`Teacher_to_Guardian` (
  `teacher_to_guardian_id` INT NOT NULL ,
  `school_message_id` INT NOT NULL ,
  `from_teacher_user_id` INT NOT NULL ,
  `to_guardian_user_id` INT NOT NULL ,
  PRIMARY KEY (`teacher_to_guardian_id`) ,
  INDEX `fk_Teacher_to_Guardian_Teacher1` (`from_teacher_user_id` ASC) ,
  INDEX `fk_Teacher_to_Guardian_Guardian1` (`to_guardian_user_id` ASC) ,
  INDEX `fk_Teacher_to_Guardian_Schoolmessage1` (`school_message_id` ASC) ,
  CONSTRAINT `fk_Teacher_to_Guardian_Teacher1`
    FOREIGN KEY (`from_teacher_user_id` )
    REFERENCES `jiffy`.`Teacher` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Teacher_to_Guardian_Guardian1`
    FOREIGN KEY (`to_guardian_user_id` )
    REFERENCES `jiffy`.`Guardian` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Teacher_to_Guardian_Schoolmessage1`
    FOREIGN KEY (`school_message_id` )
    REFERENCES `jiffy`.`Schoolmessage` (`school_message_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
