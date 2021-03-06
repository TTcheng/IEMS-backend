package me.wcc.base.infra.serial;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import me.wcc.base.helper.TimeZoneHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 根据用户的时区反序列化时间
 *
 * @author wuguokai
 */
public class DateDeserializer extends JsonDeserializer<Date> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateDeserializer.class);

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        try {
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timezone = TimeZoneHelper.timezone();
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone(timezone));
            return dateFormatGmt.parse(jsonParser.getValueAsString());
        } catch (Exception e) {
            LOGGER.warn("date format error : {}", e);
            return null;
        }
    }
}
