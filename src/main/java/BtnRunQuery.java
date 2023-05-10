import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sankhya.util.JdbcUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map;

public class BtnRunQuery implements AcaoRotinaJava {
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        ServiceContext ctx = ServiceContext.getCurrent();
        JsonObject request = ctx.getJsonRequestBody();
        try {
            String sql = request.get("sql").getAsString();
            JsonObject response = execute(sql);
            ServiceContext.getCurrent().setJsonResponse(response);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public JsonObject execute(String query) throws MGEModelException {
        JsonObject response = new JsonObject();
        JdbcWrapper jdbc = null;
        ResultSet rset = null;
        long start = System.currentTimeMillis();
        try {
            EntityFacade entity = EntityFacadeFactory.getDWFFacade();
            jdbc = entity.getJdbcWrapper();
            jdbc.openSession();
            PreparedStatement upd = jdbc.getPreparedStatement(query);
            boolean executeStatus = upd.execute();
            int rowsUpdated = upd.getUpdateCount();
            rset = upd.getResultSet();
            long end = start - System.currentTimeMillis();
            if (executeStatus) {
                Map.Entry<JsonArray, Integer> rows = GetResults(rset);
                response.add("rows", rows.getKey());
                rowsUpdated = rows.getValue();
            }
            response.addProperty("rowsUpdated", rowsUpdated);
            response.addProperty("executeStatus", executeStatus);
            response.addProperty("queryTime", Math.abs(end));
            return response;
        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JdbcUtils.closeResultSet(rset);
            JdbcWrapper.closeSession(jdbc);
        }
        return null;
    }

    public Map.Entry<JsonArray, Integer> GetResults(@NotNull ResultSet rset) throws SQLException {
        JsonArray results = new JsonArray();
        int total_cols = rset.getMetaData().getColumnCount();
        int rowsUpdated = 0;
        while (rset.next()) {
            rowsUpdated++;
            JsonObject colJson = new JsonObject();
            for (int col = 1; col <= total_cols; col++) {
                String value = rset.getString(col);
                String colLabel = rset.getMetaData().getColumnLabel(col);
                if (!colJson.has(colLabel)){
                    colJson.addProperty(colLabel, value);
                }else{
                    colJson.addProperty(colLabel+col, value); // Caso seja um campo repetido
                }
            }
            results.add(colJson);
        }
        return new AbstractMap.SimpleEntry<>(results, rowsUpdated);
    }
}
