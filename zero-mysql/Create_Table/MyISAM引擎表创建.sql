CREATE TABLE `test_myisam_engin`
(
    `id`   int(10) unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(100) DEFAULT NULL,
    `age`  int(10) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;