package de.papiertuch.utils.handler.exception;

import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger("ban-logger");

    public static void handleException( @NotNull final Exception exception, @NotNull final String message, final boolean printStackTrace ) {
        val packageArray = ExceptionHandler.class.getPackage().getName().split( "[.]" );

        val packageBuilder = new StringBuilder();
        if ( packageArray.length >= 2 ) {
            for ( int i = 0; i < 2; i++ )
                packageBuilder.append( packageArray[i] ).append( "." );
        } else {
            for ( int i = 0; i < 1; i++ )
                packageBuilder.append( packageArray[i] ).append( "." );
        }

        packageBuilder.deleteCharAt( packageBuilder.length() - 1 );

        var stackTraceCounter = 0;
        var stackTraceFilteredCounter = 0;

        for ( val traceElement : exception.getStackTrace() ) {
            stackTraceCounter++;
            if ( traceElement.getClassName().startsWith( packageBuilder.toString() ) ) {
                stackTraceFilteredCounter++;
            }
        }

        val filteredExceptionLine = ( stackTraceCounter - stackTraceFilteredCounter ) + 1;

        val stackTraceElement = exception.getStackTrace()[filteredExceptionLine];
        val className = stackTraceElement.getClassName();
        val methodName = stackTraceElement.getMethodName();
        val lineNumber = stackTraceElement.getLineNumber();

        LOGGER.log( Level.WARNING, "Message: {0} \nClass: {1} \nMethod: {2} \nLine: {3} \nException: {4}", new Object[] { message, className, methodName, lineNumber, exception } );

        if ( printStackTrace )
            exception.printStackTrace();
    }
}
