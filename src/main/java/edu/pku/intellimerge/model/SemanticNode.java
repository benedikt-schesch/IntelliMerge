package edu.pku.intellimerge.model;

import edu.pku.intellimerge.model.constant.EdgeType;
import edu.pku.intellimerge.model.constant.NodeType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class SemanticNode {
  public Map<EdgeType, List<SemanticNode>> incomingEdges = new LinkedHashMap<>();
  public Map<EdgeType, List<SemanticNode>> outgoingEdges = new LinkedHashMap<>();
  // signature
  private Boolean needToMerge;

  private SemanticNode parent;
  private List<SemanticNode> children;

  private Integer nodeID;
  private NodeType nodeType;
  private String displayName;
  private String qualifiedName;
  // original signature in source code, here we generalize the definition of signature
  private String originalSignature;
  private String comment;

  public SemanticNode() {}

  public SemanticNode(
      Integer nodeID,
      Boolean needToMerge,
      NodeType nodeType,
      String displayName,
      String qualifiedName,
      String originalSignature,
      String comment) {
    this.nodeID = nodeID;
    this.needToMerge = needToMerge;
    this.nodeType = nodeType;
    this.displayName = displayName;
    this.qualifiedName = qualifiedName;
    this.originalSignature = originalSignature;
    this.comment = comment;
    this.children = new ArrayList<>();
  }

  public Integer getNodeID() {
    return nodeID;
  }

  public NodeType getNodeType() {
    return nodeType;
  }

  public Integer getLevel() {
    return nodeType.level;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getQualifiedName() {
    return qualifiedName;
  }

  public void setQualifiedName(String qualifiedName) {
    this.qualifiedName = qualifiedName;
  }

  public String getOriginalSignature() {
    return originalSignature;
  }

  public void setOriginalSignature(String originalSignature) {
    this.originalSignature = originalSignature;
  }

  /**
   * Get the unique fully qualified signature in this project, which should represent the MAIN
   * identification of this node
   */
  public String getSignature() {
    return getQualifiedName();
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Integer hashCodeSignature() {
    return getSignature().hashCode();
  }

  public Boolean getNeedToMerge() {
    return needToMerge;
  }

  /**
   * Clone the object without children and edges
   *
   * @return
   */
  public abstract SemanticNode shallowClone();

  /**
   * Clone the object with cloning children and edges
   *
   * @return
   */
  public abstract SemanticNode deepClone();

  /** A series of methods to operate the tree structure */
  public SemanticNode getParent() {
    return parent;
  }

  public void setParent(SemanticNode parent) {
    this.parent = parent;
  }

  public List<SemanticNode> getChildren() {
    return children;
  }

  public void appendChild(SemanticNode child) {
    if (this.children == null) {
      this.children = new ArrayList<>();
    }
    this.children.add(child);
    child.setParent(this);
  }

  public void insertChild(SemanticNode child, int position) {
    if (position >= 0 && position < children.size()) {
      children.add(position, child);
    } else if (position >= children.size()) {
      appendChild(child);
    }
  }

  public SemanticNode getChildAtPosition(int position) {
    if (position >= 0 && position < children.size()) {
      return children.get(position);
    } else {
      return null;
    }
  }

  public int getChildPosition(SemanticNode child) {
    return children.indexOf(child);
  }

  /**
   * Mainly for debugging
   *
   * @return
   */
  @Override
  public String toString() {
    return nodeType.toPrettyString() + "{" + originalSignature + "}";
  }

  /**
   * Mainly for visualization
   *
   * @return
   */
  public String toPrettyString() {
    return nodeType.asString() + "::" + displayName;
  }

  /**
   * Complete string representation
   *
   * @return
   */
  public String asString() {
    return "SemanticNode{"
        + "nodeType="
        + nodeType
        + ", qualifiedName='"
        + qualifiedName
        + '\''
        + ", originalSignature='"
        + originalSignature
        + '\''
        + '}';
  }

  /**
   * To compare if two nodes are equal
   *
   * @return
   */
  public int hashCode() {
    return asString().hashCode();
  }

  public boolean equals(Object o) {
    return (o instanceof SemanticNode) && (asString().equals(((SemanticNode) o).asString()));
  }
}
