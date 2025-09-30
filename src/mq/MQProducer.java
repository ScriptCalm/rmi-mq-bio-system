package mq;

import jakarta.jms.*;
import shared.BioDetails;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;

public class MQProducer implements AutoCloseable {

    private InitialContext ctx;
    private Connection connection;
    private Session session;
    private MessageProducer producer;

    public MQProducer() throws Exception {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        env.put(Context.PROVIDER_URL, "remote+http://localhost:8080");
        env.put(Context.SECURITY_PRINCIPAL, "gabriel");
        env.put(Context.SECURITY_CREDENTIALS, "Gabriel@1234");
        env.put("wildfly.naming.client.ejb.context", "true");

        ctx = new InitialContext(env);

        ConnectionFactory cf = (ConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
        Queue queue = (Queue) ctx.lookup("jms/queue/GabrielQueue");

        connection = cf.createConnection("gabriel", "Gabriel@1234");
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(queue);
        connection.start();
    }

    public void send(BioDetails details) throws JMSException {
        ObjectMessage msg = session.createObjectMessage(details);
        producer.send(msg);
    }

    public void close() throws Exception {
        if (producer != null) producer.close();
        if (session != null) session.close();
        if (connection != null) connection.close();
        if (ctx != null) ctx.close();
    }
}
