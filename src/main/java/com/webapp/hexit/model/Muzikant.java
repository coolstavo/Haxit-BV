package com.webapp.hexit.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Muzikant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] profilePic;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String naam;
    private int leeftijd;

    @ManyToMany
    @JoinTable(
        name = "musician_genre",
        joinColumns = @JoinColumn(name = "muzikant_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres = new ArrayList<>();

    public Muzikant() {}

    public Muzikant(User user, String naam, int leeftijd) {
        this.user = user;
        this.naam = naam;
        this.leeftijd = leeftijd;
    }

    @ManyToMany
    @JoinTable(
        name = "musician_instrument",
        joinColumns = @JoinColumn(name = "muzikant_id"),
        inverseJoinColumns = @JoinColumn(name = "instrument_id")
    )
    private List<Instrument> instruments = new ArrayList<>();

    // Getters and Setters

    public Muzikant getMuzikant() {
        return this;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    // In Muzikant.java
    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public int getLeeftijd() {
        return leeftijd;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void setLeeftijd(int leeftijd) {
        this.leeftijd = leeftijd;
    }

    public List<Instrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(List<Instrument> instruments) {
        this.instruments = instruments;
    }
}
