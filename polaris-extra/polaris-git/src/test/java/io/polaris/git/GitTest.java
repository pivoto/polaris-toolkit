package io.polaris.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Qt
 * @since 1.8
 */
@TestClassOrder(ClassOrderer.DisplayName.class)
public class GitTest {
	@Test
	void test01() throws IOException, GitAPIException {
		String dir = "d:/tmp/test/test";
		Repository repo =  FileRepositoryBuilder.create(new File(dir + "/.git"));
		repo =	new FileRepositoryBuilder()
			.setGitDir(new File(dir + "/.git"))
			.setWorkTree(new File(dir))
			.build()
		;
		System.out.println(repo.getRemoteNames());

		FileOutputStream fos = new FileOutputStream(dir+"/2.txt");
		fos.write("test".getBytes());
		fos.flush();
		fos.close();

		// 获取引用
		Ref master = repo.getRefDatabase().findRef("master");
		System.out.println(master);
		// 获取该引用所指向的对象
		ObjectId masterTip  = master.getObjectId();

		// Rev-parse
		ObjectId obj = repo.resolve("HEAD^{tree}");
		System.out.println(obj);

		// 装载对象原始内容
		ObjectLoader loader = repo.open(masterTip);
		loader.copyTo(System.out);

		// 创建分支
		RefUpdate createBranch1 = repo.updateRef("refs/heads/branch1");
		createBranch1.setNewObjectId(masterTip);
		createBranch1.update();

		// 删除分支
		RefUpdate deleteBranch1 = repo.updateRef("refs/heads/branch1");
		deleteBranch1.setForceUpdate(true);
		deleteBranch1.delete();

		// 配置
		Config cfg = repo.getConfig();
		String name = cfg.getString("user", null, "name");


		Git git = new Git(repo);
		System.out.println(git.branchList().call());
		System.out.println(git.status().call().getChanged());

		git.add().addFilepattern(".").call();
		git.commit().setAllowEmpty(true).setAll(true).setMessage("test").call();

		git.push().call();

		for (RevCommit revCommit : git.log().call()) {
			System.out.println(revCommit.getTree());
			System.out.println(revCommit.getFullMessage());
			System.out.println();
		}

		git.close();

	}


}
