package it.richkmeli.jframework.util;

import java.io.InputStream;
import java.util.Properties;

public class AppInfo {

    // it works only with jar that contains pom.properties, at the end of this class there is an example of info to be added into pom file
    public static synchronized String getVersion(Class clazz){
        String version = null;

        // try to load from maven properties first
        try {
            Properties p = new Properties();
            InputStream is = /*getClass()*/clazz.getResourceAsStream("META-INF/maven/${groupId}/${artifactId}/pom.properties");
            if (is != null) {
                p.load(is);
                version = p.getProperty("version", null);
            }
        } catch (Exception e) {
            // ignore
        }

        // fallback to using Java API
        if (version == null) {
            Package aPackage = /*getClass()*/clazz.getPackage();
            if (aPackage != null) {
                version = aPackage.getImplementationVersion();
                if (version == null) {
                    version = aPackage.getSpecificationVersion();
                }
            }
        }

        /*if (version == null) {
            // we could not compute the version so use a blank
            version = "";
        }*/

        return version;
    }
}


//            <plugin>
//                <groupId>org.apache.maven.plugins</groupId>
//                <artifactId>maven-jar-plugin</artifactId>
//                <version>...</version>
//                <configuration>
//                    <archive>
//                        <index>true</index>
//                        <manifest>
//                            <addClasspath>true</addClasspath>
//                            <classpathPrefix>lib/</classpathPrefix>
//                            <mainClass>....</mainClass>
//                            <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
//                            <addDefaultSpecificationEntries>true</addDefaultSpecificationEntries>
//                        </manifest>
//                    </archive>
//                </configuration>
//            </plugin>