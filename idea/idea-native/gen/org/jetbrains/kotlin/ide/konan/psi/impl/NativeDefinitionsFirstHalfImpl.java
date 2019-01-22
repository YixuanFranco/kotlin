/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ide.konan.psi.impl;

import java.util.List;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafElement;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import static org.jetbrains.kotlin.ide.konan.psi.impl.NativeDefinitionsTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.jetbrains.kotlin.ide.konan.PropertiesEscaper;
import org.jetbrains.kotlin.ide.konan.psi.*;

public class NativeDefinitionsFirstHalfImpl extends ASTWrapperPsiElement implements NativeDefinitionsFirstHalf, PsiLanguageInjectionHost {

  public NativeDefinitionsFirstHalfImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull NativeDefinitionsVisitor visitor) {
    visitor.visitFirstHalf(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof NativeDefinitionsVisitor) accept((NativeDefinitionsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public boolean isValidHost() {
    return true;
  }

  @Override
  public PsiLanguageInjectionHost updateText(@NotNull String text) {
    //return ElementManipulators.handleContentChange(this, text);
    return this;
  }

  NativeDefinitionsFirstHalfImpl handleContentChange(@NotNull TextRange changeRange, String newContent) {
    System.out.println("Get range: " + changeRange.toString() + " content: " + newContent);
    return this;
  }

  @NotNull
  @Override
  public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
    return new PropertiesEscaper(this);
  }
}
