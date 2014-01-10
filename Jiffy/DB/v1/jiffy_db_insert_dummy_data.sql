-- phpMyAdmin SQL Dump
-- version 4.0.8
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Erstellungszeit: 10. Jan 2014 um 03:04
-- Server Version: 5.5.34-0ubuntu0.13.04.1
-- PHP-Version: 5.4.9-4ubuntu2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Datenbank: `jiffy`
--

--
-- Daten für Tabelle `Class`
--

INSERT INTO `Class` (`class_id`, `year`, `description`, `teacher_user_id`) VALUES
(2, 2013, '9B', 12),
(3, 2012, '8B', 12),
(4, 2013, '6B', 12),
(5, 2012, '5B', 12);

--
-- Daten für Tabelle `Excusemessage`
--

INSERT INTO `Excusemessage` (`guardian_message_id`, `date_from`, `date_to`, `approved`) VALUES
(2, '2014-01-10 15:00:00', '2014-01-10 16:30:00', 0);

--
-- Daten für Tabelle `Grade`
--

INSERT INTO `Grade` (`grade_id`, `grade`, `date_given`, `grade_weight`, `comment`, `schoolsubject_id`, `student_user_id`) VALUES
(1, 1.30, '2014-01-09 00:00:00', 10, 'Sehr schön. Es ist eine gute Verbesserung zu erkennen.', 1, 11),
(2, 1.30, '2014-01-10 02:07:20', 20, 'Wunderbar! Weiter so!', 1, 14);

--
-- Daten für Tabelle `Grademessage`
--

INSERT INTO `Grademessage` (`school_message_id`, `approved`, `grade_id`) VALUES
(3, 0, 1);

--
-- Daten für Tabelle `Guardian`
--

INSERT INTO `Guardian` (`user_id`, `language`, `is_contactperson`) VALUES
(4, 'de', 0);

--
-- Daten für Tabelle `Guardianmessage`
--

INSERT INTO `Guardianmessage` (`guardian_message_id`, `subject`, `message`, `send_date`, `type`, `to_teacher_user_id`, `from_guardian_user_id`) VALUES
(1, 'Question!', 'Frage!', '2014-01-09 04:24:00', NULL, 12, 4),
(2, 'Entschuldigung', 'Kann nicht kommen!', '2014-01-09 04:24:00', 'EXCUSEMSG', 12, 4);

--
-- Daten für Tabelle `Informationnote`
--

INSERT INTO `Informationnote` (`school_message_id`, `description_date`) VALUES
(6, '2014-01-09 02:13:30');

--
-- Daten für Tabelle `Schoolmessage`
--

INSERT INTO `Schoolmessage` (`school_message_id`, `subject`, `message`, `send_date`, `type`) VALUES
(3, 'Betreff', 'Nachrichtentext', '2014-01-09 02:13:30', 'GRADEMSG'),
(6, 'Info für heute', 'Infonachricht!!', '2014-01-09 02:13:30', 'INFOMSG');

--
-- Daten für Tabelle `Schoolsubject`
--

INSERT INTO `Schoolsubject` (`schoolsubject_id`, `description`, `teacher_user_id`) VALUES
(1, 'Englisch (Leistungskurs)', 12);

--
-- Daten für Tabelle `Student`
--

INSERT INTO `Student` (`user_id`, `guardian_user_id`) VALUES
(11, 4),
(13, 4),
(14, 4);

--
-- Daten für Tabelle `Student_visits_Class`
--

INSERT INTO `Student_visits_Class` (`student_user_id`, `class_id`) VALUES
(11, 2),
(11, 3),
(13, 4),
(13, 5);

--
-- Daten für Tabelle `Teacher`
--

INSERT INTO `Teacher` (`user_id`) VALUES
(12);

--
-- Daten für Tabelle `Teacher_to_Guardian`
--

INSERT INTO `Teacher_to_Guardian` (`teacher_to_guardian_id`, `school_message_id`, `from_teacher_user_id`, `to_guardian_user_id`) VALUES
(1, 3, 12, 4);

--
-- Daten für Tabelle `User`
--

INSERT INTO `User` (`user_id`, `name`, `username`, `pass_hash`, `type`, `gender`) VALUES
(1, 'Jiffy Service', 'jiffy_service', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', NULL, NULL),
(4, 'Petra Humboldt', 'petrahumboldt', '832988ff750f2ce38ee7b399f5914523f86a64c770cdd95c1f1f3c16bd8da9f4', 'GUARDIAN', 'MALE'),
(11, 'Peter Humboldt', 'peterhumboldt', '832988ff750f2ce38ee7b399f5914523f86a64c770cdd95c1f1f3c16bd8da9f4', 'STUDENT', 'MALE'),
(12, 'Horst Blumen', 'horstblumen', '832988ff750f2ce38ee7b399f5914523f86a64c770cdd95c1f1f3c16bd8da9f4', 'TEACHER', 'MALE'),
(13, 'Lea Humboldt', 'leahumboldt', '832988ff750f2ce38ee7b399f5914523f86a64c770cdd95c1f1f3c16bd8da9f4', 'STUDENT', 'FEMALE'),
(14, 'Nadja Humboldt', 'nadjahumboldt', '832988ff750f2ce38ee7b399f5914523f86a64c770cdd95c1f1f3c16bd8da9f4', 'STUDENT', 'FEMALE');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
