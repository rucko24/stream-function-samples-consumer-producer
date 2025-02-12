package oz.stream.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Valores {

    @JsonProperty("values")
    private List<DocValuesList> docValuesListList;

}
