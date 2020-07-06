package com.erikmafo.btviewer.sql;

public class SqlParser {

    public Query parse(String sql) {

        var tokenizer = new SqlTokenizer(sql);
        var queryBuilder = new QueryBuilder();
        var token = tokenizer.next();

        while (token != null) {
            queryBuilder.handleNextToken(token);
            token = tokenizer.next();
        }

        return queryBuilder.getQuery();
    }

    private static class QueryBuilder {

        private final Query query = new Query();
        private SqlParserStep step = SqlParserStep.QUERY_TYPE;

        private Field whereField;
        private Operator whereOperator;
        private Value whereValue;

        void handleNextToken(SqlToken token) {

            switch (step) {

                case QUERY_TYPE:
                    if (token.getTokenType() != SqlTokenType.SELECT) {
                        throw new IllegalArgumentException("Only SELECT queries are supported");
                    }
                    query.setQueryType(QueryType.SELECT);
                    step = SqlParserStep.SELECT_FIELD;
                    break;
                case SELECT_FIELD:
                    if (token.getTokenType() == SqlTokenType.IDENTIFIER) {
                        query.addField(new Field(token.getValue()));
                        step = SqlParserStep.SELECT_COMMA;
                    } else if (token.getTokenType() == SqlTokenType.ASTERISK) {
                        query.addField(new Field(token.getValue()));
                        step = SqlParserStep.SELECT_FROM;
                    }
                    else {
                        throw new IllegalArgumentException(String.format("Expected a field identifier or '*' but was '%s'", token.getValue()));
                    }
                    break;
                case SELECT_COMMA:
                    if (token.getTokenType() == SqlTokenType.COMMA) {
                        step = SqlParserStep.SELECT_FIELD;
                    } else if (token.getTokenType() == SqlTokenType.FROM) {
                        step = SqlParserStep.SELECT_FROM_TABLE;
                    } else {
                        throw new IllegalArgumentException(String.format("Expected ',' or 'FROM' but was '%s'", token.getValue()));
                    }
                    break;
                case SELECT_FROM:
                    if (token.getTokenType() != SqlTokenType.FROM) {
                        throw new IllegalArgumentException(String.format("Expected 'FROM' but was ", token.getValue()));
                    }
                    step = SqlParserStep.SELECT_FROM_TABLE;
                    break;
                case SELECT_FROM_TABLE:
                    if (token.getTokenType() == SqlTokenType.IDENTIFIER) {
                        query.setTableName(token.getValue());
                    } else if (token.getTokenType() == SqlTokenType.QUOTED_STRING) {
                        query.setTableName(token.getUnquotedValue());
                    } else {
                        throw new IllegalArgumentException(String.format("Expected a table identifier but was '%s'", token.getValue()));
                    }
                    step = SqlParserStep.WHERE;
                    break;
                case WHERE:
                    if (token.getTokenType() == SqlTokenType.WHERE) {
                        step = SqlParserStep.WHERE_FIELD;
                    } else if (token.getTokenType() == SqlTokenType.LIMIT) {
                        step = SqlParserStep.LIMIT_VALUE;
                    } else {
                        throw new IllegalArgumentException(String.format("Expected 'WHERE' but was '%s'", token.getValue()));
                    }
                    break;
                case WHERE_FIELD:
                    if (token.getTokenType() != SqlTokenType.IDENTIFIER) {
                        throw new IllegalArgumentException(String.format("Expected a field identifier but was '%s'", token.getValue()));
                    }
                    whereField = new Field(token.getValue());
                    step = SqlParserStep.WHERE_OPERATOR;
                    break;
                case WHERE_OPERATOR:
                    if (token.getTokenType() != SqlTokenType.OPERATOR) {
                        throw new IllegalArgumentException(String.format("Expected an operator but was '%s'", token.getValue()));
                    }
                    whereOperator = Operator.of(token.getValue());
                    step = SqlParserStep.WHERE_VALUE;
                    break;
                case WHERE_VALUE:
                    if (token.getTokenType() == SqlTokenType.INTEGER) {
                        whereValue = new Value(token.getValue(), ValueType.NUMBER);
                    }
                    else if (token.getTokenType() == SqlTokenType.QUOTED_STRING) {
                        whereValue = new Value(token.getUnquotedValue(), ValueType.STRING);
                    } else {
                        throw new IllegalArgumentException(String.format("Expected a number or a string but was '%s'", token.getValue()));
                    }
                    query.addWhereClause(new WhereClause(whereField, whereOperator, whereValue));
                    whereField = null;
                    whereOperator = null;
                    whereValue = null;
                    step = SqlParserStep.WHERE_AND;
                    break;
                case WHERE_AND:
                    if (token.getTokenType() == SqlTokenType.AND) {
                        step = SqlParserStep.WHERE_FIELD;
                    } else if (token.getTokenType() == SqlTokenType.LIMIT) {
                        step = SqlParserStep.LIMIT_VALUE;
                    } else {
                        throw new IllegalArgumentException(String.format("Expected 'AND' but was '%s'", token.getValue()));
                    }
                    break;
                case LIMIT_VALUE:
                    if (token.getTokenType() != SqlTokenType.INTEGER) {
                        throw new IllegalArgumentException(String.format("Expected an integer but was '%s'", token.getValue()));
                    }
                    query.setLimit(token.getValueAsInt());
                    break;
            }
        }

        Query getQuery() {
            return query;
        }
    }

}
