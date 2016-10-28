DROP TABLE if exists `account`;
CREATE TABLE `account` (
  `id` bigint(20) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `balance` decimal(18,2) DEFAULT '0.00',
  PRIMARY KEY (`id`)
);