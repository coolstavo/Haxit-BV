package com.webapp.hexit.model;

import jakarta.persistence.*;

@Entity
public class Musician_Instrument {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "muzikant_id", nullable = false)
  private Muzikant muzikant;

  @ManyToOne
  @JoinColumn(name = "instrument_id", nullable = false)
  private Instrument instrument;

  @Column(nullable = false)
  private int niveau;

  public Musician_Instrument() {}

  public Musician_Instrument(
    Muzikant muzikant,
    Instrument instrument,
    int niveau
  ) {
    this.muzikant = muzikant;
    this.instrument = instrument;
    this.niveau = niveau;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public Muzikant getMuzikant() {
    return muzikant;
  }

  public void setMuzikant(Muzikant muzikant) {
    this.muzikant = muzikant;
  }

  public Instrument getInstrument() {
    return instrument;
  }

  public void setInstrument(Instrument instrument) {
    this.instrument = instrument;
  }

  public int getNiveau() {
    return niveau;
  }

  public void setNiveau(int niveau) {
    this.niveau = niveau;
  }
}
