package edu.pku.intellimerge.merge;

import edu.pku.intellimerge.model.ConflictBlock;
import edu.pku.intellimerge.util.FilesManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestConflictBlock {
  @Test
  public void testExtractConflictBlocks() {
    String path =
        FilesManager.getProjectRootDir()
            + "/src/test/resources/Extract/ExtractMethod/gitMerged/SourceRoot.java";

    String code = FilesManager.readFileContent(new File(path));
    List<ConflictBlock> conflictBlocks = FilesManager.extractConflictBlocks(code);
    assertThat(conflictBlocks.size()).isEqualTo(3);
    assertThat(conflictBlocks.get(0).getBase())
        .contains("import static com.github.javaparser.utils.CodeGenerationUtils.*;");
    assertThat(conflictBlocks.get(1).getBase().length()).isZero();
    assertThat(conflictBlocks.get(2).getStartLine()).isEqualTo(210);
  }
}