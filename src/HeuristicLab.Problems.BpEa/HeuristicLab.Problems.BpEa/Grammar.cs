using HeuristicLab.Common;
using HeuristicLab.Core;
using HeuristicLab.Encodings.SymbolicExpressionTreeEncoding;
using HeuristicLab.Persistence.Default.CompositeSerializers.Storable;
using HeuristicLab.Problems.BpEa.Symbols;

namespace HeuristicLab.Problems.BpEa
{
    [StorableClass]
    [Item("BP EA Grammar", "The grammar for the BP EA GP problem.")]
    public class Grammar : SymbolicExpressionGrammar
    {
        private const string ExpressionsName = "Expressions";
        private const string ControlStatementsName = "Control Statements";
        private const string StatementsName = "Statements";
        private const string RelationalOperatorsName = "Relational Operators";
        private const string LogicalOperators = "Logical Operators";
        private const string NumericalOperatorsName = "Numerical Operators";

        [StorableConstructor]
        protected Grammar(bool deserializing) : base(deserializing) { }
        protected Grammar(Grammar original, Cloner cloner) : base(original, cloner) { }

        public Grammar()
          : base("BP EA Grammar", "The grammar for the BP EA GP problem.")
        {
            Initialize();
        }

        public override IDeepCloneable Clone(Cloner cloner)
        {
            return new Grammar(this, cloner);
        }

        private void Initialize()
        {
            #region Symbols
            var ifThenElseStat = new SimpleSymbol("IfThenElseStat", "An if statement.", 2, 3);
            var feature = new SimpleSymbol("Feature", "A feature.", 0, 0);

            var boolExpr = new SimpleSymbol("BooleanExpression", "A Boolean expression.", 1, 1);
            var numericalExpr = new SimpleSymbol("NumericalExpression", "A numerical expression.", 1, 1);

            var equal = new SimpleSymbol("Equal", "Equal comparator.", 2, 2);
            var lessThan = new SimpleSymbol("LessThan", "LessThan comparator.", 2, 2);
            var lessThanOrEqual = new SimpleSymbol("LessThanOrEqual", "LessThanOrEqual comparator.", 2, 2);
            var greaterThan = new SimpleSymbol("GreaterThan", "GreaterThan comparator.", 2, 2);
            var greaterThanOrEqual = new SimpleSymbol("GreaterThanOrEqual", "GreaterThanOrEqual comparator.", 2, 2);

            var conjunction = new SimpleSymbol("ConditionalAnd", "Conjunction comparator.", 2, byte.MaxValue);
            var disjunction = new SimpleSymbol("ConditionalOr", "Disjunction comparator.", 2, byte.MaxValue);
            var negation = new SimpleSymbol("Negation", "A negation.", 1, 1);

            var multiplication = new SimpleSymbol("Multiplication", "Multiplication operator.", 2, byte.MaxValue);

            var number = new Number();
            var logicalVal = new BooleanValue();

            #endregion

            #region Symbol Collections
            var numericalOperators = new GroupSymbol(NumericalOperatorsName, new ISymbol[] { multiplication });
            var numericalStatements = new GroupSymbol(StatementsName, new ISymbol[] { ifThenElseStat, number,  numericalOperators,feature});
            var controlStatements = new GroupSymbol(ControlStatementsName, new ISymbol[] { ifThenElseStat });
            var expressions = new GroupSymbol(ExpressionsName, new ISymbol[] { boolExpr, numericalExpr });
            var relationalOperators = new GroupSymbol(RelationalOperatorsName, new ISymbol[] { equal, lessThan, lessThanOrEqual, greaterThan, greaterThanOrEqual });
            var logicalOperators = new GroupSymbol(LogicalOperators, new ISymbol[] { conjunction, disjunction, negation });
            
            #endregion

            #region Adding Symbols
            AddSymbol(feature);
            AddSymbol(number);
            AddSymbol(logicalVal);
            AddSymbol(expressions);
            AddSymbol(controlStatements);
            AddSymbol(relationalOperators);
            AddSymbol(logicalOperators);
            AddSymbol(numericalOperators);
            #endregion

            #region Grammar Definition
            // StartSymbol
            AddAllowedChildSymbol(StartSymbol, numericalExpr);

            // IfStat
            AddAllowedChildSymbol(ifThenElseStat, boolExpr, 0);
            AddAllowedChildSymbol(ifThenElseStat, numericalExpr, 1);
            AddAllowedChildSymbol(ifThenElseStat, numericalExpr, 2);

            // Numerical Expressions
            AddAllowedChildSymbol(numericalExpr, number);
            AddAllowedChildSymbol(numericalExpr, feature);
            AddAllowedChildSymbol(numericalExpr, numericalOperators);
            AddAllowedChildSymbol(numericalExpr, ifThenElseStat);
            AddAllowedChildSymbol(numericalOperators, number);
            AddAllowedChildSymbol(numericalOperators, numericalOperators);

            // Logical Expressions
            AddAllowedChildSymbol(boolExpr, logicalVal);
            AddAllowedChildSymbol(boolExpr, logicalOperators);
            AddAllowedChildSymbol(logicalOperators, logicalVal);
            AddAllowedChildSymbol(logicalOperators, logicalOperators);
            AddAllowedChildSymbol(logicalOperators, relationalOperators);
            AddAllowedChildSymbol(relationalOperators, numericalExpr);
            #endregion
        }
    }
}