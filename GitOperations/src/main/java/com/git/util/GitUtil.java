package com.git.util;

import java.io.IOException;

import org.kohsuke.github.GHRef;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTreeBuilder;
import org.springframework.stereotype.Component;

@Component
public class GitUtil {
    
    private static final String GITHUB_REF = "refs/heads/";

    public GHRef createBranchRef(String defaultBranch, String newBranch, GHRepository ghRepository) throws IOException {
        String defaultBranchSha = ghRepository.getRef(GITHUB_REF + defaultBranch).getObject().getSha();
        return ghRepository.createRef(GITHUB_REF + newBranch, defaultBranchSha);
    }
    
    public GHTreeBuilder createTree(GHRepository ghRepository) {
        return ghRepository.createTree();
    }
    
    public void addFilesToTree(GHTreeBuilder ghTree, String path, String content) {
        ghTree.add(path, content, false);
    }
    
}
