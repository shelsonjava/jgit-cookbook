package org.dstadler.jgit.unfinished;

/*
   Copyright 2013, 2014 Dominik Stadler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;



/**
 * Simple snippet which shows how to initialize a new repository
 * 
 * @author dominik.stadler at gmx.at
 */
public class TestSubmodules {

    public static void main(String[] args) throws IOException, GitAPIException {
        File mainRepoDir = createRepository();
        
        Repository mainRepo = openMainRepo(mainRepoDir);
        
        addSubmodule(mainRepo);
        
        FileRepositoryBuilder builder = new FileRepositoryBuilder();

        Repository subRepo = builder.setGitDir(new File("testrepo/.git"))
          .readEnvironment() // scan environment GIT_* variables
          .findGitDir() // scan up the file system tree
          .build();

        if(subRepo.isBare()) {
            throw new IllegalStateException("Repository at " + subRepo.getDirectory() + " should not be bare");
        }

        System.out.println("All done!");
    }

    private static void addSubmodule(Repository mainRepo) throws GitAPIException {
        System.out.println("Adding submodule");
        Repository subRepoInit = new Git(mainRepo).submoduleAdd().
                setURI("https://github.com/github/testrepo.git").
                setPath("testrepo").
                call();
        if(subRepoInit.isBare()) {
            throw new IllegalStateException("Repository at " + subRepoInit.getDirectory() + " should not be bare");
        }
    }

    private static Repository openMainRepo(File mainRepoDir) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();

        Repository mainRepo = builder.setGitDir(new File(mainRepoDir.getAbsolutePath(), ".git"))
          .readEnvironment() // scan environment GIT_* variables
          .findGitDir() // scan up the file system tree
          .build();

        if(mainRepo.isBare()) {
            throw new IllegalStateException("Repository at " + mainRepoDir + " should not be bare");
        }
        return mainRepo;
    }

    private static File createRepository() throws IOException, GitAPIException {
        File dir = File.createTempFile("gitinit", ".test");
        dir.delete();

        Git.init()
                .setDirectory(dir)
                .call();

        Repository repository = FileRepositoryBuilder.create(new File(dir.getAbsolutePath(), ".git"));

        System.out.println("Created a new repository at " + repository.getDirectory());

        repository.close();
        
        return dir;
    }
}
