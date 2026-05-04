package ${pojoPackageName};

import it.eg.sloth.db.Query;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Dao per la tabella ${tableName}
@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ${daoClassName} {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final String SQL = ${DbUtil.genSelect(${table})};

    public ${pojoClassName} get${pojoClassName}(${DbUtil.genPrimaryKeyList(${table}, true)}) {
        Query query = new Query(SQL);

        #foreach( $tableColumn in $table.primaryKeyCollection )
        query.addParameter("${DbUtil.javaObjectName($tableColumn.name)}", ${DbUtil.javaObjectName($tableColumn.name)});
        #end

        return query.selectRow(jdbcTemplate, ${pojoClassName}.class);
    }

}
