package com.lp.config;

import com.lp.constants.MqTopicConstants;
import com.lp.listener.UserServerListener;
import com.lp.listener.WsUserServerCloseListener;
import com.lp.listener.WsUserServerListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author Administrator
 */
@Configuration
public class RedisConfig {

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory, MessageListenerAdapter wsUserServerCloseListenerAdapter,
                                                                MessageListenerAdapter wsUserServerListenerAdapter, MessageListenerAdapter userServerListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(wsUserServerListenerAdapter, new PatternTopic(MqTopicConstants.SOCKET_USER_SPRING_APPLICATION));
        container.addMessageListener(wsUserServerCloseListenerAdapter, new PatternTopic(MqTopicConstants.SOCKET_USER_SPRING_APPLICATION));
        container.addMessageListener(userServerListenerAdapter, new PatternTopic(MqTopicConstants.USER_SERVER_NAME));
        return container;
    }

    @Bean
    MessageListenerAdapter wsUserServerCloseListenerAdapter(WsUserServerCloseListener wsUserServerCloseListener) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(wsUserServerCloseListener, "onMessage");
        listenerAdapter.setSerializer(RedisSerializer.json());
        return listenerAdapter;
    }

    @Bean
    MessageListenerAdapter wsUserServerListenerAdapter(WsUserServerListener wsUserServerListener) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(wsUserServerListener, "onMessage");
        listenerAdapter.setSerializer(RedisSerializer.json());
        return listenerAdapter;
    }

    @Bean
    MessageListenerAdapter userServerListenerAdapter(UserServerListener userServerListener) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(userServerListener, "onMessage");
        listenerAdapter.setSerializer(RedisSerializer.json());
        return listenerAdapter;
    }
}
