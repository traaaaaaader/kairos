package ru.trader.kairos.config;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class DateTimeCoercing implements Coercing<OffsetDateTime, String> {

    @Override
    public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
        if (dataFetcherResult instanceof OffsetDateTime odt) {
            return odt.toString();
        }
        throw new CoercingSerializeException("Expected OffsetDateTime");
    }

    @Override
    public OffsetDateTime parseValue(Object input) throws CoercingParseValueException {
        if (input instanceof String s) {
            try {
                return OffsetDateTime.parse(s);
            } catch (DateTimeParseException e) {
                throw new CoercingParseValueException("Invalid DateTime format: " + s);
            }
        }
        throw new CoercingParseValueException("Expected String");
    }

    @Override
    public OffsetDateTime parseLiteral(Object input) throws CoercingParseLiteralException {
        if (input instanceof StringValue sv) {
            try {
                return OffsetDateTime.parse(sv.getValue());
            } catch (DateTimeParseException e) {
                throw new CoercingParseLiteralException("Invalid DateTime format: " + sv.getValue());
            }
        }
        throw new CoercingParseLiteralException("Expected StringValue");
    }
}
