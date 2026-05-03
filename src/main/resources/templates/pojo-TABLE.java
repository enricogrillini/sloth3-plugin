package ${pojoPackageName};

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Pojo per la tabella ${tableName}
@Data
public class ${pojoClassName} {

#foreach( $tableColumn in $table.plainColumnCollection )
    ${DbUtil.getJavaClass($tableColumn)} ${DbUtil.javaObjectName($tableColumn.name)};
#end

}
