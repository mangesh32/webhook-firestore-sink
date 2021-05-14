package sink.controller.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j

@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
class JsonData implements Serializable{

    JsonData(){}

    @JsonCreator
    JsonData(String message) {
        JsonData jsonData = new ObjectMapper().readValue(message, JsonData.class)
        this.keyValueMap = jsonData.keyValueMap
    }

    KeyValueMap keyValueMap


    @Override
    public String toString() {
        return "JsonData{" +
                "keyValueMap=" + keyValueMap +
                '}';
    }
}
