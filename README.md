Steps to reproduce issue:

* start the app - `sbt run` 
* In a new shell tab, run the test script - `./test_caches.sh`
* Observe the results. All 4 endpoints **should** return immediately with a status code of 200, but 2/4 actually take 5 seconds and then return an HTTP 500 since the server times out

Also note that the endpoints only fail when the app is started for real - there are some simple tests that just hit the endpoints, but the tests all pass.

In my investigation, it seems that it is related to the underlying Akka Dispatcher used to handle the requests, `akka.dispatch.BatchingExecutor`. I was not able to confirm this, but the comment seems possibly related

> WARNING: The underlying Executor's execute-method must not execute the submitted Runnable
> in the calling thread synchronously. It must enqueue/handoff the Runnable.
