#!/bin/bash

test_endpoint() {
  echo "Testing endpoint: $1"
  time curl -s -w 'status code is %{http_code}\n' -o /dev/null "http://localhost:9000$1"
  echo
}

test_endpoint "/cache/sync/get/sample_key"
test_endpoint "/cache/async/get/sample_key"
test_endpoint "/cache/asyncCompletableFuture/get/sample_key"
test_endpoint "/cache/asyncBlocking/get/sample_key"
