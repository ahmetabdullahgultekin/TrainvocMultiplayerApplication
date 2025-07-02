package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import java.util.List;

public class QuizQuestion {
    private String english;
    private String correctMeaning;
    private List<String> options;

    public QuizQuestion(String english, String correctMeaning, List<String> options) {
        this.english = english;
        this.correctMeaning = correctMeaning;
        this.options = options;
    }

    public QuizQuestion() {
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getCorrectMeaning() {
        return correctMeaning;
    }

    public void setCorrectMeaning(String correctMeaning) {
        this.correctMeaning = correctMeaning;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}