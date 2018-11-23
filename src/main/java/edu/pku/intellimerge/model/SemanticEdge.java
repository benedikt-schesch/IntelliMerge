package edu.pku.intellimerge.model;

public class SemanticEdge {
  private Integer edgeID;
  private Enum edgeType;
  private String label;
  private SemanticNode source;
  private SemanticNode target;

  public SemanticEdge(Integer edgeID, Enum edgeType, SemanticNode source, SemanticNode target) {
    this.edgeID = edgeID;
    this.edgeType = edgeType;
    this.source = source;
    this.target = target;
  }

  public Enum getEdgeType() {
    return edgeType;
  }
}
