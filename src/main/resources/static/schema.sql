CREATE TABLE `user` (
                        `user_no` int NOT NULL AUTO_INCREMENT,
                        `user_id` varchar(20) DEFAULT NULL,
                        `password` varchar(100) DEFAULT NULL,
                        `name` varchar(20) DEFAULT NULL,
                        `email` varchar(100) DEFAULT NULL,
                        `phone` varchar(20) DEFAULT NULL,
                        `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                        `updated_by` int DEFAULT NULL,
                        PRIMARY KEY (`user_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `post` (
                        `post_no` int NOT NULL AUTO_INCREMENT,
                        `user_no` int NOT NULL,
                        `title` varchar(150) DEFAULT NULL,
                        `contents` varchar(3000) DEFAULT NULL,
                        `is_temp` tinyint(1) DEFAULT '0',
                        `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                        `updated_by` int DEFAULT NULL,
                        PRIMARY KEY (`post_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `comment` (
                           `comment_no` int NOT NULL AUTO_INCREMENT,
                           `user_no` int NOT NULL,
                           `post_no` int NOT NULL,
                           `contents` varchar(150) DEFAULT NULL,
                           `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                           `updated_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                           `updated_by` int DEFAULT NULL,
                           PRIMARY KEY (`comment_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `file` (
                        `file_no` int NOT NULL AUTO_INCREMENT,
                        `post_no` int NOT NULL,
                        `user_no` int NOT NULL,
                        `file_path` varchar(100) DEFAULT NULL,
                        `file_name` varchar(100) DEFAULT NULL,
                        `file_ext` varchar(50) DEFAULT NULL,
                        `is_temp` tinyint(1) DEFAULT '0',
                        `is_deleted` tinyint(1) DEFAULT '0',
                        `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                        `updated_by` int DEFAULT NULL,
                        PRIMARY KEY (`file_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
