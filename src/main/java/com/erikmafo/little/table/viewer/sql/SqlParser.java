package com.erikmafo.little.table.viewer.sql;

import com.erikmafo.little.table.viewer.sql.functions.AggregationExpression;
import org.jetbrains.annotations.NotNull;

public class SqlParser {

    /**
     * Takes inn a raw sql query and converts this into a structured query object.
     *
     * @param sql a rqw query string.
     * @return a {@link SqlQuery} object.
     * @throws IllegalArgumentException if the raw query string is invalid.
     */
    public SqlQuery parse(String sql) {

        var tokenizer = new SqlTokenizer(sql);
        var queryBuilder = new QueryBuilder();
        var token = tokenizer.next();

        while (token != null) {
            queryBuilder.handleNextToken(token);
            token = tokenizer.next();
        }

        return queryBuilder.getSqlQuery();
    }

    private static class QueryBuilder {

        private final SqlQuery sqlQuery = new SqlQuery();

        private Step step = Step.QUERY_TYPE;
        private Field whereField;
        private Operator whereOperator;

        enum Step {
            QUERY_TYPE,
            FIELD,
            SELECT_COMMA,
            SELECT_FROM,
            SELECT_FROM_TABLE,
            WHERE,
            WHERE_FIELD,
            WHERE_OPERATOR,
            WHERE_VALUE,
            WHERE_AND,
            LIMIT_VALUE,
        }

        public SqlQuery getSqlQuery() {
            return sqlQuery;
        }

        public void handleNextToken(SqlToken token) {
            ensureValid(token);

            switch (step) {
                case QUERY_TYPE:
                    queryType(token);
                    break;
                case FIELD:
                    selectField(token);
                    break;
                case SELECT_COMMA:
                    selectComma(token);
                    break;
                case SELECT_FROM:
                    selectFrom(token);
                    break;
                case SELECT_FROM_TABLE:
                    selectFromTable(token);
                    break;
                case WHERE:
                    where(token);
                    break;
                case WHERE_FIELD:
                    whereField(token);
                    break;
                case WHERE_OPERATOR:
                    whereOperator(token);
                    break;
                case WHERE_VALUE:
                    whereValue(token);
                    break;
                case WHERE_AND:
                    whereAnd(token);
                    break;
                case LIMIT_VALUE:
                    limitValue(token);
                    break;
                default:
                    throw new AssertionError(String.format("unsupported sql token %s", token));
            }
        }

        private void ensureValid(@NotNull SqlToken token) {
            if (token.getTokenType().equals(SqlTokenType.INVALID)) {
                throw new IllegalArgumentException(token.getError());
            }
        }

        private void limitValue(@NotNull SqlToken token) {
            if (token.getTokenType() != SqlTokenType.INTEGER) {
                throw new IllegalArgumentException(String.format("Expected an integer but was '%s'", token.getValue()));
            }
            sqlQuery.setLimit(token.getValueAsInt());
        }

        private void whereAnd(@NotNull SqlToken token) {
            if (token.getTokenType() == SqlTokenType.AND) {
                step = Step.WHERE_FIELD;
            } else if (token.getTokenType() == SqlTokenType.LIMIT) {
                step = Step.LIMIT_VALUE;
            } else {
                throw new IllegalArgumentException(String.format("Expected 'AND' but was '%s'", token.getValue()));
            }
        }

        private void whereValue(@NotNull SqlToken token) {
            var whereValue = Value.from(token);
            sqlQuery.addWhereClause(new WhereClause(whereField, whereOperator, whereValue));
            whereField = null;
            whereOperator = null;
            step = Step.WHERE_AND;
        }

        private void whereOperator(@NotNull SqlToken token) {
            if (token.getTokenType() != SqlTokenType.OPERATOR) {
                throw new IllegalArgumentException(String.format("Expected an operator but was '%s'", token.getValue()));
            }
            whereOperator = Operator.of(token.getValue());
            step = Step.WHERE_VALUE;
        }

        private void whereField(@NotNull SqlToken token) {
            if (token.getTokenType() != SqlTokenType.IDENTIFIER) {
                throw new IllegalArgumentException(String.format("Expected a field identifier but was '%s'", token.getValue()));
            }
            whereField = new Field(token.getValue());
            step = Step.WHERE_OPERATOR;
        }

        private void where(@NotNull SqlToken token) {
            if (token.getTokenType() == SqlTokenType.WHERE) {
                step = Step.WHERE_FIELD;
            } else if (token.getTokenType() == SqlTokenType.LIMIT) {
                step = Step.LIMIT_VALUE;
            } else {
                throw new IllegalArgumentException(String.format("Expected 'WHERE' but was '%s'", token.getValue()));
            }
        }

        private void selectFromTable(@NotNull SqlToken token) {
            if (token.getTokenType() == SqlTokenType.IDENTIFIER) {
                sqlQuery.setTableName(token.getValue());
            } else if (token.getTokenType() == SqlTokenType.QUOTED_STRING) {
                sqlQuery.setTableName(token.getUnquotedValue());
            } else {
                throw new IllegalArgumentException(String.format("Expected a table identifier but was '%s'", token.getValue()));
            }
            step = Step.WHERE;
        }

        private void selectFrom(@NotNull SqlToken token) {
            if (token.getTokenType() != SqlTokenType.FROM) {
                throw new IllegalArgumentException(String.format("Expected 'FROM' but was ", token.getValue()));
            }
            step = Step.SELECT_FROM_TABLE;
        }

        private void selectComma(@NotNull SqlToken token) {
            if (token.getTokenType() == SqlTokenType.COMMA) {
                step = Step.FIELD;
            } else if (token.getTokenType() == SqlTokenType.FROM) {
                step = Step.SELECT_FROM_TABLE;
            } else {
                throw new IllegalArgumentException(String.format("Expected ',' or 'FROM' but was '%s'", token.getValue()));
            }
        }

        private void selectField(@NotNull SqlToken token) {
            if (token.getTokenType() == SqlTokenType.IDENTIFIER) {
                sqlQuery.addField(new Field(token.getValue()));
                step = Step.SELECT_COMMA;
            } else if (token.getTokenType() == SqlTokenType.ASTERISK) {
                sqlQuery.addField(new Field(token.getValue()));
                step = Step.SELECT_FROM;
            } else if (token.getTokenType() == SqlTokenType.FUNCTION_EXPRESSION) {
                var aggregation = AggregationExpression.from(token);
                sqlQuery.addAggregation(aggregation);
                step = Step.SELECT_FROM;
            }
            else {
                throw new IllegalArgumentException(String.format("Expected a field identifier or '*' but was '%s'", token.getValue()));
            }
        }

        private void queryType(@NotNull SqlToken token) {
            if (token.getTokenType() == SqlTokenType.SELECT) {
                sqlQuery.setQueryType(QueryType.SELECT);
            } else {
                throw new IllegalArgumentException("Only SELECT queries are supported");
            }
            step = Step.FIELD;
        }
    }
}
