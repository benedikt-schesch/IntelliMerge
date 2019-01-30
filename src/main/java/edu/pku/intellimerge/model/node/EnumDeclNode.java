package edu.pku.intellimerge.model.node;

import com.github.javaparser.Range;
import edu.pku.intellimerge.model.constant.EdgeType;
import edu.pku.intellimerge.model.constant.NodeType;

import java.util.ArrayList;
import java.util.Optional;

public class EnumDeclNode extends TerminalNode {

  public EnumDeclNode(
      Integer nodeID,
      Boolean needToMerge,
      NodeType nodeType,
      String displayName,
      String qualifiedName,
      String body,
      Optional<Range> range) {
    super(nodeID, needToMerge, nodeType, displayName, qualifiedName, body, range);
    this.incomingEdges.put(EdgeType.DEFINE_ENUM, new ArrayList<>());
  }

  public EnumDeclNode(
      Integer nodeID,
      Boolean needToMerge,
      NodeType nodeType,
      String displayName,
      String qualifiedName,
      String originalSignature,
      String body,
      Optional<Range> range) {
    super(
        nodeID, needToMerge, nodeType, displayName, qualifiedName, originalSignature, body, range);
    this.incomingEdges.put(EdgeType.DEFINE_ENUM, new ArrayList<>());
  }
}