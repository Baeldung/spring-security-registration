package com.baeldung.spring;

import java.io.File;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;

@Configuration
public class DatabaseReaderConfig {

    @Bean(name = "GeoIPCountry")
    public static DatabaseReader databaseReader() throws IOException, GeoIp2Exception {
        final File resource = new File("src/main/resources/maxmind/GeoLite2-Country.mmdb");
        return new DatabaseReader.Builder(resource).build();
    }
}
