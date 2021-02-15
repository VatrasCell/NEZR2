package application;

import message.MessageId;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static application.GlobalFuncs.getURL;

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

    public String getPropertyValue(MessageId messageId) {
        return properties.getProperty(messageId.name());
    }
}
