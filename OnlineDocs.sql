-- MySQL dump 10.13  Distrib 8.0.12, for macos10.13 (x86_64)
--
-- Host: localhost    Database: OnlineDocs
-- ------------------------------------------------------
-- Server version	8.0.16

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cooperate`
--

DROP TABLE IF EXISTS `cooperate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `cooperate` (
  `permission` varchar(20) NOT NULL,
  `user_iduser` int(11) NOT NULL,
  `document_iddocument` int(11) NOT NULL,
  PRIMARY KEY (`permission`,`user_iduser`,`document_iddocument`),
  KEY `fk_cooperate_user_idx` (`user_iduser`),
  KEY `fk_cooperate_document1_idx` (`document_iddocument`),
  CONSTRAINT `fk_cooperate_document1` FOREIGN KEY (`document_iddocument`) REFERENCES `document` (`iddocument`),
  CONSTRAINT `fk_cooperate_user` FOREIGN KEY (`user_iduser`) REFERENCES `user_info` (`iduser`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `document`
--

DROP TABLE IF EXISTS `document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `document` (
  `iddocument` int(11) NOT NULL AUTO_INCREMENT,
  `document_name` varchar(45) NOT NULL,
  `create_time` datetime NOT NULL,
  `last_modify_time` datetime NOT NULL,
  `text_path` varchar(128) NOT NULL,
  `log_path` varchar(128) NOT NULL,
  PRIMARY KEY (`iddocument`),
  UNIQUE KEY `iddocument_UNIQUE` (`iddocument`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invite_info`
--

DROP TABLE IF EXISTS `invite_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `invite_info` (
  `invite_id` int(11) NOT NULL AUTO_INCREMENT,
  `document_id` int(11) NOT NULL,
  `auth` varchar(20) NOT NULL,
  `invite_time` datetime NOT NULL,
  `invite_user_id` int(11) NOT NULL,
  PRIMARY KEY (`invite_id`),
  KEY `document_id_idx` (`document_id`),
  KEY `invite_user_id_idx` (`invite_user_id`),
  CONSTRAINT `document_id` FOREIGN KEY (`document_id`) REFERENCES `document` (`iddocument`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `invite_user_id` FOREIGN KEY (`invite_user_id`) REFERENCES `user_info` (`iduser`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user_info` (
  `iduser` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(45) NOT NULL,
  `password` varchar(128) NOT NULL,
  `user_email` varchar(128) NOT NULL,
  PRIMARY KEY (`iduser`),
  UNIQUE KEY `user_name_UNIQUE` (`user_name`),
  UNIQUE KEY `iduser_UNIQUE` (`iduser`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-07-06  9:30:24
