package com.erikmafo.btviewer.sql.functions;

import com.erikmafo.btviewer.sql.SqlToken;
import com.erikmafo.btviewer.sql.Value;
import com.erikmafo.btviewer.sql.ValueType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ValueFunctionExpressionParser extends FunctionExpressionParser {

    private final List<Value> args = new ArrayList<>();

    public static Value parse(List<SqlToken> tokens) {
        var expression = new ValueFunctionExpressionParser();
        expression.read(tokens);
        return expression.parse();
    }

    public Value parse() {
        switch (getFunction()) {
            case REVERSE: return reverse(args);
            case CONCAT: return concat(args);
            default: throw new IllegalStateException(
                    String.format("The %s(...) expression could not be evaluated", getFunction().value()));
        }
    }

    @Override
    protected void onInputToken(SqlToken inputToken) {
        args.add(Value.from(inputToken));
    }

    @NotNull
    private Value concat(@NotNull List<Value> args) {
        var stringValue = args.stream().map(Value::asString).collect(Collectors.joining(""));
        return new Value(stringValue, ValueType.STRING);
    }

    @NotNull
    @Contract("_ -> new")
    private Value reverse(@NotNull List<Value> args) {
        if (args.size() != 1) {
            throw new IllegalArgumentException(String.format("%s(...) takes exactly one argument", Function.REVERSE));
        }
        var arg = args.get(0);
        if (arg.getValueType() != ValueType.STRING) {
            throw new IllegalArgumentException(String.format("%s(...) takes only string as argument", Function.REVERSE));
        }

        var reversed = new StringBuilder(arg.asString()).reverse().toString();
        return new Value(reversed, ValueType.STRING);
    }
}
