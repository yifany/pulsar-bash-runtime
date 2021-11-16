package org.coding.yifany.runtime.bash.function;

import org.apache.commons.io.FileUtils;
import org.apache.pulsar.functions.api.Context;
import org.coding.yifany.runtime.bash.common.Constants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class BashRuntimeFunctionTest {

    @Test
    public void isClasspathResource() {
        String path1 = "classpath:/file1.txt";
        String path2 = "classpath:file1.txt";
        String path3 = "/file1.txt";
        String path4 = "file:///file1.txt";

        BashRuntimeFunction
                function = new BashRuntimeFunction();
        Assert.assertTrue(function.isClasspathResource(path1));
        Assert.assertTrue(function.isClasspathResource(path2));
        Assert.assertFalse(function.isClasspathResource(path3));
        Assert.assertFalse(function.isClasspathResource(path4));
    }

    @Test
    public void scriptTempName() {
        String path1 = "/depth1/depth2/depth3";
        String path2 = "/depth1/depth2/depth3/file.txt";

        BashRuntimeFunction
                function = new BashRuntimeFunction();
        Assert.assertEquals("depth3", function.scriptTempName(path1));
        Assert.assertEquals("file.txt", function.scriptTempName(path2));
    }

    @Test
    public void streamAsList() {
        List<String>
                expected = new ArrayList<>();
        expected.add("command-response-1");
        expected.add("command-response-2");

        BashRuntimeFunction
                function = new BashRuntimeFunction();
        List<String> actual = function.streamAsList(new ByteArrayInputStream(String.join(Constants.NEW_LINE_UNIX, expected).getBytes()));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void createProcessBuilder() throws IOException {
        String scriptOutputPath = FileUtils.getTempDirectoryPath();

        BashRuntimeFunction function =
                new BashRuntimeFunction("classpath:/append-exclamation-mark.sh", scriptOutputPath);
        Context mockedContext = this.mockedContext();

        ProcessBuilder processBuilder =
                function.createProcessBuilder("--help", "append-exclamation-mark.sh", mockedContext);
        Assert.assertEquals(new File(scriptOutputPath), processBuilder.directory());
        Assert.assertEquals(Arrays.asList("sh", "append-exclamation-mark.sh", "--help"), processBuilder.command());
    }

    private Context mockedContext() {
        Logger logger = mock(Logger.class);
        Context context = mock(Context.class);

        when(context.getLogger()).thenReturn(logger);

        return context;
    }
}