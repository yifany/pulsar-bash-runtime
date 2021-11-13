package org.coding.yifany.runtime.bash.function;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;
import org.coding.yifany.runtime.bash.common.Constants;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class BashRuntimeFunction implements Function<String, String> {

    private String scriptOutputPath;
    private String scriptInputPath;

    public String process(String input, Context context) throws Exception {
        this.resolveUserConfig(context);

        String scriptTempName = this.scriptTempName();

        ProcessBuilder
                processBuilder = this.createProcessBuilder(input, scriptTempName);
        Process process = processBuilder.start();

        int exitCode = process.waitFor();

        List<String> outputList =
                this.streamAsList(process.getInputStream());
        List<String> errorList = this.streamAsList(process.getErrorStream());

        return exitCode == 0 ? outputList.get(0) : errorList.get(0);
    }

    public void resolveUserConfig(Context context) {
        this.scriptOutputPath =
                Constants.getEnvOrElse(Constants.SCRIPT_INPUT_PATH,
                        FileUtils.getTempDirectory().getAbsolutePath(), context);
        this.scriptInputPath = Constants.getEnvOrElseException(Constants.SCRIPT_OUTPUT_PATH, context);
    }

    protected ProcessBuilder createProcessBuilder(String args, String scriptTempName) throws IOException {
        File scriptTempPath =
                new File(String.format("%s/%s", this.scriptOutputPath, scriptTempName));
        InputStream scriptInput =
                ClassLoader.getSystemResourceAsStream(this.scriptInputPath);
        if (scriptInput != null) {
            FileUtils.copyInputStreamToFile(scriptInput, scriptTempPath);
        }
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
