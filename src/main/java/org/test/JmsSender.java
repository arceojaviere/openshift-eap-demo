package org.test;

import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

@Named("sender")
public class JmsSender {
	
	//En caso de que no utilicen CDI (JEE 6) en MUI, será necesario cargar los recursos manualmente en el codigo
	//utilizando el mecanísmo estándar de JNDI. Por ejemplo: 
	//this.factory = (QueueConnectionFactory)InitialContext.lookup("java:/FuseConnectionFactory")
	
//	@Resource(lookup="java:/ConnectionFactory")
	@Resource(lookup="java:/FuseConnectionFactory")
	QueueConnectionFactory factory;
	
//	@Resource(lookup="java:/jms/queue/OutQueueLocal")
	@Resource(lookup="java:/jms/queue/AMQInQueue")
	Queue outQueue;
	
//	@Resource(lookup="java:/jms/queue/InQueueLocal")
	@Resource(lookup="java:/jms/queue/AMQOutQueue")
	Queue inQueue;
	
	@Inject
	private ResponseHolder holder;
	
	public void sendMessage(String message) throws Exception{
		QueueConnection connection = null;
		QueueSession session;
		Message jmsMessage;
		MessageProducer producer;
		QueueReceiver receiver;
		byte[] bytes;
		long waitTime;
		String correlationId;
		String shortId;
		
		try{
			correlationId = UUID.randomUUID().toString();
			shortId = correlationId.replaceAll("-.*", "");
			
			System.out.println(shortId + ": Message to send: " + message);
			
			connection = this.factory.createQueueConnection();
			session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			producer = session.createProducer(this.outQueue);
			connection.start();
			
			jmsMessage = session.createTextMessage(message);
			jmsMessage.setJMSCorrelationID(correlationId);
			
			System.out.println(shortId + ": Message correlation ID: " + jmsMessage.getJMSCorrelationID());
			
			System.out.println(shortId + ": Sending...");
			
			
			//Como prueba, se envia el contenido de un text input de la pantalla. Para el caso de la ejecucion de programas debe enviarse
			//un payload json con la siguiente estructura: {"name":"nombreSimbolico", "arguments":["arg1", "arg2"]}
			//El nombre simbolico debe estar registrado en el archivo de propiedades del componente de ejecucion.
			//Puede utilizarse el bean Program y efectuar un marshalling JSON utilizando cualquier libreria (Jackson, Gson, etc) para generar la estructura JSON del payload
			
			producer.send(jmsMessage);
			
			System.out.println(shortId + ": Sent");
			/* ------------------------------------------------------------------------------------------- */
			//TODO: Para pruebas de concurrencia solamente. No implementar en codigo productivo
			
			waitTime = (long) (Math.random() * 30000);
			
			System.out.println(shortId + ": Sleeping " + waitTime + "ms");
			Thread.sleep(waitTime);
			System.out.println(shortId + ": Awoken. Receiving ");
			
			/* ------------------------------------------------------------------------------------------- */
			
			
			receiver = session.createReceiver(this.inQueue,"JMSCorrelationID='"+correlationId+"'");
			jmsMessage = receiver.receive();
			
			System.out.println(shortId + ": Response message correlation ID: " + jmsMessage.getJMSCorrelationID());
			System.out.println(shortId + ": Response message type: " + jmsMessage.getJMSType());
			System.out.println(shortId + ": Response message: " + jmsMessage);
			
			
			if("Text".equals(jmsMessage.getJMSType())){
				this.holder.setResponse(((TextMessage)jmsMessage).getText());
				System.out.println(shortId + ": " + this.holder.getResponse());
				
			}else if("Bytes".equals(jmsMessage.getJMSType())){
				System.out.println(shortId + ": " + ((BytesMessage)jmsMessage).getBodyLength());
				bytes = new byte[(int) ((BytesMessage)jmsMessage).getBodyLength()];
				((BytesMessage)jmsMessage).readBytes(bytes);
				System.out.println(shortId + ": " + bytes);
				
				this.holder.setResponse(new String(bytes));
				System.out.println(shortId + ": " + this.holder.getResponse());
			}else{
				throw new Exception("Unknown message type");
			}
		}catch(Exception exc){
			exc.printStackTrace();
			throw exc;
		}finally{
			if(connection != null){
				connection.close();
			}
		}
		
		
	}	
}
