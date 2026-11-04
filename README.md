# SoundAura

SoundAura is a Java command-line music library and player backed by MySQL or MariaDB. It has user, artist, and admin flows, including search, playlists, liked tracks, playback history, recommendations, and artist track uploads. JavaFX provides audio playback.

## Project contents

- `src/soundaura/` — Java application source.
- `database/database.sql` — database tables, triggers, three synthetic demo users, and the artist profile row required by the demo artist account. It contains no track, playlist, listening-history, or lyric rows.
- `database/ER_Diagram.jpeg` — database relationship diagram.
- `tracks/` — local audio files referenced by track records. Files in this directory are ignored by Git by default.
- `artist/track/` — local audio files for artist uploads; contents are ignored by Git by default.
- `artist/lyrics/` — local lyrics text files for artist uploads; contents are ignored by Git by default.
- `graphic_text/` — text art used by the terminal screens.
- `lib/` — local dependencies downloaded separately; they are excluded from the repository.

This public copy intentionally contains no songs or lyrics. The project author does not have licenses to redistribute the removed songs. Do not add copyrighted audio or lyrics unless you created them, have permission, or their license expressly allows the use and redistribution you plan.

## Requirements

- A recent JDK compatible with JavaFX 26.0.1.
- MySQL or MariaDB.
- JavaFX SDK 26.0.1 for your operating system and architecture.
- MySQL Connector/J 8.1.0.

The JavaFX SDK and MySQL Connector/J binaries are not stored in this repository. The JavaFX SDK includes a 105 MB native library that exceeds GitHub's per-file upload limit. Keeping dependencies out of Git also avoids redistributing the Connector/J binary without its corresponding source package. Download them from [Gluon's JavaFX downloads](https://gluonhq.com/products/javafx/) and [MySQL Connector/J downloads](https://dev.mysql.com/downloads/connector/j/), then set them up locally:

- Extract the JavaFX SDK so its `lib/` directory is at `lib/javafx-sdk-26.0.1/lib/` within this project.
- Put the MySQL Connector/J 8.1.0 JAR at `lib/mysql-connector-j-8.1.0.jar`.

## Set up the database

1. Start MySQL or MariaDB.
2. Create the database:

   ```sql
   CREATE DATABASE soundaura;
   ```

3. From the project root, import the schema and the three synthetic users:

   ```sh
   mysql -u root -p soundaura < database/database.sql
   ```

   The SQL file creates the tables and inserts only three fictional user records: an admin, a regular user, and an artist, plus the artist's profile row. It does not populate tracks, audio, lyrics, playlists, or listening history. Add any track metadata you want to use to the `tracks` table yourself, and make sure each `file_path` matches an audio filename available in `tracks/`.

4. Update `src/soundaura/dbms/JdbcConnection.java` for your local database connection. The current code uses `jdbc:mysql://localhost:3306/soundaura`, database user `root`, and an empty password. Use a dedicated database account and keep its credentials out of public source code for any non-local setup.

### Demo user records

**Warning:** The SQL seed includes three public demo accounts, and their placeholder passwords are stored as plain text because that is how the current application works. Use these accounts only in a local demo. Change or remove them before using real accounts, and never put real personal information or passwords in a public seed file.

## Third-party software

The MIT license in this repository applies to the original SoundAura code and documentation only. Third-party software keeps its own license terms; the MIT license does not relicense those files.

- OpenJFX 26.0.1 uses the GPLv2 with the Classpath Exception for applicable JavaFX code and includes separate third-party components. Keep the license, exception, and third-party notice files supplied in the JavaFX SDK when using or redistributing that SDK.
- MySQL Connector/J 8.1.0 is licensed under GPLv2 with Oracle's Universal FOSS Exception 1.0. Its `LICENSE` notice is inside the downloaded JAR. The exception addresses use with other free/open-source software under its stated conditions; it does not change the JAR's license to MIT. This repository does not distribute the JAR; obtain it separately from the official MySQL download page.

The media directories contain placeholders only and no songs or lyrics. No music license is included or implied by the SoundAura code license. Their contents are ignored by Git by default to reduce the chance of accidentally publishing files you do not have rights to redistribute. If you have rights to share a particular file publicly, you can deliberately force-add it.

## Add your own media

The application expects track file names in the database to refer to files under `tracks/`. Artist upload asks for an MP3 file from `artist/track/` and optionally reads a text file from `artist/lyrics/`. Add only media you are authorized to use. The repository does not provide songs or lyrics; users must add suitable files and the corresponding track information to their own local database. Media files are ignored by Git by default, so they stay local unless you deliberately force-add files you are licensed to redistribute.

## Build and run

Run from the project root because the program resolves terminal artwork and media using relative paths. After installing JavaFX SDK 26.0.1 and MySQL Connector/J 8.1.0 as described above:

```sh
mkdir -p out
javac --module-path lib/javafx-sdk-26.0.1/lib \
  --add-modules javafx.controls,javafx.media \
  -cp 'lib/mysql-connector-j-8.1.0.jar' \
  -d out $(find src -name '*.java')

java --module-path lib/javafx-sdk-26.0.1/lib \
  --add-modules javafx.controls,javafx.media \
  -cp 'out:lib/mysql-connector-j-8.1.0.jar' \
  soundaura.Main
```

On Windows, use `;` instead of `:` between classpath entries. Make sure the JavaFX SDK matches your OS and architecture.

## Known limitations

- The database connection settings are hard-coded.
- The application currently stores and compares passwords as plain text. This is unsuitable for real accounts.
- Media paths are relative to the working directory, so launch from the project root.
- See the root [`LICENSE`](LICENSE) for terms applying to SoundAura's original code and documentation. Third-party software and media remain under their own licenses.
