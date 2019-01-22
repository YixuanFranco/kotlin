/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ide.konan.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.ASTNode
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.ide.konan.*
import javax.swing.Icon

class NativeDefinitionsElementType(debugName: String) : IElementType(debugName, NativeDefinitionsLanguage.INSTANCE)

class NativeDefinitionsTokenType(debugName: String) : IElementType(debugName, NativeDefinitionsLanguage.INSTANCE) {
    override fun toString(): String = "NativeDefinitionsTokenType." + super.toString()
}

class NativeDefinitionsFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, NativeDefinitionsLanguage.INSTANCE) {

    override fun getFileType(): FileType = NativeDefinitionsFileType.INSTANCE

    override fun toString(): String = NATIVE_DEFINITIONS_DESCRIPTION

    override fun getIcon(flags: Int): Icon? = super.getIcon(flags)
}

class NativeDefinitionsFirstHalf(node: ASTNode) : ASTWrapperPsiElement(node), PsiLanguageInjectionHost {

    fun accept(visitor: NativeDefinitionsVisitor) = visitor.visitFirstHalf(this)

    override fun accept(visitor: PsiElementVisitor) {
        if (visitor is NativeDefinitionsVisitor)
            accept(visitor)
        else
            super.accept(visitor)
    }

    override fun isValidHost(): Boolean = true

    override fun updateText(text: String): PsiLanguageInjectionHost {
        return ElementManipulators.handleContentChange(this, text)
    }

    internal fun handleContentChange(changeRange: TextRange, newContent: String): NativeDefinitionsFirstHalf {
        println("Get range: " + changeRange.toString() + " content: " + newContent)
        return this
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {
        return PropertiesEscaper(this)
    }
}

class NativeDefinitionsSecondHalf(node: ASTNode) : ASTWrapperPsiElement(node), PsiLanguageInjectionHost {
    override fun updateText(text: String): PsiLanguageInjectionHost {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun accept(visitor: NativeDefinitionsVisitor) = visitor.visitSecondHalf(this)

    override fun accept(visitor: PsiElementVisitor) {
        if (visitor is NativeDefinitionsVisitor)
            accept(visitor)
        else
            super.accept(visitor)
    }

    override fun isValidHost(): Boolean = true
}

val FIRST_HALF: IElementType = NativeDefinitionsElementType("FIRST_HALF")
val SECOND_HALF: IElementType = NativeDefinitionsElementType("SECOND_HALF")

val HOST_TOKEN: IElementType = NativeDefinitionsTokenType("HOST_TOKEN")
val DELIM_TOKEN: IElementType = NativeDefinitionsTokenType("DELIM_TOKEN")

fun createNativeDefinionsElement(node: ASTNode): PsiElement {
    val type = node.elementType
    if (type === FIRST_HALF) {
        return NativeDefinitionsFirstHalf(node)
    } else if (type === SECOND_HALF) {
        return NativeDefinitionsSecondHalf(node)
    }
    throw AssertionError("Unknown element type: $type")
}
