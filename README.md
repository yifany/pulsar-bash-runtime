**Technical Overview**

The goal of this project is to run bash script in Pulsar cluster. Since it's not supported natively, 
my solution is to create a Pulsar function in Java to invoke os process to execute the bash script defined in the function configuration.

Right now the function supports running a provisioned bash script defined in the classpath, or script sitting on the os for flexibility sake, 
by defining the `scriptInputPath` in the function configuration. Along with each instance of deployment, the user can also specify input(s)/output/log 
topics and parallelism via the same setting.

The implementation is straightforward, the Pulsar function `BashRuntimeFunction` act as the runtime environment to execute corresponding bash script.
The messages coming from input topics will be treated as arguments, and passed along with the script file. The response will be streamed back to the defined
output topic.

For the sake of end-to-end testing, I've also included a simple `PulsarOps` service which helps to create producer/consumer components so the runtime function
can be tested once deployed.

**How to run**
- assuming there is a docker daemon running on the test machine (I'm using Docker Desktop OSX 3.6.0) 
- assuming Maven is available (mvn command and I'm using Maven 3.6.0)
- at the root level, run `docker-compose up` and this should pull the docker image and start the Pulsar standalone cluster
- I do notice the `pulsar-standalone` container could fail to start up randomly, and usually `Ctrl+C` followed by a retry will fix it
- at the root level, either run `sh docker-run-local.sh` to start `localrun` mode, or run `sh docker-run-dev.sh` to `create` function in the cluster
- at the root level, run `mvn test` to see some end to end action. One of the tests `DefaultPulsarOpsTest#invokeLocal` will try to produce one uuid to the input
topic of the `add-exclamation-mark.sh`, and the the consumer will start and terminate once the produced message was received.

**Goals did not completed**

Due to personal/work reason, I did not have enough time to complete Goal 3 (k8s deploy) and skipped it completely and stick with Docker to spawn the standalone cluster.

**Feedback for the task**

This is a interesting/informative task and definitely a learning experience for me. A few things I did not get to ask is:

- where should the bash script be sourced from (what's intended in the task) ? I made it so it can read from classpath (e.g. approved script) or anywhere in the fs
- I wonder how the Pulsar function would handle it if the response of the script is a stream (e.g. `tail -f file.txt`), so the response of the function needs to be something like `Outputstream`