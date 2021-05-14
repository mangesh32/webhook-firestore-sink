package sink.controller.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.util.logging.Slf4j

@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
class Payload implements Serializable{

    Payload() {}

    @JsonCreator
    Payload(String message) {
        log.info("JsonData => {}",message)
    }

    String tenantId
    String hubId
    Long epochTimeMillis

    JsonData jsonData


    @Override
    public String toString() {
        return "Payload{" +
                "tenantId='" + tenantId + '\'' +
                ", hubId='" + hubId + '\'' +
                ", epochTimeMillis=" + epochTimeMillis +
                ", jsonData=" + jsonData +
                '}';
    }
}
