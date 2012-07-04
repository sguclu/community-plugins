package ext.deployit.community.cli.manifestexport.dar;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.copyOf;
import static ext.deployit.community.cli.manifestexport.collect.Maps2.transformKeys;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class DarManifestBuilder extends ManifestBuilder {
    public static final String APPLICATION_ATTRIBUTE_NAME = "Ci-Application";
    public static final String VERSION_ATTRIBUTE_NAME = "Ci-Version";
    private static final String PACKAGE_FORMAT_VERSION_ATTRIBUTE_NAME = "Deployit-Package-Format-Version";
    private static final String PACKAGE_FORMAT_VERSION_NUMBER = "1.3";

    public DarManifestBuilder() {
        addMainAttribute(PACKAGE_FORMAT_VERSION_ATTRIBUTE_NAME, 
                PACKAGE_FORMAT_VERSION_NUMBER);
    }

    public DarManifestBuilder setApplication(@Nonnull String applicationName) {
        addMainAttribute(APPLICATION_ATTRIBUTE_NAME, checkNotNull(applicationName, "applicationName"));
        return this;
    }

    public DarManifestBuilder setVersion(@Nonnull String version) {
        addMainAttribute(VERSION_ATTRIBUTE_NAME, checkNotNull(version, "version"));
        return this;
    }

    public DarManifestBuilder addDarEntry(@Nonnull DarEntry entry) {
        entry.addToManifest(this);
        return this;
    }

    public DarManifestBuilder addDarEntries(@Nonnull Iterable<DarEntry> entries) {
        for (DarEntry entry : entries) {
            addDarEntry(entry);
        }
        return this;
    }

    public static class DarEntry {
        // null object to indicate "no result"
        public static final DarEntry NULL = new DarEntry("", ImmutableMap.<String, String>of(), "");
        public static final String CI_ATTRIBUTE_PREFIX = "Ci-";

        private static final String TYPE_ATTRIBUTE_NAME = CI_ATTRIBUTE_PREFIX + "type";

        private final String type;
        private final Map<String, String> properties;
        private final String jarEntryPath;

        public DarEntry(@Nonnull String type, @Nonnull Map<String, String> properties, 
                @Nonnull String jarEntryPath) {
            this.type = checkNotNull(type, "type");
            this.properties = copyOf(checkNotNull(properties, "properties"));
            this.jarEntryPath = checkNotNull(jarEntryPath, "jarEntryPath");
        }

        private void addToManifest(@Nonnull ManifestBuilder builder) {
            // type and all the properties
            Map<String, String> attributes = Maps.newHashMapWithExpectedSize(
                    1 + properties.size());
            attributes.put(TYPE_ATTRIBUTE_NAME, type);
            attributes.putAll(transformKeys(properties, new Function<String, String>() {
                    @Override
                    public String apply(String input) {
                        return CI_ATTRIBUTE_PREFIX + input;
                    }
                }));
            builder.addEntryAttributes(jarEntryPath, attributes);
        }

        @Override
        public String toString() {
            return "DarEntry [type=" + type + ", properties=" + properties
                    + ", jarEntryPath=" + jarEntryPath + "]";
        }
    }
}