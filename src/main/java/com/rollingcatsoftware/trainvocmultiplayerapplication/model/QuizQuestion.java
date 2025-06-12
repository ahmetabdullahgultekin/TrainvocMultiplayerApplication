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

    // getter ve setter'lar
}