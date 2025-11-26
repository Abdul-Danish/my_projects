package com.vote.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vote.lib.models.Vote;
import com.vote.lib.repository.VoteRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VoteService {

    @Value("${get.vote.result.api}")
    private String resultApi;
    
    @Autowired
    private VoteRepository voteRepository;
    
    @Autowired
    private RestTemplate restTemplate;

    public Vote updateVote(Vote vote) {
        return voteRepository.save(vote);
    }
    
    @SuppressWarnings("unchecked")
    public List<Vote> getVoteResults() {
        log.info("Executing URI: {}", resultApi);
        return restTemplate.exchange(resultApi, HttpMethod.GET, null, List.class).getBody();
//        return restTemplate.getForObject(resultApi, List.class);
    }

}
