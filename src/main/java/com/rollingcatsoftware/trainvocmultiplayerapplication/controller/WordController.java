package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Word;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word.WordRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/words")
public class WordController {
    private final WordRepository wordRepository;

    public WordController(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @GetMapping
    public List<Word> getAllWords() {
        return wordRepository.findAll();
    }

    @GetMapping("/by-level")
    public List<Word> getWordsByLevel(@RequestParam String level) {
        return wordRepository.findByLevel(level);
    }

    @GetMapping("/by-exam")
    public List<Word> getWordsByExam(@RequestParam String exam) {
        return wordRepository.findByExam(exam);
    }
}
