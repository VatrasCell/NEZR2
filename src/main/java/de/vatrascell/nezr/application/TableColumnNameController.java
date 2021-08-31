package de.vatrascell.nezr.application;

import de.vatrascell.nezr.message.TableColumnNameId;

public class TableColumnNameController {

    private static PropertyLoader propertyLoader;

    static {
        propertyLoader = new PropertyLoader("tableColumnName");
    }

    public static String getColumnName(TableColumnNameId name) {
        return propertyLoader.getPropertyValue(name);
    }
}
