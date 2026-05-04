package it.eg.sloth.mavenplugin.common;

import it.eg.sloth.dbmodeler.model.schema.code.Argument;
import it.eg.sloth.dbmodeler.model.schema.code.Function;
import it.eg.sloth.dbmodeler.model.schema.code.Method;
import it.eg.sloth.dbmodeler.model.schema.table.Table;
import it.eg.sloth.dbmodeler.model.schema.table.TableColumn;
import it.eg.sloth.dbmodeler.model.schema.view.ViewColumn;
import org.apache.commons.text.CaseUtils;

import java.text.MessageFormat;

/**
 * Project: sloth-plugin
 * Copyright (C) 2019-2021 Enrico Grillini
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author Enrico Grillini
 */
public class DbUtil {

    private DbUtil() {
        // NOP
    }


    private static final String STATEMENT_FUNCTION_BOOL = "'{' ? = call lib.booleanToChar({0}({1})) '}'";
    private static final String STATEMENT_FUNCTION = "'{' ? = call {0}({1}) '}'";
    private static final String STATEMENT_PROCEDURE = "'{' call {0}({1}) '}'";

    private static final String ORACLE_BOOLEAN = "PL/SQL BOOLEAN";

    public static String javaClassName(String dbName) {
        return CaseUtils.toCamelCase(dbName, true, '_');
    }

    public static String javaObjectName(String dbName) {
        return CaseUtils.toCamelCase(dbName, false, '_');
    }

    public static boolean isOracleBoolean(String dataType) {
        if (dataType == null)
            return false;

        return dataType.indexOf(ORACLE_BOOLEAN) == 0;
    }

    private static String getJavaClass(String type) {
        String dataType = type.toUpperCase();

        if (dataType.equalsIgnoreCase("NUMBER(38,0)") || dataType.startsWith("BIT") || dataType.startsWith("BIGINT") || dataType.startsWith("INT") || dataType.startsWith("TINYINT") || dataType.startsWith("UNIQUEIDENTIFIER")) {
            return "Long";
        } else if (dataType.startsWith("NUMBER") || dataType.startsWith("DOUBLE") || dataType.startsWith("FLOAT") || dataType.startsWith("DECIMAL")) {
            return "Double";
        } else if (dataType.startsWith("DATE")) {
            return "LocalDate";
        } else if (dataType.startsWith("TIMESTAMP")) {
            return "LocalDateTime";
        } else if (dataType.startsWith("VARCHAR") || dataType.startsWith("CHAR") || dataType.startsWith("LONG") || dataType.startsWith("TEXT")) {
            return "String";
        } else if (dataType.startsWith("BLOB") || dataType.startsWith("BYTEA")) {
            return "byte[]";
        } else if (dataType.startsWith("CLOB")) {
            return "String";
        } else if (dataType.startsWith(ORACLE_BOOLEAN) || dataType.startsWith("BOOL")) {
            return "Boolean";
        } else {
            return null;
        }
    }

    public static String getJavaClass(ViewColumn tableColumn) {
        return getJavaClass(tableColumn.getType());
    }

    public static String getJavaClass(TableColumn tableColumn) {
        return getJavaClass(tableColumn.getType());
    }

    public static String getJavaClass(Argument argument) {
        return getJavaClass(argument.getType());
    }

    public static String getJavaClass(Function function) {
        return getJavaClass(function.getReturnType());
    }

    public static String genPrimaryKeyList(Table table, boolean type) {
        StringBuilder result = new StringBuilder();
        for (TableColumn column : table.getPrimaryKeyCollection()) {
            if (result.length() != 0) {
                result.append(", ");
            }

            if (type) {
                result.append(getJavaClass(column) + " ");
            }

            result.append(javaObjectName(column.getName()));
        }

        return result.toString();
    }

    public static String genSelect(Table table) {
        StringBuilder result = new StringBuilder("Select *\n" + "From " + table.getName() + "\n");

        int i = 0;
        for (TableColumn column : table.getPrimaryKeyCollection()) {
            result
                    .append(i++ == 0 ? "Where " : " And\n       ")
                    .append(column.getName() + " = :" + javaObjectName(column.getName()));
        }

        return GenUtil.stringToJava(result.toString(), true);
    }

