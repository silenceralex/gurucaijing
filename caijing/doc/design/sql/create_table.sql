CREATE TABLE `user` (
  `uid` int(11) NOT NULL auto_increment,
  `username` varchar(18) default NULL,
  `password` varchar(32),
  `nickname` varchar(250) default NULL,
  `introduction` mediumtext,
  `default_setting` text,
  `reg_time` datetime default NULL,
  `birthday` varchar(10) default NULL,
  `credit` int(11) default NULL,
  PRIMARY KEY  (`uid`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8