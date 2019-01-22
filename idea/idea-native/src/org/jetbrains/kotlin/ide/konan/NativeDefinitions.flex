package org.jetbrains.kotlin.ide.konan;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import org.jetbrains.kotlin.ide.konan.psi.impl.NativeDefinitionsTypes;

%%

%class NativeDefinitionsLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

WHITE_SPACE=[\ \v\n\t\f]
CHAR=[^\r\n-]

NON_DELIM=({CHAR}+(--?)?)+
PART=NON_DELIM

%state AFTER_TOKEN

%%

{WHITE_SPACE}+ { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<YYINITIAL> {
  --- { yybegin(AFTER_TOKEN); return NativeDefinitionsTypes.DELIM; }
  ({CHAR}+(--?)?)+ { yybegin(AFTER_TOKEN); return NativeDefinitionsTypes.A_LINE; }
}
