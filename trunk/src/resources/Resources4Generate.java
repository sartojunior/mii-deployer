package resources;

public interface Resources4Generate {
    public static class RESOURCE_WITH_PATH {
        public final String res;
        public final String path;
        public RESOURCE_WITH_PATH(String res, String path)
        {
            this.res = res;
            this.path = path;
        }
        public String getPath () 
        {
            return path + res;
        }
    };
    
    RESOURCE_WITH_PATH[] JSResources = {
        new RESOURCE_WITH_PATH("MiiReader", "asem/data/"),
        new RESOURCE_WITH_PATH("Common", "asem/utils/")
    };
    
    RESOURCE_WITH_PATH[] HTMLResources = {
        new RESOURCE_WITH_PATH("index", ""),
    };
}
