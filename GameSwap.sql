-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

CREATE SCHEMA IF NOT EXISTS `GameSwap` DEFAULT CHARACTER SET utf8 ;
USE `GameSwap` ;


CREATE TABLE IF NOT EXISTS `GameSwap`.`Address` (
  `postal_code` VARCHAR(250) NOT NULL,
  `city` VARCHAR(250) NOT NULL,
  `state` VARCHAR(250) NOT NULL,
  `latitude` FLOAT NOT NULL,
  `longitude` FLOAT NOT NULL,
  PRIMARY KEY (`postal_code`))
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `GameSwap`.`Phone` (
  `phone_number` VARCHAR(250) NOT NULL,
  `user_email` VARCHAR(250) NOT NULL,
  `phone_type` VARCHAR(250) NOT NULL,
  `share` BIT NOT NULL,
  PRIMARY KEY (`phone_number`),
  CONSTRAINT `fk_Phone_User`
    FOREIGN KEY (`user_email`)
    REFERENCES `GameSwap`.`User` (`email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `GameSwap`.`User` (
  `email` VARCHAR(250) NOT NULL,
  `password` VARCHAR(250) NOT NULL,
  `nickname` VARCHAR(250) NOT NULL,
  `first_name` VARCHAR(250) NOT NULL,
  `last_name` VARCHAR(250) NOT NULL,
  `postal_code` VARCHAR(250) NOT NULL,
  PRIMARY KEY (`email`),
  CONSTRAINT `fk_User_Address`
    FOREIGN KEY (`postal_code`)
    REFERENCES `GameSwap`.`Address` (`postal_code`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `GameSwap`.`Item` (
  `item_number` INT NOT NULL AUTO_INCREMENT,
  `user_email` VARCHAR(250) NOT NULL,
  `name` VARCHAR(250) NULL,
  `condition` VARCHAR(250) NULL,
  `description` VARCHAR(250) NULL,
  PRIMARY KEY (`item_number`, `user_email`),
  CONSTRAINT `fk_Item_User`
    FOREIGN KEY (`user_email`)
    REFERENCES `GameSwap`.`User` (`email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `GameSwap`.`BoardItem` (
  `item_number` INT NOT NULL,
  PRIMARY KEY (`item_number`),
  CONSTRAINT `fk_BoardItem_Item`
    FOREIGN KEY (`item_number`)
    REFERENCES `GameSwap`.`Item` (`item_number`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `GameSwap`.`CardItem` (
  `item_number` INT NOT NULL,
  PRIMARY KEY (`item_number`),
  CONSTRAINT `fk_CardItem_Item`
    FOREIGN KEY (`item_number`)
    REFERENCES `GameSwap`.`Item` (`item_number`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `GameSwap`.`VideoItem` (
  `item_number` INT NOT NULL,
  `media` VARCHAR(250) NOT NULL,
  `platform` VARCHAR(250) NOT NULL,
  PRIMARY KEY (`item_number`),
  CONSTRAINT `fk_VideoItem_Item`
    FOREIGN KEY (`item_number`)
    REFERENCES `GameSwap`.`Item` (`item_number`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_VideoItem_Platform`
    FOREIGN KEY (`platform`)
    REFERENCES `GameSwap`.`VideoPlatform` (`platform`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `GameSwap`.`VideoPlatform` (
  `platform` VARCHAR(250) NOT NULL,
  PRIMARY KEY (`platform`))
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `GameSwap`.`ComputerItem` (
  `item_number` INT NOT NULL,
  `platform` VARCHAR(250) NOT NULL,
  PRIMARY KEY (`item_number`),
  CONSTRAINT `fk_ComputerItem_Item`
    FOREIGN KEY (`item_number`)
    REFERENCES `GameSwap`.`Item` (`item_number`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `GameSwap`.`JigsawItem` (
  `item_number` INT NOT NULL,
  `piece_count` INT NOT NULL,
  PRIMARY KEY (`item_number`),
  CONSTRAINT `fk_JigsawItem_Item`
    FOREIGN KEY (`item_number`)
    REFERENCES `GameSwap`.`Item` (`item_number`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `GameSwap`.`Swap` (
  `proposed_item_number` INT NOT NULL,
  `desired_item_number` INT NOT NULL,
  `status` BIT NULL,
  `counterparty_rating` INT NULL,
  `proposer_rating` INT NULL,
  `accept_reject_date` DATE NULL,
  `proposed_date` DATE NOT NULL,
  PRIMARY KEY (`proposed_item_number`,  `desired_item_number`),
  CONSTRAINT `fk_Swap_Item`
    FOREIGN KEY (`proposed_item_number`)
    REFERENCES `GameSwap`.`Item` (`item_number`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Swap_Item2`
    FOREIGN KEY (`desired_item_number`)
    REFERENCES `GameSwap`.`Item` (`item_number`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
