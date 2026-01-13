package com.webapp.hexit.model;

import jakarta.persistence.*;

@Entity
public class MuzikantInstrument {

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
    private String level;

    public MuzikantInstrument() {}

    public MuzikantInstrument(
        Muzikant muzikant,
        Instrument instrument,
        String level
    ) {
        this.muzikant = muzikant;
        this.instrument = instrument;
        this.level = level;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
