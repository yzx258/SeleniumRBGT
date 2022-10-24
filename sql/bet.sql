CREATE
DATABASE `yb_bet` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

-- yb_bet.bet_game_account_info definition

CREATE TABLE `bet_game_account_info`
(
    `id`      varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
    `acc_key` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `acc_val` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- yb_bet.bet_game_info definition

CREATE TABLE `bet_game_info`
(
    `id`                          varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
    `create_time`                 datetime                                DEFAULT NULL,
    `single_or_double`            int(11) DEFAULT NULL,
    `operate_thread_type`         int(11) DEFAULT NULL,
    `lock_id`                     varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `competition_name`            varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `home_team_name`              varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `home_team_score`             varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `away_team_name`              varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `away_team_score`             varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `which_section`               varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `screenings`                  varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `original_amount`             decimal(10, 0)                          DEFAULT NULL,
    `balance_amount`              decimal(10, 0)                          DEFAULT NULL,
    `bet_amount`                  decimal(10, 0)                          DEFAULT NULL,
    `is_settlement`               int(11) DEFAULT NULL,
    `competition_result`          int(11) DEFAULT NULL,
    `competition_magnification`   decimal(10, 0)                          DEFAULT NULL,
    `competition_dividend_amount` decimal(10, 0)                          DEFAULT NULL,
    `competition_type`            int(11) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- yb_bet.bet_lock definition

CREATE TABLE `bet_lock`
(
    `id`         varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
    `lock_key`   varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `lock_value` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;