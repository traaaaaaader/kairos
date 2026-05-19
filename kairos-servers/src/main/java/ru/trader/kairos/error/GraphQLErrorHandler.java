package ru.trader.kairos.error;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class GraphQLErrorHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof ResponseStatusException rse) {
            HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());
            String message = rse.getReason();

            ErrorType errorType = switch (status) {
                case NOT_FOUND -> ErrorType.NOT_FOUND;
                case FORBIDDEN -> ErrorType.FORBIDDEN;
                case BAD_REQUEST -> ErrorType.BAD_REQUEST;
                case UNAUTHORIZED -> ErrorType.UNAUTHORIZED;
                default -> ErrorType.INTERNAL_ERROR;
            };

            return GraphqlErrorBuilder.newError()
                    .message(message)
                    .errorType(errorType)
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        }

        return GraphqlErrorBuilder.newError()
                .message("Internal server error")
                .errorType(ErrorType.INTERNAL_ERROR)
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .build();
    }
}
