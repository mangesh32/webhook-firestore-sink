package sink.controller.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.util.logging.Slf4j

@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
class JsonData implements Serializable{

    JsonData() {}

    @JsonCreator
    JsonData(String message) {
        log.info("JsonData => {}",message)
    }

    String hubid

    Double latitude

    Double longitude

    Long epochTimeMillis

    @JsonProperty("1512_1")
    Integer threatFlag

    @JsonProperty("1512_2")
    String threatValue


    @Override
    public String toString() {
        return "JsonData{" +
                "hubid='" + hubid + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", epochTimeMillis=" + epochTimeMillis +
                ", threatFlag=" + threatFlag +
                ", threatValue='" + threatValue + '\'' +
                '}';
    }
}
