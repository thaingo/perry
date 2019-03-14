package gov.ca.cwds.service.scripts;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.script.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.IntStream;

/**
 * Created by dmitry.rudenko on 5/9/2017.
 */
class Script {
  private String[] variables;
  private String newScript;
  private ScriptEngine scriptEngine;

  @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    //script file location taken from property file only!
  Script(String filePath, String... variables) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(filePath));
    newScript = new String(bytes, StandardCharsets.UTF_8);
    this.variables = variables;
    int dotIndex = filePath.lastIndexOf('.');
    String fileExtension = dotIndex == -1 ? "" : filePath.substring(dotIndex + 1);
    ScriptEngineManager factory = new ScriptEngineManager();
    scriptEngine = factory.getEngineByName(fileExtension);
  }

  Object eval(Object... objects) throws ScriptException {
    ScriptContext scriptContext = new SimpleScriptContext();
    IntStream.range(0, variables.length)
            .forEach(i -> scriptContext.setAttribute(variables[i], objects[i], ScriptContext.ENGINE_SCOPE));
    return scriptEngine.eval(newScript, scriptContext);
  }
}
