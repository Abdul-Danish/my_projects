package com.vote.result.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vote.lib.models.Vote;
import com.vote.result.service.VoteResultService;

@RestController
@RequestMapping("/api/v1/vote/result")
public class VoteResultController {

    @Autowired
    private VoteResultService voteResultService;

    @GetMapping
    public ResponseEntity<List<Vote>> getVoteResults() {
        return ResponseEntity.ok(voteResultService.getVoteResults());
    }

}
