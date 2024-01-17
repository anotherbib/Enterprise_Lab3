/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmsprimeinput;
import java.util.Scanner;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import jmsprimeinput.TextListener;
/**
 *
 * @author thebi
 */
public class Main {

    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/JMSQueue")
    private static Queue queue;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Connection connection = null;
        TextListener listener = null;
        
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(
                        false,
                        Session.AUTO_ACKNOWLEDGE);
            listener = new TextListener();
            Queue tempDest = session.createTemporaryQueue();
            MessageConsumer responseConsumer = session.createConsumer(tempDest);
            responseConsumer.setMessageListener(listener);
            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage();
            //message.setText("Hello friend" );
            message.setJMSReplyTo(tempDest);
            String correlationId = "12345";
            message.setJMSCorrelationID(correlationId);
            connection.start();
            //System.out.println("Sending message: " + message.getText());
            //producer.send(message);
            
            String ch = "";
            Scanner inp = new Scanner(System.in);
            System.out.println("press q to quit...");
            while(true) {
                System.out.print("Insert number: ");
                ch = inp.nextLine();
                if(ch.equals("q")){
                    break;
                }else{
                    message.setText(ch);
                    producer.send(message);
                }

            }
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }
    
}
