package com.programm.projects.scripty.app.files;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ReposConfigFile {

    static final String KEY_REPO_URLS = "repos.repo";

    private final List<String> repoUrls;
}
