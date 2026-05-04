package ${pojoPackageName};

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Dao per la vista ${tableName}
@Data
public class ${daoClassName} {

#foreach( $tableColumn in $table.plainColumnCollection )
    ${DbUtil.getJavaClass($tableColumn)} ${DbUtil.javaObjectName($tableColumn.name)};
#end

}
