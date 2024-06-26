package edu.pku.intellimerge.model.mapping;

import edu.pku.intellimerge.model.SemanticEdge;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class NodeContext {
  private Set<SemanticEdge> incomingEdges;
  private Set<SemanticEdge> outgoingEdges;

  // use map to save vectors for random access
  private Map<Integer, Integer> incomingVector = new LinkedHashMap<>();
  private Map<Integer, Integer> outgoingVector = new LinkedHashMap<>();

  public NodeContext(
      Set<SemanticEdge> incomingEdges,
      Set<SemanticEdge> outgoingEdges,
      Map<Integer, Integer> incomingVector,
      Map<Integer, Integer> outgoingVector) {
    this.incomingEdges = incomingEdges;
    this.outgoingEdges = outgoingEdges;
    this.incomingVector = incomingVector;
    this.outgoingVector = outgoingVector;
  }

  public NodeContext(Set<SemanticEdge> incomingEdges, Set<SemanticEdge> outgoingEdges) {
    this.incomingEdges = incomingEdges;
    this.outgoingEdges = outgoingEdges;
    for (Integer i = 0; i < EdgeLabel.values().length; i++) {
      this.incomingVector.put(i, 0);
      this.outgoingVector.put(i, 0);
    }
  }

  public Set<SemanticEdge> getIncomingEdges() {
    return incomingEdges;
  }

  public Set<SemanticEdge> getOutgoingEdges() {
    return outgoingEdges;
  }

  public void setOutgoingEdges(Set edges) {
    this.outgoingEdges = edges;
  }

  public Map<Integer, Integer> getIncomingVector() {
    return incomingVector;
  }

  public Map<Integer, Integer> getOutgoingVector() {
    return outgoingVector;
  }

  public void putIncomingVector(Integer key, Integer value) {
    this.incomingVector.put(key, value);
  }

  public void putOutgoingVector(Integer key, Integer value) {
    this.outgoingVector.put(key, value);
  }

  public NodeContext join(NodeContext context) {
    Set<SemanticEdge> combinedIncomingEdges = new LinkedHashSet<>();
    Set<SemanticEdge> combinedOutgoingEdges = new LinkedHashSet<>();
    combinedIncomingEdges.addAll(this.incomingEdges);
    combinedIncomingEdges.addAll(context.getIncomingEdges());
    combinedOutgoingEdges.addAll(this.outgoingEdges);
    combinedOutgoingEdges.addAll(context.getOutgoingEdges());

    Map<Integer, Integer> combinedInVec = new LinkedHashMap<>(this.incomingVector);
    Map<Integer, Integer> combinedOutVec = new LinkedHashMap<>(this.outgoingVector);
    context
        .getIncomingVector()
        .forEach((key, value) -> combinedInVec.merge(key, value, (v1, v2) -> (v1 + v2)));
    context
        .getOutgoingVector()
        .forEach((key, value) -> combinedOutVec.merge(key, value, (v1, v2) -> (v1 + v2)));

    return new NodeContext(
        combinedIncomingEdges, combinedOutgoingEdges, combinedInVec, combinedOutVec);
  }
}
