
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `person` (
  `id` varchar (31) NOT NULL,
  `password` varchar (255) NOT NULL,
  `UUID` varchar (255) DEFAULT NULL,
  `name` varchar (255) DEFAULT NULL,
  `bankroll` decimal (11) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
