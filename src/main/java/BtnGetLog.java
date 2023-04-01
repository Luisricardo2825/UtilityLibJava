import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.ws.ServiceContext;
import com.google.gson.JsonObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BtnGetLog implements AcaoRotinaJava {
    JsonObject values = new JsonObject();
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        String path = System.getProperty("org.jboss.boot.log.file");
        try {
            Path filePath = Paths.get(path);
            values.addProperty("filePath", filePath.toString());
            byte[] bytes = Files.readAllBytes(filePath);

            JsonObject res = new JsonObject();
            String[] lines = new String(bytes).split(System.lineSeparator());
            List<String> list = Arrays.asList(lines);
            Collections.reverse(list);
            list = list.stream().limit(1000).collect(Collectors.toList());

            String content = String.join(System.lineSeparator(),list);
            values.addProperty("content", content);
            res.addProperty("log", content);

            ServiceContext.getCurrent().setJsonResponse(res);
        }catch (Exception e){
            throw new Exception("Erro: "+path+" values:"+values.toString());
        }

    }
}