package com.lp.config;

import com.lp.constants.MqTopicConstants;
import com.lp.listener.WsUserServerListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory, MessageListenerAdapter wsUserServerListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(wsUserServerListenerAdapter, new PatternTopic(MqTopicConstants.SOCKET_USER_SPRING_APPLICATION));
        return container;
    }

    @Bean
    MessageListenerAdapter wsUserServerListenerAdapter(WsUserServerListener wsUserServerListener) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(wsUserServerListener, "onMessage");
        listenerAdapter.setSerializer(RedisSerializer.json());
        return listenerAdapter;
    }
}
