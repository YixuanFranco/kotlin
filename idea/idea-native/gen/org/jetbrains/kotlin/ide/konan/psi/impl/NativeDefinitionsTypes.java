// This is a generated file. Not intended for manual editing.
package org.jetbrains.kotlin.ide.konan.psi.impl;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.kotlin.ide.konan.psi.NativeDefinitionsElementType;
import org.jetbrains.kotlin.ide.konan.psi.NativeDefinitionsTokenType;

public interface NativeDefinitionsTypes {

  IElementType FIRST_HALF = new NativeDefinitionsElementType("FIRST_HALF");
  IElementType SECOND_HALF = new NativeDefinitionsElementType("SECOND_HALF");

  IElementType HOST_TOKEN = new NativeDefinitionsTokenType("HOST_TOKEN");
  IElementType DELIM_TOKEN = new NativeDefinitionsTokenType("DELIM_TOKEN");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == FIRST_HALF) {
        return new NativeDefinitionsFirstHalfImpl(node);
      }
      else if (type == SECOND_HALF) {
        return new NativeDefinitionsSecondHalfImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
