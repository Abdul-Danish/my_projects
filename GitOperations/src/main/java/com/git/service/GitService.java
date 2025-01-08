package com.git.service;

import java.io.IOException;
import java.util.UUID;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRef;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTreeBuilder;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.git.util.GitUtil;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GitService {

    private static final String DEFAULT_BRANCH = "main";

    @Value("${git.access.token}")
    private String gitAccessToken;

    @Autowired
    private GitUtil gitUtil;

    private GHRepository ghRepository = null;

    @PostConstruct
    public void init() throws IOException {
        GitHub gitHub = new GitHubBuilder().withOAuthToken(gitAccessToken).build();
        ghRepository = gitHub.getRepository("AbdulDanish/git_operations");
    }

    public void createBranch() throws IOException {
        String branchName = UUID.randomUUID().toString();
        String path = "src/main/java/com/sample/SampleFile.java";
        String content = "package com.digitaldots.connector.sql;\\n\\n    import java.sql.PreparedStatement;\\n    import java.sql.SQLException;\\n    import java.util.HashMap;\\n    import java.util.Map;\\n    import java.util.Objects;\\n    import java.util.Properties;\\n        \\n    import org.springframework.dao.DataAccessException;\\n    import org.springframework.jdbc.core.PreparedStatementCallback;\\n    import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;\\n    import org.springframework.stereotype.Component;\\n        \\n    import com.digitaldots.connector.ConnectorException;\\n    import com.digitaldots.connector.cache.Store;\\n    import com.zaxxer.hikari.HikariConfig;\\n    import com.zaxxer.hikari.HikariDataSource;\\n        \\n    import lombok.extern.slf4j.Slf4j;\\n        \\n    @Component\\n    @Slf4j\\n    public class SqlBeverDS implements Store<NamedParameterJdbcTemplate, NamedParameterJdbcTemplate> {\\n    \\n        private static final String JDBC_URL = \"jdbcUrl\";\\n        private static final String USERNAME = \"authUserName\";\\n        private static final String PASSWORD = \"authPassword\";\\n        private static final String DATABASE = \"database\";\\n    \\n        private void checkNull(Map<String, Object> configProperties) {\\n            if (Objects.isNull(configProperties.get(JDBC_URL))) {\\n                throw new ConnectorException(\"Jdbc Url cannot be null\");\\n            }\\n            if (Objects.isNull(configProperties.get(USERNAME))) {\\n                throw new ConnectorException(\"username cannot be null\");\\n            }\\n            if (Objects.isNull(configProperties.get(PASSWORD))) {\\n                throw new ConnectorException(\"password cannot be null\");\\n            }\\n        }\\n    \\n        @Override\\n        public NamedParameterJdbcTemplate getDataSource(Map<String, Object> configProperties) {\\n            Map<String, String> props = new HashMap<>();\\n            checkNull(configProperties);\\n            props.put(JDBC_URL, (String) configProperties.getOrDefault(JDBC_URL, \"\"));\\n            props.put(\"dataSource.user\", (String) configProperties.get(USERNAME));\\n            props.put(\"dataSource.password\", (String) configProperties.get(PASSWORD));\\n            props.put(\"dataSource.connectionTimeout\", configProperties.getOrDefault(\"connectionTimeOut\", 30000).toString());\\n            props.put(\"dataSource.idleTimeout\", configProperties.getOrDefault(\"idleTimeout\", 600000).toString());\\n            props.put(\"dataSource.maxLifetime\", configProperties.getOrDefault(\"maxLifetime\", 1800000).toString());\\n            props.put(\"dataSource.minimumIdle\", configProperties.getOrDefault(\"minimumIdle\", 5).toString());\\n            props.put(\"dataSource.maximumPoolSize\", configProperties.getOrDefault(\"maximumPoolSize\", 20).toString());\\n            if (Boolean.TRUE.equals(configProperties.getOrDefault(\"cachePrepStmts\", Boolean.FALSE))) {\\n                props.put(\"dataSource.prepStmtCacheSize\", configProperties.getOrDefault(\"prepStmtCacheSize\", 25).toString());\\n                props.put(\"dataSource.prepStmtCacheSqlLimit\", configProperties.getOrDefault(\"prepStmtCacheSqlLimit\", 256).toString());\\n            \\n            }\\n            Properties properties = new Properties();\\n            properties.putAll(props);\\n            log.info(\"Connection Pool properties are {}\",properties);\\n            HikariConfig config = new HikariConfig(properties);\\n    \\t\\tlog.info(\"Jdbc url is {}\",config.getJdbcUrl());\\n            HikariDataSource dataSource = new HikariDataSource(config);\\n            log.debug(dataSource.getJdbcUrl());\\n            return new NamedParameterJdbcTemplate(dataSource);\\n        }\\n    \\n        @Override\\n        public String getDBTye() {\\n            return \"SQL\";\\n        }\\n    \\n        @Override\\n        public NamedParameterJdbcTemplate getConnection(NamedParameterJdbcTemplate jdbcTemplate) {\\n            return jdbcTemplate;\\n        }\\n    \\n        @Override\\n        public String validate(NamedParameterJdbcTemplate jdbcTemplate, Map<String, Object> properties) {\\n            try {\\n                jdbcTemplate.execute(properties.get(\"testQuery\").toString(), new PreparedStatementCallback<Object>() {\\n                    public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {\\n                        return null;\\n                    }\\n                });\\n                log.info(\"SQL connection is validated\");\\n                return \"Successfully Connected\";\\n            } catch (Exception e) {\\n                log.error(\"Exception Occured during SQL connection {}\", e);\\n                throw e;\\n            }\\n        }\\n    \\n    }";

        /*
         * creates branch from the parent
         */
        GHRef createdBranchRef = gitUtil.createBranchRef(DEFAULT_BRANCH, branchName, ghRepository);
        log.info("Branch {} created", branchName);

        /*
         * A Git tree is a snapshot of your repository at a specific point in time. It represents the hierarchical structure of files and
         * directories.
         * 
         * Purpose of GHTreeBuilder is to Add files or directories
         */
        GHTreeBuilder ghTree = gitUtil.createTree(ghRepository);
        gitUtil.addFilesToTree(ghTree, path, content);

        /*
         * Updating a Base Tree (baseTree the SHA of tree you want to update with new data)
         */
        String newTreeSha = ghTree.baseTree(createdBranchRef.getObject().getSha()).create().getSha();

        /*
         * Commit Changes and make parent point to latest commit
         */
        GHCommit ghCommit = ghRepository.createCommit().message("added file").parent(createdBranchRef.getObject().getSha()).tree(newTreeSha)
            .create();
        createdBranchRef.updateTo(ghCommit.getSHA1());

        log.info("Commited Successfully {}", ghCommit.getSHA1());
    }

}
