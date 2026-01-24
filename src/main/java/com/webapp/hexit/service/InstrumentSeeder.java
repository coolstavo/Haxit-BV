package com.webapp.hexit.service;

import com.webapp.hexit.model.Instrument;
import com.webapp.hexit.repository.InstrumentRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InstrumentSeeder implements CommandLineRunner {

  private final InstrumentRepository instrumentRepository;

  public InstrumentSeeder(InstrumentRepository instrumentRepository) {
    this.instrumentRepository = instrumentRepository;
  }

  @Override
  public void run(String... args) {
    if (instrumentRepository.count() == 0) {
      List<String> instruments = Arrays.asList(
        "Gitaar",
        "Piano",
        "Zang",
        "Viool",
        "Cello",
        "Trompet",
        "Saxofoon",
        "Drums",
        "Fluit",
        "Klarinet",
        "Harp",
        "Mandoline",
        "Bas",
        "Ukelele",
        "Hobo",
        "Fagot",
        "Accordeon",
        "Harmonica",
        "Trombone",
        "Hoorn",
        "Banjo",
        "Keyboard",
        "Orgel",
        "Djembe",
        "Contrabas",
        "Rapper",
        "Producer",
        "DJ"
      );

      for (String name : instruments) {
        Instrument instrument = new Instrument(name);
        instrumentRepository.save(instrument);
      }

      System.out.println(
        "Instruments seeded successfully: " +
          instruments.size() +
          " instruments added."
      );
    } else {
      System.out.println(
        "Instruments already exist in database. Skipping seeding."
      );
    }
  }
}
