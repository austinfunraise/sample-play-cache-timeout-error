package controllers;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

public class CacheControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void testSyncCacheGet() {
        testRouteStatusOk("/cache/sync/get/test_key");
    }

    @Test
    public void testAsyncCacheGet() {
        testRouteStatusOk("/cache/async/get/test_key");
    }

    @Test
    public void testAsyncCompletableFutureCacheGet() {
        testRouteStatusOk("/cache/asyncCompletableFuture/get/test_key");
    }

    @Test
    public void testAsyncBlockingCacheGet() {
        testRouteStatusOk("/cache/asyncBlocking/get/test_key");
    }

    private void testRouteStatusOk(String path) {
        Http.RequestBuilder request = new Http.RequestBuilder()
            .method(GET)
            .uri(path);

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

}
