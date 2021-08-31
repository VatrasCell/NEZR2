package de.vatrascell.nezr.application;

import de.vatrascell.nezr.message.AbstractId;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static de.vatrascell.nezr.application.GlobalFuncs.getURL;

public class PropertyLoader {

    private Properties properties;

    public PropertyLoader(String propertyName) {
        String path = getURL(String.format("property/%s.properties", propertyName)).getPath();
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(path));
            properties = appProps;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T extends AbstractId> String getPropertyValue(T id) {
        return properties.getProperty(id.name());
    }
}
