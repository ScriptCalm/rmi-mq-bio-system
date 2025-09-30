package mq;

import java.io.FileOutputStream;
import java.io.PrintStream;

import jakarta.jms.*;
import shared.BioDetails;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Properties;

public class AuditLogger {

    private static final String CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String QUEUE_NAME = "jms/queue/GabrielQueue";

    private static final String USERNAME = "gabriel";
    private static final String PASSWORD = "Gabriel@1234";
    private static final String AUDIT_FILE = "audit.log";

    private InitialContext ctx;
    private Connection connection;
    private Session session;
    private MessageConsumer consumer;
    private PrintWriter writer;

    public AuditLogger() throws Exception {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        env.put(Context.PROVIDER_URL, "remote+http://localhost:8080");
        env.put(Context.SECURITY_PRINCIPAL, USERNAME);
        env.put(Context.SECURITY_CREDENTIALS, PASSWORD);
        env.put("wildfly.naming.client.ejb.context", "true");

        ctx = new InitialContext(env);

        ConnectionFactory cf = (ConnectionFactory) ctx.lookup(CONNECTION_FACTORY);
        Queue queue = (Queue) ctx.lookup(QUEUE_NAME);

        connection = cf.createConnection(USERNAME, PASSWORD);
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        consumer = session.createConsumer(queue);
        writer = new PrintWriter(new FileWriter(AUDIT_FILE, true), true);

        connection.start();
    }

    public void run() throws Exception {
        System.out.println("📡 AuditLogger running — waiting for messages... (Ctrl+C to stop)");
        while (true) {
            Message msg = consumer.receive(60000); // Wait up to 60s
            if (msg == null) continue;

            if (msg instanceof ObjectMessage objMsg) {
                BioDetails bio = (BioDetails) objMsg.getObject();
                String line = String.format("[%s] MQ Received: %s", LocalDateTime.now(), bio);
                System.out.println(line);
                writer.println(line);
            } else {
                System.out.println("⚠ Received non-object JMS message.");
            }
        }
    }

    public void close() throws Exception {
        if (writer != null) writer.close();
        if (consumer != null) consumer.close();
        if (session != null) session.close();
        if (connection != null) connection.close();
        if (ctx != null) ctx.close();
    }

    public static void main(String[] args) {
        System.setProperty("java.util.logging.config.file", "src/mq/logging.properties");

        AuditLogger logger = null;
        try {
            logger = new AuditLogger();
            logger.run(); // blocks
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (logger != null) {
                try {
                    logger.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
