package org.kpa.sonar.interpolate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = InterpolationCallable.class, name = "interpolate_task"),
        @JsonSubTypes.Type(value = Data.class, name = "task_data"),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface JsonWork {
}
