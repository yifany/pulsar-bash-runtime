package org.coding.yifany.runtime.bash.function;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;
import org.coding.yifany.runtime.bash.common.Constants;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class BashRuntimeFunction implements Function<String, List<String>> {

    private String scriptOutputPath;
    private String scriptInputPath;

    public List<String> process(String input, Context context) throws Exception {
        this.resolveUserConfig(context);

        String scriptTempName = this.scriptTempName();

        ProcessBuilder
                processBuilder = this.createProcessBuilder(input, scriptTempName, context);
        Process process = processBuilder.start();

        int exitCode = process.waitFor();

        List<String> outputList =
                this.streamAsList(process.getInputStream());
        List<String> errorList = this.streamAsList(process.getErrorStream());

        context.getLogger().info(String.format("exitCode is %s", exitCode));

        return exitCode == 0 ? outputList : errorList;
    }

    public void resolveUserConfig(Context context) {
        this.scriptOutputPath =
                Constants.getEnvOrElse(Constants.SCRIPT_OUTPUT_PATH,
                        FileUtils.getTempDirectory().getAbsolutePath(), context);
        context.getLogger().info(String.format("scriptOutputPath is %s", this.scriptOutputPath));

        this.scriptInputPath = Constants.getEnvOrElseException(Constants.SCRIPT_INPUT_PATH, context);

        context.getLogger().info(String.format("scriptInputPath is %s", this.scriptInputPath));
    }

    protected ProcessBuilder createProcessBuilder(String args, String scriptTempName, Context context) throws IOException {
        File scriptTempPath =
                new File(String.format("%s/%s", this.scriptOutputPath, scriptTempName));
        boolean writable = scriptTempPath.setWritable(true, false);

        InputStream scriptInput =
                this.getClass().getResourceAsStream(this.scriptInputPath);
        if (scriptInput != null) {
            FileUtils.copyInputStreamToFile(scriptInput, scriptTempPath);

            context.getLogger().info(String.format("copy input stream from [%s] to [%s] writable [%s]", scriptInput, scriptTempPath, writable));
        } else
            context.getLogger().info("scriptInput is null");

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("sh", scriptTempName, args);
        processBuilder.directory(new File(this.scriptOutputPath));

        return processBuilder;
    }

    protected List<String> streamAsList(InputStream ins) {
        return new BufferedReader(new InputStreamReader(ins)).lines().collect(Collectors.toList());
    }

    protected String scriptTempName() {
        return FilenameUtils.getName(this.scriptInputPath);
    }
}
