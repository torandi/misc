CREATE TABLE `bostader` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`lan` varchar(64) NOT NULL,
	`objekttyp` varchar(64) NOT NULL,
	`adress` varchar(64) NOT NULL,
	`area` float NOT NULL,
	`rum` int(11) NOT NULL,
	`pris` decimal(12,2) NOT NULL,
	`avgift` decimal(12,2) NOT NULL,
	PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
	
