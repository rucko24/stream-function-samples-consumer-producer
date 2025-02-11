package oz.stream.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DocValuesList {

    @JsonIgnore
    private String dato;

    @JsonProperty("doc_count")
    private Long docCount;
}
