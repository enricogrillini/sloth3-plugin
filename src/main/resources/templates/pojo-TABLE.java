package ${tableBeanPackageName};

import lombok.Data;

// Pojo per la tabella ${tableName}
@Data
public class ${rowBeanClassName} {

    // Setter/Getter
#foreach( $tableColumn in $table.plainColumnCollection )
    ${DbUtil.getJavaClass($tableColumn)} ${DbUtil.javaObjectName($tableColumn.name)};
#end

}
