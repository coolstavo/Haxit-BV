package com.webapp.hexit.seeder;

import com.webapp.hexit.model.Genre;
import com.webapp.hexit.repository.GenreRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Seeder to populate the database with common music genres.
 * Runs automatically on application startup.
 */
@Component
@Order(1) // Run this seeder first
public class GenreSeeder implements CommandLineRunner {

    private final GenreRepository genreRepository;

    public GenreSeeder(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if genres already exist
        if (genreRepository.count() > 0) {
            System.out.println("Genres already exist in database. Skipping seeder.");
            return;
        }

        System.out.println("Seeding genres...");

        // List of common music genres
        List<String> genreNames = Arrays.asList(
            "Jazz",
            "Pop",
            "Rock",
            "Classical",
            "Hip Hop",
            "Electronic",
            "R&B",
            "Country",
            "Blues",
            "Metal",
            "Folk",
            "Reggae",
            "Soul",
            "Funk",
            "Disco",
            "Latin",
            "Ska",
            "Punk",
            "Indie",
            "Alternative",
            "Dance",
            "House",
            "Techno",
            "Trance",
            "Dubstep",
            "Drum & Bass",
            "Gospel",
            "Opera",
            "Swing",
            "Bluegrass",
            "Grunge",
            "Ambient",
            "World Music",
            "Afrobeat",
            "Salsa",
            "Flamenco",
            "Bossa Nova",
            "Samba"
        );

        // Create and save genres
        for (String name : genreNames) {
            Genre genre = new Genre(name);
            genreRepository.save(genre);
        }

        System.out.println("Successfully seeded " + genreNames.size() + " genres!");
    }
}
