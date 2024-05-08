package io.polaris.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.FileSetCheck;
import com.puppycrawl.tools.checkstyle.api.FileText;

import java.io.File;

/**
 * @author Qt
 * @version Jun 17, 2021
 */
public class Test2Check extends AbstractFileSetCheck {

	@Override
	protected void processFiltered(File file, FileText fileText) throws CheckstyleException {

	}
}
