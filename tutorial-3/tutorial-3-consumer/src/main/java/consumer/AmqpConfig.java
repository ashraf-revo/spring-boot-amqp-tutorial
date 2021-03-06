package consumer;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

@EnableRabbit
@Configuration
public class AmqpConfig implements RabbitListenerConfigurer {
    static final String EXCHANGE_NAME = "tutorial-3-exchange";
    static final String QUEUE1_NAME = "tutorial-3-1";
    static final String QUEUE2_NAME = "tutorial-3-2";

    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    FanoutExchange exchange(RabbitAdmin rabbitAdmin) {
        FanoutExchange exchange = new FanoutExchange(EXCHANGE_NAME);
        rabbitAdmin.declareExchange(exchange);

        Queue queue1 = new Queue(QUEUE1_NAME);
        Queue queue2 = new Queue(QUEUE2_NAME);
        rabbitAdmin.declareQueue(queue1);
        rabbitAdmin.declareQueue(queue2);

        rabbitAdmin.declareBinding(BindingBuilder.bind(queue1)
                .to(exchange));
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue2)
                .to(exchange));

        return exchange;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(myHandlerMethodFactory());
    }

    @Bean
    public DefaultMessageHandlerMethodFactory myHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(new MappingJackson2MessageConverter());
        return factory;
    }
}
