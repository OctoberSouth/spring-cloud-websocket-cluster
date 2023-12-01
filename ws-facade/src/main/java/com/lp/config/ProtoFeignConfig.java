package com.lp.config;

import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

/**
 * 让feign支持Proto
 *
 * @author lp
 */
@Configuration
public class ProtoFeignConfig {
    
    @Bean
    public ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
    }

    @Bean
    public Encoder springEncoder(ObjectFactory<HttpMessageConverters> messageConverterObjectFactory) {
        return new SpringEncoder(messageConverterObjectFactory);
    }

    @Bean
    public Decoder springDecoder(ObjectFactory<HttpMessageConverters> messageConverterObjectFactory, ObjectProvider<HttpMessageConverterCustomizer> messageConverterCustomizerProvider) {
        return new ResponseEntityDecoder(new SpringDecoder(messageConverterObjectFactory, messageConverterCustomizerProvider));
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}
