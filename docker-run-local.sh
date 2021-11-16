#!/usr/bin/env bash

docker exec pulsar-standalone /bin/bash -c "mkdir -p /pulsar/pulsar-bash-runtime"

docker cp target/pulsar-bash-runtime-1.0.jar pulsar-standalone:/pulsar/pulsar-bash-runtime
docker cp append-exclamation-mark.yaml pulsar-standalone:/pulsar/pulsar-bash-runtime

docker exec -it pulsar-standalone /bin/bash -c "bin/pulsar-admin functions localrun --function-config-file pulsar-bash-runtime/append-exclamation-mark.yaml"

#bin/pulsar-admin functions localrun \
#--classname org.coding.yifany.runtime.bash.function.BashRuntimeFunction \
#--output-serde-classname org.coding.yifany.runtime.bash.function.BashRuntimeSerDe \
#--jar pulsar-bash-runtime-1.0.jar \
#--inputs persistent://public/default/bash-runtime-input-1 \
#--output persistent://public/default/bash-runtime-output-1 \
#--tenant public \
#--namespace default \
#--name BashRuntimeFunction \
#--user-config '{"scriptInputPath":"/append-exclamation-mark.sh"}'

#bin/pulsar-admin functions create \
#--classname org.coding.yifany.runtime.bash.function.BashRuntimeFunction \
#--jar pulsar-bash-runtime-1.0.jar \
#--inputs persistent://public/default/bash-runtime-input-1 \
#--output persistent://public/default/bash-runtime-output-1 \
#--tenant public \
#--namespace default \
#--name BashRuntimeFunction \
#--user-config '{"scriptInputPath":"append-exclamation-mark.sh"}'

#bin/pulsar-admin functions trigger \
#--tenant public \
#--namespace default \
#--name BashRuntimeFunction \
#--topic persistent://public/default/bash-runtime-input-1 \
#--trigger-value "hello pulsar functions"

#bin/pulsar-admin functions delete --name BashRuntimeFunction
#bin/pulsar-admin functions status --name BashRuntimeFunction