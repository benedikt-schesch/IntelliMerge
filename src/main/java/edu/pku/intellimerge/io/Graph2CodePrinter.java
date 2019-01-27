package edu.pku.intellimerge.io;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import edu.pku.intellimerge.model.SemanticNode;
import edu.pku.intellimerge.model.constant.NodeType;
import edu.pku.intellimerge.model.constant.Side;
import edu.pku.intellimerge.model.node.CompilationUnitNode;
import edu.pku.intellimerge.model.node.NonTerminalNode;
import edu.pku.intellimerge.model.node.TerminalNode;
import edu.pku.intellimerge.util.FilesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Graph2CodePrinter {

  private static final Logger logger = LoggerFactory.getLogger(Graph2CodePrinter.class);

  public static void printCU(SemanticNode node, CompilationUnitNode cu, String resultFolder) {
    if (node != null && cu != null) {
      String resultFilePath = resultFolder + File.separator + cu.getRelativePath();
      // merged package imports
      StringBuilder builder = new StringBuilder();
      builder.append(cu.getPackageStatement());
      cu.getImportStatements().forEach(importStatement -> builder.append(importStatement));
      // merged content, field-constructor-method, and reformat in google-java-format
      builder.append(printNode(node));
      String reformattedCode = reformatCode(builder.toString());
      FilesManager.writeContent(resultFilePath, reformattedCode);
      logger.info("Merge result saved in: {}", resultFilePath);
    }
  }

  /**
   * Reformat the printed code in google-java-format
   *
   * @param code
   * @return
   */
  private static String reformatCode(String code) {
    String reformattedCode = "";
    try {
      // comment all conflict symbols because it causes exceptions for the formatter
      reformattedCode =
          code.replaceAll(
                  "<<<<<<< " + Side.OURS.asString(), "/* <<<<<<< " + Side.OURS.asString() + " */")
              .replaceAll("=======", "/* ======= */")
              .replaceAll(
                  ">>>>>>> " + Side.THEIRS.asString(),
                  "/* >>>>>>> " + Side.THEIRS.asString() + " */");

      reformattedCode = new Formatter().formatSource(reformattedCode);

      reformattedCode =
          reformattedCode
              .replaceAll(
                  "/\\* <<<<<<< " + Side.OURS.asString() + " \\*/",
                  "<<<<<<< " + Side.OURS.asString())
              .replaceAll("/\\* ======= \\*/", "=======")
              .replaceAll(
                  "/\\* >>>>>>> " + Side.THEIRS.asString() + " \\*/",
                  ">>>>>>> " + Side.THEIRS.asString());
    } catch (FormatterException e) {
      e.printStackTrace();
    }
    return reformattedCode;
  }

  /**
   * Print the node content and children into code string
   *
   * @param node
   * @return
   */
  private static String printNode(SemanticNode node) {
    StringBuilder builder = new StringBuilder();
    if (node instanceof TerminalNode) {
      builder.append(node.getOriginalSignature());
      builder.append(((TerminalNode) node).getBody()).append("\n");
    } else if (node instanceof NonTerminalNode) {
      if (!node.getNodeType().equals(NodeType.CU)) {
        builder.append(node.getOriginalSignature());
        builder.append("{\n");
      }
      for (SemanticNode child : node.getChildren()) {
        builder.append(printNode(child));
      }
      if (!node.getNodeType().equals(NodeType.CU)) {
        builder.append("\n}\n");
      }
    }
    return builder.toString();
  }
}
