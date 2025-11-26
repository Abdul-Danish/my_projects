package com.vote.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vote.lib.models.Vote;
import com.vote.service.VoteService;

@RestController
@RequestMapping("/api/v1/vote")
public class VotingController {

    @Autowired
    private VoteService voteService;

    @PostMapping
    public ResponseEntity<Vote> updateVote(@RequestBody Vote vote) {
        return ResponseEntity.ok(voteService.updateVote(vote));
    }

    @GetMapping
    public ResponseEntity<List<Vote>> getVoteResults() {
        return ResponseEntity.ok(voteService.getVoteResults());
    }

}
