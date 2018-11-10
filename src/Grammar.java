/** Mapping rules number to rules
 *
 */
class Grammar {
    private static String[] rules = new String[] {
        /* 1 */"<Program> -> BEGINPROG [ProgName] [EndLine] <Variables> <Code> ENDPROG",
        /* 2 */"<Variables> -> VARIABLES <VarList> [EndLine]",
        /* 3 */"<Variables> -> ɛ",
        /* 4 */"<VarList> -> [VarName] <VarList_prim>",
        /* 5 */"<VarList_prim> -> , [VarName] <VarList_prim>",
        /* 6 */"<VarList_prim> -> ɛ",
        /* 7 */"<Code> -> <Instruction> [EndLine] <Code>",
        /* 8 */"<Code> -> ɛ",
        /* 9 */"<Instruction> -> <Assign>",
        /* 10 */"<Instruction> -> <If>",
        /* 11 */"<Instruction> -> <While>",
        /* 12 */"<Instruction> -> <For>",
        /* 13 */"<Instruction> -> <Print>",
        /* 14 */"<Instruction> -> <Read>",
        /* 15 */"<Assign> -> [VarName] := <ExprArith>",
        /* 16 */"<ExprArith> -> <Term> <ExprArith_prim>",
        /* 17 */"<ExprArith_prim> -> + <Term> <ExprArith_prim>",
        /* 18 */"<ExprArith_prim> -> - <Term> <ExprArith_prim>",
        /* 19 */"<ExprArith_prim> -> ɛ",
        /* 20 */"<Term> -> <Atom> <Term_prim>",
        /* 21 */"<Term_prim> -> * <Atom> <Term_prim>",
        /* 22 */"<Term_prim> -> / <Atom> <Term_prim>",
        /* 23 */"<Term_prim> -> ɛ",
        /* 24 */"<Atom> -> [Number]",
        /* 25 */"<Atom> -> [VarName]",
        /* 26 */"<Atom> -> ( <ExprArith> )",
        /* 27 */"<Atom> -> - <Atom>",
        /* 28 */"<If> -> IF ( <Cond> ) THEN [EndLine] <Code> <IfSeq>",
        /* 29 */"<IfSeq> -> ENDIF",
        /* 30 */"<IfSeq> -> ELSE [EndLine] <Code> ENDIF",
        /* 31 */"<Cond> -> <AndCond> <Cond_prim>",
        /* 32 */"<Cond_prim> -> OR <AndCond> <Cond_prim>",
        /* 33 */"<Cond_prim> -> ɛ",
        /* 34 */"<AndCond> -> <SimpleCond> <AndCond_prim>",
        /* 35 */"<AndCond_prim> -> AND <SimpleCond> <AndCond_prim>",
        /* 36 */"<AndCond_prim> -> ɛ",
        /* 37 */"<SimpleCond> -> <ExprArith> <Comp> <ExprArith>",
        /* 38 */"<SimpleCond> -> NOT <SimpleCond>",
        /* 39 */"<Comp> -> =",
        /* 40 */"<Comp> -> <=",
        /* 41 */"<Comp> -> <",
        /* 42 */"<Comp> -> >=",
        /* 43 */"<Comp> -> >",
        /* 44 */"<Comp> -> <>",
        /* 45 */"<While> -> WHILE ( <Cond> ) DO [EndLine] <Code> ENDWHILE",
        /* 46 */"<For> -> FOR [VarName] := <ExprArith> TO <ExprArith> DO [EndLine] <Code> ENDFOR",
        /* 47 */"<Print> -> PRINT ( <ExpList> )",
        /* 48 */"<Read> -> READ ( <VarList> )",
        /* 49 */"<ExpList> -> <ExprArith> <ExpList_prim>",
        /* 50 */"<ExpList_prim> -> , <ExprArith> <ExpList_prim>",
        /* 51 */"<ExpList_prim> -> ɛ"
    };

    /** Returns the rule corresponding to the given rule number
     * 
     */
    public static String rule(int s) {
        return rules[s - 1];
    }
}