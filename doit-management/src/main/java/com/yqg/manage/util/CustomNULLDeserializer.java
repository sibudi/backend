package com.yqg.manage.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.manage.service.order.request.OrderSearchRequest.OrderChannelEnum;
import com.yqg.manage.service.order.request.OrderSearchRequest.UserRoleEnum;
import java.io.IOException;
import org.springframework.util.StringUtils;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 *
 ****/
public class CustomNULLDeserializer {

    public static class UserRoleDeserializer extends JsonDeserializer<UserRoleEnum>{
        @Override
        public UserRoleEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
            if(StringUtils.isEmpty(jsonParser.getText())){
                return null;
            }
            return UserRoleEnum.valueOf(jsonParser.getText());
        }
    }


    public static class OrderChannelDeserializer extends JsonDeserializer<OrderChannelEnum>{
        @Override
        public OrderChannelEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
            if(StringUtils.isEmpty(jsonParser.getText())){
                return null;
            }
            return OrderChannelEnum.valueOf(jsonParser.getText());
        }
    }

    public static class OrderStatusDeserializer extends JsonDeserializer<OrdStateEnum>{
        @Override
        public OrdStateEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
            if(StringUtils.isEmpty(jsonParser.getText())){
                return null;
            }
            return OrdStateEnum.valueOf(jsonParser.getText());
        }
    }


}
