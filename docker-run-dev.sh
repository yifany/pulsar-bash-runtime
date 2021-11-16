#!/usr/bin/env bash

docker exec pulsar-standalone /bin/bash -c "mkdir -p /pulsar/pulsar-bash-runtime"

docker cp target/pulsar-bash-runtime-1.0.jar pulsar-standalone:/pulsar/pulsar-bash-runtime
docker cp append-exclamation-mark-dev.yaml pulsar-standalone:/pulsar/pulsar-bash-runtime

docker exec -it pulsar-standalone /bin/bash -c "bin/pulsar-admin functions create --function-config-file pulsar-bash-runtime/append-exclamation-mark-dev.yaml"
docker exec -it pulsar-standalone /bin/bash -c "bin/pulsar-admin functions status --name BashRuntimeFunction"