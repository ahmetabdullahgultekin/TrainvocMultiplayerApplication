package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "words")
public class Word {
    // getter ve setter'lar
    @Id
    @Column(name = "word")
    private String word;

    @Column(name = "meaning")
    private String meaning;

    @Column(name = "level")
    private String level;

    @Column(name = "last_reviewed")
    private Long lastReviewed;

    @Column(name = "stat_id")
    private Integer statId;

    @Column(name = "seconds_spent")
    private Integer secondsSpent;

    // Getters, setters, constructors
    public Word() {
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Long getLastReviewed() {
        return lastReviewed;
    }

    public void setLastReviewed(Long lastReviewed) {
        this.lastReviewed = lastReviewed;
    }

    public Integer getStatId() {
        return statId;
    }

    public void setStatId(Integer statId) {
        this.statId = statId;
    }

    public Integer getSecondsSpent() {
        return secondsSpent;
    }

    public void setSecondsSpent(Integer secondsSpent) {
        this.secondsSpent = secondsSpent;
    }
}