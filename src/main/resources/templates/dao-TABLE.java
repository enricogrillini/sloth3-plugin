package ${daoPackageName};

import ${pojoPackageName}.*;
import it.eg.sloth.db.Query;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ${pojoPackageName}.${pojoClassName};

import java.time.LocalDate;
import java.time.LocalDateTime;

// Dao per la tabella ${tableName}
@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ${daoClassName} {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final String SQL_SELECT = ${DbUtil.genSelect(${table})};

    private static final String SQL_DELETE = ${DbUtil.genDelete(${table})};

    private static final String SQL_INSERT = ${DbUtil.genInsert(${table})};

    private static final String SQL_UPDATE = ${DbUtil.genUpdate(${table})};

#if(${DbUtil.genPrimaryKeyList(${table}, true)})
    // Update
    public int delete(${DbUtil.genPrimaryKeyList(${table}, true)}) {
        Query query = new Query(SQL_DELETE);

        #foreach( $tableColumn in $table.primaryKeyCollection )
        query.addParameter("${DbUtil.javaObjectName($tableColumn.name)}", ${DbUtil.javaObjectName($tableColumn.name)});
        #end

        return query.execute(jdbcTemplate);
        }
#end

#if(${DbUtil.genPrimaryKeyList(${table}, true)})
    // Select
    public ${pojoClassName} get(${DbUtil.genPrimaryKeyList(${table}, true)}) {
        Query query = new Query(SQL_SELECT);

        #foreach( $tableColumn in $table.primaryKeyCollection )
        query.addParameter("${DbUtil.javaObjectName($tableColumn.name)}", ${DbUtil.javaObjectName($tableColumn.name)});
        #end

        return query.selectRow(jdbcTemplate, ${pojoClassName}.class);
    }
#end

    // Insert
    public int insert(${pojoClassName} ${pojoObjectName}) {
        Query query = new Query(SQL_INSERT);

        #foreach( $tableColumn in $table.tableColumnCollection )
        query.addParameter("${DbUtil.javaObjectName($tableColumn.name)}", ${pojoObjectName}.get${DbUtil.javaClassName($tableColumn.name)}());
        #end

        return query.execute(jdbcTemplate);
    }

#if(${DbUtil.genPrimaryKeyList(${table}, true)})
    // Update
    public int update(${DbUtil.genPrimaryKeyList(${table}, true)}, ${pojoClassName} ${pojoObjectName}) {
        Query query = new Query(SQL_UPDATE);

        #foreach( $tableColumn in $table.tableColumnCollection )
        query.addParameter("${DbUtil.javaObjectName($tableColumn.name)}", ${pojoObjectName}.get${DbUtil.javaClassName($tableColumn.name)}());
        #end

        return query.execute(jdbcTemplate);
    }
#end

}
