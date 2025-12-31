package com.dsa.json;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.format.datetime.standard.InstantFormatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonSerialization {

    public static void main(String[] args) throws JsonMappingException, JsonProcessingException, ParseException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        String result = "[{\"col_smallint\":\"1\",\"col_integer\":\"1000\",\"col_bigint\":\"1234567890123\",\"col_decimal\":\"1234.56\",\"col_numeric\":\"7890.12345\",\"col_real\":\"3.14\",\"col_double_precision\":\"2.718281828\",\"col_smallserial\":\"1\",\"col_serial\":\"1\",\"col_bigserial\":\"1\",\"col_money\":\"99.99\",\"col_char\":\"A         \",\"col_varchar\":\"Example Text\",\"col_text\":\"This is some long text.\",\"col_bytea_hex\":\"[B@26da4a67\",\"col_bytea_escape\":\"[B@5a8e7935\",\"col_date\":\"2025-07-17\",\"col_time\":\"14:30:00\",\"col_time_tz\":\"14:30:00\",\"col_timestamp\":\"2025-07-17 14:30:00.0\",\"col_timestamp_tz\":\"2025-07-17 14:30:00.0\",\"col_interval\":\"0 years 0 mons 2 days 3 hours 0 mins 0.0 secs\",\"col_boolean\":\"true\",\"col_mood\":\"happy\",\"col_uuid\":\"550e8400-e29b-41d4-a716-446655440000\",\"col_json\":\"{\\\"key\\\": \\\"value\\\"}\",\"col_jsonb\":\"{\\\"key\\\": \\\"value\\\"}\",\"col_int_array\":\"{1,2,3}\",\"col_inet\":\"192.168.1.1\",\"col_cidr\":\"192.168.0.0/16\",\"col_macaddr\":\"08:00:2b:01:02:03\",\"col_macaddr8\":\"08:00:2b:01:02:03:04:05\",\"col_bit_fixed\":\"10101010\",\"col_bit_varying\":\"101010101111\",\"col_tsvector\":\"\\\"chatgpt\\\" \\\"openai\\\" \\\"rocks\\\"\",\"col_tsquery\":\"\\\"chat\\\" & \\\"gpt\\\"\",\"col_point\":\"(1.0,2.0)\",\"col_line\":\"{1.0,2.0,3.0}\",\"col_lseg\":\"[(1.0,2.0),(3.0,4.0)]\",\"col_box\":\"(3.0,4.0),(1.0,2.0)\",\"col_path\":\"[(1.0,2.0),(2.0,3.0),(3.0,4.0)]\",\"col_polygon\":\"((1.0,1.0),(2.0,2.0),(3.0,1.0))\",\"col_circle\":\"<(3.0,4.0),2.0>\",\"col_int_range\":\"[1,11)\",\"col_num_range\":\"[100.5,200.75]\",\"col_ts_range\":\"[\\\"2025-07-17 12:00:00\\\",\\\"2025-07-17 14:00:00\\\"]\",\"col_tstz_range\":\"[\\\"2025-07-17 12:00:00+05:30\\\",\\\"2025-07-17 14:00:00+05:30\\\"]\",\"col_date_range\":\"[2025-07-17,2025-07-21)\",\"col_xml\":\"org.postgresql.jdbc.PgSQLXML@2ee272b7\",\"col_oid\":\"12345\",\"col_pg_lsn\":\"16/B374D848\"}]";
//        List<Map<String, Object>> convertedResult = objectMapper.readValue(result, new TypeReference<List<Map<String, Object>>>() {
//        });
//        System.out.println("res: " + convertedResult);
//        
//        System.out.println("Json Array: " + new JSONArray(result));
//        
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("Data", convertedResult);
//        
//        System.out.println("json res: " + jsonObject);
        
//        String str = "[2025-07-17,2025-07-21)";
//         System.out.println("repl: " + str.replaceAll("[\\]\\()", ""));
//        System.out.println("repl: " + str.replace(")", "");
        
        
        // ISO conversion
        List<Object> list = new ArrayList<>();
        list.add("2025-07-17 12:00:00");
        list.add("2025-07-21 17:18:35.994661+05:30");
        
//        LocalDateTime localDateTime = LocalDateTime.parse("2025-07-21 17:18:35.994661");
//        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
//        System.out.println("Instant: " + instant);
//        String format = DateTimeFormatter.ISO_INSTANT.format(instant);
//        System.out.println(format);

        list = list.stream().map(timestamp -> {
            System.out.println("ts: " + new String(timestamp.toString()));
            LocalDateTime localDateTime = LocalDateTime.parse(String.valueOf(timestamp).replace(" ", "T").split("\\+")[0]);
            Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            return DateTimeFormatter.ISO_INSTANT.format(instant);
        }).collect(Collectors.toList());
        System.out.println(("Formatted List: " + list));
    }
}
