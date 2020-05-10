/*
 Navicat Premium Data Transfer

 Source Server         : shanghai-server
 Source Server Type    : MySQL
 Source Server Version : 50730
 Source Host           : 129.211.0.142:3306
 Source Schema         : flash_sale

 Target Server Type    : MySQL
 Target Server Version : 50730
 File Encoding         : 65001

 Date: 10/05/2020 21:50:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for item
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item`  (
  `id` bigint(32) UNSIGNED NOT NULL,
  `title` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `description` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `sales` int(11) NOT NULL DEFAULT 0,
  `img_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `gmt_create` datetime(0) NOT NULL,
  `gmt_modified` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  `is_deleted` tinyint(4) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of item
-- ----------------------------
INSERT INTO `item` VALUES (1, 'iphone11 Pro', 7000.00, '最好用的苹果手机', 0, 'https://store.storeimages.cdn-apple.com/8756/as-images.apple.com/is/iphone11-gallery5-2019?wid=3360&hei=1280&fmt=jpeg&qlt=80&op_usm=0.5,0.5&.v=1567280207602', '2020-05-04 21:44:01', '2020-05-10 21:49:47', 0);

-- ----------------------------
-- Table structure for item_order
-- ----------------------------
DROP TABLE IF EXISTS `item_order`;
CREATE TABLE `item_order`  (
  `id` bigint(32) UNSIGNED NOT NULL,
  `user_id` bigint(32) UNSIGNED NOT NULL DEFAULT 0,
  `item_id` bigint(32) UNSIGNED NOT NULL DEFAULT 0,
  `item_price` decimal(20, 2) NOT NULL DEFAULT 0.00,
  `amount` bigint(11) UNSIGNED NOT NULL DEFAULT 0,
  `order_price` decimal(20, 2) NOT NULL DEFAULT 0.00,
  `gmt_create` datetime(0) NOT NULL DEFAULT '2020-05-04 21:40:48',
  `gmt_modified` datetime(0) NOT NULL DEFAULT '2020-05-04 21:40:48' ON UPDATE CURRENT_TIMESTAMP(0),
  `is_deleted` tinyint(4) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for item_stock
-- ----------------------------
DROP TABLE IF EXISTS `item_stock`;
CREATE TABLE `item_stock`  (
  `id` bigint(32) UNSIGNED NOT NULL,
  `stock` bigint(32) UNSIGNED NOT NULL DEFAULT 0,
  `item_id` bigint(32) UNSIGNED NOT NULL DEFAULT 0,
  `gmt_create` datetime(0) NOT NULL DEFAULT '2020-05-04 21:40:48',
  `gmt_modified` datetime(0) NOT NULL DEFAULT '2020-05-04 21:40:48' ON UPDATE CURRENT_TIMESTAMP(0),
  `is_deleted` tinyint(4) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `item_id_index`(`item_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of item_stock
-- ----------------------------
INSERT INTO `item_stock` VALUES (1, 1000000, 1, '2020-05-04 21:40:48', '2020-05-10 21:49:53', 0);

SET FOREIGN_KEY_CHECKS = 1;
