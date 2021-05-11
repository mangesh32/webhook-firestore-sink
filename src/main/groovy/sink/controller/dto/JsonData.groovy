package sink.controller.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class JsonData implements Serializable{

    String hubid

    Double latitude

    Double longitude

    Long epochTimeMillis

    @JsonProperty("1512_1")
    Integer threatFlag

    @JsonProperty("1512_2")
    String threatValue

}
