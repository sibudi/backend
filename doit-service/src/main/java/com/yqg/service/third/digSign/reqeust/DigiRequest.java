package com.yqg.service.third.digSign.reqeust;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


//
// mapper.setVisibilityChecker（mapper.getSerializationConfig（）.getDefaultVisibilityChecker（）
//         .withFieldVisibility（JsonAutoDetect.Visibility.ANY）
//         .withGetterVisibility（JsonAutoDetect.Visibility.NONE）
//         .withSetterVisibility JsonAutoDetect.Visibility.NONE）
//         .withCreatorVisibility（JsonAutoDetect.Visibility.NONE））


@Getter
@Setter
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class DigiRequest<T> {
   @JsonProperty(value = "JSONFile")
    private T JSONFile;

    public DigiRequest<T> withJsonFile(T data){
        setJSONFile(data);
        return this;
    }
}
