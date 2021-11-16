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

    public BashRuntimeFunction() {

    }

    public BashRuntimeFunction(String scriptInputPath, String scriptOutputPath) {
        this.scriptInputPath = scriptInputPath;
        this.scriptOutputPath = scriptOutputPath;
    }

    public List<String> process(String input, Context context) throws Exception {
        this.resolveUserConfig(context);

        String scriptTempName =
                this.scriptTempName(this.scriptInputPath);
        ProcessBuilder processBuilder =
                this.createProcessBuilder(input, scriptTempName, context);
        Process process = processBuilder.start();

        int exitCode = process.waitFor();

        List<String> outputList =
                this.streamAsList(process.getInputStream());
        List<String> errorList = this.streamAsList(process.getErrorStream());

        context.getLogger().info(String.format("Process finished with exitCode [%s]", exitCode));

        return exitCode == 0 ? outputList : errorList;
    }

    public void resolveUserConfig(Context context) {
        this.scriptOutputPath =
                Constants.getEnvOrElse(Constants.SCRIPT_OUTPUT_PATH,
                        FileUtils.getTempDirectory().getAbsolutePath(), context);
        context.getLogger().info(String.format("Bash temporary directory is defined as [%s]", this.scriptOutputPath));

        this.scriptInputPath =
                Constants.getEnvOrElseException(Constants.SCRIPT_INPUT_PATH, context);
        context.getLogger().info(String.format("Bash input path is defined as [%s]", this.scriptInputPath));
    }

    protected ProcessBuilder createProcessBuilder(String args, String scriptTempName, Context context) throws IOException {
        File scriptTempPath =
                new File(String.format("%s/%s", this.scriptOutputPath, scriptTempName));
        InputStream scriptInput =
                this.openInputStream(this.scriptInputPath);
        if (scriptInput != null) {
            FileUtils.copyInputStreamToFile(scriptInput, scriptTempPath);
        } else
            context.getLogger().info("Unable to copy script to temp directory");

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("sh", scriptTempName, args);
        processBuilder.directory(new File(this.scriptOutputPath));

        return processBuilder;
    }

    protected List<String> streamAsList(InputStream ins) {
        return new BufferedReader(new InputStreamReader(ins)).lines().collect(Collectors.toList());
    }

    protected String scriptTempName(String scriptInputPath) {
        return FilenameUtils.getName(scriptInputPath);
    }

    protected InputStream openInputStream(String scriptInputPath) throws IOException {
        if (this.isClasspathResource(scriptInputPath)) {
            String scriptPath =
                    scriptInputPath.substring(Constants.PREFIX_CLASSPATH.length());
            return this.getClass().getResourceAsStream(scriptPath);
        } else
            return FileUtils.openInputStream(new File(scriptInputPath));
    }

    protected boolean isClasspathResource(String path) {
        return path.startsWith(Constants.PREFIX_CLASSPATH);
    }
}
