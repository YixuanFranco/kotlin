// This is a generated file. Not intended for manual editing.
package org.jetbrains.kotlin.ide.konan;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static org.jetbrains.kotlin.ide.konan.psi.impl.NativeDefinitionsTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;


@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class NativeDefinitionsParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    ASTNode tree = b.getTreeBuilt();
    return tree;
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == FIRST_HALF) {
      r = FIRST_HALF(b, 0);
    }
    else if (t == SECOND_HALF) {
      r = SECOND_HALF(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return ROOT(b, l + 1);
  }

  /* ********************************************************** */
  // A_LINE*
  public static boolean FIRST_HALF(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FIRST_HALF")) return false;
    Marker m = enter_section_(b, l, _NONE_, FIRST_HALF, "<first half>");
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, HOST_TOKEN)) break;
      if (!empty_element_parsed_guard_(b, "FIRST_HALF", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // FIRST_HALF (DELIM SECOND_HALF)?
  static boolean ROOT(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ROOT")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = FIRST_HALF(b, l + 1);
    r = r && ROOT_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DELIM SECOND_HALF)?
  private static boolean ROOT_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ROOT_1")) return false;
    ROOT_1_0(b, l + 1);
    return true;
  }

  // DELIM SECOND_HALF
  private static boolean ROOT_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ROOT_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DELIM_TOKEN);
    r = r && SECOND_HALF(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // A_LINE*
  public static boolean SECOND_HALF(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SECOND_HALF")) return false;
    Marker m = enter_section_(b, l, _NONE_, SECOND_HALF, "<second half>");
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, HOST_TOKEN)) break;
      if (!empty_element_parsed_guard_(b, "SECOND_HALF", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

}