    public static String genInsert(Table table) {
        StringBuilder result = new StringBuilder("Insert into " + table.getName() + "\n");

        int i = 0;
        for (TableColumn column : table.getPlainColumnCollection()) {
            if (!column.isLob()) {
                result
                        .append(i++ == 0 ? "      (" : ",\n       ")
                        .append(column.getName());
            }
        }
        result.append(")\n");

        i = 0;
        for (TableColumn column : table.getPlainColumnCollection()) {
            result
                    .append(i++ == 0 ? "Values (" : ",\n        ")
                    .append("?");
        }
        result.append(")");
        return GenUtil.stringToJava(result.toString(), true);
    }

    public static String genDelete(Table table) {
        StringBuilder result = new StringBuilder("Delete From " + table.getName() + "\n");

        int i = 0;
        for (TableColumn column : table.getPrimaryKeyCollection()) {
            result
                    .append(i++ == 0 ? "Where " : " And\n       ")
                    .append(column.getName() + " = ?");
        }

        return GenUtil.stringToJava(result.toString(), true);
    }


    public static String genUpdate(Table table) {
        StringBuilder result = new StringBuilder("Update " + table.getName() + "\n");

        int i = 0;
        for (TableColumn column : table.getTableColumnCollection()) {
            if (!column.isLob()) {
                result
                        .append(i++ == 0 ? "Set " : ",\n    ")
                        .append(column.getName() + " = ?");
            }
        }
        result.append("\n");

        i = 0;
        for (TableColumn column : table.getPrimaryKeyCollection()) {
            result
                    .append(i++ == 0 ? "Where " : " And\n       ")
                    .append(column.getName() + " = ?");

        }

        return GenUtil.stringToJava(result.toString(), true);
    }

    public static String genUdateLob(Table table, TableColumn column) {
        StringBuilder result = new StringBuilder("Update " + table.getName() + "\n");
        if (column.isClob()) {
            result.append("Set " + column.getName() + " = empty_CLOB()\n");
        } else if (column.isBlob()) {
            result.append("Set " + column.getName() + " = empty_BLOB()\n");
        }

        int i = 0;
        for (TableColumn tableColumn : table.getPrimaryKeyCollection()) {
            result
                    .append(i++ == 0 ? "Where " : " And\n       ")
                    .append(tableColumn.getName() + " = ?");

        }

        return GenUtil.stringToJava(result.toString(), true);
    }

    public static String genStatement(String packageName, Method dbMethod) {
        String prefix = BaseFunction.isBlank(packageName) ? StringUtil.EMPTY : packageName + ".";
        if (dbMethod instanceof Function) {
            if (isOracleBoolean(((Function) dbMethod).getReturnType()))
                return MessageFormat.format(STATEMENT_FUNCTION_BOOL, prefix + dbMethod.getName(), genArgumentParamList(dbMethod));
            else
                return MessageFormat.format(STATEMENT_FUNCTION, prefix + dbMethod.getName(), genArgumentParamList(dbMethod));
        } else {
            return MessageFormat.format(STATEMENT_PROCEDURE, prefix + dbMethod.getName(), genArgumentParamList(dbMethod));
        }
    }

    public static String genArgumentParamList(Method procedure) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < procedure.getArguments().size(); i++) {
            if (i != 0) {
                result.append(", ");
            }
            result.append("?");
        }

        return result.toString();
    }

    public static String genArgumentList(Method procedure, boolean connection, boolean type) {
        StringBuilder result = new StringBuilder();

        if (connection) {
            if (type) {
                result.append("Connection connection");
            } else {
                result.append("connection");
            }
        }

        for (Argument argument : procedure.getArguments()) {
            if (result.length() != 0) {
                result.append(", ");
            }

            if (type) {
                result.append(getJavaClass(argument) + " ");
            }
            result.append(GenUtil.initLow(argument.getName()));
        }

        return result.toString();
    }
}
