package it.eg.sloth.mavenplugin.writer.bean2;

import it.eg.sloth.dbmodeler.model.DataBase;
import it.eg.sloth.dbmodeler.model.database.DataBaseType;
import it.eg.sloth.dbmodeler.model.schema.Schema;
import it.eg.sloth.dbmodeler.model.schema.code.Function;
import it.eg.sloth.dbmodeler.model.schema.code.Package;
import it.eg.sloth.dbmodeler.model.schema.code.Procedure;
import it.eg.sloth.dbmodeler.model.schema.sequence.Sequence;
import it.eg.sloth.dbmodeler.model.schema.table.Table;
import it.eg.sloth.dbmodeler.model.schema.view.View;
import it.eg.sloth.mavenplugin.writer.bean2.h2.H2BeanWriter;
import it.eg.sloth.mavenplugin.writer.bean2.oracle.OracleBeanWriter;
import it.eg.sloth.mavenplugin.writer.bean2.postgres.PostgresBeanWriter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

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
public interface BeanWriter {

    void writeTables() throws IOException;

    void writeViews() throws IOException;

    void writeSequences() throws IOException;

    class Factory {
        private Factory() {
            // NOP
        }

        public static BeanWriter getBeanWriter(File outputJavaDirectory, String genPackage, DataBase dataBase) {
            // Imposto il reader corretto
            switch (dataBase.getDbConnection().getDataBaseType()) {
                case H2:
                    return new H2BeanWriter(outputJavaDirectory, genPackage, dataBase);
                case ORACLE:
                    return new OracleBeanWriter(outputJavaDirectory, genPackage, dataBase);
                case POSTGRES:
                    return new PostgresBeanWriter(outputJavaDirectory, genPackage, dataBase);
                default:
                    // NOP
            }

            return null;
        }
    }

}
