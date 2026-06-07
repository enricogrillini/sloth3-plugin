package it.eg.sloth.mavenplugin.writer.bean2;

import it.eg.sloth.dbmodeler.model.DataBase;
import it.eg.sloth.dbmodeler.model.schema.table.Table;
import it.eg.sloth.dbmodeler.model.schema.view.View;
import it.eg.sloth.mavenplugin.common.DbUtil;
import it.eg.sloth.mavenplugin.common.GenUtil;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
public class AbstractBeanWriter implements BeanWriter {

    private static final String DAO = "/templates/dao-{0}.java";
    private static final String POJO = "/templates/pojo-{0}.java";

    private static final String PACKAGE_TABLE_POJO = ".model.table";
    private static final String PACKAGE_VIEW_POJO = ".model.view";

    private static final String PACKAGE_TABLE_DAO = ".dao.table";
    private static final String PACKAGE_VIEW_DAO = ".dao.view";
    private static final String PACKAGE_SEQUENCE_DAO = ".dao.sequence";


    File outputJavaDirectory;
    String genPackage;

    VelocityEngine velocityEngine;
    Template pojoTemplateForTable;
    Template daoTemplateForTable;
    Template pojoTemplateForView;
    Template daoTemplateForView;

    Template daoTemplateForSequence;

    @Getter
    DataBase dataBase;


    public AbstractBeanWriter(File outputJavaDirectory, String genPackage, DataBase dataBase) {
        this.outputJavaDirectory = outputJavaDirectory;
        this.genPackage = genPackage;
        this.dataBase = dataBase;

        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
        velocityEngine.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init();

        pojoTemplateForTable = velocityEngine.getTemplate(MessageFormat.format(POJO, "TABLE"));
        daoTemplateForTable = velocityEngine.getTemplate(MessageFormat.format(DAO, "TABLE"));

        pojoTemplateForView = velocityEngine.getTemplate(MessageFormat.format(POJO, "VIEW"));

        daoTemplateForSequence = velocityEngine.getTemplate(MessageFormat.format(DAO, "SEQUENCE"));

    }

    public void writeTables() throws IOException {
        for (Table table : getDataBase().getSchema().getTableCollection()) {
            writeTable(table);
        }
    }

    public void writeTable(Table table) throws IOException {
        String className = DbUtil.javaClassName(table.getName());
        String objectName = DbUtil.javaObjectName(table.getName());

        // Gestione conflitti di naming
        if (table.getName().contains("_") && dataBase.getSchema().getTable(DbUtil.javaClassName(table.getName())) != null) {
            className = GenUtil.initCap(table.getName());
            objectName = GenUtil.initLow(table.getName());
        }

        // VelocityContext
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("pojoPackageName", genPackage + PACKAGE_TABLE_POJO);
        velocityContext.put("pojoClassName", className + "Pojo");
        velocityContext.put("pojoObjectName", objectName + "Pojo");

        velocityContext.put("daoPackageName", genPackage + PACKAGE_TABLE_DAO);
        velocityContext.put("daoClassName", className + "BaseDao");
        velocityContext.put("daoObjectName", objectName + "BaseDao");

        velocityContext.put("tableName", table.getName().toUpperCase());
        velocityContext.put("table", table);

        velocityContext.put("DbUtil", DbUtil.class);
        velocityContext.put("GenUtil", GenUtil.class);

        // Write class - Pojo
        File pojoClassFile = GenUtil.getClassFile(outputJavaDirectory, genPackage + PACKAGE_TABLE_POJO, className + "Pojo");
        FileUtils.forceMkdir(pojoClassFile.getParentFile());
        try (FileWriter fileWriter = new FileWriter(pojoClassFile)) {
            pojoTemplateForTable.merge(velocityContext, fileWriter);
        }

        // Write class - DAO
        File daoClassFile = GenUtil.getClassFile(outputJavaDirectory, genPackage + PACKAGE_TABLE_DAO, className + "BaseDao");
        FileUtils.forceMkdir(daoClassFile.getParentFile());
        try (FileWriter fileWriter = new FileWriter(daoClassFile)) {
            daoTemplateForTable.merge(velocityContext, fileWriter);
        }
    }

    public void writeViews() throws IOException {
        for (View view : getDataBase().getSchema().getViewCollection()) {
            writeView(view);
        }
    }

    public void writeView(View view) throws IOException {
        String className = DbUtil.javaClassName(view.getName());
        String objectName = DbUtil.javaObjectName(view.getName());

        // Gestione conflitti di naming
        if (view.getName().contains("_") && dataBase.getSchema().getTable(DbUtil.javaClassName(view.getName())) != null) {
            className = GenUtil.initCap(view.getName());
            objectName = GenUtil.initLow(view.getName());
        }

        // VelocityContext
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("pojoPackageName", genPackage + PACKAGE_VIEW_POJO);
        velocityContext.put("pojoClassName", className + "Pojo");
        velocityContext.put("pojoObjectName", objectName + "Pojo");

        velocityContext.put("daoPackageName", genPackage + PACKAGE_VIEW_DAO);
        velocityContext.put("daoClassName", className + "BaseDao");
        velocityContext.put("daoObjectName", objectName + "BaseDao");

        velocityContext.put("tableName", view.getName().toUpperCase());
        velocityContext.put("table", view);

        velocityContext.put("DbUtil", DbUtil.class);
        velocityContext.put("GenUtil", GenUtil.class);

        // Write class - Pojo
        File pojoClassFile = GenUtil.getClassFile(outputJavaDirectory, genPackage + PACKAGE_VIEW_POJO, className + "Pojo");
        FileUtils.forceMkdir(pojoClassFile.getParentFile());
        try (FileWriter fileWriter = new FileWriter(pojoClassFile)) {
            pojoTemplateForView.merge(velocityContext, fileWriter);
        }

    }

    public void writeSequences() throws IOException {
        String className = "SequenceDao";

        getDataBase().getSchema().getSequenceCollection();

        // VelocityContext
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("daoPackageName", genPackage + PACKAGE_SEQUENCE_DAO);
        velocityContext.put("daoClassName", className);

        velocityContext.put("sequences", getDataBase().getSchema().getSequenceCollection());

        velocityContext.put("DbUtil", DbUtil.class);
        velocityContext.put("GenUtil", GenUtil.class);

        // Write class - Pojo
        File pojoClassFile = GenUtil.getClassFile(outputJavaDirectory, genPackage + PACKAGE_SEQUENCE_DAO, className);
        FileUtils.forceMkdir(pojoClassFile.getParentFile());
        try (FileWriter fileWriter = new FileWriter(pojoClassFile)) {
            daoTemplateForSequence.merge(velocityContext, fileWriter);
        }

    }


}
