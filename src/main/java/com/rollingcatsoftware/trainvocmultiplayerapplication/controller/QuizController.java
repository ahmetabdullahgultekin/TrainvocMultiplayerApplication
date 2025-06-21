package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizQuestion;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.QuizService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/question")
    public Object getQuestion(@RequestParam(required = false) String level, @RequestParam(required = false) Integer optionCount) {
        if (level == null || level.isEmpty()) {
            return java.util.Collections.singletonMap("error", "Missing or empty parameter: level");
        }
        if (optionCount == null || optionCount <= 0) {
            return java.util.Collections.singletonMap("error", "Missing or invalid parameter: optionCount");
        }
        try {
            QuizQuestion question = quizService.generateQuestion(level, optionCount);
            if (question == null) {
                return java.util.Collections.singletonMap("error", "No question found for the given parameters.");
            }
            return question;
        } catch (RuntimeException ex) {
            return java.util.Collections.singletonMap("error", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred while generating the question.");
        }
    }
}