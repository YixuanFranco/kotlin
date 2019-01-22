/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ide.konan

import com.intellij.lang.*
import com.intellij.lexer.Lexer
import com.intellij.lexer.LexerBase
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.fileTypes.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.tree.*
import org.jetbrains.kotlin.ide.konan.psi.*
import javax.swing.Icon
import org.jetbrains.kotlin.idea.KotlinIcons
import org.jetbrains.kotlin.konan.library.KDEFINITIONS_FILE_EXTENSION
import java.lang.StringBuilder


const val NATIVE_DEFINITIONS_NAME = "KND"
const val NATIVE_DEFINITIONS_DESCRIPTION = "Definitions file for Kotlin/Native C interop"

class NativeDefinitionsFileType : LanguageFileType(NativeDefinitionsLanguage.INSTANCE) {

    override fun getName(): String = NATIVE_DEFINITIONS_NAME

    override fun getDescription(): String = NATIVE_DEFINITIONS_DESCRIPTION

    override fun getDefaultExtension(): String = KDEFINITIONS_FILE_EXTENSION

    override fun getIcon(): Icon = KotlinIcons.NATIVE

    companion object {
        val INSTANCE = NativeDefinitionsFileType()
    }
}

class NativeDefinitionsLanguage private constructor() : Language(NATIVE_DEFINITIONS_NAME) {
    companion object {
        val INSTANCE = NativeDefinitionsLanguage()
    }
}

class NativeDefinitionsLexer : LexerBase() {
    private class Token(val begin: Int, val end: Int, val type: IElementType)

    private var myText: CharSequence? = null
    private var myTokens: Array<Token> = emptyArray()
    private var myCurrentTokenId: Int = 0

    private fun currentToken() = if (myCurrentTokenId < myTokens.size) myTokens[myCurrentTokenId] else null

    override fun advance() {
        myCurrentTokenId++
    }

    override fun getState(): Int = 0

    override fun getBufferEnd(): Int = myText?.length ?: 0

    override fun getBufferSequence(): CharSequence = myText ?: throw RuntimeException("Buffer is requested without initialization.")

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        myText = buffer
        myCurrentTokenId = 0

        val match = Regex("(\\A|\\R)---(\\R|\\z)").find(buffer)
        if (match != null) {
            myTokens = arrayOf(
                Token(0, match.range.first, HOST_TOKEN),
                Token(match.range.first, match.range.last + 1, DELIM_TOKEN),
                Token(match.range.last + 1, buffer.length, HOST_TOKEN)
            )
        } else {
            myTokens = arrayOf(Token(0, buffer.length, HOST_TOKEN))
        }
    }

    override fun getTokenStart(): Int = currentToken()?.begin ?: 0

    override fun getTokenEnd(): Int = currentToken()?.end ?: 0

    override fun getTokenType(): IElementType? = currentToken()?.type
}

class NativeDefinitionsParser : PsiParser, LightPsiParser {
    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        parseLight(root, builder)
        return builder.treeBuilt
    }

    private fun consumeElement(element: IElementType, builder: PsiBuilder) {
        val marker = builder.mark()
        val tt = builder.tokenText
        println("> $element -> <<$tt>>")
        builder.advanceLexer()
        marker.done(element)
    }

    override fun parseLight(root: IElementType, builder: PsiBuilder) {
        val rootNode = builder.mark()
        consumeElement(FIRST_HALF, builder)
        if (!builder.eof()) {
            builder.advanceLexer()
            consumeElement(SECOND_HALF, builder)
        }
        rootNode.done(root)
    }
}

class NativeDefinitionsParserDefinition : ParserDefinition {
    private val FILE = IFileElementType(NativeDefinitionsLanguage.INSTANCE)

    override fun getWhitespaceTokens(): TokenSet = TokenSet.WHITE_SPACE
    override fun getCommentTokens(): TokenSet = TokenSet.EMPTY
    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY
    override fun getFileNodeType(): IFileElementType = FILE

    override fun createLexer(project: Project): Lexer = NativeDefinitionsLexer()
    override fun createParser(project: Project): PsiParser = NativeDefinitionsParser()

    override fun createFile(viewProvider: FileViewProvider): PsiFile = NativeDefinitionsFile(viewProvider)
    override fun createElement(node: ASTNode): PsiElement = createNativeDefinionsElement(node)
}

class PropertiesEscaper(host: NativeDefinitionsFirstHalf) : LiteralTextEscaper<NativeDefinitionsFirstHalf>(host) {
    override fun isOneLine(): Boolean = false

    override fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int = offsetInDecoded

    override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
        outChars.append(myHost.text)
        return true
    }
}

class NativeDefinitionsLanguageInjector : LanguageInjector {
    val propertiesLanguage = Language.findLanguageByID("Properties")
    val objectiveCLanguage = Language.findLanguageByID("ObjectiveC")

    override fun getLanguagesToInject(host: PsiLanguageInjectionHost, registrat: InjectedLanguagePlaces) {
        if (!host.isValid) return

        if (host is NativeDefinitionsFirstHalf && propertiesLanguage != null) {
            registrat.addPlace(propertiesLanguage, host.getTextRange(), null, null)
        }

        if (host is NativeDefinitionsSecondHalf && objectiveCLanguage != null) {
            registrat.addPlace(objectiveCLanguage, host.getTextRange(), null, null)
        }
    }
}

class NativeDefinitionsSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> =
        when (tokenType) {
            FIRST_HALF, SECOND_HALF -> HOST_KEYS
            DELIM_TOKEN -> DELIM_KEYS
            else -> EMPTY_KEYS
        }

    override fun getHighlightingLexer(): Lexer = NativeDefinitionsLexer()

    companion object {
        private fun createKeys(externalName: String, key: TextAttributesKey): Array<TextAttributesKey> {
            return arrayOf(TextAttributesKey.createTextAttributesKey(externalName, key))
        }

        val DELIM_KEYS = createKeys("Delimiter", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val HOST_KEYS = createKeys("Language host", DefaultLanguageHighlighterColors.STRING)
        val EMPTY_KEYS = emptyArray<TextAttributesKey>()
    }
}

class NativeDefinitionsSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter =
        NativeDefinitionsSyntaxHighlighter()
}