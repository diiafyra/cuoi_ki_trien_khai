package com.example.demo.models;

import java.util.List;

import com.example.demo.models.sub.Artist;
import com.example.demo.models.sub.Track;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isActive;

    // Danh sách thể loại yêu thích (ví dụ: Pop, Rock)
    @ElementCollection
    private List<String> favoriteGenres;

    // Quan hệ n-n: profile có thể thích nhiều artist
    @ManyToMany
    @JoinTable(
        name = "profile_favorite_artists",
        joinColumns = @JoinColumn(name = "profile_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private List<Artist> favoriteArtists;

    // Quan hệ n-n: profile có thể thích nhiều track
    @ManyToMany
    @JoinTable(
        name = "profile_favorite_tracks",
        joinColumns = @JoinColumn(name = "profile_id"),
        inverseJoinColumns = @JoinColumn(name = "track_id")
    )
    private List<Track> favoriteTracks;
}
