package edu.pku.intellimerge.util;

import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.matchers.SimilarityMetrics;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;
import edu.pku.intellimerge.model.SemanticNode;
import edu.pku.intellimerge.model.mapping.EdgeLabel;
import edu.pku.intellimerge.model.mapping.NodeContext;
import edu.pku.intellimerge.model.node.CompositeNode;
import edu.pku.intellimerge.model.node.FieldDeclNode;
import edu.pku.intellimerge.model.node.TerminalNode;
import info.debatty.java.stringsimilarity.Cosine;
import org.eclipse.jdt.core.dom.ASTParser;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SimilarityAlg {

  /**
   * Compute the similarity between two terminal declarations, considering signature as well as
   * context
   *
   * @param n1
   * @param n2
   * @return
   */
  public static double terminal(TerminalNode n1, TerminalNode n2) {
    double similarity = 0.0;
    // naive average in all dimensions of context(incoming and outgoing edges)
    similarity += context(n1.context, n2.context);
    // naive string similarity of terminal signature
    similarity += string(n1.getQualifiedName(), n2.getQualifiedName());
    similarity += string(n1.getOriginalSignature(), n2.getOriginalSignature());
    if (n1 instanceof FieldDeclNode && n2 instanceof FieldDeclNode) {
      if (!((FieldDeclNode) n1).getFieldType().equals(((FieldDeclNode) n2).getFieldType())) {
        return 0D;
      } else {
        similarity += string(n1.getBody(), n2.getBody());
      }
    } else {
      similarity += bodyAST(n1.getBody(), n2.getBody());
    }
    similarity /= 4;
    return similarity;
  }

  /**
   * Compute the similarity of context edges of 2 nodes
   *
   * @param node1
   * @param node2
   * @return
   */
  public static double context2(SemanticNode node1, SemanticNode node2) {
    // compute the cosine similarity
    double inVectorSim =
        vector(node1.context.getIncomingVector(), node2.context.getIncomingVector());
    double outVectorSim =
        vector(node1.context.getOutgoingVector(), node2.context.getOutgoingVector());
    return (inVectorSim + outVectorSim) / 2; // average for now
  }

  /**
   * Compute the cosine similarity of two vectors
   *
   * @param vector1
   * @param vector2
   * @return
   */
  private static double vector(Map<Integer, Integer> vector1, Map<Integer, Integer> vector2) {
    double dotProduct = 0.0;
    double norm1 = 0.0;
    double norm2 = 0.0;

    for (Integer index = 0; index < EdgeLabel.values().length; ++index) {
      Integer a = vector1.get(index);
      Integer b = vector2.get(index);
      dotProduct += a * b;
      norm1 += Math.pow(a, 2);
      norm2 += Math.pow(b, 2);
    }
    norm1 = (Math.sqrt(norm1));
    norm2 = (Math.sqrt(norm2));

    double product = norm1 * norm2;
    return product == 0.0 ? 0.0 : dotProduct / product;
  }

  /**
   * Compute the similarity between two terminal declarations, considering signature as well as
   * context
   *
   * @param n1
   * @param n2
   * @return
   */
  public static double composite(CompositeNode n1, CompositeNode n2) {
    double similarity = 0.0;
    // naive average in all dimensions of context(incoming and outgoing edges)
    similarity += context(n1.context, n2.context);
    // navie string similarity of terminal signature
    similarity += string(n1.getQualifiedName(), n2.getQualifiedName());
    similarity /= 2;
    return similarity;
  }

  /**
   * Signature textual similarity, but terminal name and parameter types should be the most
   * important
   *
   * @param s1
   * @param s2
   * @return
   */
  public static double string(String s1, String s2) {
    Cosine cosine = new Cosine();
    return cosine.similarity(s1, s2);
  }

  /**
   * Compute the similarity of edges around the vertex
   *
   * @return
   */
  public static double context(NodeContext context1, NodeContext context2) {
    Set<String> targetQNames1 =
        context1.getIncomingEdges().stream()
            .map(edge -> edge.getSource().getQualifiedName())
            .collect(Collectors.toSet());
    Set<String> targetQNames2 =
        context2.getIncomingEdges().stream()
            .map(edge -> edge.getSource().getQualifiedName())
            .collect(Collectors.toSet());

    double inSimi = jaccard(targetQNames1, targetQNames2);

    targetQNames1 =
        context1.getOutgoingEdges().stream()
            .map(edge -> edge.getTarget().getQualifiedName())
            .collect(Collectors.toSet());
    targetQNames2 =
        context2.getOutgoingEdges().stream()
            .map(edge -> edge.getTarget().getQualifiedName())
            .collect(Collectors.toSet());

    double outSimi = jaccard(targetQNames1, targetQNames2);
    return (inSimi + outSimi) / 2;
  }

  /**
   * Compute terminal body subtree similarity based on gumtree
   *
   * @param body1
   * @param body2
   * @return
   */
  public static double bodyAST(String body1, String body2) {
    double similarity = 0D;
    try {
      JdtTreeGenerator generator = new JdtTreeGenerator();
      generator.setKind(ASTParser.K_STATEMENTS);
      TreeContext baseContext = generator.generateFrom().string(body1);
      TreeContext othersContext = generator.generateFrom().string(body2);
      ITree baseRoot = baseContext.getRoot();
      ITree othersRoot = othersContext.getRoot();
      Matcher matcher = Matchers.getInstance().getMatcher();
      MappingStore mappings = matcher.match(baseRoot, othersRoot);
      similarity = SimilarityMetrics.chawatheSimilarity(baseRoot, othersRoot, mappings);
      if (Double.isNaN(similarity)) {
        similarity = 0D;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return similarity;
  }
  /**
   * Jaccard = Intersection/Union [0,1]
   *
   * @param s1
   * @param s2
   * @return
   */
  private static double jaccard(Set s1, Set s2) {
    Set<String> union = new LinkedHashSet<>();
    union.addAll(s1);
    union.addAll(s2);
    Set<String> intersection = new LinkedHashSet<>();
    intersection.addAll(s1);
    intersection.retainAll(s2);
    if (union.size() <= 0) {
      return 0D;
    } else {
      return (double) intersection.size() / union.size();
    }
  }

  /**
   * Compute field similarity according to field type, name and initializer
   *
   * @param f1
   * @param f2
   * @return
   */
  public static double field(FieldDeclNode f1, FieldDeclNode f2) {
    double similarity = 0.0;
    String fieldAsString1 = f1.getFieldType() + f1.getFieldName() + f1.getBody();
    String fieldAsString2 = f2.getFieldType() + f2.getFieldName() + f2.getBody();
    similarity = string(fieldAsString1, fieldAsString2);
    return similarity;
  }
}
