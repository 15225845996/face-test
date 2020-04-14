

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for face_info
-- ----------------------------
DROP TABLE IF EXISTS `face_info`;
CREATE TABLE `face_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `face_data` blob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
