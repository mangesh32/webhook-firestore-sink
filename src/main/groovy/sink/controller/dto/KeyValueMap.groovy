package sink.controller.dto


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.util.logging.Slf4j

@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
class KeyValueMap implements Serializable{

    @JsonProperty("gpsLatitude")
    Double latitude

    @JsonProperty("gpsLongitude")
    Double longitude

    @JsonProperty("1516_1")
    String threatFlag

    @JsonProperty("1512_1")
    String threatValue

    @Override
    public String toString() {
        return "KeyValueMap{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", threatFlag='" + threatFlag + '\'' +
                ", threatValue='" + threatValue + '\'' +
                '}';
    }
}
