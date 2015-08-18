/*
SQLyog Community v12.04 (32 bit)
MySQL - 5.6.21 : Database - roidev
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`roidev` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `roidev`;

/*Table structure for table `failinfo` */

DROP TABLE IF EXISTS `failinfo`;

CREATE TABLE `failinfo` (
  `failid` int(8) NOT NULL AUTO_INCREMENT,
  `runid` int(8) DEFAULT NULL,
  `testcasename` text,
  `failreason` text,
  `additionalinfo` text,
  PRIMARY KEY (`failid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `months` */

DROP TABLE IF EXISTS `months`;

CREATE TABLE `months` (
  `month` int(2) DEFAULT NULL,
  `saving` int(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `project` */

DROP TABLE IF EXISTS `project`;

CREATE TABLE `project` (
  `pid` int(4) NOT NULL AUTO_INCREMENT,
  `pname` varchar(200) DEFAULT NULL,
  `pgroup` varchar(200) DEFAULT NULL,
  `pdev` varchar(200) DEFAULT NULL,
  `parea` varchar(100) DEFAULT NULL,
  `psaving` float(4,2) DEFAULT NULL COMMENT 'in hrs',
  `ptestcasecount` int(4) DEFAULT NULL,
  PRIMARY KEY (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=189 DEFAULT CHARSET=latin1;

/*Table structure for table `runs` */

DROP TABLE IF EXISTS `runs`;

CREATE TABLE `runs` (
  `runid` int(8) NOT NULL AUTO_INCREMENT,
  `pid` int(4) DEFAULT NULL,
  `currentstatus` varchar(10) DEFAULT NULL COMMENT 'running/stopped/compled',
  `rundate` datetime DEFAULT NULL,
  `runcompletedate` datetime DEFAULT NULL,
  `machinename` varchar(200) DEFAULT NULL,
  `machineuser` varchar(200) DEFAULT NULL,
  `passcount` int(5) DEFAULT '0',
  `failcount` int(5) DEFAULT '0',
  `comments` text,
  PRIMARY KEY (`runid`),
  KEY `pname` (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=1836 DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
