package com.webapp.hexit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "jam_instrument")
public class JamInstrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "jam_id", nullable = false)
    private Jam jam;

    @ManyToOne
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    public JamInstrument() {}

    public JamInstrument(Jam jam, Instrument instrument) {
        this.jam = jam;
        this.instrument = instrument;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Jam getJam() {
        return jam;
    }

    public void setJam(Jam jam) {
        this.jam = jam;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }
}
