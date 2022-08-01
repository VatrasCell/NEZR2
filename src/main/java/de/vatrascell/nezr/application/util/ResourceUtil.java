package de.vatrascell.nezr.application.util;

import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

@UtilityClass
public class ResourceUtil {

    public static URL getURL(String path) {
        return Objects.requireNonNull(ResourceUtil.class.getClassLoader().getResource(path));
    }

    public static InputStream getInputStream(String path) {
        return Objects.requireNonNull(ResourceUtil.class.getClassLoader().getResourceAsStream(path));
    }
}
