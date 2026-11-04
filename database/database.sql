SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `soundaura`
--

-- --------------------------------------------------------

--
-- Table structure for table `artists_profile`
--

CREATE TABLE `artists_profile` (
  `artist_id` int(20) NOT NULL,
  `user_id` int(20) NOT NULL,
  `verified_status` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `artists_profile`
--
DELIMITER $$
CREATE TRIGGER `trg_before_delete_artist` BEFORE DELETE ON `artists_profile` FOR EACH ROW BEGIN
    DELETE FROM tracks WHERE artist_id = OLD.user_id;
    DELETE FROM users WHERE user_id = OLD.user_id;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `liked_songs`
--

CREATE TABLE `liked_songs` (
  `like_id` int(20) NOT NULL,
  `user_id` int(20) NOT NULL,
  `track_id` int(20) NOT NULL,
  `liked_at` timestamp(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `playlists`
--

CREATE TABLE `playlists` (
  `playlist_id` int(30) NOT NULL,
  `user_id` int(30) NOT NULL,
  `name` varchar(100) NOT NULL,
  `created_at` timestamp(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Table structure for table `playlists_tracks`
--

CREATE TABLE `playlists_tracks` (
  `playlist_id` int(20) NOT NULL,
  `track_id` int(20) NOT NULL,
  `track_order` int(20) NOT NULL,
  `added_at` timestamp(6) NOT NULL DEFAULT current_timestamp(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Table structure for table `play_history`
--

CREATE TABLE `play_history` (
  `Play_id` bigint(100) NOT NULL,
  `user_id` int(20) NOT NULL,
  `track_id` int(20) NOT NULL,
  `played_at` timestamp(6) NOT NULL DEFAULT current_timestamp(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Table structure for table `tracks`
--

CREATE TABLE `tracks` (
  `track_id` int(30) NOT NULL,
  `title` varchar(255) NOT NULL,
  `artist_id` int(30) NOT NULL,
  `genre` varchar(100) DEFAULT NULL,
  `duration_seconds` int(255) NOT NULL,
  `lyrics` longtext DEFAULT NULL,
  `file_path` varchar(150) NOT NULL,
  `release_date` varchar(50) NOT NULL,
  `total__views` int(50) DEFAULT NULL,
  `is_approved` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Triggers `tracks`
--
DELIMITER $$
CREATE TRIGGER `trg_before_delete_track` BEFORE DELETE ON `tracks` FOR EACH ROW BEGIN
    DELETE FROM playlists_tracks WHERE track_id = OLD.track_id;
    DELETE FROM liked_songs WHERE track_id = OLD.track_id;
    DELETE FROM play_history WHERE track_id = OLD.track_id;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(20) NOT NULL,
  `username` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` enum('USER','ARTIST','ADMIN') DEFAULT NULL,
  `account_status` enum('ACTIVE','BANNED') DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Triggers `users`
--
DELIMITER $$
CREATE TRIGGER `trg_before_delete_user` BEFORE DELETE ON `users` FOR EACH ROW BEGIN
    -- if this user is an artist, this cascades through triggers 2 and 1
    DELETE FROM artists_profile WHERE user_id = OLD.user_id;

    -- clean up this user's own playlists (playlists_tracks has no FK at all,
    -- so this must be handled explicitly)
    DELETE FROM playlists_tracks
        WHERE playlist_id IN (SELECT playlist_id FROM playlists WHERE user_id = OLD.user_id);
    DELETE FROM playlists WHERE user_id = OLD.user_id;

    -- clean up the rest of this user's own activity
    DELETE FROM liked_songs WHERE user_id = OLD.user_id;
    DELETE FROM play_history WHERE user_id = OLD.user_id;
END
$$
DELIMITER ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `artists_profile`
--
ALTER TABLE `artists_profile`
  ADD PRIMARY KEY (`artist_id`),
  ADD KEY `fk_artist_user` (`user_id`);

--
-- Indexes for table `liked_songs`
--
ALTER TABLE `liked_songs`
  ADD PRIMARY KEY (`like_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `track_id` (`track_id`);

--
-- Indexes for table `playlists`
--
ALTER TABLE `playlists`
  ADD PRIMARY KEY (`playlist_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `playlists_tracks`
--
ALTER TABLE `playlists_tracks`
  ADD KEY `playlist_id` (`playlist_id`),
  ADD KEY `track_id` (`track_id`);

--
-- Indexes for table `play_history`
--
ALTER TABLE `play_history`
  ADD PRIMARY KEY (`Play_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `track_id` (`track_id`);

--
-- Indexes for table `tracks`
--
ALTER TABLE `tracks`
  ADD PRIMARY KEY (`track_id`),
  ADD KEY `fk_tracks_artist` (`artist_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `uq_users_email` (`email`),
  ADD UNIQUE KEY `uq_users_username` (`username`),
  ADD UNIQUE KEY `username` (`username`,`email`),
  ADD UNIQUE KEY `username_2` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `artists_profile`
--
ALTER TABLE `artists_profile`
  MODIFY `artist_id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

--
-- AUTO_INCREMENT for table `liked_songs`
--
ALTER TABLE `liked_songs`
  MODIFY `like_id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

--
-- AUTO_INCREMENT for table `playlists`
--
ALTER TABLE `playlists`
  MODIFY `playlist_id` int(30) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

--
-- AUTO_INCREMENT for table `play_history`
--
ALTER TABLE `play_history`
  MODIFY `Play_id` bigint(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

--
-- AUTO_INCREMENT for table `tracks`
--
ALTER TABLE `tracks`
  MODIFY `track_id` int(30) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `artists_profile`
--
ALTER TABLE `artists_profile`
  ADD CONSTRAINT `fk_artist_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `liked_songs`
--
ALTER TABLE `liked_songs`
  ADD CONSTRAINT `liked_songs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `liked_songs_ibfk_2` FOREIGN KEY (`track_id`) REFERENCES `tracks` (`track_id`);

--
-- Constraints for table `playlists`
--
ALTER TABLE `playlists`
  ADD CONSTRAINT `playlists_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `play_history`
--
ALTER TABLE `play_history`
  ADD CONSTRAINT `play_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `play_history_ibfk_2` FOREIGN KEY (`track_id`) REFERENCES `tracks` (`track_id`) ON UPDATE CASCADE;

--
-- Constraints for table `tracks`
--
ALTER TABLE `tracks`
  ADD CONSTRAINT `fk_tracks_artist` FOREIGN KEY (`artist_id`) REFERENCES `users` (`user_id`);

-- Synthetic demonstration accounts only; all addresses use the reserved .test domain.
INSERT INTO `users` (`user_id`, `username`, `email`, `password`, `role`, `account_status`, `created_at`) VALUES
(1, 'Admin', 'admin-demo@example.test', 'DemoOnly-Admin-7xQ2', 'ADMIN', 'ACTIVE', '2026-11-04 00:00:00'),
(2, 'SampleUser', 'user-demo@example.test', 'DemoOnly-User-4mP8', 'USER', 'ACTIVE', '2026-11-04 00:00:00'),
(3, 'SampleArtist', 'artist-demo@example.test', 'DemoOnly-Artist-9rK5', 'ARTIST', 'ACTIVE', '2026-11-04 00:00:00');

-- The artist account needs its matching profile row to use the artist flow.
INSERT INTO `artists_profile` (`artist_id`, `user_id`, `verified_status`) VALUES
(1, 3, 0);

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
