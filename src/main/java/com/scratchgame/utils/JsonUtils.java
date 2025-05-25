// JsonUtils.java
  package com.scratchgame.utils;
  
  import com.fasterxml.jackson.databind.ObjectMapper;
  import com.scratchgame.Config;
  
  import java.io.IOException;
  import java.nio.file.Path;
  
  public class JsonUtils {
      public static Config loadConfig(Path path) {
          ObjectMapper mapper = new ObjectMapper();
          try {
              return mapper.readValue(path.toFile(), Config.class);
          } catch (IOException e) {
              e.printStackTrace();
              return null;
          }
      }
  }