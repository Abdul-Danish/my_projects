package com.vote.result.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vote.lib.models.Vote;
import com.vote.lib.repository.VoteRepository;

@Service
public class VoteResultService {

    @Autowired
    private VoteRepository voteRepository;
    
    public List<Vote> getVoteResults() {
        return voteRepository.findAll();
    }
    
}
