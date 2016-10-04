-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Oct 04, 2016 at 04:53 PM
-- Server version: 5.5.52-0ubuntu0.14.04.1
-- PHP Version: 5.5.9-1ubuntu4.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `projectmanagement`
--
CREATE DATABASE IF NOT EXISTS `projectmanagement` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
USE `projectmanagement`;

-- --------------------------------------------------------

--
-- Table structure for table `Assignments`
--

CREATE TABLE IF NOT EXISTS `Assignments` (
  `AssignmentID` int(11) NOT NULL AUTO_INCREMENT,
  `TaskIDFS` int(11) NOT NULL,
  `EmployeeIDFS` int(11) NOT NULL,
  PRIMARY KEY (`AssignmentID`),
  KEY `TaskIDFS` (`TaskIDFS`),
  KEY `EmployeeIDFS` (`EmployeeIDFS`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=45 ;

--
-- Dumping data for table `Assignments`
--

INSERT INTO `Assignments` (`AssignmentID`, `TaskIDFS`, `EmployeeIDFS`) VALUES
(28, 18, 1),
(29, 19, 1),
(30, 20, 1),
(31, 21, 1),
(32, 22, 1),
(33, 18, 1),
(34, 19, 1),
(35, 20, 1),
(36, 21, 1),
(37, 22, 1),
(38, 18, 7),
(39, 19, 7),
(40, 20, 7),
(41, 21, 7),
(42, 22, 7),
(43, 21, 2),
(44, 21, 2);

-- --------------------------------------------------------

--
-- Table structure for table `Bookings`
--

CREATE TABLE IF NOT EXISTS `Bookings` (
  `BookingID` int(11) NOT NULL AUTO_INCREMENT,
  `AssignmentIDFS` int(11) NOT NULL,
  `Month` int(11) NOT NULL,
  `Hours` float NOT NULL,
  PRIMARY KEY (`BookingID`),
  KEY `AssignmentID` (`AssignmentIDFS`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=3 ;

--
-- Dumping data for table `Bookings`
--

INSERT INTO `Bookings` (`BookingID`, `AssignmentIDFS`, `Month`, `Hours`) VALUES
(1, 28, 1, 103),
(2, 38, 1, 177);

-- --------------------------------------------------------

--
-- Table structure for table `Employees`
--

CREATE TABLE IF NOT EXISTS `Employees` (
  `EmployeeID` int(11) NOT NULL AUTO_INCREMENT,
  `Firstname` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `Lastname` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `Kuerzel` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `Password` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `Mail` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `Supervisor` int(11) DEFAULT NULL,
  PRIMARY KEY (`EmployeeID`),
  UNIQUE KEY `Kuerzel` (`Kuerzel`),
  UNIQUE KEY `Mail` (`Mail`),
  KEY `Supervisor` (`Supervisor`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=9 ;

--
-- Dumping data for table `Employees`
--

INSERT INTO `Employees` (`EmployeeID`, `Firstname`, `Lastname`, `Kuerzel`, `Password`, `Mail`, `Supervisor`) VALUES
(1, 'Janine', 'Walther', 'walj', 'cojt0Pw//L6ToM8G41aOKFIWh7w=', 'walj@zhaw.ch', NULL),
(2, 'Test', 'User', 'test', 'cojt0Pw//L6ToM8G41aOKFIWh7w=', 'test@test.ch', 1),
(7, 'Sami', 'Klaus', 'sami', 'cojt0Pw//L6ToM8G41aOKFIWh7w=', 'sami@whatever.com', 1),
(8, 'Panda', 'BÃ¤r', 'pand', 'cojt0Pw//L6ToM8G41aOKFIWh7w=', 'panda.baer@pandasareawesome.com', 1);

-- --------------------------------------------------------

--
-- Table structure for table `Expenses`
--

CREATE TABLE IF NOT EXISTS `Expenses` (
  `ExpenseID` int(11) NOT NULL AUTO_INCREMENT,
  `ProjectIDFS` int(11) NOT NULL,
  `EmployeeIDFS` int(11) NOT NULL,
  `Costs` float NOT NULL,
  `Type` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `Description` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `Date` date NOT NULL,
  PRIMARY KEY (`ExpenseID`),
  KEY `EmployeeIDFS` (`EmployeeIDFS`),
  KEY `ProjectIDFS` (`ProjectIDFS`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=5 ;

--
-- Dumping data for table `Expenses`
--

INSERT INTO `Expenses` (`ExpenseID`, `ProjectIDFS`, `EmployeeIDFS`, `Costs`, `Type`, `Description`, `Date`) VALUES
(1, 8, 1, 900, 'Travel', 'Flight to secret destination', '2013-09-20'),
(2, 8, 1, 280, 'Overnight Stay', 'Hotel Oceanview 2 Nights inkl. meals', '2013-09-20'),
(4, 8, 1, 900, 'Travel', 'Back from secret destination', '2015-09-20');

-- --------------------------------------------------------

--
-- Table structure for table `Projects`
--

CREATE TABLE IF NOT EXISTS `Projects` (
  `ProjectIDFS` int(11) NOT NULL AUTO_INCREMENT,
  `ProjectShortname` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `ProjectName` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `ProjectLeader` int(11) NOT NULL,
  `TotalBudget` float NOT NULL,
  `Currency` enum('CHF','EUR') COLLATE utf8_unicode_ci NOT NULL DEFAULT 'CHF',
  `ProjectStart` date NOT NULL,
  `ProjectEnd` date NOT NULL,
  `Partner` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '-',
  `Archive` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ProjectIDFS`),
  KEY `ProjectLeader` (`ProjectLeader`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=12 ;

--
-- Dumping data for table `Projects`
--

INSERT INTO `Projects` (`ProjectIDFS`, `ProjectShortname`, `ProjectName`, `ProjectLeader`, `TotalBudget`, `Currency`, `ProjectStart`, `ProjectEnd`, `Partner`, `Archive`) VALUES
(8, 'IAPNH I', 'Insert awesome project name here', 1, 300000, 'CHF', '2016-09-01', '2018-08-31', 'ABCD, CDEF', 0),
(9, 'IAPNH II', 'Insert awesome project name here', 1, 300000, 'CHF', '2016-01-01', '2018-01-31', 'ABCD, CDEF', 0),
(10, 'IAPNH III', 'Insert awesome project name here', 2, 300000, 'CHF', '2016-01-01', '2018-01-31', 'ABCD, CDEF', 0),
(11, 'IAPNH IV', 'Insert awesome project name here', 7, 300000, 'CHF', '2016-01-01', '2018-01-31', 'ABCD, CDEF', 0);

-- --------------------------------------------------------

--
-- Table structure for table `Tasks`
--

CREATE TABLE IF NOT EXISTS `Tasks` (
  `TaskID` int(11) NOT NULL AUTO_INCREMENT,
  `WorkpackageIDFS` int(11) NOT NULL,
  `TaskName` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `TaskStart` date NOT NULL,
  `TaskEnd` date NOT NULL,
  `PMs` int(11) NOT NULL,
  `Budget` float DEFAULT NULL,
  PRIMARY KEY (`TaskID`),
  KEY `WorkpackageIDFS` (`WorkpackageIDFS`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=38 ;

--
-- Dumping data for table `Tasks`
--

INSERT INTO `Tasks` (`TaskID`, `WorkpackageIDFS`, `TaskName`, `TaskStart`, `TaskEnd`, `PMs`, `Budget`) VALUES
(18, 16, 'Cool Task I', '2016-09-01', '2016-12-31', 8, 40500),
(19, 16, 'Cool Task II', '2017-01-01', '2017-01-31', 2, 10100),
(20, 17, 'Awesome Task', '2017-02-01', '2017-10-31', 20, 101000),
(21, 18, 'Cool and Awesome Task', '2017-11-01', '2018-03-31', 10, 50500),
(22, 19, 'Ultra Awesome and Cool', '2018-04-01', '2018-08-31', 10, 50500),
(23, 20, 'Cool Task I', '2016-01-01', '2016-01-31', 8, 40500),
(24, 20, 'Cool Task II', '2017-01-01', '2017-01-31', 2, 10100),
(25, 21, 'Awesome Task', '2017-01-01', '2017-01-31', 20, 101000),
(26, 22, 'Cool and Awesome Task', '2017-01-01', '2018-01-31', 10, 50500),
(27, 23, 'Ultra Awesome and Cool', '2018-01-01', '2018-01-31', 10, 50500),
(28, 24, 'Cool Task I', '2016-01-01', '2016-01-31', 8, 40500),
(29, 24, 'Cool Task II', '2017-01-01', '2017-01-31', 2, 10100),
(30, 25, 'Awesome Task', '2017-01-01', '2017-01-31', 20, 101000),
(31, 26, 'Cool and Awesome Task', '2017-01-01', '2018-01-31', 10, 50500),
(32, 27, 'Ultra Awesome and Cool', '2018-01-01', '2018-01-31', 10, 50500),
(33, 28, 'Cool Task I', '2016-01-01', '2016-01-31', 8, 40500),
(34, 28, 'Cool Task II', '2017-01-01', '2017-01-31', 2, 10100),
(35, 29, 'Awesome Task', '2017-01-01', '2017-01-31', 20, 101000),
(36, 30, 'Cool and Awesome Task', '2017-01-01', '2018-01-31', 10, 50500),
(37, 31, 'Ultra Awesome and Cool', '2018-01-01', '2018-01-31', 10, 50500);

-- --------------------------------------------------------

--
-- Table structure for table `Wage`
--

CREATE TABLE IF NOT EXISTS `Wage` (
  `WageID` int(11) NOT NULL AUTO_INCREMENT,
  `EmployeeIDFS` int(11) NOT NULL,
  `WagePerHour` float NOT NULL,
  `ValidFrom` date NOT NULL,
  PRIMARY KEY (`WageID`),
  KEY `EmployeeIDFS` (`EmployeeIDFS`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=10 ;

--
-- Dumping data for table `Wage`
--

INSERT INTO `Wage` (`WageID`, `EmployeeIDFS`, `WagePerHour`, `ValidFrom`) VALUES
(1, 1, 30, '2015-06-01'),
(2, 1, 30, '2015-06-26'),
(3, 2, 30, '2015-06-01'),
(5, 7, 60, '2015-06-01'),
(6, 8, 30, '2016-10-03');

-- --------------------------------------------------------

--
-- Table structure for table `Workpackages`
--

CREATE TABLE IF NOT EXISTS `Workpackages` (
  `WorkpackageID` int(11) NOT NULL AUTO_INCREMENT,
  `ProjectIDFS` int(11) NOT NULL,
  `WPName` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `WPStart` date NOT NULL,
  `WPEnd` date NOT NULL,
  PRIMARY KEY (`WorkpackageID`),
  KEY `ProjectIDFS` (`ProjectIDFS`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=32 ;

--
-- Dumping data for table `Workpackages`
--

INSERT INTO `Workpackages` (`WorkpackageID`, `ProjectIDFS`, `WPName`, `WPStart`, `WPEnd`) VALUES
(16, 8, 'Cool Workpackage', '2016-09-01', '2017-01-31'),
(17, 8, 'Awesome Workpackage', '2017-02-01', '2017-10-31'),
(18, 8, 'Cool and Awesome Workpackage', '2017-11-01', '2018-03-31'),
(19, 8, 'Ultra Awesome and Cool Workpackage', '2018-04-01', '2018-08-31'),
(20, 9, 'Cool Workpackage', '2016-01-01', '2017-01-31'),
(21, 9, 'Awesome Workpackage', '2017-01-01', '2017-01-31'),
(22, 9, 'Cool and Awesome Workpackage', '2017-01-01', '2018-01-31'),
(23, 9, 'Ultra Awesome and Cool Workpackage', '2018-01-01', '2018-01-31'),
(24, 10, 'Cool Workpackage', '2016-01-01', '2017-01-31'),
(25, 10, 'Awesome Workpackage', '2017-01-01', '2017-01-31'),
(26, 10, 'Cool and Awesome Workpackage', '2017-01-01', '2018-01-31'),
(27, 10, 'Ultra Awesome and Cool Workpackage', '2018-01-01', '2018-01-31'),
(28, 11, 'Cool Workpackage', '2016-01-01', '2017-01-31'),
(29, 11, 'Awesome Workpackage', '2017-01-01', '2017-01-31'),
(30, 11, 'Cool and Awesome Workpackage', '2017-01-01', '2018-01-31'),
(31, 11, 'Ultra Awesome and Cool Workpackage', '2018-01-01', '2018-01-31');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Assignments`
--
ALTER TABLE `Assignments`
  ADD CONSTRAINT `Assignments_ibfk_1` FOREIGN KEY (`TaskIDFS`) REFERENCES `Tasks` (`TaskID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Assignments_ibfk_2` FOREIGN KEY (`EmployeeIDFS`) REFERENCES `Employees` (`EmployeeID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Bookings`
--
ALTER TABLE `Bookings`
  ADD CONSTRAINT `Bookings_ibfk_1` FOREIGN KEY (`AssignmentIDFS`) REFERENCES `Assignments` (`AssignmentID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Employees`
--
ALTER TABLE `Employees`
  ADD CONSTRAINT `Employees_ibfk_1` FOREIGN KEY (`Supervisor`) REFERENCES `Employees` (`EmployeeID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Expenses`
--
ALTER TABLE `Expenses`
  ADD CONSTRAINT `Expenses_ibfk_1` FOREIGN KEY (`ProjectIDFS`) REFERENCES `Projects` (`ProjectIDFS`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Expenses_ibfk_2` FOREIGN KEY (`EmployeeIDFS`) REFERENCES `Employees` (`EmployeeID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Projects`
--
ALTER TABLE `Projects`
  ADD CONSTRAINT `Projects_ibfk_1` FOREIGN KEY (`ProjectLeader`) REFERENCES `Employees` (`EmployeeID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Tasks`
--
ALTER TABLE `Tasks`
  ADD CONSTRAINT `Tasks_ibfk_1` FOREIGN KEY (`WorkpackageIDFS`) REFERENCES `Workpackages` (`WorkpackageID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Wage`
--
ALTER TABLE `Wage`
  ADD CONSTRAINT `Wage_ibfk_1` FOREIGN KEY (`EmployeeIDFS`) REFERENCES `Employees` (`EmployeeID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Workpackages`
--
ALTER TABLE `Workpackages`
  ADD CONSTRAINT `Workpackages_ibfk_1` FOREIGN KEY (`ProjectIDFS`) REFERENCES `Projects` (`ProjectIDFS`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
