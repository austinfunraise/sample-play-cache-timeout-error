package controllers;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.inject.Inject;
import play.cache.AsyncCacheApi;
import play.cache.SyncCacheApi;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

public class CacheController extends Controller {
    @Inject
    private SyncCacheApi syncCache;

    @Inject
    private AsyncCacheApi asyncCache;

    // works fine
    public Result getSyncEntry(String key) {
        return syncCache
            .get(key)
            .map(String::valueOf)
            .map(Results::ok)
            .orElse(ok("<not present>"));
    }

    // works fine
    public CompletionStage<Result> getAsyncEntry(String key) {
        return asyncCache
            .get(key)
            .thenApply(opt -> opt
                .map(String::valueOf)
                .orElse("<not present>")
            )
            .thenApply(Results::ok);
    }

    // throws a TimeoutException, and returns an HTTP 500
    public Result getFutureAsyncEntry(String key) throws InterruptedException, ExecutionException, TimeoutException {
        return asyncCache
            .get(key)
            .thenApplyAsync(opt -> opt
                .map(String::valueOf)
                .orElse("<not present>")
            )
            .thenApplyAsync(Results::ok)
            .toCompletableFuture()
            .get(5L, TimeUnit.SECONDS);
    }

    // throws a TimeoutException, and returns an HTTP 500
    public Result getBlockingAsyncEntry(String key) {
        return blocking(asyncCache.get(key))
            .map(String::valueOf)
            .map(Results::ok)
            .orElse(ok("<not present>"));
    }

    public Result setSyncEntry(String key, String value) {
        syncCache.set(key, value);
        return ok("set syncCache key: " + key + ", value: " + value);
    }

    public CompletionStage<Result> setAsyncEntry(String key, String value) {
        return asyncCache
            .set(key, value)
            .thenApply(_done -> ok("set asyncCache key: " + key + ", value: " + value));
    }

    /**
     * copied from play.cache.DefaultSyncCacheApi.blocking(stage)
     */
    private static <T> T blocking(CompletionStage<T> stage) {
        boolean interrupted = false;
        try {
            for (; ; ) {
                try {
                    return stage.toCompletableFuture().get(5_000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } catch (ExecutionException | TimeoutException e) {
            throw new RuntimeException("Could not get value from cache", e);
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
