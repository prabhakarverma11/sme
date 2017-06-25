-- Database : arq_sme
drop database IF EXISTS `arq_sme`;
create database arq_sme;
use arq_sme;
set foreign_key_checks=0;
DROP TABLE IF EXISTS `campaign`;
CREATE TABLE `campaign` (
`id` int(11) unsigned NOT NULL AUTO_INCREMENT,
`api_id` bigint(20),
`name` varchar(200),
`status` varchar(50),
`bidding_strategy_id` int(11),
`advertising_channel_type` varchar(50),
`locationInclude` varchar(4000),
`locationInclude_Criteria` int(11),
`locationExclude` varchar(4000),
`locationExclude_Criteria` int(11),
`budget_amount` double, 
`budget_id` bigint(15) ,
`isBudgetChanged` int(1) DEFAULT 0,
`isLocationChanged` int(1) DEFAULT 0,
`start_date` date DEFAULT NULL,
`end_date` date DEFAULT NULL,
`user_id` int(11) unsigned NOT NULL,
`created_by` varchar(50) NOT NULL DEFAULT 'Admin',
`created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`updated_by` varchar(50),
`updated_on` timestamp NULL DEFAULT NULL,
PRIMARY KEY(`id`),
CONSTRAINT `campaign_fkey_uid` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ;


DROP TABLE IF EXISTS `adgroup`;
-- CREATE TABLE `adgroup` (
-- `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
-- `api_id` bigint(20),
-- `ad_api_id` bigint(20),
-- `name` varchar(200),
-- `campaign_id` int(11) unsigned NOT NULL,
-- `status` int(11),
-- `category_name` varchar(500),
-- `product_name` varchar(500),
-- `business_name` varchar(500),
-- `phone_number` varchar(100),
-- `description_line1` varchar(4000),
-- `description_line2` varchar(4000),
-- `display_url` varchar(500),
-- `verification_url` varchar(500),
-- `keyword_ids` varchar(4000),
-- `keyword_names` varchar(4000),
-- `threshold_keyword_avgCPC` double,
-- `user_id` int(11) unsigned NOT NULL,
-- `created_by` varchar(50) NOT NULL DEFAULT 'Admin',
-- `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
-- `updated_by` varchar(50),
-- `updated_on` timestamp NULL DEFAULT NULL,
-- PRIMARY KEY(`id`),
-- CONSTRAINT `adgroup_fkey_cmpid` FOREIGN KEY (`campaign_id`) REFERENCES `campaign` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
-- CONSTRAINT `adgroup_fkey_uid` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
-- );

CREATE TABLE `adgroup` (
`id` int(11) unsigned NOT NULL AUTO_INCREMENT,
`api_id` bigint(20),
`name` varchar(200),
`campaign_id` int(11) unsigned NOT NULL,
`status` varchar(50),
`category_name` varchar(500),
`product_name` varchar(500),
`threshold_keyword_avgCPC` double,
`created_by` varchar(50) NOT NULL DEFAULT 'Admin',
`created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`updated_by` varchar(50),
`updated_on` timestamp NULL DEFAULT NULL,
PRIMARY KEY(`id`),
CONSTRAINT `adgroup_fkey_cmpid` FOREIGN KEY (`campaign_id`) REFERENCES `campaign` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS 'ad';
CREATE TABLE `ad` (
`id` int(11) unsigned NOT NULL AUTO_INCREMENT,
`api_id` bigint(20),
`adgroup_id` int(11) unsigned NOT NULL, 
`adgroup_api_id` bigint(20),
`status` varchar(50),
`business_name` varchar(500),
`phone_number` varchar(100),
`description_line1` varchar(4000),
`description_line2` varchar(4000),
`display_url` varchar(500),
`verification_url` varchar(500),
`created_by` varchar(50) NOT NULL DEFAULT 'Admin',
`created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`updated_by` varchar(50),
`updated_on` timestamp NULL DEFAULT NULL,
PRIMARY KEY(`id`),
CONSTRAINT `ad_fkey_adgroupid` FOREIGN KEY (`adgroup_id`) REFERENCES `adgroup` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);


DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(200),
  `email` varchar(100), 
  `phoneno` varchar(100), 
  `password` varchar(100),
  `created_by` varchar(50) NOT NULL DEFAULT 'Admin',
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50),
  `updated_on` timestamp NULL DEFAULT NULL,
  PRIMARY KEY(`id`)
);




-- DROP TABLE IF EXISTS `location`;
-- CREATE TABLE `location` (
-- `criteria_id` int(11) unsigned NOT NULL,
-- `name` varchar(200),
-- `canonical_name` varchar(1000),
-- `parent_id` varchar(200),
-- `country_code` varchar(10),
-- `target_type` varchar(200),
-- `status` varchar(50),
-- PRIMARY KEY(`criteria_id`)
-- );

-- DROP TABLE IF EXISTS `category`;
-- CREATE TABLE `category` (
--   `id` int(11) unsigned NOT NULL,
--   `prime_category` varchar(100),
--   `category` varchar(1000),
--   PRIMARY KEY(`id`)
-- );



DROP TABLE IF EXISTS `CAMPAIGN_PERFORMANCE_REPORT`;
CREATE TABLE CAMPAIGN_PERFORMANCE_REPORT(
	`id` int(11) unsigned NOT NULL AUTO_INCREMENT,
	`campaign_id` varchar(100) NOT NULL,
	`campaign_name` varchar(100),
	`budget_id` bigint(15),
	`budget_amount` double,
	`status` varchar(50),
`Clicks` bigint(15),
`impressions` bigint(15),
`cost` bigint(15),
`date` date,
`startDate` date,
`endDate` date,
PRIMARY KEY(`id`)
);


DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `role` varchar(50) NOT NULL DEFAULT 'User',
  `user_id` int(11) unsigned NOT NULL,
  `created_by` varchar(50) NOT NULL DEFAULT 'Admin',
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50),
  `updated_on` timestamp NULL DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `user_role_fkey_uid` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS `keyword`;
CREATE TABLE `keyword` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `api_id` bigint(20),
  `text` varchar(200),
  `status` varchar(50),
  `categoryName` varchar(1000),
  `bid` double,
  `avg_monthly_search` bigint(20),
  `competition` double,
  `avg_CPC` double,
  `match_type` varchar(20),
  `adgroup_id` int(11) unsigned NOT NULL, 
  `adgroup_api_id` bigint(20),
  `created_by` varchar(50) NOT NULL DEFAULT 'Admin',
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50),
  `updated_on` timestamp NULL DEFAULT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `keyword_fkey_adgroupid` FOREIGN KEY (`adgroup_id`) REFERENCES `adgroup` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

--DROP TABLE IF EXISTS `productcategory`;
-- CREATE TABLE `productcategory` (
--   `id` int(11) unsigned NOT NULL,
--   `category` varchar(1000),
--   PRIMARY KEY(`id`)
-- );


insert into user(name,email,phoneno,password,created_on,is_verified) values('Manu Mishra','manu.mishra@hindustantimes.com',9910000000,'manu',now(),1);
insert into user(name,email,phoneno,password,created_on,is_verified) values('Admin','admin@arq.co.in',9910000000,'Admin',now(),1);
insert into user_role(id,role,user_id,created_by,created_on,updated_on,updated_by) values(1,'Admin',1,'Super Admin','2016-09-06 18:33:49',null,null);
insert into user_role(id,role,user_id,created_by,created_on,updated_on,updated_by) values(2,'Admin',2,'Super Admin','2016-09-06 18:33:49',null,null);


