package org.n.riesgos.asyncwrapper.utils

/**
 * Generic wrapper to retry a piece of code several times in case an exception occurs.
 * Can be used as this:
 *
 *
 * val result = retry<ResultTime, UnluckyButRetrybleException>(UnluckyButRetrybleException::java.class, 10, 1000L, {
 *     // it is the current retry count - in case you want to log it
 *     val part1 = riskyFunctionThatCanThrowAnException()
 *     val part2 = anotherRiskyFunction(part1)
 *     riskyCleanup(part2)
 * }
 *
 */
fun <R, E> retry(exceptionToRetry: Class<E>, maxRetries: Int, sleepTimeMilliSeconds: Long, func: (Int) -> R): R where E: Exception {
    var retryCount = 0
    var lastException: Throwable? = null

    while(retryCount <= maxRetries) { //first run is not a retry (max runs = maxRetries + 1)
        try {
            val result = func(retryCount)
            return result
        } catch (ex: Exception) {
            if (!(exceptionToRetry.isInstance(ex))) {
                throw ex
            }
            // ignore this specific exception and retry
            retryCount++
            lastException = ex
            Thread.sleep(sleepTimeMilliSeconds)
        }
    }
    throw lastException as Throwable
}